package hu.pethical.mavkep.global;

import hu.pethical.mavkep.data.DatabaseHelper;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OpenHelperManager;

@SuppressLint("Registered")
public class PethicalActivity extends Activity {

	private DatabaseHelper helper = null;

	public DatabaseHelper getHelper() {
		if (helper == null)
			helper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
		return helper;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onPause() {
		super.onPause();
		return;
		/*
		 * if (helper != null) { OpenHelperManager.releaseHelper(); helper =
		 * null; }
		 */
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	public <T> T findView(Class<T> type, int id) {
		return type.cast(findView(id));
	}

	@SuppressWarnings("unchecked")
	public <T> T findView(int id) {
		return (T) super.findViewById(id);
	}

	public String getSubTitle() {
		return null;
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

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	protected void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// getActionBar().setDisplayHomeAsUpEnabled(true);
			ActionBar bar = getActionBar();
			if (bar != null) {
				bar.setSubtitle(getSubTitle());
			}
		}
	}

	public String getTextById(int id) {
		View v = findViewById(id);
		if ((v instanceof TextView)) {
			return TextView.class.cast(v).getText().toString();
		}
		if (v instanceof AutoCompleteTextView) {
			return AutoCompleteTextView.class.cast(v).getText().toString();
		}
		throw new IllegalArgumentException();
	}

	public void setTextById(int id, String text) {
		View v = findViewById(id);
		if ((v instanceof TextView)) {
			TextView.class.cast(v).setText(text);
		} else if (v instanceof AutoCompleteTextView) {
			AutoCompleteTextView.class.cast(v).setText(text);
		} else
			throw new IllegalArgumentException();
	}

	public void setTextById(int id, int resId) {
		View v = findViewById(id);
		if ((v instanceof TextView) || (v instanceof AutoCompleteTextView)) {
			TextView.class.cast(v).setText(getText(resId));
		} else
			throw new IllegalArgumentException();
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

	/*
	 * @Override protected void onCreate(Bundle savedInstanceState) {
	 * super.onCreate(savedInstanceState); }
	 */

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (helper != null) {
			OpenHelperManager.releaseHelper();
			helper = null;
		}
	}

}
