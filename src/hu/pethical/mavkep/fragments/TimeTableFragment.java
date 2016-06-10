package hu.pethical.mavkep.fragments;

import hu.pethical.mavkep.R;
import hu.pethical.mavkep.activities.DetailsListActivity;
import hu.pethical.mavkep.activities.LocalTransferActivity;
import hu.pethical.mavkep.adapters.TimeTableAdapter;
import hu.pethical.mavkep.elvira.ElviraException;
import hu.pethical.mavkep.elvira.ElviraRequest;
import hu.pethical.mavkep.elvira.ElviraResponse;
import hu.pethical.mavkep.elvira.IElviraCallback;
import hu.pethical.mavkep.entities.Changes;
import hu.pethical.mavkep.entities.TimeTableDataSource;
import hu.pethical.mavkep.global.Container;
import hu.pethical.mavkep.global.PethicalFragment;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TimeTableFragment extends PethicalFragment implements
		OnGroupClickListener, OnChildClickListener, IElviraCallback, Runnable {

	public interface OnItemClickListener {
		public void onClick(String url, String number);
	}

	String to, from, date;
	ElviraResponse lastElviraResponse = null;
	private String url;
	private OnItemClickListener listener = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.timetable, container, false);
		final ExpandableListView listView = (ExpandableListView) view
				.findViewById(R.id.menetrend);
		listView.setGroupIndicator(null);
		listView.setScrollingCacheEnabled(false);
		listView.setOnChildClickListener(this);
		listView.setOnGroupClickListener(this);
		return view;
	}

	public void load(String url, String from, String to, String date) {
		this.url = url;
		this.from = from;
		this.date = date;
		this.to = to;
		this.refresh(true);
	}

	public void setAdapter(ExpandableListAdapter adapter) {
		ExpandableListView listview = (ExpandableListView) getView()
				.findViewById(R.id.menetrend);
		listview.setAdapter(adapter);
	}

	public void setOnItemClickListener(OnItemClickListener listener) {
		this.listener = listener;
	}

	public boolean onItemClick(String url, String map) {
		String number = null;
		if (map != null && map != "") {
			number = map.split("=")[1];
		}
		if (this.listener != null) {
			this.listener.onClick(url, number);
			return true;
		}
		Intent intent = new Intent(getActivity(), DetailsListActivity.class);
		intent.putExtra("url", url);
		intent.putExtra("number", number);
		intent.putExtra("from", from);
		intent.putExtra("to", to);
		intent.putExtra("date", date);
		startActivity(intent);
		return true;
	}

	public void refresh(boolean cacheOk) {
		getView().findViewById(R.id.menetrendProgress).setVisibility(
				ProgressBar.VISIBLE);
		ElviraRequest request = new ElviraRequest(getActivity());
		request.cacheOk = cacheOk;
		request.Request(url, this, 0);
	}

	@Override
	public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
		inflater.inflate(R.menu.menetrend, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.refresh_timetable) {
			ExpandableListView lv = (ExpandableListView) getView()
					.findViewById(R.id.menetrend);
			lv.setAdapter((ExpandableListAdapter) null);
			getView().findViewById(R.id.menetrendProgress).setVisibility(
					ProgressBar.VISIBLE);
			refresh(false);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {

		TimeTableAdapter adapter = (TimeTableAdapter) parent
				.getExpandableListAdapter();
		Changes c = (Changes) adapter.getChild(groupPosition, childPosition);
		if (c.getNumber().contains("h")) {
			Intent intent = new Intent(getActivity(),
					LocalTransferActivity.class);
			intent.putExtra("from", c.getFrom());
			if (c.getTo() != null && !c.getTo().equals("")) {
				intent.putExtra("to", c.getTo());
			} else {
				intent.putExtra("to", to);
			}
			if (childPosition != 0) {
				c = (Changes) adapter
						.getChild(groupPosition, childPosition - 1);
			}
			intent.putExtra("when", c.getDestinationtime());
			startActivity(intent);
			return true;
		}
		String url = adapter.getChild(groupPosition, childPosition)
				.getGet_url();
		String map = adapter.getChild(groupPosition, childPosition).getMap();
		return onItemClick(url, map);
	}

	@Override
	public boolean onGroupClick(ExpandableListView parent, View v,
			int groupPosition, long id) {
		TimeTableAdapter adapter = (TimeTableAdapter) parent
				.getExpandableListAdapter();
		Changes c = (Changes) adapter.getGroup(groupPosition);
		if (c.getNumber().contains("h"))
			return false;
		if (adapter.getGroup(groupPosition).getChange() > 0)
			return false;
		String url = adapter.getGroup(groupPosition).getGet_url();
		String map = adapter.getGroup(groupPosition).getMap();
		return onItemClick(url, map);
	}

	@Override
	public void run() {

	}

	@Override
	public void RequestDone(final ElviraResponse response) {
		final TimeTableAdapter adapter;
		if (response.getException() == null) {
			lastElviraResponse = response;
			try {
				ObjectMapper om = Container.getMapper();
				adapter = new TimeTableAdapter(getActivity(), om.readValue(
						response.getResponse(), TimeTableDataSource.class));
				Activity activity = getActivity();
				if (activity != null)
					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							ExpandableListView lv = (ExpandableListView) getView()
									.findViewById(R.id.menetrend);
							getView().findViewById(R.id.menetrendProgress)
									.setVisibility(ProgressBar.GONE);
							lv.setAdapter(adapter);
						}
					});
			} catch (JsonParseException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			adapter = null;
			if (response.getException() instanceof ElviraException) {
				Activity activity = getActivity();
				if (activity != null)
					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(getActivity(),
									response.getException().getMessage(),
									Toast.LENGTH_LONG).show();
						}
					});

			} else {
				Activity activity = getActivity();
				if (activity != null)
					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(
									getActivity(),
									R.string.nem_sikerult_betolteni_az_adatokat,
									Toast.LENGTH_LONG).show();
						}
					});
			}
		}
	}
}
