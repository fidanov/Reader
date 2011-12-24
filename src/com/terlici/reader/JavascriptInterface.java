package com.terlici.reader;



public class JavascriptInterface {
	
	public interface FinishListener {
		public void finished(JavascriptInterface jsi);
	}
	
	int mTotal = 0;
	int mLast = 0;
	int mPage = 1;
	int mLoadedChapters = 0;
	FinishListener mListener;
	
	public void addPages(int number) {
		mTotal += number;
		mLast = number;
		
		mLoadedChapters += 1;
	}
	
	public int getTotal() {
		return mTotal;
	}
	
	public int getLast() {
		return mLast;
	}
	
	public int getPage() {
		return mPage;
	}
	
	public void setPage(int page) {
		mPage = page;
	}
	
	public int getLoadedChapters() {
		return mLoadedChapters;
	}
	
	public void setFinishListener(FinishListener listener) {
		mListener = listener;
	}
	
	public void finish() {
		if (mListener != null) {
			mListener.finished(this);
		}
	}
}
