package com.projectyellowbean.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;

public class StocksHelper {
	private Context context;
	
	public StocksHelper(Context context) {
		this.context = context;
	}
	
	public final StocksHelper createHelper(Context context) {
		this.context = context;
		return this;
	}
	
	public final StocksHelper updateStock(String stockName, double stockValue, double stockAmount) {
		ContentValues values = new ContentValues();
		values.put(StocksDatabase.COL_STOCK_NAME, stockName);
		values.put(StocksDatabase.COL_STOCK_VALUE, stockValue);
		values.put(StocksDatabase.COL_STOCK_AMOUNT, stockValue);
		
		context.getContentResolver().insert(StocksProvider.CONTENT_URI, values);
		return this;
	}
	
	public final StocksHelper updateStock(String stockName[], double stockValue[], double stockAmount[]) {
		ContentValues values[] = new ContentValues[stockName.length];
		
		for(int i = 0; i < values.length; i++) {
			values[i] = new ContentValues();
			values[i].put(StocksDatabase.COL_STOCK_NAME, stockName[i]);
			values[i].put(StocksDatabase.COL_STOCK_VALUE, stockValue[i]);
			values[i].put(StocksDatabase.COL_STOCK_AMOUNT, stockValue[i]);
		}
		
		context.getContentResolver().bulkInsert(StocksProvider.CONTENT_URI, values);
		return this;
	}
	
	public final CursorLoader queryAllStock() {
		String projection[] = new String[] {StocksDatabase._ID, StocksDatabase.COL_STOCK_NAME, StocksDatabase.COL_STOCK_SYMBOL, StocksDatabase.COL_STOCK_VALUE, StocksDatabase.COL_STOCK_AMOUNT };
		String sortOrder = StocksDatabase.COL_STOCK_NAME + " asc";
		CursorLoader loader = new CursorLoader(context, StocksProvider.CONTENT_URI, projection, null, null, sortOrder);
		
		return loader;
	}
	
	public final CursorLoader querySelectedStockName(String query) {
		String projection[] = new String[] {StocksDatabase._ID, StocksDatabase.COL_STOCK_NAME, StocksDatabase.COL_STOCK_SYMBOL, StocksDatabase.COL_STOCK_VALUE, StocksDatabase.COL_STOCK_AMOUNT };
		String selection = StocksDatabase.COL_STOCK_NAME + " like ?";
		String selectionArgs[] = new String[] { "%" + query + "%" };
		String sortOrder = StocksDatabase.COL_STOCK_NAME + " asc";
		
		CursorLoader loader = new CursorLoader(context, StocksProvider.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
		return loader;
	}
	
	@SuppressLint("DefaultLocale")
	public final CursorLoader querySelectedStockTicker(String query) {
		String projection[] = new String[] {StocksDatabase._ID, StocksDatabase.COL_STOCK_NAME, StocksDatabase.COL_STOCK_SYMBOL, StocksDatabase.COL_STOCK_VALUE, StocksDatabase.COL_STOCK_AMOUNT };
		String selection = StocksDatabase.COL_STOCK_SYMBOL + " like ?";
		String selectionArgs[] = new String[] { query.toUpperCase() + "%" };
		String sortOrder = StocksDatabase.COL_STOCK_NAME + " asc";
		
		CursorLoader loader = new CursorLoader(context, StocksProvider.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
		
		return loader;
	}
	
	public final StocksHelper updateUserStock(String stockTicker, double stockValue, double userAmount) {
		ContentValues values = new ContentValues();
		values.put(StocksDatabase.COL_STOCK_SYMBOL, stockTicker);
		values.put(StocksDatabase.COL_STOCK_VALUE, stockValue);
		values.put(StocksDatabase.COL_USER_AMOUNT, userAmount);
		
		context.getContentResolver().insert(StocksProvider.CONTENT_URI, values);
		return this;
	}
	
	public final void finish() {
		this.context = null;
	}

}
