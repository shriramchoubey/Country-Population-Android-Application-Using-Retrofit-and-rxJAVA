package io.shriram.country;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.shriram.country.Model.Contacts;
import io.shriram.country.Model.Country;
import io.shriram.country.common.ResponseClass;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity{
    private ArrayList<Country> arraylist= new ArrayList<>();
    private ArrayList<Contacts> Storecontacts = new ArrayList<>();
    ProgressBar pb,pb2;
    Button bt_sync;
    private RecyclerView recyclerview;
    public static final String BASE_URL = "http://www.androidbegin.com/tutorial/";

    public  static final int RequestPermissionCode  = 1 ;
    private Cursor cursor ;
    private String name, phonenumber ;
    private CompositeDisposable mCompositeDisposable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);

        setContentView(R.layout.activity_main);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("population");
        O
        recyclerview = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerview.addOnItemTouchListener( new RecyclerItemClickListener(MainActivity.this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override public void onItemClick(View view, int position) {
                // TODO Handle item click

                Country c = arraylist.get(position);
                Intent in = new Intent(MainActivity.this,ImageViewer.class);
                in.putExtra("country",c.getCountry());
                in.putExtra("flag",c.getFlag());
                startActivity(in);

            }
        }));
        //initialising sync button
        bt_sync = (Button) findViewById(R.id.bt);


        //initialising progressbar
        pb = (ProgressBar) findViewById(R.id.pb);
        pb2 = (ProgressBar) findViewById(R.id.pb2);
        pb.setVisibility(View.VISIBLE);
        pb2.setVisibility(View.INVISIBLE);

        mCompositeDisposable = new CompositeDisposable();

        //loading Data from data source
        loadJSON();



    }

    //function to sync data
    public void sync(View v){
        bt_sync.setVisibility(View.INVISIBLE);
        pb2.setVisibility(View.VISIBLE);
        PublishSubject contactpipe = PublishSubject.create();

        Thread thread = new Thread()
        {
            @Override
            public void run() {
                try {
                    while(true) {
                        sleep(1000);
                        GetContactsIntoArrayList();
                    }
                } catch (InterruptedException e) {
                    Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG).show();
                }
            }
        };

        thread.start();
    }

    //subscribes to new thread
    public void contactReader(){

    }



    //Get All the contacts and show save them in the CSV format;
    public void GetContactsIntoArrayList(){
       // EnableRuntimePermission();
        cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null, null, null);
        String s="";
        while (cursor.moveToNext()) {

            name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

            phonenumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            Storecontacts.add(new Contacts(name,phonenumber));
            s+=name+" : "+phonenumber+"\n";
        }
        Log.d("Contacts : \n",s);

        cursor.close();
//        bt_sync.setVisibility(View.VISIBLE);
    }

    // showing the contents of countries list through arraylist
    public void initRecycler(ArrayList<Country> data){
        arraylist = data;
        final RecyclerCustomAdapter adapter = new RecyclerCustomAdapter(MainActivity.this,arraylist);
        recyclerview.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this,LinearLayoutManager.VERTICAL,false);
        //GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this,2);
        recyclerview.setLayoutManager(layoutManager);
    }

    //loading data through datasourse using retrofit and rxJAVA
    public void loadJSON(){

        //creating retrofit interface
        RequestInterface requestInterface = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(RequestInterface.class);


        mCompositeDisposable.add(requestInterface.getData()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<ResponseClass>() {
                    @Override
                    public void onNext(ResponseClass value) {
                        //setting visibility off
                        pb.setVisibility(View.INVISIBLE);

                        List<Country> c = value.getCountry();

                        //initialising Recyclerview
                        initRecycler(new ArrayList<Country>(c));
                    }

                    @Override
                    public void onError(Throwable e) {

                        pb.setVisibility(View.INVISIBLE);
                        Log.d("Response : ", ""+ e);
                    }

                    @Override
                    public void onComplete() {

                    }
                }));

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.clear();
    }


    public static class RecyclerCustomAdapter extends
            RecyclerView.Adapter<RecyclerCustomAdapter.ViewHolder> {

        Context mContext;
        ArrayList<Country> mArrayList;

        //constructor
        public RecyclerCustomAdapter(Context context,ArrayList<Country> marrayList){
            mContext = context;
            mArrayList = marrayList;
        }

        //easy access to context items objects in recyclerView
        private Context getContext() {
            return mContext;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            // Inflate the custom layout
            View contactView = inflater.inflate(R.layout.data_card, parent, false);

            // Return a new holder instance
            ViewHolder viewHolder = new ViewHolder(contactView);
            return viewHolder;

        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {

            // Get the data model based on position
            Country cat = mArrayList.get(position);

            // Set item views based on your views and data model
            TextView tv_name = viewHolder.name;
            TextView tv_population = viewHolder.population;
            SimpleDraweeView img = viewHolder.cat_img;


            tv_name.setText(cat.getCountry());
            tv_population.setText(cat.getPopulation()+"  people");

            //fetch image through uri
            Log.d("aaaaa","4545");
            Uri imageUri = Uri.parse(cat.getFlag());
            Drawable myIcon = getContext().getResources().getDrawable( R.drawable.placeholder );

            GenericDraweeHierarchyBuilder builder =
                    new GenericDraweeHierarchyBuilder(getContext().getResources())
                            .setActualImageScaleType(ScalingUtils.ScaleType.FIT_XY);;
            GenericDraweeHierarchy hierarchy = builder
                    .setFadeDuration(300)
                    .setPlaceholderImage(myIcon)

                    .build();
            img.setHierarchy(hierarchy);
            img.setImageURI(imageUri);
        }

        @Override
        public int getItemCount() {
            return mArrayList.size();
        }

        // Provide a direct reference to each of the views within a data item
        // Used to cache the views within the item layout for fast access
        public static class ViewHolder extends RecyclerView.ViewHolder {
            // Your holder should contain a member variable
            // for any view that will be set as you render a row
            public TextView name,population;
            SimpleDraweeView cat_img;

            // We also create a constructor that accepts the entire item row
            // and does the view lookups to find each subview
            public ViewHolder(View itemView) {
                // Stores the itemView in a public final member variable that can be used
                // to access the context from any ViewHolder instance.
                super(itemView);


                name = (TextView) itemView.findViewById(R.id.tv_name);
                population = (TextView) itemView.findViewById(R.id.tv_population);
                cat_img = (SimpleDraweeView) itemView.findViewById(R.id.iv_country);

            }
        }
    }

    public void EnableRuntimePermission(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(
                MainActivity.this,
                Manifest.permission.READ_CONTACTS))
        {

            Toast.makeText(MainActivity.this,"CONTACTS permission allows us to Access CONTACTS app", Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(MainActivity.this,new String[]{
                    Manifest.permission.READ_CONTACTS}, RequestPermissionCode);

        }
    }

    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] PResult) {

        switch (RC) {

            case RequestPermissionCode:

                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(MainActivity.this,"Permission Granted, Now your application can access CONTACTS.", Toast.LENGTH_LONG).show();

                } else {

                    Toast.makeText(MainActivity.this,"Permission Canceled, Now your application cannot access CONTACTS.", Toast.LENGTH_LONG).show();

                }
                break;
        }
    }


}
