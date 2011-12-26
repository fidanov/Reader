package com.terlici.reader;

import nl.siegmann.epublib.domain.Book;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;

public class EpubPageAdapter extends PagerAdapter {
	
	Book mBook;
	int mTotal = 0;
	int[] mChapters;
	JavascriptInterface mJS;
	WebView mWebView;
	Context mContext;
	String mScript;
	
	View.OnTouchListener mPrevent = new View.OnTouchListener() {

	    public boolean onTouch(View v, MotionEvent event) {
	      return (event.getAction() == MotionEvent.ACTION_MOVE);
	    }
	};
	
	public EpubPageAdapter(Context context, Book book, int pages, int[] chapters) {
		mContext = context;
		mBook = book;
		mJS = new JavascriptInterface();
		mWebView = new WebView(context);
		mTotal = pages;
		mChapters = chapters;
		mScript = Utilities.loadjs(context, "js/paginate.js");
	}
	
	private int position2chapter(int position) {
		int total = 0;
		
		for (int i = 0; i < mChapters.length; ++i) {
			total += mChapters[i];
			
			if (position <= total) {
				return i;
			}
		}
		
		return -1;
	}
	
	private int position2page(int position) {
		
		for (int i = 0; i < mChapters.length; ++i) {
			if (position < mChapters[i]) {
				return position;
			}
			
			position -= mChapters[i];
		}
		
		return 0;
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
		int chapter = position2chapter(position);
		int page = position2page(position);
		
		Log.d("Reader", position + " " + chapter + " " + page);
		
		WebView webview = new WebViewBuilder(mContext)
		.setBook(mBook)
		.setChapter(chapter)
		.setPage(page)
		.setScript(mScript)
		.setJavascriptInterface(mJS)
		.getWebView();
		
		webview.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		
    	container.addView(webview);
		
		/*Resource r = mBook.getSpine().getResource(position);
    	String content = new String(r.getData());
    	
    	content = content.replace('\n', ' ');
    	content = content.replace('\r', ' ');
    	content = content.replace("blockquote", "p");
    	content = content.replace("div", "span");
    	content = content.substring(content.indexOf("<body>") + 6, content.indexOf("</body>"));
    	
    	new AlertDialog.Builder(mContext)
    	.setMessage(content)
    	.show();
    	
    	
    	TextView webview = new TextView(mContext);
    	webview.setTextColor(Color.WHITE);
    	webview.setTextSize(TypedValue.COMPLEX_UNIT_PX, 36);
    	webview.setText(Html.fromHtml(content));
    	container.addView(webview);*/
    	
		return webview;
	}
}
