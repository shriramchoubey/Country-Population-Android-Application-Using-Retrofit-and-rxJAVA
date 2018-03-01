package io.shriram.country.ReadWrite;


import android.util.Log;

import org.apache.commons.io.FileUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static io.reactivex.BackpressureStrategy.BUFFER;

/**
 * Created by shriram on 07-09-2017.
 * use function getFiledir()
 * to third parameter in constructor
 *
 */

public class ReadWrite {
    public static void writeItems(String s,String filename_with_xtention,File dir) {

        File filesDir = dir;
        File todoFile = new File(filesDir,filename_with_xtention);
        try {


            FileUtils.writeStringToFile(todoFile ,s);   // TODO: add depenencies for fill utils
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static File readItems(String filename_with_xtention,File dir)  {

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
    public static void zip(File[] _files, String zipFileName) {
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
}
