package hu.pethical.mavkep.fragments;

import hu.pethical.mavkep.R;
import hu.pethical.mavkep.activities.MainActivity;
import hu.pethical.mavkep.adapters.FavoriteAdapter;
import hu.pethical.mavkep.data.DatabaseHelper;
import hu.pethical.mavkep.data.Favorite;
import hu.pethical.mavkep.global.PethicalFragment;

import java.sql.SQLException;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;

import com.j256.ormlite.android.apptools.OpenHelperManager;

public class FavoriteFragment extends PethicalFragment implements OnGroupClickListener {

	public interface OnFavoriteSelectedListener {
		public boolean onFavoriteSelected(Favorite favorite);
	}

	private OnFavoriteSelectedListener	listener	= null;

	public void setFavoriteSelectedListener(OnFavoriteSelectedListener listener) {
		this.listener = listener;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.favorites, container);
		final ExpandableListView listView = (ExpandableListView) view.findViewById(R.id.favlist);
		listView.setGroupIndicator(null);
		listView.setScrollingCacheEnabled(false);
		listView.setOnGroupClickListener(this);
		// setupActionBar();
		try
		{
			FavoriteAdapter adapter = new FavoriteAdapter(getActivity());
			listView.setAdapter(adapter);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(android.widget.AdapterView<?> parent, View v, final int position, long id) {
				final Favorite favorite = (Favorite) ((ExpandableListView) parent).getExpandableListAdapter().getGroup(position);
				new AlertDialog.Builder(getActivity()).setIcon(android.R.drawable.ic_dialog_alert).setTitle(R.string.kedvenc_torlese_title).setMessage(R.string.toroljuk_ezt_a_kedvencet).setPositiveButton(R.string.torlom, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						DatabaseHelper dbHelper = OpenHelperManager.getHelper(getActivity(), DatabaseHelper.class);
						try
						{
							getHelper().getFavoritesDao().delete(favorite);
							dbHelper.getFavoritesDao().delete(favorite);
							((FavoriteAdapter) listView.getExpandableListAdapter()).remove(position);
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
		return view;
	}

	public void refreshData() {
		try
		{
			FavoriteAdapter adapter = new FavoriteAdapter(getActivity());
			((ExpandableListView) getView().findViewById(R.id.favlist)).setAdapter(adapter);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

	}

	@Override
	public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
		inflater.inflate(R.menu.favorites, menu);
	}

	@Override
	public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
		Favorite f = (Favorite) ((ExpandableListView) getView().findViewById(R.id.favlist)).getExpandableListAdapter().getGroup(groupPosition);
		if (listener != null) return listener.onFavoriteSelected(f);
		Intent intent = new Intent(getActivity(), MainActivity.class);
		intent.putExtra("from", f.departure);
		intent.putExtra("to", f.destination);
		NavUtils.navigateUpTo(getActivity(), intent);
		return true;
	}

}
