package hu.pethical.mavkep.global;

import hu.pethical.mavkep.data.DatabaseHelper;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class PethicalFragmentActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setupActionBar();
	}

	public DatabaseHelper getHelper() {
		return Container.getHelper(this);
	}

	protected void setupActionBar() {
	}

}
