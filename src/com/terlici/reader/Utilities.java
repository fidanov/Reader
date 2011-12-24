package com.terlici.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

public class Utilities {
	
	public static String read(InputStream input) throws IOException {
    	BufferedReader r = new BufferedReader(new InputStreamReader(input));
    	StringBuilder total = new StringBuilder();
    	String line;
    	
		while ((line = r.readLine()) != null) {
			total.append(line);
		}		
    	
    	return total.toString();
    }
	
	public static String loadjs(Context context, String path) {
    	Log.i("Reader", "Loading javascript");
    	
    	AssetManager assetManager = context.getAssets();
    	
    	try {
			return read(assetManager.open(path))
						.replace('\r', ' ')
						.replace('\n', ' ')
						;
		} catch (IOException e) {}
    	
    	return "";
    }
}
