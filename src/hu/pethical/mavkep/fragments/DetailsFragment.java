package hu.pethical.mavkep.fragments;

import hu.pethical.mavkep.R;
import hu.pethical.mavkep.activities.TrainMapActivity;
import hu.pethical.mavkep.adapters.DetailsAdapter;
import hu.pethical.mavkep.elvira.ElviraRequest;
import hu.pethical.mavkep.elvira.ElviraResponse;
import hu.pethical.mavkep.elvira.IElviraCallback;
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
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class DetailsFragment extends PethicalFragment implements IElviraCallback {

	protected static String Number;
	protected ElviraRequest request = null;
	private ElviraResponse lastElviraResponse = null;
	private String url = null;
	private boolean refreshEnabled = true;
	public boolean hasdata = false;
	DetailsAdapter adapter = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		// setRetainInstance(true);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	public void setNumber(String number) {
		Number = number;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.details_list, container);
		final ExpandableListView list = (ExpandableListView) view.findViewById(R.id.details);
		list.setGroupIndicator(null);
		if (lastElviraResponse != null) {
			view.findViewById(R.id.detailsProgress).setVisibility(ProgressBar.GONE);
			list.setAdapter(adapter);
		}
		return view;
	}

	public void refresh(String url, boolean cacheOk) {
		getView().findViewById(R.id.detailsProgress).setVisibility(ProgressBar.VISIBLE);
		final ExpandableListView list = (ExpandableListView) getView().findViewById(R.id.details);
		list.setVisibility(ExpandableListView.GONE);
		this.url = url;
		if (request == null) request = new ElviraRequest(getActivity());
		refreshEnabled = false;
		request.cacheOk = cacheOk;
		request.Request(url, this, 0);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.details_list, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.show_on_map) {
			try {
				Intent intent = new Intent(getActivity(), TrainMapActivity.class);
				intent.putExtra("number", Number);
				startActivity(intent);
			}
			catch (Exception e) {
				Toast.makeText(getActivity(), R.string.nem_sikerult_inicializalni_a_terkepet,
						Toast.LENGTH_SHORT).show();
			}
			return true;
		}
		if (item.getItemId() == R.id.refresh_details) {
			if (url == null) {
				Activity activity = getActivity();
				if (activity != null) {
					Toast.makeText(activity, R.string.valaszd_ki_a_vonatot, Toast.LENGTH_SHORT)
							.show();
					return true;
				}
			}
			if (refreshEnabled) {
				refresh(url, false);
			}
			else {
				Toast.makeText(getActivity(), R.string.a_frissites_folyamatban_van,
						Toast.LENGTH_SHORT).show();
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void RequestDone(ElviraResponse response) {
		if (response == null || response.getException() != null) {
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(getActivity(), R.string.nem_sikerult_betolteni_az_adatokat,
							Toast.LENGTH_SHORT).show();
					refreshEnabled = true;
					getView().findViewById(R.id.detailsProgress).setVisibility(ProgressBar.GONE);
				}
			});
			return;
		}
		lastElviraResponse = response;
		try {
			this.adapter = new DetailsAdapter(response.getResponse(), getActivity());
			final DetailsAdapter adapter = this.adapter;
			Activity activity = getActivity();
			if (activity != null) activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					final ExpandableListView list = (ExpandableListView) getView().findViewById(
							R.id.details);
					getView().findViewById(R.id.detailsProgress).setVisibility(ProgressBar.GONE);
					list.setVisibility(ExpandableListView.VISIBLE);
					list.setAdapter(adapter);
					refreshEnabled = true;
					hasdata = true;
				}
			});
		}
		catch (JsonParseException e) {
			e.printStackTrace();
		}
		catch (JsonMappingException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

}
