package hu.pethical.mavkep.adapters;

import hu.pethical.mavkep.R;
import hu.pethical.mavkep.entities.LocalTravel;

import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LocalTransferAdapter extends BaseExpandableListAdapter {
	List<LocalTravel>	Local;
	Activity			context;

	public LocalTransferAdapter(List<LocalTravel> local, Activity context) {
		Local = local;
		this.context = context;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return null;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) view = context.getLayoutInflater().inflate(R.layout.localitem, null);
		LocalTravel group = (LocalTravel) getGroup(groupPosition);
		view.findViewById(R.id.csik).setBackgroundColor(Color.TRANSPARENT);
		((TextView) view.findViewById(R.id.text1)).setText(group.getInstruction().getSteps().get(childPosition));
		return view;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		List<String> steps = Local.get(groupPosition).getInstruction().getSteps();
		if (steps == null) return 0;
		return steps.size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return Local.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return Local.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return 0;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) view = context.getLayoutInflater().inflate(R.layout.localitem, null);
		LocalTravel group = (LocalTravel) getGroup(groupPosition);
		((TextView) view.findViewById(R.id.text1)).setText(group.getInstruction().getText());
		view.findViewById(R.id.fw).setVisibility(ImageView.GONE);
		return view;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}

}
