package com.projectyellowbean.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class StocksProvider extends ContentProvider {
	private StocksDatabase mDatabase;
	public static final String AUTHORITY = "com.projectyellowbean.database";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + StocksDatabase.TABLE_STOCK);
	
	private static final int ALL_ROWS = 1;
	private static final int SINGLE_ROW = 2;
	
	private static final UriMatcher matcher;
	
	static {
		matcher	= new UriMatcher(UriMatcher.NO_MATCH);
		matcher.addURI(AUTHORITY, StocksDatabase.TABLE_STOCK, ALL_ROWS);
		matcher.addURI(AUTHORITY, StocksDatabase.TABLE_STOCK + "/#", SINGLE_ROW);
	}
	
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		
		SQLiteDatabase db = mDatabase.getWritableDatabase();
		
		switch(matcher.match(uri)) {
		case SINGLE_ROW : {
			String rowID = uri.getPathSegments().get(1);
			selection = StocksDatabase._ID + " = " + rowID + ((!TextUtils.isEmpty(selection)) ? " and (" + selection + ")" : "");
			break;
		}
		}
		
		int deleteCount = db.delete(StocksDatabase.TABLE_STOCK, selection, selectionArgs);
		
		getContext().getContentResolver().notifyChange(uri, null);
		
		return deleteCount;
	}

	@Override
	public String getType(Uri uri) {
		switch(matcher.match(uri)) {
		case ALL_ROWS : 
			return "vnd.android.cursor.dir/vnd.projectyellowbean.database";
		case SINGLE_ROW :
			return "vnd.android.cursor.item/vnd.projectyellowbean.database";
		default :
			throw new IllegalArgumentException("Unsupported URI : " + uri.toString());
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = mDatabase.getWritableDatabase();
		
		long id = db.insert(StocksDatabase.TABLE_STOCK, null, values);
		
		if(id > -1) {
			Uri insertedUri = ContentUris.withAppendedId(uri, id);
			getContext().getContentResolver().notifyChange(insertedUri, null);
		}
		else {
			return null;
		}
		return uri;
	}

	@Override
	public boolean onCreate() {
		mDatabase = new StocksDatabase(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		
		SQLiteDatabase db;
		db = mDatabase.getReadableDatabase();
		
		String groupBy = null;
		String having = null;
		
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		
		switch(matcher.match(uri)) {
		case SINGLE_ROW : {
			String rowID = uri.getPathSegments().get(1);
			builder.appendWhere(StocksDatabase._ID + " = " + rowID);
			break;
		}
		}
		
		builder.setTables(StocksDatabase.TABLE_STOCK);
		Cursor c = builder.query(db, projection, selection, selectionArgs, groupBy, having, sortOrder);
		c.moveToFirst();
		
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		SQLiteDatabase db = mDatabase.getWritableDatabase();
		
		switch(matcher.match(uri)) {
		case SINGLE_ROW : {
			String rowID = uri.getPathSegments().get(1);
			selection = StocksDatabase._ID + " = " + rowID + ((!TextUtils.isEmpty(selection)) ? " and (" + selection + ")" : "");
			break;
		}
		}
		
		int updateCount = db.update(StocksDatabase.TABLE_STOCK, values, selection, selectionArgs);
		
		getContext().getContentResolver().notifyChange(uri, null);
		
		return updateCount;
	}
}
