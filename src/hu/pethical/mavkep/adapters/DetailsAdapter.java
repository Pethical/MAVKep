package hu.pethical.mavkep.adapters;

import hu.pethical.mavkep.R;
import hu.pethical.mavkep.entities.Details;
import hu.pethical.mavkep.entities.Table;
import hu.pethical.mavkep.global.PethicalActivity;

import java.io.IOException;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class DetailsAdapter extends BaseExpandableListAdapter {

	private final Details	datasource;
	private final Activity	context;

	public DetailsAdapter(String content, Activity context) throws JsonParseException, JsonMappingException, IOException {
		datasource = Details.CreateFromString(content);
		this.context = context;
	}

	public DetailsAdapter(PethicalActivity context, Details details) {
		this.context = context;
		datasource = details;
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
		return null;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return 0;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return datasource.getTable().get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return datasource.getTable().size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return 0;
	}

	public static TextView FindView(View view, int id) {
		return (TextView) view.findViewById(id);
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

		View view = convertView;
		if (view == null) view = context.getLayoutInflater().inflate(R.layout.item2, null);

		Table item = datasource.getTable().get(groupPosition);
		FindView(view, R.id.Station).setText(item.getName());
		FindView(view, R.id.Indulas).setText(item.getSchedule().getDeparture());
		FindView(view, R.id.Erkezes).setText(item.getSchedule().getArrival());

		FindView(view, R.id.Erkezes_real).setText(item.getReal().getArrival());
		FindView(view, R.id.Indulas_real).setText(item.getReal().getDeparture());

		if (item.getReal().getDeparture() == null || item.getReal().getArrival() == null || item.getReal().getDeparture().equals("") || item.getReal().getArrival().equals(""))
		{
			FindView(view, R.id.teny).setText("");
		}
		else
		{
			if (item.getReal().getIsreal1())
			{
				FindView(view, R.id.Indulas_real).setTextColor(android.graphics.Color.parseColor("#669900"));
				FindView(view, R.id.teny).setText(R.string.tenyleges);
			}
			else
			{
				FindView(view, R.id.Indulas_real).setTextColor(android.graphics.Color.BLACK);
				FindView(view, R.id.teny).setText(R.string.varhato);
			}
			if (item.getReal().getIsreal2())
			{
				FindView(view, R.id.Erkezes_real).setTextColor(android.graphics.Color.parseColor("#669900"));
			}
			else
			{
				FindView(view, R.id.Erkezes_real).setTextColor(android.graphics.Color.BLACK);
			}
		}
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
