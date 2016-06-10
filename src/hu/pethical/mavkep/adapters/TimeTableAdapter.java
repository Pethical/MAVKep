package hu.pethical.mavkep.adapters;

import hu.pethical.mavkep.R;
import hu.pethical.mavkep.entities.Changes;
import hu.pethical.mavkep.entities.TimeTableDataSource;
import hu.pethical.mavkep.entities.Timetable;

import java.io.IOException;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class TimeTableAdapter extends BaseExpandableListAdapter {

	private final Activity context;
	private final TimeTableDataSource datasource;

	public TimeTableAdapter(Activity context, String menetrend) throws JsonParseException,
			JsonMappingException, IOException {
		super();
		this.context = context;
		datasource = TimeTableDataSource.createFromString(menetrend);
	}

	public TimeTableAdapter(Activity context, TimeTableDataSource menetrend) {
		super();
		this.context = context;
		datasource = menetrend;
	}

	@Override
	public Changes getChild(int group, int child) {
		Timetable Group = datasource.getTimetable().get(group);
		return Group.getChanges().get(child);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}

	public View GetView(Changes item, View convertView, ViewGroup parent, boolean canhide) {
		if (item.getNumber() != null && !item.getNumber().contains("h") || !canhide) {
			View view = convertView;
			if ((view != null) && (view.findViewById(R.id.When) == null)) {
				view = null;
			}
			if (view == null) view = context.getLayoutInflater().inflate(R.layout.item, null);

			view.findViewById(R.id.fw).setVisibility(ImageView.GONE);
			if (view.findViewById(R.id.When) == null) {
				view = null;
			}
			((TextView) view.findViewById(R.id.When)).setText(item.getWhen());
			String type = "";
			type = item.getType();

			((TextView) view.findViewById(R.id.Info)).setText(type);
			((TextView) view.findViewById(R.id.From)).setText(item.getFrom());
			((TextView) view.findViewById(R.id.To)).setText(item.getTo());
			((TextView) view.findViewById(R.id.Platform)).setText(item.getPlatform());

			if (item.getChange() > 0) {
				view.findViewById(R.id.csik).setBackgroundColor(Color.parseColor("#0099CC"));
			}
			else {
				view.findViewById(R.id.csik).setBackgroundColor(Color.TRANSPARENT);
			}

			int delay = item.getDelay();

			view.findViewById(R.id.kesescsik).setVisibility(RelativeLayout.VISIBLE);
			String keses = item.getWhen_real();
			((TextView) view.findViewById(R.id.RealTime)).setText(keses);
			view.findViewById(R.id.imageView1).setVisibility(ImageView.VISIBLE);
			if (delay < 1) {
				((TextView) view.findViewById(R.id.Keses)).setText(R.string.nincs_keses);
			}
			else {
				((TextView) view.findViewById(R.id.Keses)).setText(String.format(
						context.getString(R.string.keses_d_perc), delay));
			}
			if (delay > 10) {
				((ImageView) view.findViewById(R.id.imageView1)).setImageResource(R.drawable.snail);
			}
			else if (delay >= 5) {
				((ImageView) view.findViewById(R.id.imageView1)).setImageResource(R.drawable.dot2);
			}
			else {
				((ImageView) view.findViewById(R.id.imageView1)).setImageResource(R.drawable.dot);
			}
			return view;
		}
		else {
			View view = context.getLayoutInflater().inflate(R.layout.local, null);
			((TextView) view.findViewById(R.id.helyi1)).setText(item.getFrom() + " - "
					+ item.getTo());
			return view;
		}
	}

	@Override
	public View getChildView(int group, int child, boolean isLast, View convertView,
			ViewGroup parent) {
		Changes g = getGroup(group);
		if (g.getChange() == 0) {
			// return null;
		}
		Changes c = getChild(group, child);
		View view = GetView(c, convertView, parent, true);
		if (c.getNumber().contains("h")) return view;
		view.setPadding(2, 0, 0, 0);
		view.findViewById(R.id.fw).setVisibility(ImageView.VISIBLE);
		return view;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		int c = getGroup(groupPosition).getChange();
		if (c == 0) return c;
		return getGroup(groupPosition).getChanges().size();
	}

	@Override
	public Timetable getGroup(int groupPosition) {
		return datasource.getTimetable().get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return datasource.getTimetable().size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return 0; // Long.parseLong(getGroup(groupPosition).getNumber());
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
			ViewGroup viewGroup) {
		return GetView(getGroup(groupPosition), convertView, viewGroup, false);
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int group, int child) {
		return true;
	}

}
