package hu.pethical.mavkep.activities;

import hu.pethical.mavkep.R;
import hu.pethical.mavkep.entities.Mapinfo;
import hu.pethical.mavkep.entities.TrainInfo;
import hu.pethical.mavkep.global.Container;
import hu.pethical.mavkep.global.Jni;
import hu.pethical.mavkep.global.TrainOverlay;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class TrainMapActivity extends MapActivity implements Runnable {

	protected String Number = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Location l = null;
		l = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

		Number = getIntent().getStringExtra("number");
		if (Number == null) Number = "";
		MapView mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		if (l != null) {
			int lat = (int) (l.getLatitude() * 1E6);
			int lng = (int) (l.getLongitude() * 1E6);
			GeoPoint point = new GeoPoint(lat, lng);
			mapView.getController().setCenter(point);
		}
		mapView.getController().setZoom(15);
		Thread r = new Thread(this);
		r.start();
	}

	public HttpResponse RequestMap(String Url) throws ClientProtocolException, IOException {
		HttpClient client = Container.getHttpClient();
		HttpGet httpGet = new HttpGet(Url);
		httpGet.setHeader("accept", "application/json");
		HttpResponse response = client.execute(httpGet);
		StatusLine statusLine = response.getStatusLine();

		int statusCode = statusLine.getStatusCode();
		if (statusCode == 200) {
			return response;
		}
		else {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(TrainMapActivity.this,
							R.string.nem_sikerult_letolteni_az_adatokat, Toast.LENGTH_SHORT).show();
				}
			});
			throw new IOException(getString(R.string.failed_to_load_data_from_server));
		}
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.map, menu);
		return true;
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	public void run() {
		InputStream content;
		try {
			String url = Jni.GetMapUrl();
			if (!Number.equals("")) {
				url += "?number=" + Number;
			}
			HttpResponse response = this.RequestMap(url);
			if (response == null)
				throw new NullPointerException(getString(R.string.response_is_null));
			HttpEntity entity = response.getEntity();
			content = entity.getContent();
			// String line;
			final MapView mapView = (MapView) findViewById(R.id.mapview);
			final List<Overlay> mapOverlays = mapView.getOverlays();
			/*
			 * BufferedReader reader = new BufferedReader(new
			 * InputStreamReader(content), 8192); StringBuilder builder = new
			 * StringBuilder(); while ((line = reader.readLine()) != null) {
			 * builder.append(line); } reader.close();
			 */
			ObjectMapper mapper = Container.getMapper();
			final Mapinfo mapinfo = mapper.readValue(content, Mapinfo.class);
			final Drawable drawable = TrainMapActivity.this.getResources().getDrawable(
					R.drawable.ic_launcher);
			final Drawable drawable2 = TrainMapActivity.this.getResources().getDrawable(
					R.drawable.marker);
			final Drawable drawable3 = TrainMapActivity.this.getResources().getDrawable(
					R.drawable.yellow_train_marker);
			final Drawable drawable4 = TrainMapActivity.this.getResources().getDrawable(
					R.drawable.green_train_marker);

			TrainOverlay itemizedoverlay1 = new TrainOverlay(drawable, TrainMapActivity.this);
			TrainOverlay itemizedoverlay4 = new TrainOverlay(drawable4, TrainMapActivity.this);
			TrainOverlay itemizedoverlay3 = new TrainOverlay(drawable3, TrainMapActivity.this);
			TrainOverlay itemizedoverlay2 = new TrainOverlay(drawable2, TrainMapActivity.this);
			for (TrainInfo info : mapinfo.getMaps()) {
				GeoPoint point = new GeoPoint((int) (info.getLatitude() * 1e6),
						(int) (info.getLongitude() * 1e6));
				String keses;
				if (info.getDelay() != null) {
					keses = String.format(getString(R.string.s_perc_keses), info.getDelay());
				}
				else {
					keses = getString(R.string.nincs_keses);
				}
				OverlayItem overlayitem = new OverlayItem(point, info.getStart() + " - "
						+ info.getEnd(), keses);

				if (info.isHighlight()) {
					itemizedoverlay1.addOverlay(overlayitem);
					MapController mapController = mapView.getController();
					mapController.setCenter(point);
					// itemizedoverlay1 = new TrainOverlay(drawable,
					// TrainMapActivity.this);
				}
				else {
					String delay = info.getDelay();
					if (delay == null || delay.equals("")) delay = "0";
					if (Integer.parseInt(delay) < 5) {
						// itemizedoverlay = new TrainOverlay(drawable4,
						// TrainMapActivity.this);
						itemizedoverlay4.addOverlay(overlayitem);
					}
					else if (Integer.parseInt(delay) < 10) {
						// itemizedoverlay = new TrainOverlay(drawable3,
						// TrainMapActivity.this);
						itemizedoverlay3.addOverlay(overlayitem);
					}
					else {
						// itemizedoverlay = new TrainOverlay(drawable2,
						// TrainMapActivity.this);
						itemizedoverlay2.addOverlay(overlayitem);
					}
				}
			}
			if (itemizedoverlay4.size() > 0)
				mapOverlays.add(itemizedoverlay4);
			else
				Log.e("MAP", "Nincs adat 4");
			if (itemizedoverlay3.size() > 0)
				mapOverlays.add(itemizedoverlay3);
			else
				Log.e("MAP", "Nincs adat 3");
			if (itemizedoverlay2.size() > 0)
				mapOverlays.add(itemizedoverlay2);
			else
				Log.e("MAP", "Nincs adat 2");
			if (itemizedoverlay1.size() > 0)
				mapOverlays.add(itemizedoverlay1);
			else
				Log.e("MAP", "Nincs adat 1");
			// mapView.invalidate();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
