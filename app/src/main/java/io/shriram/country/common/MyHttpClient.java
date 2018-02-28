package io.shriram.country.common;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import io.shriram.country.*;

/**
 * Created by Sunny on 03-09-2017.
 */

public class MyHttpClient extends AsyncTask<Void,Void,String> {
    public io.shriram.country.MyCallback callback;
    Context context;
    public String result;
    String link;
    String[] data;

    public MyHttpClient(Context context,String link,String[] data)
    {
        this.context=context;
        this.link=link;
        this.data= data;
    }
    @Override
    protected String doInBackground(Void... params) {


        try{

            URL url =new URL(link);
            HttpURLConnection httpurlconnection= (HttpURLConnection) url.openConnection();
            httpurlconnection.setRequestMethod("POST");
            httpurlconnection.setDoOutput(true);
            httpurlconnection.setDoInput(true);

            OutputStream outputstream = httpurlconnection.getOutputStream();
            BufferedWriter bufferedwriter = new BufferedWriter(new OutputStreamWriter(outputstream, "UTF-8"));
            String post_data ="";
           if(data!=null){

               for(int i=0;i<data.length;i+=2){

                   if(i==0){
                       post_data = URLEncoder.encode(data[0], "UTF-8") + "=" + URLEncoder.encode(data[1], "UTF-8");
                       //+ "&"
                       //+ URLEncoder.encode("otp", "UTF-8") + "=" + URLEncoder.encode(str.toString(), "UTF-8");
                   }else
                post_data += "&" + URLEncoder.encode(data[i], "UTF-8") + "=" + URLEncoder.encode(data[i+1], "UTF-8");
            }}
            //= URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(getIntent().getStringExtra("Username"), "UTF-8") + "&"
                    //+ URLEncoder.encode("otp", "UTF-8") + "=" + URLEncoder.encode(str.toString(), "UTF-8");

            bufferedwriter.write(post_data);
            bufferedwriter.flush();
            bufferedwriter.close();

            InputStream inputstream= httpurlconnection.getInputStream();
            BufferedReader bufferedreader= new BufferedReader(new InputStreamReader(inputstream,"iso-8859-1"));
            String result="";
            String line="";
            while((line = bufferedreader.readLine())!=null){
                result+=line;
            }
            bufferedreader.close();
            inputstream.close();
            httpurlconnection.disconnect();

            return result;

        }catch(MalformedURLException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }


    @Override
    protected void onPreExecute() {


    }

    @Override
    protected void onPostExecute(String result_http ) {
        result=result_http;

        onEvent();
    }

    void onEvent() {
        callback.callbackCall();
    }


    // Toast.makeText(Splash.this, ""+username1, Toast.LENGTH_SHORT).show();
}