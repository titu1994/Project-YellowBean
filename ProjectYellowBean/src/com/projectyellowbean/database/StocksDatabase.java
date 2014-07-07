package com.projectyellowbean.database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.projectyellowbean.R;

public class StocksDatabase extends SQLiteOpenHelper{
	
	private static final String TAG = StocksDatabase.class.getSimpleName();
	
	private Context context;
	private static final String dbName = "stocks.sq";
	private static final int dbVersion = 1;
	
	public static final String TABLE_STOCK = "stockTable";
	
	public static final String _ID = "_id";
	public static final String COL_STOCK_NAME = "stock_name";
	public static final String COL_STOCK_SYMBOL = "stock_symbol";
	public static final String COL_STOCK_VALUE = "stock_value";
	public static final String COL_STOCK_AMOUNT = "stock_amount";
	
	private static final String CREATE_TABLE_STOCK = "create table " + TABLE_STOCK + "(" 
			+ _ID + " integer primary key autoincrement, " 
			+ COL_STOCK_NAME + " text not null, "
			+ COL_STOCK_SYMBOL + " text not null, "
			+ COL_STOCK_VALUE + " real not null, " 
			+ COL_STOCK_AMOUNT + " real default 0);";
			
	
	public StocksDatabase(Context context) {
		super(context, dbName, null, dbVersion);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_STOCK);
		Log.d(TAG, "Table created");
		threadedLoadDatabase(db);
	}
	
	private void threadedLoadDatabase(final SQLiteDatabase db) {
		Thread loadThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					loadDatabase(db);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		loadThread.start();
	}

	private void loadDatabase(SQLiteDatabase db) throws IOException {
		db.beginTransaction();
		
		Resources resources = context.getResources();
		
		InputStream is = resources.openRawResource(R.raw.companylist);
		BufferedReader bb = new BufferedReader(new InputStreamReader(is));
		
		String line;
		String data[];
		
		while((line = bb.readLine()) != null) {
			data = line.split(",");
			saveData(db, data);
		}
		
		db.setTransactionSuccessful();
		Log.d(TAG, "Database Loaded");
		db.endTransaction();
	}

	private void saveData(SQLiteDatabase db, String[] data) {
		ContentValues values = new ContentValues();
		values.put(COL_STOCK_NAME, data[0]);
		values.put(COL_STOCK_SYMBOL, data[1]);
		values.put(COL_STOCK_VALUE, data[2]);
		
		db.insert(TABLE_STOCK, null, values);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d(TAG, "Table is being distroyed");
		db.execSQL("drop table if exists " + TABLE_STOCK);
		onCreate(db);
	}

}
