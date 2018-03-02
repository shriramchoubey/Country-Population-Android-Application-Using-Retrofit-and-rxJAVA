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

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import io.shriram.country.Checkers.InternetChecker;
import io.shriram.country.Model.Country;
import io.shriram.country.common.ResponseClass;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity{
    private ArrayList<Country> arraylist= new ArrayList<>();
    private ProgressBar pb;
    private RecyclerView recyclerview;
    public static final String BASE_URL = "http://www.androidbegin.com/tutorial/";

    private CompositeDisposable mCompositeDisposable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);

        setContentView(R.layout.activity_main);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("population");

        //creating recycler view
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

        //initialising progressbar
        pb = (ProgressBar) findViewById(R.id.pb);
        pb.setVisibility(View.VISIBLE);

        mCompositeDisposable = new CompositeDisposable();

        //loading Data from data source
        if(InternetChecker.isNetworkAvailable(MainActivity.this)){
            loadJSON();
        }else{
            Toast.makeText(this, "Please check Internet Connection!", Toast.LENGTH_SHORT).show();
        }

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


    // Creating custom RecyclerView Adapter class
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

    //loading Task 2 on Button Click
    public void next(View v){
        Intent intent = new Intent(MainActivity.this,Task2.class);
        startActivity(intent);
        this.finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.clear();
    }

}
