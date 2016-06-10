package hu.pethical.mavkep.activities;

import hu.pethical.mavkep.R;
import hu.pethical.mavkep.fragments.DetailsFragment;
import hu.pethical.mavkep.fragments.TimeTableFragment;
import hu.pethical.mavkep.fragments.TimeTableFragment.OnItemClickListener;
import hu.pethical.mavkep.global.Jni;
import hu.pethical.mavkep.global.PethicalFragmentActivity;

import java.net.URLEncoder;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.os.Build;
import android.os.Bundle;

public class TimeTableActivity extends PethicalFragmentActivity implements OnItemClickListener {

	private String from;
	private String Url;
	private String to;
	private String date;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timetable);
		if (savedInstanceState != null) {
			from = savedInstanceState.getString("from");
			to = savedInstanceState.getString("to");
			date = savedInstanceState.getString("date");
			Url = savedInstanceState.getString("url");
		}
		if (from == null || from.equals("") || Url == null || Url.equals("")) {
			Url = buildUrl();
		}

		if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
			setupActionBar();
		}

		TimeTableFragment fragment = (TimeTableFragment) getSupportFragmentManager()
				.findFragmentById(R.id.timetable_fragment);
		if (getSupportFragmentManager().findFragmentById(R.id.details_fragment) != null
				&& findViewById(R.id.details_fragment) != null) {
			fragment.setOnItemClickListener(this);
		}
		else {
			fragment.setOnItemClickListener(null);
		}
		fragment.load(Url, from, to, date);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			ActionBar bar = getActionBar();
			if (bar != null) {
				bar.setTitle("Menetrend " + date);
				bar.setSubtitle(from + " - " + to);
			}
		}
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putString("url", Url);
		savedInstanceState.putString("from", from);
		savedInstanceState.putString("to", to);
		savedInstanceState.putString("date", date);
		super.onSaveInstanceState(savedInstanceState);
	}

	private String buildUrl() {
		from = getIntent().getStringExtra("from");
		to = getIntent().getStringExtra("to");
		date = getIntent().getStringExtra("date");
		final StringBuilder urlBuilder = new StringBuilder();
		urlBuilder.append(Jni.GetBaseUrl());
		urlBuilder.append(URLEncoder.encode(from));
		urlBuilder.append("&to=");
		urlBuilder.append(URLEncoder.encode(to));
		urlBuilder.append("&date=");
		urlBuilder.append(getIntent().getStringExtra("date"));
		String via = getIntent().getStringExtra("via");
		if (via != null && !via.equals("")) {
			urlBuilder.append("&via=");
			urlBuilder.append(URLEncoder.encode(via));
		}
		if (getIntent().getBooleanExtra("wotransfer", false)) {
			urlBuilder.append("&wotransfer=true");
		}
		if (getIntent().getBooleanExtra("woextrafee", false)) {
			urlBuilder.append("&woextrafee=true");
		}
		if (getIntent().getBooleanExtra("bpmonthlycard", false)) {
			urlBuilder.append("&bpmonthlycard=true");
		}
		if (getIntent().getBooleanExtra("bicycle", false)) {
			urlBuilder.append("&bicycle=true");
		}
		return urlBuilder.toString();
	}

	@Override
	public void onClick(String url, String number) {
		DetailsFragment details = ((DetailsFragment) getSupportFragmentManager().findFragmentById(
				R.id.details_fragment));
		if (details != null) {
			details.setNumber(number);
			details.refresh(url, true);
		}
	}

}
