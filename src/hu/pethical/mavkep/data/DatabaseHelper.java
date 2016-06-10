package hu.pethical.mavkep.data;

import hu.pethical.mavkep.elvira.ElviraResponse;
import hu.pethical.mavkep.global.StationCreator;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	private static final String				DATABASE_NAME		= "mavkep.db";
	private static final int				DATABASE_VERSION	= 27;
	private Dao<Favorite, Integer>			favoriteDao			= null;
	private Dao<ElviraResponse, Integer>	responseDao			= null;
	private Dao<Station, Integer>			stationDao			= null;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public void fillStations(ProgressCallback callback) {
		Thread thread = new Thread(new StationCreator(this, callback));
		thread.start();
	}

	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connection) {
		try
		{
			TableUtils.createTable(connectionSource, Favorite.class);
			TableUtils.createTable(connectionSource, ElviraResponse.class);
			TableUtils.createTable(connectionSource, Station.class);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connection, int oldVersion, int newVersion) {
		try
		{
			TableUtils.dropTable(connectionSource, ElviraResponse.class, true);
			TableUtils.createTable(connectionSource, ElviraResponse.class);
			TableUtils.dropTable(connectionSource, Station.class, true);
			TableUtils.createTable(connectionSource, Station.class);
			TableUtils.dropTable(connectionSource, Favorite.class, true);
			TableUtils.createTable(connectionSource, Favorite.class);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	public Dao<Favorite, Integer> getFavoritesDao() throws SQLException {
		if (favoriteDao == null)
		{
			favoriteDao = getDao(Favorite.class);
		}
		return favoriteDao;
	}

	public Dao<Station, Integer> getStationDao() throws SQLException {
		if (stationDao == null)
		{
			stationDao = getDao(Station.class);
		}
		return stationDao;
	}

	public Dao<ElviraResponse, Integer> getResponseDao() throws SQLException {
		if (responseDao == null)
		{
			responseDao = getDao(ElviraResponse.class);
		}
		return responseDao;
	}

	@Override
	public void close() {
		super.close();
		favoriteDao = null;
		responseDao = null;
		stationDao = null;
	}

}
