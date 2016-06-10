package archived;

import hu.pethical.mavkep.R;
import hu.pethical.mavkep.activities.MainActivity;
import hu.pethical.mavkep.activities.PethicalActivity;
import hu.pethical.mavkep.adapters.FavoriteAdapter;
import hu.pethical.mavkep.data.DatabaseHelper;
import hu.pethical.mavkep.data.Favorite;

import java.sql.SQLException;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import com.j256.ormlite.android.apptools.OpenHelperManager;

public class FavoritesActivity extends PethicalActivity implements ExpandableListView.OnGroupClickListener {

	protected ExpandableListView getExpandableListView() {
		return findView(R.id.favlist);
	}

	protected void setListAdapter(ExpandableListAdapter adapter) {
		getExpandableListView().setAdapter(adapter);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.favorites);
		ExpandableListView listView = findView(R.id.favlist);
		listView.setGroupIndicator(null);
		listView.setScrollingCacheEnabled(false);
		getExpandableListView().setOnGroupClickListener(this);
		setupActionBar();
		try
		{
			FavoriteAdapter adapter = new FavoriteAdapter(this);
			setListAdapter(adapter);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		getExpandableListView().setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(android.widget.AdapterView<?> parent, View v, final int position, long id) {
				final Favorite favorite = (Favorite) FavoritesActivity.this.getExpandableListView().getExpandableListAdapter().getGroup(position);
				new AlertDialog.Builder(FavoritesActivity.this).setIcon(android.R.drawable.ic_dialog_alert).setTitle(R.string.kedvenc_torlese_title).setMessage(R.string.toroljuk_ezt_a_kedvencet).setPositiveButton(R.string.torlom, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						DatabaseHelper dbHelper = OpenHelperManager.getHelper(FavoritesActivity.this, DatabaseHelper.class);
						try
						{
							getHelper().getFavoritesDao().delete(favorite);
							dbHelper.getFavoritesDao().delete(favorite);
							((FavoriteAdapter) getExpandableListView().getExpandableListAdapter()).remove(position);
						}
						catch (SQLException e)
						{
							e.printStackTrace();
						}
					}
				}).setNegativeButton(R.string.nem, null).show();
				return true;
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.favorites, menu);
		return true;
	}

	@Override
	public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
		Favorite f = (Favorite) getExpandableListView().getExpandableListAdapter().getGroup(groupPosition);
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra("from", f.departure);
		intent.putExtra("to", f.destination);
		NavUtils.navigateUpTo(this, intent);
		return true;
	}

}
