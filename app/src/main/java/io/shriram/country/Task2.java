package io.shriram.country;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;


import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.shriram.country.Model.Contacts;
import io.shriram.country.ReadWrite.ReadWrite;

public class Task2 extends AppCompatActivity {

    private ArrayList<Contacts> Storecontacts = new ArrayList<>();
    private ReadWrite readWrite = new ReadWrite();
    public  static final int RequestPermissionCode  = 1 ;
    public  static final int RequestPermissionCodeStorage  = 2 ;
    public  static final int RequestPermissionCodeRead  = 3 ;
    public  static final String CSV_FILE_NAME="/sdcard/contacts.csv";
    public  static final String ZIP_FILE_NAME="/sdcard/contacts.zip";

    private Cursor cursor ;
    private String CONTACTS="";
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

    // on Sync Button Click -- function to sync data
    public void sync(View v){
        bt_sync.setVisibility(View.INVISIBLE);
        pb2.setVisibility(View.VISIBLE);

        pb2.setVisibility(View.INVISIBLE);
        Startsync();

    }


    //getting Runtime permission
    public void Startsync(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(
                Task2.this,
                Manifest.permission.READ_CONTACTS))
        {
            //Getting Contacts to arraylist and convert them to csv through runtime permmision
           GetContactsIntoArrayList();
            readfile();
        } else {

            ActivityCompat.requestPermissions(Task2.this,new String[]{
                    Manifest.permission.READ_CONTACTS}, RequestPermissionCode);

        }
    }

    //Get All the contacts and show save them in the CSV format;
    public void GetContactsIntoArrayList(){
        cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null, null, null);
        Observable.just(cursor)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribe(new Observer<Cursor>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Cursor value) {

                        //prepare string to save inn csv file
                        CONTACTS="Name,Number\n";
                        Log.d("cursor", ""+value);
                        while (cursor.moveToNext()) {

                            name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                            phonenumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            Storecontacts.add(new Contacts(name,phonenumber));
                            CONTACTS+=name+","+phonenumber+"\n";
                        }

                        //write to storage writing to csv file and then to its zipped file
                        startwriting(CONTACTS);

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
                        ImageView imageView = (ImageView) Task2.this.findViewById(R.id.iv_file);
                        imageView.setImageResource(R.drawable.zipicon);
                    }
                });

    }

    //getting Runtime permission to write to external
    public void startwriting(String s){

        if (ActivityCompat.shouldShowRequestPermissionRationale(
                Task2.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE))
        {

           // Toast.makeText(Task2.this,"Allowed to write Internal Storage", Toast.LENGTH_LONG).show();
           WritetoZippedFile();
        } else {

            ActivityCompat.requestPermissions(Task2.this,new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, RequestPermissionCodeStorage);

        }
    }

    //write csv to zipped file
    public void WritetoZippedFile(){
        //creating CSV File
        readWrite.writeItems(CONTACTS,new File(CSV_FILE_NAME));

        //writing CSV to Zipped File
        File[] f =new File[1];
        f[0] = new File(CSV_FILE_NAME);
        readWrite.zip(f,ZIP_FILE_NAME);
    }

    //for getting Runtime read permission
    public void readfile(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                Task2.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)){


        } else {

            ActivityCompat.requestPermissions(Task2.this,new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE}, RequestPermissionCodeRead);

        }
    }

    //Runtime permission
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

    // show Snackbar
    public void showSnackbar(){
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, "Contacts sync completed", Snackbar.LENGTH_LONG)
                .setAction("Open", new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //opening the zip file
                Intent i = new Intent();
                i.setAction(android.content.Intent.ACTION_VIEW);
                i.setDataAndType(FileProvider.getUriForFile(Task2.this, BuildConfig.APPLICATION_ID,new File(ZIP_FILE_NAME)), "application/zip");
                i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                PackageManager pm = Task2.this.getPackageManager();
                if (i.resolveActivity(pm) != null) {
                    startActivity(i);
                }
            }
        });
        snackbar.setActionTextColor(Color.GREEN);
        snackbar.show();
    }
}
