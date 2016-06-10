package hu.pethical.mavkep.activities;

import hu.pethical.mavkep.R;
import hu.pethical.mavkep.data.Favorite;
import hu.pethical.mavkep.data.ProgressCallback;
import hu.pethical.mavkep.elvira.ElviraResponse;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.format.Time;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.DeleteBuilder;

public class _MainActivity extends PethicalActivity implements OnClickListener, LocationListener {

	private class ProgressUpdater implements Runnable {
		private long	current;

		private ProgressUpdater(long current) {
			this.current = current;
		}

		@Override
		public void run() {
			progress.setProgress((int) current);
		}
	}

	private EditText	datum;
	private String		from, to, via;
	ProgressDialog		progress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		setupActionBar();
		progress = new ProgressDialog(this);
		ImageButton csereGomb = findView(R.id.a);
		Button keresesGomb = findView(R.id.button1);
		keresesGomb.setOnClickListener(this);
		csereGomb.setOnClickListener(this);
		datum = findView(R.id.startdate);
		datum.setOnClickListener(this);
		from = getIntent().getStringExtra("from");
		to = getIntent().getStringExtra("to");
		via = getIntent().getStringExtra("via");
		if ((from != null) && (!from.equals(""))) findView(AutoCompleteTextView.class, R.id.FromTextView).setText(from);
		if ((to != null) && (!to.equals(""))) findView(AutoCompleteTextView.class, R.id.ToTextView).setText(getIntent().getStringExtra("to"));
		if ((via != null) && (!via.equals(""))) findView(AutoCompleteTextView.class, R.id.keresztul).setText(getIntent().getStringExtra("via"));
		Time t = new Time();
		t.setToNow();
		datum.setText(t.format("%Y.%m.%d"));
		t = null;
		findView(ImageButton.class, R.id.where1).setEnabled(false);
		try
		{
			if (getHelper().getStationDao().countOf() == 0)
			{
				getHelper().fillStations(new ProgressCallback() {
					ProgressUpdater	updater;

					@Override
					public void onStart(final long all) {
						updater = new ProgressUpdater(0);
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								progress.setIndeterminate(false);
								progress.setMax((int) all);
								progress.setTitle(getString(R.string.telepites));
								progress.setMessage(getString(R.string.az_indexek_elso_felepitese_a_kesobbi_gyors_elereshez));
								progress.setCancelable(false);
								progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
								progress.show();
							}
						});
					}

					@Override
					public void onProgress(final long current, final long all) {
						updater.current = current;
						runOnUiThread(updater);
					}

					@Override
					public void onDone() {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (progress != null && progress.isShowing()) progress.dismiss();
								bindadapter();
							}
						});
					}
				});
			}
			else
			{
				bindadapter();
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	private void bindadapter() {
		Cursor c = getHelper().getWritableDatabase().query("station", new String[] { "_id", "name" }, null, null, null, null, null);
		@SuppressWarnings("deprecation")
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, c, new String[] { "name" }, new int[] { android.R.id.text1 });
		adapter.setCursorToStringConverter(new SimpleCursorAdapter.CursorToStringConverter() {
			@Override
			public CharSequence convertToString(Cursor cursor) {
				return cursor.getString(cursor.getColumnIndex("name"));
			}
		});
		adapter.setFilterQueryProvider(new FilterQueryProvider() {
			@Override
			public Cursor runQuery(CharSequence constraint) {
				return getHelper().getWritableDatabase().query("station", new String[] { "_id", "name" }, "name LIKE '" + constraint + "%'", null, null, null, null);

			}
		});
		findView(AutoCompleteTextView.class, R.id.FromTextView).setAdapter(adapter);
		findView(AutoCompleteTextView.class, R.id.ToTextView).setAdapter(adapter);
		findView(AutoCompleteTextView.class, R.id.keresztul).setAdapter(adapter);

		ImageButton button = findView(R.id.where1);
		button.setEnabled(true);
		final LocationManager locationManager = (LocationManager) this.getSystemService(MainActivity.LOCATION_SERVICE);
		if ((locationManager == null) || !locationManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER))
		{
			button.setVisibility(Button.GONE);
		}
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				progress = ProgressDialog.show(MainActivity.this, getString(R.string.hely_keresese), getString(R.string.kerlek_varj), true, true, new ProgressDialog.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						locationManager.removeUpdates(MainActivity.this);
					}
				});
				if (locationManager == null)
				{
					Toast.makeText(MainActivity.this, R.string.a_hely_nem_tamogatott, Toast.LENGTH_LONG).show();
					return;
				}
				locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, MainActivity.this);
				return;
			}
		});

	}

	private void search() {
		Intent intent = new Intent(this, TimeTableActivity.class);
		AutoCompleteTextView From = findView(R.id.FromTextView);
		AutoCompleteTextView To = findView(R.id.ToTextView);
		AutoCompleteTextView Via = findView(R.id.keresztul);
		if (From.getText().toString().equals(""))
		{
			Toast.makeText(this, R.string.kerem_adja_meg_az_indulasi_allomast, Toast.LENGTH_LONG).show();
			return;
		}
		if (To.getText().toString().equals(""))
		{
			Toast.makeText(this, R.string.kerem_adja_meg_az_erkezesi_allomast, Toast.LENGTH_LONG).show();
			return;
		}
		intent.putExtra("from", From.getText().toString());
		intent.putExtra("to", To.getText().toString());
		intent.putExtra("via", Via.getText().toString());
		intent.putExtra("date", ((EditText) findViewById(R.id.startdate)).getText().toString());
		intent.putExtra("wotransfer", ((ToggleButton) findViewById(R.id.atszallas)).isChecked());
		intent.putExtra("woextrafee", ((ToggleButton) findViewById(R.id.potjegy)).isChecked());
		intent.putExtra("bpmonthlycard", ((ToggleButton) findViewById(R.id.bpberlet)).isChecked());
		intent.putExtra("bicycle", ((ToggleButton) findViewById(R.id.kerekpar)).isChecked());
		startActivity(intent);
	}

	private void swapFromTo() {
		String From = getTextById(R.id.FromTextView);
		String To = getTextById(R.id.ToTextView);
		setTextById(R.id.FromTextView, To);
		setTextById(R.id.ToTextView, From);
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("SimpleDateFormat")
	private void dateSelect() {
		final Calendar c = Calendar.getInstance();
		final int year = c.get(Calendar.YEAR);
		final int month = c.get(Calendar.MONTH);
		final int day = c.get(Calendar.DAY_OF_MONTH);
		DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				Time t = new Time();
				t.set(dayOfMonth, monthOfYear, year);
				datum.setText(t.format("%Y.%m.%d"));
				datum.clearFocus();
				t = null;
			}
		}, year, month, day);
		SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
		Date dt;
		try
		{
			dt = format.parse(datum.getText().toString());
			dialog.updateDate(dt.getYear() + 1900, dt.getMonth(), dt.getDate());
			format = null;
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}
		dialog.show();

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id)
		{
			case R.id.a:
				swapFromTo();
				break;
			case R.id.button1:
				search();
				break;
			case R.id.startdate:
				dateSelect();
				break;
		}
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putString("from", from);
		savedInstanceState.putString("to", to);
		savedInstanceState.putString("via", via);
		super.onSaveInstanceState(savedInstanceState);
	}

	public void addToFavorites() {
		String departure = getTextById(R.id.FromTextView);
		String destination = getTextById(R.id.ToTextView);
		if ((departure.equals("")) || (departure == null))
		{
			if ((destination.equals("")) || (destination == null))
			{
				Toast.makeText(this, R.string.kerem_adja_meg_az_indul_s_helyet_es_a_celallomast, Toast.LENGTH_LONG).show();
			}
			else
			{
				Toast.makeText(this, R.string.kerem_adja_meg_az_indulas_helyet, Toast.LENGTH_LONG).show();
			}
			return;
		}
		else if ((destination.equals("")) || (destination == null))
		{
			Toast.makeText(this, R.string.kerem_adja_meg_a_celallomast, Toast.LENGTH_LONG).show();
			return;
		}
		try
		{
			Dao<Favorite, Integer> dao = getHelper().getFavoritesDao();
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("destination", destination);
			map.put("departure", departure);
			List<Favorite> van = dao.queryForFieldValues(map);
			String msg;
			if (van.size() > 0)
			{
				msg = getString(R.string.az_utvonal_mar_kedvenc);
			}
			else
			{
				Favorite f = new Favorite();
				f.departure = departure;
				f.destination = destination;
				dao.create(f);
				msg = getString(R.string.az_uj_kedvenc_elmentve);
			}
			Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
		}
		catch (SQLException e)
		{
			Toast.makeText(this, R.string.hiba_tortent, Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId())
		{
			case R.id.addtofavorites:
				addToFavorites();
				return true;
			case R.id.favorites:
				intent = new Intent(this, FavoritesActivity.class);
				startActivity(intent);
				return true;
			case R.id.showmap:
				intent = new Intent(this, TrainMapActivity.class);
				startActivity(intent);
				return true;
			case R.id.clearthecache:
				try
				{
					DeleteBuilder<ElviraResponse, Integer> db;
					db = getHelper().getResponseDao().deleteBuilder();
					db.setWhere(db.where().ne("_id", "-1"));
					getHelper().getResponseDao().delete(db.prepare());

				}
				catch (SQLException e)
				{
					e.printStackTrace();
				}
				Toast.makeText(this, R.string.a_gyorsitotar_torolve, Toast.LENGTH_SHORT).show();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	protected void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		{
			getActionBar().setTitle(R.string.mainactivity_title);
			getActionBar().setSubtitle(R.string.mainactivity_subtitle);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onLocationChanged(Location location) {
		double lat = location.getLatitude() * 1e6;
		double lon = location.getLongitude() * 1e6;
		LocationManager locationManager = (LocationManager) MainActivity.this.getSystemService(MainActivity.LOCATION_SERVICE);
		locationManager.removeUpdates(MainActivity.this);
		//@formatter:off
		GenericRawResults<String[]> stations;
		try
		{			
			stations = getHelper().getStationDao().queryRaw(
					"SELECT name ," +
					"(" +
					"6378.1*(cast(latitude as FLOAT)/1000000 - " + Double.toString(lat/1000000) + ") *  (cast(latitude as FLOAT) / 1000000 - " + Double.toString(lat/1000000) + ") + " +
					" "+Double.toString(Math.cos( Math.toRadians( lat/1000000 ) ) )+" * "+
					"6378.1*(cast(longitude as FLOAT)/1000000 - " + Double.toString(lon/1000000) + ") *  (cast(longitude as FLOAT) / 1000000 - " + Double.toString(lon/1000000) + ")" +
					") " +
					"from station ORDER BY " +
					"(" +
					"6378.1*(cast(latitude as FLOAT)/1000000 - " + Double.toString(lat/1000000) + ") *  (cast(latitude as FLOAT) / 1000000 - " + Double.toString(lat/1000000) + ") + " +
					" "+Double.toString(Math.cos( Math.toRadians( lat/1000000 ) ) )+" * "+
					"6378.1*(cast(longitude as FLOAT)/1000000 - " + Double.toString(lon/1000000) + ") *  (cast(longitude as FLOAT) / 1000000 - " + Double.toString(lon/1000000) + ")" +
					") LIMIT 5" );
					
			// @formatter:on
			List<String[]> f = stations.getResults();
			final ArrayList<String> st = new ArrayList<String>();
			final ArrayList<String> st2 = new ArrayList<String>();
			double meter;
			for (String[] a : f)
			{
				st.add(a[0]);
				meter = Math.sqrt(Double.parseDouble(a[1])) * 1000;
				meter = Math.round(meter / 1000);
				// meter = meter / 1000;
				st2.add(a[0]); // " (kb. "+ Double.toString( meter )+ " km)" );
			}

			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					progress.dismiss();
					AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
					builder.setTitle(R.string.kozeli_allomosok);
					builder.setItems(st2.toArray(new String[1]), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int item) {
							findView(AutoCompleteTextView.class, R.id.FromTextView).setText(st.get(item));
						}
					});
					builder.show();
				}
			});
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

	}

	@Override
	public void onProviderDisabled(String provider) {

	}

	@Override
	public void onProviderEnabled(String provider) {

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		switch (status)
		{
			case LocationProvider.OUT_OF_SERVICE:
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						progress.dismiss();
						Toast.makeText(MainActivity.this, R.string.a_hely_nem_elerheto, Toast.LENGTH_LONG).show();
					}
				});
				break;
			case LocationProvider.TEMPORARILY_UNAVAILABLE:
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						progress.dismiss();
						Toast.makeText(MainActivity.this, R.string.a_hely_atmenetileg_nem_elerheto, Toast.LENGTH_LONG).show();
					}
				});
				break;
		}
	}

}
