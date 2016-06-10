package hu.pethical.mavkep.activities;

import hu.pethical.mavkep.R;
import hu.pethical.mavkep.fragments.DetailsFragment;
import hu.pethical.mavkep.global.PethicalFragmentActivity;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;

public class DetailsListActivity extends PethicalFragmentActivity {
	String url;
	String Number;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);
		url = getIntent().getStringExtra("url");
		Number = getIntent().getStringExtra("number");
		if (url == "") {
			NavUtils.navigateUpTo(this, null);
			return;
		}
		DetailsFragment df = (DetailsFragment) getSupportFragmentManager()
				.findFragmentById(R.id.details_list_fragment);
		df.setNumber(Number);
		if (!df.hasdata)
			df.refresh(url, true);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			ActionBar bar = getActionBar();
			if (bar != null) {
				bar.setTitle(R.string.title_activity_details_list);
				bar.setSubtitle(getIntent().getStringExtra("from") + " - "
						+ getIntent().getStringExtra("to"));
			}
		}
	}
}
