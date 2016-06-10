package hu.pethical.mavkep.global;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import hu.pethical.mavkep.data.DatabaseHelper;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

public class PethicalFragment extends Fragment {

	DatabaseHelper	helper	= null;

	public DatabaseHelper getHelper() {
		if (helper == null) helper = OpenHelperManager.getHelper(getActivity(), DatabaseHelper.class);
		return helper;
	}

	public String getTextById(int id) {
		View v = getView().findViewById(id);
		if ((v instanceof TextView))
		{
			return TextView.class.cast(v).getText().toString();
		}
		if (v instanceof AutoCompleteTextView)
		{
			return AutoCompleteTextView.class.cast(v).getText().toString();
		}
		throw new IllegalArgumentException();
	}

}
