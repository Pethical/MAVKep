package hu.pethical.mavkep.activities;

import hu.pethical.mavkep.R;
import hu.pethical.mavkep.adapters.TimeTableAdapter;
import hu.pethical.mavkep.elvira.ElviraException;
import hu.pethical.mavkep.elvira.ElviraRequest;
import hu.pethical.mavkep.elvira.ElviraResponse;
import hu.pethical.mavkep.elvira.IElviraCallback;
import hu.pethical.mavkep.entities.Changes;
import hu.pethical.mavkep.entities.TimeTableDataSource;
import hu.pethical.mavkep.global.Container;

import java.io.IOException;
import java.net.URLEncoder;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TimeTableActivity extends PethicalActivity implements IElviraCallback, ExpandableListView.OnChildClickListener, ExpandableListView.OnGroupClickListener, Runnable {

	protected String		from				= null;
	protected String		to					= null;
	private String			Url					= null;
	private ElviraResponse	lastElviraResponse	= null;
	private ElviraRequest	request;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menetrend);
		lastElviraResponse = (ElviraResponse) getLastNonConfigurationInstance();
		if (savedInstanceState != null)
		{
			from = savedInstanceState.getString("from");
			to = savedInstanceState.getString("to");
			Url = savedInstanceState.getString("url");
		}
		if (from == null || from.equals("") || Url == null || Url.equals(""))
		{
			Url = buildUrl();
		}
		setupActionBar();
		setSubtitle(from + " - " + to);
		final ExpandableListView listView = findView(R.id.menetrend);
		listView.setGroupIndicator(null);
		listView.setScrollingCacheEnabled(false);
		listView.setOnChildClickListener(this);
		listView.setOnGroupClickListener(this);
		if (lastElviraResponse == null)
		{
			findView(ProgressBar.class, R.id.menetrendProgress).setVisibility(ProgressBar.VISIBLE);
			refresh(true);
		}
		else
		{
			final Thread thread = new Thread(this);
			thread.start();
		}
	}

	public void refresh(boolean cacheOk) {
		request = new ElviraRequest(this);
		request.cacheOk = cacheOk;
		request.Request(Url, this, 0);
	}

	public void run() {
		RequestDone(lastElviraResponse);
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		return lastElviraResponse;
	}

	public void showLocalTransfer() {

	}

	@Override
	public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
		TimeTableAdapter adapter = (TimeTableAdapter) parent.getExpandableListAdapter();
		Changes c = (Changes) adapter.getGroup(groupPosition);
		if (c.getNumber().contains("h")) return false;
		if (adapter.getGroup(groupPosition).getChange() > 0) return false;
		String url = adapter.getGroup(groupPosition).getGet_url();
		String map = adapter.getGroup(groupPosition).getMap();
		return onItemClick(url, map);
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
		TimeTableAdapter adapter = (TimeTableAdapter) parent.getExpandableListAdapter();
		Changes c = (Changes) adapter.getChild(groupPosition, childPosition);
		if (c.getNumber().contains("h"))
		{
			Intent intent = new Intent(this, LocalTransferActivity.class);
			intent.putExtra("from", c.getFrom());
			if (c.getTo() != null && !c.getTo().equals(""))
			{
				intent.putExtra("to", c.getTo());
			}
			else
			{
				intent.putExtra("to", to);
			}
			if (childPosition != 0)
			{
				c = (Changes) adapter.getChild(groupPosition, childPosition - 1);
			}
			intent.putExtra("when", c.getDestinationtime());
			startActivity(intent);
			return true;
		}
		String url = adapter.getChild(groupPosition, childPosition).getGet_url();
		String map = adapter.getChild(groupPosition, childPosition).getMap();
		return onItemClick(url, map);
	}

	public boolean onItemClick(String url, String map) {
		String number = null;
		if (map != null && map != "")
		{
			number = map.split("=")[1];
		}
		Intent intent = new Intent(TimeTableActivity.this, DetailsListActivity.class);
		intent.putExtra("url", url);
		intent.putExtra("number", number);
		intent.putExtra("from", from);
		intent.putExtra("to", to);
		intent.putExtra("date", getIntent().getStringExtra("date"));
		startActivity(intent);
		return true;
	}

	private String buildUrl() {
		from = getIntent().getStringExtra("from");
		to = getIntent().getStringExtra("to");
		final StringBuilder urlBuilder = new StringBuilder();
		urlBuilder.append("http://train.pethical.hu:3333/index.php?from=");
		urlBuilder.append(URLEncoder.encode(from));
		urlBuilder.append("&to=");
		urlBuilder.append(URLEncoder.encode(to));
		urlBuilder.append("&date=");
		urlBuilder.append(getIntent().getStringExtra("date"));
		String via = getIntent().getStringExtra("via");
		if (via != null && !via.equals(""))
		{
			urlBuilder.append("&via=");
			urlBuilder.append(URLEncoder.encode(via));
		}
		if (getIntent().getBooleanExtra("wotransfer", false))
		{
			urlBuilder.append("&wotransfer=true");
		}
		if (getIntent().getBooleanExtra("woextrafee", false))
		{
			urlBuilder.append("&woextrafee=true");
		}
		if (getIntent().getBooleanExtra("bpmonthlycard", false))
		{
			urlBuilder.append("&bpmonthlycard=true");
		}
		if (getIntent().getBooleanExtra("bicycle", false))
		{
			urlBuilder.append("&bicycle=true");
		}
		return urlBuilder.toString();
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putString("url", Url);
		savedInstanceState.putString("from", from);
		savedInstanceState.putString("to", to);
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menetrend, menu);
		return true;
	}

	@Override
	public void RequestDone(final ElviraResponse response) {
		final TimeTableAdapter adapter;
		if (response.getException() == null)
		{
			lastElviraResponse = response;
			try
			{
				ObjectMapper om = Container.getMapper();
				adapter = new TimeTableAdapter(this, om.readValue(response.getResponse(), TimeTableDataSource.class));
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						ExpandableListView lv = findView(R.id.menetrend);
						findViewById(R.id.menetrendProgress).setVisibility(ProgressBar.GONE);
						lv.setAdapter(adapter);
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
		else
		{
			adapter = null;
			if (response.getException() instanceof ElviraException)
			{
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						showToast(response.getException().getMessage());
						finish();
					}
				});

			}
			else
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						showToast(getString(R.string.nem_sikerult_elerni_a_szervert));
						finish();
					}
				});
		}
	}

	public void showToast(String toast) {
		Toast.makeText(this, toast, Toast.LENGTH_LONG).show();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.refresh_timetable)
		{
			ExpandableListView lv = findView(R.id.menetrend);
			lv.setAdapter((ExpandableListAdapter) null);
			findViewById(R.id.menetrendProgress).setVisibility(ProgressBar.VISIBLE);
			refresh(false);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
