package hu.pethical.mavkep.activities;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class StationOverlay extends ItemizedOverlay<OverlayItem> {

	private ArrayList<OverlayItem>	mOverlays	= new ArrayList<OverlayItem>();
	private Activity				mContext;

	public StationOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));

	}

	@Override
	protected boolean onTap(int index) {
		OverlayItem item = mOverlays.get(index);
		AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
		dialog.setTitle(item.getTitle());
		dialog.setMessage(item.getSnippet());
		dialog.show();
		return true;
	}

	public StationOverlay(Drawable defaultMarker, Activity context) {
		super(boundCenterBottom(defaultMarker));
		mContext = context;
	}

	public void addOverlay(OverlayItem overlay) {
		mOverlays.add(overlay);
		populate();
	}

	@Override
	protected OverlayItem createItem(int i) {
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		return mOverlays.size();
	}

}
