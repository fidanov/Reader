package com.terlici.reader;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;

public class WebViewBuilder {
	private WebView mWebView;
	private Context mContext;
	private int mChapter, mPage;
	private String mScript;
	private JavascriptInterface mJS;
	private Book mBook;
	
	private View.OnTouchListener mPrevent = new View.OnTouchListener() {

	    public boolean onTouch(View v, MotionEvent event) {
	      return (event.getAction() == MotionEvent.ACTION_MOVE);
	    }
	};
	
	public WebViewBuilder(Context context) {
		mContext = context;
	}
	
	public WebViewBuilder setBook(Book book) {
		mBook = book;
		return this;
	}
	
	public WebViewBuilder setChapter(int chapter) {
		mChapter = chapter;
		return this;
	}
	
	public WebViewBuilder setPage(int page) {
		mPage = page;
		return this;
	}
	
	public WebViewBuilder setScript(String script) {
		mScript = script;
		return this;
	}
	
	public WebViewBuilder setJavascriptInterface(JavascriptInterface js) {
		mJS = js;
		return this;
	}
	
	public WebViewBuilder setWebView(WebView webview) {
		mWebView = webview;
		return this;
	}
	
	public WebView getWebView() {
		build();
		return mWebView;
	}
	
	public void build() {
		if (mWebView == null) {
			mWebView = new WebView(mContext);
		}
		
		if (mJS != null) {
			mJS.setPage(mPage);
		}
		
		// Big - 42
    	// Normal - 36
    	// Small - 30
		mWebView.getSettings().setMinimumFontSize(36);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.setVerticalScrollBarEnabled(false);
		mWebView.setHorizontalScrollBarEnabled(false);
		mWebView.setInitialScale(100);
		//mWebView.setOnTouchListener(mPrevent);
		mWebView.addJavascriptInterface(mJS, "manager");
		mWebView.setWebViewClient(new PrepareWebView(mBook, mScript));
		
		Resource r = mBook.getSpine().getResource(mChapter);
    	String content = new String(r.getData());
    	
    	content = content.replace('\n', ' ');
    	content = content.replace('\r', ' ');
    	content = content.replace("blockquote", "p");
    	//content = content.replace("div", "span");
    	
    	mWebView.loadDataWithBaseURL("http://www.terlici.com/", content, "text/html", "utf-8", null);
	}
}
