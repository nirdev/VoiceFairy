package com.example.android.lovefairyv4;

import android.os.Environment;

import java.io.File;

/**
 * Created by Nir on 3/17/2016.
 */
public class Constants {

    static File mFilePath = Environment.getExternalStorageDirectory();
    final static String FILE_NAME = "NIRCORDER.wav";
    final static String FILE_PATH = mFilePath.toString() + "/" + FILE_NAME;

}
