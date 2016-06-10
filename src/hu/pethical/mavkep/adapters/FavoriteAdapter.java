package hu.pethical.mavkep.adapters;

import hu.pethical.mavkep.R;
import hu.pethical.mavkep.data.DatabaseHelper;
import hu.pethical.mavkep.data.Favorite;

import java.sql.SQLException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;

public class FavoriteAdapter extends BaseExpandableListAdapter implements CheckBox.OnCheckedChangeListener {

	private Activity		context;
	private List<Favorite>	favorites;

	DatabaseHelper			helper;

	public FavoriteAdapter(Context context) throws SQLException {
		this.context = (Activity) context;
		helper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
		favorites = helper.getFavoritesDao().queryForAll();
	}

	/*
	 * @Override public int getCount() { return favorites.size(); }
	 * @Override public Object getItem(int position) { return
	 * favorites.get(position); }
	 * @Override public long getItemId(int position) { return
	 * favorites.get(position)._id; }
	 * @Override public View getView(int position, View convertView, ViewGroup
	 * parent) { View view = convertView; if (view == null) view =
	 * context.getLayoutInflater().inflate(R.layout.favorite_item, null);
	 * TextView text = (TextView) view.findViewById(R.id.favorite_text);
	 * text.setText(getItem(position).toString()); return view; }
	 * @Override public boolean hasStableIds() { return false; }
	 * @Override public boolean isEmpty() { return false; }
	 * @Override public boolean areAllItemsEnabled() { return true; }
	 * @Override public boolean isEnabled(int position) { return true; }
	 */

	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) view = context.getLayoutInflater().inflate(R.layout.favorite_item, null);
		TextView text = (TextView) view.findViewById(R.id.favorite_text);
		text.setText(favorites.get(position).toString());
		view.findViewById(R.id.favorite_check).setFocusable(false);
		CheckBox box = (CheckBox) view.findViewById(R.id.favorite_check);
		box.setTag(favorites.get(position));
		box.setChecked(favorites.get(position).sync);
		box.setOnCheckedChangeListener(this);
		return view;
	}

	public void remove(int position) {
		favorites.remove(position);
		this.notifyDataSetChanged();
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return favorites.get(groupPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) view = context.getLayoutInflater().inflate(R.layout.favoritesetting, null);
		CheckBox c1 = (CheckBox) view.findViewById(R.id.favorite_settings_check1);
		CheckBox c2 = (CheckBox) view.findViewById(R.id.favorite_settings_check2);
		c1.setChecked(favorites.get(groupPosition).sync);
		c2.setChecked(favorites.get(groupPosition).sync);
		// text.setText(favorites.get(position).toString());
		return view;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return 0;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return favorites.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return favorites.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return favorites.get(groupPosition)._id;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		return getView(groupPosition, convertView, parent);
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		Favorite favorite = (Favorite) buttonView.getTag();
		favorite.sync = isChecked;
		try
		{
			helper.getFavoritesDao().update(favorite);
		}
		catch (SQLException e)
		{
			Toast.makeText(context, R.string.nem_sikerult_menteni_a_modositast, Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}

	}
}
