package hu.pethical.mavkep.activities;

import hu.pethical.mavkep.R;
import hu.pethical.mavkep.data.Favorite;
import hu.pethical.mavkep.elvira.ElviraResponse;
import hu.pethical.mavkep.fragments.FavoriteFragment;
import hu.pethical.mavkep.fragments.FavoriteFragment.OnFavoriteSelectedListener;
import hu.pethical.mavkep.fragments.SearchFragment;
import hu.pethical.mavkep.fragments.SearchFragment.OnFavoriteAddedListener;
import hu.pethical.mavkep.global.PethicalFragmentActivity;

import java.sql.SQLException;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.j256.ormlite.stmt.DeleteBuilder;

public class MainActivity extends PethicalFragmentActivity implements OnFavoriteSelectedListener,
		OnFavoriteAddedListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		SearchFragment sf = (SearchFragment) getSupportFragmentManager().findFragmentById(
				R.id.search_fragment);
		sf.setOnFavoriteAddedListener(this);
		String from = getIntent().getStringExtra("from");
		String to = getIntent().getStringExtra("to");
		String via = getIntent().getStringExtra("via");
		sf.setFromToVia(from, to, via);
		FavoriteFragment favf = (FavoriteFragment) getSupportFragmentManager().findFragmentById(
				R.id.favorite_fragment);
		if (favf != null) {
			if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
				checkFavorites();
			}
			if (findViewById(R.id.favorite_fragment) != null) {
				favf.setFavoriteSelectedListener(this);
			}
			else {
				favf.setFavoriteSelectedListener(null);
			}
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			ActionBar bar = getActionBar();
			if (bar != null) {
				bar.setTitle(R.string.mainactivity_title);
				bar.setSubtitle(R.string.mainactivity_subtitle);
			}
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void checkFavorites() {
		invalidateOptionsMenu();
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void hideFavoritesMenuU(Menu menu) {
		menu.findItem(R.id.favorites).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void showFavoritesMenu(Menu menu) {
		menu.findItem(R.id.favorites).setShowAsAction(
				MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			if (getSupportFragmentManager().findFragmentById(R.id.favorite_fragment) != null
					&& findViewById(R.id.favorite_fragment) != null) {
				hideFavoritesMenuU(menu);
			}
			else {
				showFavoritesMenu(menu);
			}
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
		case R.id.favorites:
			intent = new Intent(this, FavoritesActivity.class);
			startActivity(intent);
			return true;
		case R.id.showmap:
			intent = new Intent(this, TrainMapActivity.class);
			startActivity(intent);
			return true;
		case R.id.clearthecache:
			try {
				DeleteBuilder<ElviraResponse, Integer> db;
				db = getHelper().getResponseDao().deleteBuilder();
				// db.setWhere(db.where().ne("_id", "-1"));
				getHelper().getResponseDao().delete(db.prepare());
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
			Toast.makeText(this, R.string.a_gyorsitotar_torolve, Toast.LENGTH_SHORT).show();
			return true;

		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onFavoriteSelected(Favorite favorite) {
		SearchFragment sf = (SearchFragment) getSupportFragmentManager().findFragmentById(
				R.id.search_fragment);
		if (sf != null) {
			sf.setFromToVia(favorite.departure, favorite.destination, "");
		}
		return true;
	}

	@Override
	public void onFavoriteAdded() {
		FavoriteFragment favf = (FavoriteFragment) getSupportFragmentManager().findFragmentById(
				R.id.favorite_fragment);
		if (favf != null) if (findViewById(R.id.favorite_fragment) != null) favf.refreshData();
	}
}
