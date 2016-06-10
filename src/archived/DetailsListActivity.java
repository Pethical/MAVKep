package hu.pethical.mavkep.activities;

import hu.pethical.mavkep.R;
import hu.pethical.mavkep.adapters.DetailsAdapter;
import hu.pethical.mavkep.elvira.ElviraRequest;
import hu.pethical.mavkep.elvira.ElviraResponse;
import hu.pethical.mavkep.elvira.IElviraCallback;

import java.io.IOException;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class DetailsListActivity extends PethicalActivity implements IElviraCallback {

	protected static String	Number;
	protected ElviraRequest	request				= null;
	private ElviraResponse	lastElviraResponse	= null;
	private String			url					= null;
	private boolean			refreshEnabled		= true;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.details_list);
		setupActionBar();
		final ExpandableListView list = findView(R.id.details);
		list.setGroupIndicator(null);
		url = getIntent().getStringExtra("url");
		Number = getIntent().getStringExtra("number");
		if (url == "")
		{
			NavUtils.navigateUpTo(this, null);
			return;
		}
		request = new ElviraRequest(this);
		lastElviraResponse = (ElviraResponse) getLastNonConfigurationInstance();
		if (lastElviraResponse == null)
		{
			refresh(url, true);
		}
		else
		{
			final Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					RequestDone(lastElviraResponse);
				}
			});
			thread.start();
		}
	}

	public String getSubTitle() {
		return getIntent().getStringExtra("from") + " - " + getIntent().getStringExtra("to");
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		return lastElviraResponse;
	}

	protected void refresh(String url, boolean cacheOk) {
		refreshEnabled = false;
		// invalidateOptionsMenu();
		request.cacheOk = cacheOk;
		request.Request(url, this, 0);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.details_list, menu);
		// MenuItem item = menu.findItem(R.id.refresh_details);
		// item.setEnabled(this.refreshEnabled);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.show_on_map)
		{
			Intent intent = new Intent(this, TrainMapActivity.class);
			intent.putExtra("number", Number);
			startActivity(intent);
			return true;
		}
		if (item.getItemId() == R.id.refresh_details)
		{
			if (refreshEnabled)
			{
				refresh(url, false);
			}
			else
			{
				Toast.makeText(this, R.string.a_frissites_folyamatban_van, Toast.LENGTH_SHORT).show();
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void RequestDone(ElviraResponse response) {
		if (response == null || response.getException() != null)
		{
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(DetailsListActivity.this, R.string.nem_sikerult_betolteni_az_adatokat, Toast.LENGTH_SHORT).show();
					refreshEnabled = true;
					// invalidateOptionsMenu();
					findView(ProgressBar.class, R.id.detailsProgress).setVisibility(ProgressBar.GONE);
				}
			});
			return;
		}
		lastElviraResponse = response;
		try
		{
			final DetailsAdapter adapter = new DetailsAdapter(response.getResponse(), this);
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					final ExpandableListView list = findView(R.id.details);
					findView(ProgressBar.class, R.id.detailsProgress).setVisibility(ProgressBar.GONE);
					list.setAdapter(adapter);
					refreshEnabled = true;
					// invalidateOptionsMenu();
				}
			});
		}
		catch (JsonParseException e)
		{
			e.printStackTrace();
		}
		catch (JsonMappingException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
