package io.shriram.country.ReadWrite;


import android.content.Context;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import org.apache.commons.io.FileUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static io.reactivex.BackpressureStrategy.BUFFER;

/**
 * Created by shriram on 07-09-2017.
 * use function getFiledir()
 * to third parameter in constructor
 *
 */

public class ReadWrite extends AppCompatActivity {
    private String filenameInternal, filenameExternal;

    public void writeItems(String s,File file) {

        File filesDir = file;
        try {
            FileWriter writer = new FileWriter(filesDir);
            writer.write(s);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File readItems(String filename_with_xtention,File dir)  {

        File filesDir = dir;
        File todoFile = new File(filesDir,filename_with_xtention);
        return todoFile;
       /* try {
            return FileUtils.readFileToString(todoFile);
        } catch (IOException e) {
            return "";
        }*/
    }

    //used to zip file
    public void zip(File[] _files, String zipFileName) {
        int BUFFER = 100000; // means you can create Buffer of 10mb at a time
        try {
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(zipFileName);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
                    dest));
            byte data[] = new byte[BUFFER];

            for (int i = 0; i < _files.length; i++) {
                Log.v("Compress", "Adding: " + _files[i]);
                FileInputStream fi = new FileInputStream(_files[i]);
                origin = new BufferedInputStream(fi, BUFFER);

                ZipEntry entry = new ZipEntry(String.valueOf(_files[i]));
                out.putNextEntry(entry);
                int count;

                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }

            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeFileInternalStorage( String s, String filenameExternal) {
        createUpdateFile(filenameInternal, s, false);
    }

    public void appendFileInternalStorage(String s, String filenameExternal) {
        createUpdateFile(filenameInternal, s, true);
    }

    private void createUpdateFile(String fileName, String content, boolean update) {
        FileOutputStream outputStream;

        try {
            if (update) {
                outputStream = openFileOutput(fileName, Context.MODE_APPEND);
            } else {
                outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
            }
            outputStream.write(content.getBytes());
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String readFileInternalStorage(String view) {
        String s=null;
        try {
            FileInputStream fileInputStream = openFileInput(filenameInternal);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));

            StringBuffer sb = new StringBuffer();
            String line = reader.readLine();

            while (line != null) {
                sb.append(line);
                line = reader.readLine();
            }

            s = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return s;
    }

    public void createTemporaryFile(View view) {
        try {
            String fileName = "couponstemp";
            String coupons = "Get upto 50% off shoes @ xyx shop \n Get upto 80% off on shirts @ yuu shop";

            File file = File.createTempFile(fileName, null, getCacheDir());

            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(coupons.getBytes());
            outputStream.flush();
            outputStream.close();

        } catch (IOException e) {
        }
    }

    public void deleteFile(View view) {
        try {
            String fileName = "couponstemp";
            File file = File.createTempFile(fileName, null, getCacheDir());

            file.delete();
        } catch (IOException e) {
        }
    }

}
