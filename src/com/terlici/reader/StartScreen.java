package com.terlici.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.SpineReference;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.epub.EpubReader;
import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;

public class StartScreen extends Activity {
    /** Called when the activity is first created. */
	String paginatejs;
	Book book;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        loadjs();
        prepare();
        logBook();
    }
    
    private void logBook() {
    	AssetManager assetManager = getAssets();
    	try {
    		// find InputStream for book
    		InputStream epubInputStream = assetManager
    				.open("books/book.epub");

    		// Load Book from inputStream
    		book = (new EpubReader()).readEpub(epubInputStream);

    		// Log the book's authors
    		Log.i("epublib", "author(s): " + book.getMetadata().getAuthors());

    		// Log the book's title
    		Log.i("epublib", "title: " + book.getTitle());

    		// Log the book's coverimage property
    		Bitmap coverImage = BitmapFactory.decodeStream(book.getCoverImage()
    				.getInputStream());
    		Log.i("epublib", "Coverimage is " + coverImage.getWidth() + " by "
    				+ coverImage.getHeight() + " pixels");

    		// Log the tale of contents
    		logTableOfContents(book.getTableOfContents().getTocReferences(), 0);
    		
    		Log.i("epublib", "Spine size:" + book.getSpine().size());
    		
    		logSpineTypes(book.getSpine().getSpineReferences());
    		
    	} catch (IOException e) {
    		Log.e("epublib", e.getMessage());
    	}
    }
    
    public String read(InputStream input) throws IOException {
    	BufferedReader r = new BufferedReader(new InputStreamReader(input));
    	StringBuilder total = new StringBuilder();
    	String line;
    	
		while ((line = r.readLine()) != null) {
			total.append(line);
		}		
    	
    	return total.toString();
    }
    
    public void loadjs() {
    	Log.i("epublib", "Loading JS");
    	AssetManager assetManager = getAssets();
    	
    	
    	try {
			paginatejs = read(assetManager.open("js/paginate.js"))
						.replace('\r', ' ')
						.replace('\n', ' ')
						;
			Log.i("epublib", paginatejs);
		} catch (IOException e) {
		}
    }
    
    public void prepare() {
    	WebView wbox = (WebView)findViewById(R.id.webtext);

    	wbox.setWebViewClient(new WebViewClient() {
    		@Override
        	public WebResourceResponse shouldInterceptRequest(WebView view,
        			String url) {
        		Log.i("epublib", "Loading something: " + url);
        		
        		String prefix = "http://www.terlici.com/";
        		if (url.startsWith(prefix)) {
        			url = url.substring(prefix.length());
        		}
        		
        		Log.i("epublib", "Real url: " + url);
        		
        		Resource r = book.getResources().getByHref(url);
        		
        		if (r != null) {
        			WebResourceResponse response = null;
        			
        			try {
    					response = new WebResourceResponse(r.getMediaType().toString(),
    							r.getInputEncoding(), r.getInputStream());
    				} catch (IOException e) {
    					Log.i("epublib", "Problem loading resource: " + url);
    				}
        			
        			if (response != null) {
        				Log.i("epublib", "Loading: " + url);
        				return response;
        			}
        		}
        		
        		return super.shouldInterceptRequest(view, url);
        	}
    		
    		@Override
    		public void onPageFinished(WebView view, String url) {
    			Log.i("epublib", "Page Loaded");
    			super.onPageFinished(view, url);
    	        
    	        view.loadUrl("javascript:(function() { " + paginatejs + "})()");
    		}
    	});
    	wbox.setInitialScale(100);
    	// Big - 42
    	// Normal - 36
    	// Small - 30
    	wbox.getSettings().setMinimumFontSize(36);
    	wbox.getSettings().setJavaScriptEnabled(true);
    	wbox.setVerticalScrollBarEnabled(false);
    	wbox.setHorizontalScrollBarEnabled(false);
    	wbox.setOnTouchListener(new View.OnTouchListener() {

    	    public boolean onTouch(View v, MotionEvent event) {
    	      return (event.getAction() == MotionEvent.ACTION_MOVE);
    	    }
    	  });
    }
    
    public void load(View v) {
    	String value = ((EditText)findViewById(R.id.spineIndex)).getText().toString();
    	int index = Integer.parseInt(value);
    	
    	Resource r = book.getSpine().getResource(index);
    	String text = new String(r.getData());

    	WebView wbox = (WebView)findViewById(R.id.webtext);
    	// The "http://www.terlici.com/" is necessary for shouldInterceptRequest
    	// to be called.
    	wbox.loadDataWithBaseURL("http://www.terlici.com/", text, "text/html", "utf-8", null);
    }
    
    private void logSpineTypes(List<SpineReference> spineReferences) {
    	for (SpineReference sref : spineReferences) {
    		Resource r = sref.getResource();
    		
    		Log.i("epublib", r.getMediaType().toString());
    	}
    }
    
    private void logTableOfContents(List<TOCReference> tocReferences, int depth) {
    	if (tocReferences == null) {
    		return;
    	}
    	
    	for (TOCReference tocReference : tocReferences) {
    		StringBuilder tocString = new StringBuilder();
    		
    		for (int i = 0; i < depth; i++) {
    			tocString.append("\t");
    		}
    		
    		tocString.append(tocReference.getTitle());
    		Log.i("epublib", tocString.toString());

    		logTableOfContents(tocReference.getChildren(), depth + 1);
    	}
    }
}