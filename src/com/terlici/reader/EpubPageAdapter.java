package com.terlici.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import android.content.Context;
import android.content.res.AssetManager;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

public class EpubPageAdapter extends PagerAdapter {
	
	Book mBook;
	int mTotal = 0;
	int[] mChapters;
	JavascriptInterface mJS;
	WebView mWebView;
	Context mContext;
	String computejs;
	String paginatejs;
	
	View.OnTouchListener mPrevent = new View.OnTouchListener() {

	    public boolean onTouch(View v, MotionEvent event) {
	      return (event.getAction() == MotionEvent.ACTION_MOVE);
	    }
	};
	
	public EpubPageAdapter(Context context, Book book) {
		mContext = context;
		mBook = book;
		mJS = new JavascriptInterface();
		mWebView = new WebView(context);
		
		
		load();
	}
	
	private String read(InputStream input) throws IOException {
    	BufferedReader r = new BufferedReader(new InputStreamReader(input));
    	StringBuilder total = new StringBuilder();
    	String line;
    	
		while ((line = r.readLine()) != null) {
			total.append(line);
		}		
    	
    	return total.toString();
    }
	
	private void prepareWebViewForCompute() {
		Log.i("Reader", "Preparing web view.");
		
		// Big - 42
    	// Normal - 36
    	// Small - 30
		mWebView.getSettings().setMinimumFontSize(36);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.setVerticalScrollBarEnabled(false);
		mWebView.setHorizontalScrollBarEnabled(false);
		mWebView.setInitialScale(100);
		mWebView.setOnTouchListener(mPrevent);
		mWebView.addJavascriptInterface(mJS, "manager");
		mWebView.setWebViewClient(new PrepareWebView(mBook, computejs));
		
	}
	
	private void prepareWebViewForDisplay(WebView webview, int page) {
		Log.i("Reader", "Preparing page.");
		
		mJS.setPage(page);
		
		// Big - 42
    	// Normal - 36
    	// Small - 30
		webview.getSettings().setMinimumFontSize(36);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.setVerticalScrollBarEnabled(false);
		webview.setHorizontalScrollBarEnabled(false);
		webview.setOnTouchListener(mPrevent);
		webview.addJavascriptInterface(mJS, "manager");
		webview.setWebViewClient(new PrepareWebView(mBook, paginatejs));
		webview.setInitialScale(100);
	}
	
	private void loadjs() {
    	Log.i("Reader", "Loading javascript");
    	
    	AssetManager assetManager = mContext.getAssets();
    	
    	try {
			computejs = read(assetManager.open("js/compute.js"))
						.replace('\r', ' ')
						.replace('\n', ' ')
						;
			
			paginatejs = read(assetManager.open("js/paginate.js"))
					.replace('\r', ' ')
					.replace('\n', ' ')
					;
		} catch (IOException e) {}
    }
	
	private void load() {
		int size = mBook.getSpine().size();
		mChapters = new int[size];
		
		Log.i("Reader", "Number of chapters: " + size);
		
		loadjs();
		prepareWebViewForCompute();
		
		for (int i = 0; i < size; ++i) {
			Resource r = mBook.getSpine().getResource(i);
	    	String text = new String(r.getData());

	    	// The "http://www.terlici.com/" is necessary for shouldInterceptRequest
	    	// to be called.
	    	mWebView.loadDataWithBaseURL("http://www.terlici.com/", text, "text/html", "utf-8", null);
	    	mChapters[i] = mJS.getLast();
		}
		
		mTotal = mJS.getTotal();
		
		Log.i("Reader","Number of pages: " + mTotal);
	}
	
	private int page2chapter(int position) {
		int total = 0;
		
		for (int i = 0; i < mChapters.length; ++i) {
			total += mChapters[i];
			
			if (position <= total) {
				return i;
			}
		}
		
		return -1;
	}

	@Override
	public int getCount() {
		return mTotal;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}
	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View)object);
	}
	
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		WebView page = new WebView(mContext);
		prepareWebViewForDisplay(page, position);
		
		int chapter = page2chapter(position);
		Resource r = mBook.getSpine().getResource(chapter);
    	String text = new String(r.getData());
    	page.loadDataWithBaseURL("http://www.terlici.com/", text, "text/html", "utf-8", null);
		
    	container.addView(page);
    	
		return page;
	}
}
