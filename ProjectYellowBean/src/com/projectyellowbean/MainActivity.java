package com.projectyellowbean;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.Menu;
import android.view.MenuItem;

import com.projectyellowbean.database.StocksProvider;


public class MainActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor>{

	SimpleCursorAdapter mAdapter; 				
    CursorLoader cursorLoader;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_main);
		
		//Added here
		String[] columns;
		Cursor cursor;
		int[] to;
		
		String[] uiBindFrom = {};		
		int[] uiBindTo = {};
		mAdapter = new SimpleCursorAdapter(this,R.layout.db_view_main, null, uiBindFrom, uiBindTo,0);  
	    setListAdapter(mAdapter);
	    getLoaderManager().initLoader(0, null, this);
		
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
		
		CursorLoader cursL=new CursorLoader(this,StocksProvider.CONTENT_URI, null, null, null, null);
		
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
		
		Cursor stock = getContentResolver().query(StocksProvider.CONTENT_URI,null,null,null,null);
		if(stock==null){
			getContentResolver().registerContentObserver(StocksProvider.CONTENT_URI,true,new ContObserver(null));
			
			getLoaderManager().restartLoader(0, null, this);  
		}
		else
		{
			mAdapter.swapCursor(stock);
		}
			
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub
		
	}

}


class ContObserver extends ContentObserver{

	public ContObserver(Handler handler) {
		super(handler);
		
	}
	@Override
	   public void onChange(boolean selfChange) {

	      this.onChange(selfChange, StocksProvider.CONTENT_URI);

	   }     

	
}