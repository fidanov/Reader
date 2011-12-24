package com.terlici.reader;

import java.io.IOException;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import android.util.Log;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class PrepareWebView extends WebViewClient {
	
	Book mBook;
	String mPostjs;
	
	public PrepareWebView(Book book, String postjs) {
		mBook = book;
		mPostjs = postjs;
	}
	
	@Override
	public WebResourceResponse shouldInterceptRequest(WebView view,
			String url) {
		String prefix = "http://www.terlici.com/";
		
		if (url.startsWith(prefix)) {
			url = url.substring(prefix.length());
		}
		
		Resource r = mBook.getResources().getByHref(url);
		
		if (r != null) {
			WebResourceResponse response = null;
			
			try {
				response = new WebResourceResponse(r.getMediaType().toString(),
						r.getInputEncoding(), r.getInputStream());
			} catch (IOException e) {
				Log.i("epublib", "Problem loading resource: " + url);
			}
			
			return response;
		}
		
		return super.shouldInterceptRequest(view, url);
	}
	
	@Override
	public void onPageFinished(WebView view, String url) {
		super.onPageFinished(view, url);
        
        view.loadUrl("javascript:(function() { " + mPostjs + "})()");
	}
}
