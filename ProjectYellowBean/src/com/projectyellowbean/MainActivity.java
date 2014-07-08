package com.projectyellowbean;

import android.app.Activity;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
//WTF. DO NOT USE ANY SUPPORT LIBRARIES. Whenever it asks ALWAYS SUPPLY THE CORRECT ONE.
//Eg. This is correct.
import android.widget.SimpleCursorAdapter;

// THIS IS WRONG. IT WILL CRASH 
//import android.support.v4.widget.SimpleCursorAdapter;



import com.projectyellowbean.database.StocksDatabase;
import com.projectyellowbean.database.StocksProvider;

//								DONT USE LISTACTIVITY. IT SUCKS!
public class MainActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor>{

	// Use constants like this to tidy up code. Dont just pass 0 everywhere.
	private static final int DATA_LOAD = 0;
	
	//Cache the listview and the adapter
	private ListView mListView;
	private SimpleCursorAdapter mAdapter; 				

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_main);

		mListView = (ListView) findViewById(R.id.stock_list);
		
		// Made lots of changes here. See what I did.
		String[] uiBindFrom = new String[] {StocksDatabase.COL_STOCK_NAME, StocksDatabase.COL_STOCK_SYMBOL, StocksDatabase.COL_STOCK_VALUE, StocksDatabase.COL_STOCK_AMOUNT};		
		int[] uiBindTo = new int[] {R.id.text_stock_name, R.id.text_stock_symbol, R.id.text_stock_value, R.id.text_stock_amount};
		
		mAdapter = new SimpleCursorAdapter(this, R.layout.stock_list_items, null, uiBindFrom, uiBindTo, 0);  
		mListView.setAdapter(mAdapter);
		
		// You forgot to call startLoading(). It won't load otherwise
		getLoaderManager().initLoader(DATA_LOAD, null, this).startLoading();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// Why did you pass null when the cursor loader was correct ?
		CursorLoader cursL = new CursorLoader(this, StocksProvider.CONTENT_URI, null, null, null, null);
		return cursL;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
		if(arg1 == null){
			/**
			 * Don't make outer classes like that. Android complains about class not found.
			 * Make inner Anonymous classes like i've done.
			 * 
			 * We dont need to be notified about the children. Worry about the parent database only.
			 * Start up a progress dialog to show that we are doing a lot of work on the database. 
			 * This loading will happen only once.
			 */
			final ProgressDialog pd = ProgressDialog.show(this, "Please wait", "Loading database", true, false);
			getContentResolver().registerContentObserver(StocksProvider.CONTENT_URI, false, new ContentObserver(new Handler()) {

				@Override
				public void onChange(boolean selfChange) {
					super.onChange(selfChange);
					
					if(selfChange) {
						
						//No need for progress dialog anymore
						pd.dismiss();
						// Restart loader like this.
						getLoaderManager().restartLoader(DATA_LOAD, null, MainActivity.this).startLoading();
						//We dont need to observe the database anymore now.
						getContentResolver().unregisterContentObserver(this);
					}
				}
				
			});
		}
		else {
			mAdapter.swapCursor(arg1);
			mAdapter.notifyDataSetChanged();
		}

	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		mAdapter.swapCursor(null);
		mAdapter.notifyDataSetChanged();
		
	}

}