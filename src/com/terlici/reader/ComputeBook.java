package com.terlici.reader;

import nl.siegmann.epublib.domain.Book;
import android.content.Context;
import android.webkit.WebView;

import com.terlici.reader.JavascriptInterface.FinishListener;

public class ComputeBook {
	
	public interface ComputeListener {
		public void finished(int[] chapters, int pages);
	}
	
	Book mBook;
	Context mContext;
	JavascriptInterface mJSI;
	WebView mWebView;
	int current = 0;
	int[] mChapters;
	String mScript;
	
	public ComputeBook(Context context, Book book, int w, int h) {
		mContext = context;
		mBook = book;
		mJSI = new JavascriptInterface();
		mWebView = new WebView(mContext);
		mWebView.layout(0, 0, w, h);
		mChapters = new int[mBook.getSpine().size()];
		mScript = Utilities.loadjs(context, "js/compute.js");
	}
	
	public void compute(final ComputeListener listener) {
		mJSI.setFinishListener(new FinishListener() {
			
			@Override
			public void finished(JavascriptInterface jsi) {
				mChapters[current] = jsi.getLast();
				current++;
				
				if (current < mChapters.length) {
					process(current);
				} else {
					if (listener != null) {
						listener.finished(mChapters, jsi.getTotal());
					}
				}
			}
		});
		
		process(0);
	}
	
	private void process(int chapter) {
		new WebViewBuilder(mContext)
		.setBook(mBook)
		.setChapter(chapter)
		.setScript(mScript)
		.setJavascriptInterface(mJSI)
		.setWebView(mWebView)
		.build();
	}
	
}
