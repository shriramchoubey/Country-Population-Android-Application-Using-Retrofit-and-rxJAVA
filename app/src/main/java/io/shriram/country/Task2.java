package io.shriram.country;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.annotation.RequiresPermission;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;


import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.shriram.country.Model.Contacts;
import io.shriram.country.ReadWrite.ReadWrite;

public class Task2 extends AppCompatActivity {

    private ArrayList<Contacts> Storecontacts = new ArrayList<>();
    public  static final int RequestPermissionCode  = 1 ;
    public  static final int RequestPermissionCodeStorage  = 2 ;
    public  static final int RequestPermissionCodeRead  = 3 ;

    private Cursor cursor ;
    private String name, phonenumber ;
    private Button bt_sync;
    private CoordinatorLayout coordinatorLayout;
    private ProgressBar pb2;
    ImageView imageView;

    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(myIntent, 0);
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task2);

        //initialising sync button
        bt_sync = (Button) findViewById(R.id.bt);
        imageView = (ImageView) findViewById(R.id.iv_file);


        //backButton over actionbar
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Sync");

        pb2 = (ProgressBar) findViewById(R.id.pb2);
        pb2.setVisibility(View.INVISIBLE);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                .cordinator);
    }

    //function to sync data
    public void sync(View v){
        bt_sync.setVisibility(View.INVISIBLE);
        pb2.setVisibility(View.VISIBLE);

        pb2.setVisibility(View.INVISIBLE);
        Startsync();

    }




    //Get All the contacts and show save them in the CSV format;
    public void GetContactsIntoArrayList(){
        // EnableRuntimePermission();
        cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null, null, null);
        Observable.just(cursor)
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())

                .subscribe(new Observer<Cursor>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Cursor value) {

                        //prepare string to save inn csv file
                        String s="Name,Number\n";
                        Log.d("cursor", ""+value);
                        while (cursor.moveToNext()) {

                            name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                            phonenumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            Storecontacts.add(new Contacts(name,phonenumber));
                            s+=name+","+phonenumber+"\n";
                        }

                        //writing the csv file
                        ReadWrite.writeItems(s,"contacts.csv",Environment.getRootDirectory());

                        //write to storage zipped files
                        startwriting();

                        Log.d("Contacts : \n",s);

                        //close the cursor
                        cursor.close();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("Contacts : \n", e+"");

                    }

                    @Override
                    public void onComplete() {

                        Log.d("Observable CONTTACTS : "," Completed!");
                        //imageView.setImageResource(R.drawable.imagefile);
                        showSnackbar();
                    }
                });

    }

    //getting Runtime permission to write to external
    public void startwriting(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(
                Task2.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE))
        {

           // Toast.makeText(Task2.this,"Allowed to write Internal Storage", Toast.LENGTH_LONG).show();
            GetContactsIntoArrayList();
        } else {

            ActivityCompat.requestPermissions(Task2.this,new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, RequestPermissionCodeStorage);

        }
    }


    //write csv to zipped file
    public void WritetoZippedFile(){

        //writing file to zip
        File[] file = new File[1];
        file[0]=ReadWrite.readItems("contacts.csv",Environment.getRootDirectory());
        ReadWrite.zip(file,Environment.getRootDirectory()+"/hello/contacts.zip");
    }

//getting Runtime permission
    public void Startsync(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(
                Task2.this,
                Manifest.permission.READ_CONTACTS))
        {

           // Toast.makeText(Task2.this,"CONTACTS permission allows us to Access CONTACTS app", Toast.LENGTH_LONG).show();
            GetContactsIntoArrayList();
        } else {

            ActivityCompat.requestPermissions(Task2.this,new String[]{
                    Manifest.permission.READ_CONTACTS}, RequestPermissionCode);

        }
    }

    public void readfile(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                Task2.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)){


        } else {

            ActivityCompat.requestPermissions(Task2.this,new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE}, RequestPermissionCodeRead);

        }
    }

    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] PResult) {

        switch (RC) {

            case RequestPermissionCode:

                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {
                    GetContactsIntoArrayList();
                  //  Toast.makeText(Task2.this,"Permission Granted, Now your application can access CONTACTS.", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(Task2.this,"Permission Canceled, Now your application cannot access CONTACTS.", Toast.LENGTH_LONG).show();
                }
                break;

            case RequestPermissionCodeStorage:
                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {
                    WritetoZippedFile();
                //    Toast.makeText(Task2.this,"Permission Granted, Now your application can write files.", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(Task2.this,"Permission Canceled, Now your application cannot write files", Toast.LENGTH_LONG).show();
                }
                break;

            case RequestPermissionCodeRead:
                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {
                    WritetoZippedFile();
                    //    Toast.makeText(Task2.this,"Permission Granted, Now your application can write files.", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(Task2.this,"Permission Canceled, Now your application cannot read files", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    public void showSnackbar(){
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, "Contacts sync completed", Snackbar.LENGTH_LONG)
                .setAction("Done!", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readfile();
                Intent i = new Intent();
                File file = ReadWrite.readItems("contacts.zip",Environment.getRootDirectory());
                try {
                    Toast.makeText(Task2.this, ""+ FileUtils.readFileToString(file), Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                i.setAction(android.content.Intent.ACTION_VIEW);

                    i.setDataAndType(Uri.fromFile(file), "application/zip");

                //startActivity(i);
            }
        });
        snackbar.setActionTextColor(Color.GREEN);
        snackbar.show();
    }


}
