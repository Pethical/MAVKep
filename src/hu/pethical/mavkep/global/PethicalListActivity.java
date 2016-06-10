package hu.pethical.mavkep.global;

import hu.pethical.mavkep.data.DatabaseHelper;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ExpandableListActivity;
import android.os.Build;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import com.j256.ormlite.android.apptools.OpenHelperManager;

public class PethicalListActivity extends ExpandableListActivity {

	private DatabaseHelper helper = null;

	public DatabaseHelper getHelper() {
		if (helper == null)
			helper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
		return helper;
	}

	public PethicalListActivity() {
		super();
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	protected void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			ActionBar bar = getActionBar();
			if (bar != null) {
				bar.setDisplayHomeAsUpEnabled(true);
			}
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	protected void setSubtitle(String subtitle) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			ActionBar bar = getActionBar();
			if (bar != null) {
				bar.setSubtitle(subtitle);
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
