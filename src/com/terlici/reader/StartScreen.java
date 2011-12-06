package com.terlici.reader;

import java.io.IOException;
import java.io.InputStream;
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
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;

public class StartScreen extends Activity {
    /** Called when the activity is first created. */
	
	Book book;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
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
    
    public void load(View v) {
    	String value = ((EditText)findViewById(R.id.spineIndex)).getText().toString();
    	int index = Integer.parseInt(value);
    	
    	Resource r = book.getSpine().getResource(index);
    	
    	String text = new String(r.getData());
    	
    	//TextView box = (TextView)findViewById(R.id.text);
    	//box.setText(text);
  
    	
    	WebView wbox = (WebView)findViewById(R.id.webtext);
    	wbox.loadDataWithBaseURL(null, text, "text/html", "utf-8", null);
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