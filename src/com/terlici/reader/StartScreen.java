package com.terlici.reader;

import java.io.IOException;
import java.io.InputStream;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubReader;
import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.terlici.reader.ComputeBook.ComputeListener;

public class StartScreen extends Activity {
    /** Called when the activity is first created. */
	ViewPager mViewPager;
	Book book;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mViewPager = new ViewPager(this);
        setContentView(mViewPager);

        AssetManager assetManager = getAssets();
        
		InputStream epubInputStream;
		try {
			epubInputStream = assetManager.open("books/book.epub");
			book = (new EpubReader()).readEpub(epubInputStream);
		} catch (IOException e) {}
		
		ComputeBook processor = new ComputeBook(this, book);
		processor.compute(new ComputeListener() {
			
			@Override
			public void finished(int[] chapters, int pages) {
				Log.i("Reader", "Total: " + pages);
				
			}
		});
		
		/*
		Log.i("Reader", "Loading book");
		mViewPager.setAdapter(new EpubPageAdapter(this, book));
		
		Log.i("Reader", "Loading chapter");
		mViewPager.setCurrentItem(0);
		Log.i("Reader", "Chapter loaded");
		*/
    }
    
}