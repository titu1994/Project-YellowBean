package com.projectyellowbean.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class StocksHelper {
	private Context context;
	
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
	
	public final Cursor queryAllStock() {
		String projection[] = new String[] {StocksDatabase._ID, StocksDatabase.COL_STOCK_NAME, StocksDatabase.COL_STOCK_SYMBOL, StocksDatabase.COL_STOCK_VALUE, StocksDatabase.COL_STOCK_AMOUNT };
		String sortOrder = StocksDatabase.COL_STOCK_NAME + " asc";
		Cursor allStocks = context.getContentResolver().query(StocksProvider.CONTENT_URI, projection, null, null, sortOrder);
		allStocks.moveToFirst();
		
		return allStocks;
	}
	
	public final Cursor querySelectedStock() {
		Cursor cursor = null;
		
		
		return cursor;
	}
	
	public final void finish() {
		this.context = null;
	}

}
