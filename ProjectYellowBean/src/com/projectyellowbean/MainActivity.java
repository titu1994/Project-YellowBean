package com.projectyellowbean;

import android.app.Activity;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.Loader;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
// THIS IS WRONG. IT WILL CRASH 
//import android.support.v4.widget.SimpleCursorAdapter;
import android.widget.SearchView;
//WTF. DO NOT USE ANY SUPPORT LIBRARIES. Whenever it asks ALWAYS SUPPLY THE CORRECT ONE.
//Eg. This is correct.
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.projectyellowbean.database.StocksDatabase;
import com.projectyellowbean.database.StocksHelper;
import com.projectyellowbean.database.StocksProvider;

public class MainActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor>{

	private static final int DATA_LOAD = 0;
	private static final int SEARCH_NAME = 1;

	private ListView mListView;
	private SimpleCursorAdapter mAdapter; 	
	private ProgressDialog listProgressDialog;
	
	private SearchView searchView;	private String searchQuery;
	private StocksHelper stockHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		stockHelper = new StocksHelper(this);
		mListView = (ListView) findViewById(R.id.stock_list);

		String[] uiBindFrom = new String[] {StocksDatabase.COL_STOCK_NAME, StocksDatabase.COL_STOCK_SYMBOL, StocksDatabase.COL_STOCK_VALUE, StocksDatabase.COL_STOCK_AMOUNT};		
		int[] uiBindTo = new int[] {R.id.text_stock_name, R.id.text_stock_symbol, R.id.text_stock_value, R.id.text_stock_amount};

		mAdapter = new SimpleCursorAdapter(this, R.layout.stock_list_items, null, uiBindFrom, uiBindTo, 0);  
		mListView.setAdapter(mAdapter);
		
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
			Toast.makeText(this, "Do Something Later", Toast.LENGTH_LONG).show();
			return true;
		}
		else if(id == R.id.action_search) {
			if(searchView == null) {
				searchView = (SearchView) item.getActionView();
				
				searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

					@Override
					public boolean onQueryTextSubmit(String query) {
						
						return false;
					}

					@Override
					public boolean onQueryTextChange(String query) {
						searchQuery = query.trim();
						
						if(!TextUtils.isEmpty(searchQuery))
							getLoaderManager().restartLoader(SEARCH_NAME, null, MainActivity.this).startLoading();
						return true;
					}
				});
			}
			return true;
		} 
		return super.onOptionsItemSelected(item);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		
		if(id == DATA_LOAD) {
			if(listProgressDialog == null || !listProgressDialog.isShowing())
				listProgressDialog = ProgressDialog.show(this, "Please wait", "Loading database", true, false);
			return stockHelper.queryAllStock();
		}
		else if(id == SEARCH_NAME) {
			return stockHelper.querySelectedStockName(searchQuery);
		}
		
		
		return null;
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
			
			if(!listProgressDialog.isShowing())
				listProgressDialog = ProgressDialog.show(this, "Please wait", "Loading database", true, false);
			getContentResolver().registerContentObserver(StocksProvider.CONTENT_URI, false, new ContentObserver(new Handler()) {

				@Override
				public void onChange(boolean selfChange) {
					super.onChange(selfChange);

					if(selfChange) {

						getLoaderManager().restartLoader(DATA_LOAD, null, MainActivity.this).startLoading();
						getContentResolver().unregisterContentObserver(this);
						listProgressDialog.dismiss();
					}
				}

			});
		}
		else {
			if(listProgressDialog.isShowing())
				listProgressDialog.dismiss();
			
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