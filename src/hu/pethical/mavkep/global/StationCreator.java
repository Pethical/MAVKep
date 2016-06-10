package hu.pethical.mavkep.global;

import hu.pethical.mavkep.data.DatabaseHelper;
import hu.pethical.mavkep.data.ProgressCallback;
import hu.pethical.mavkep.data.Station;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.concurrent.Callable;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import android.util.Log;

import com.j256.ormlite.dao.Dao;

public class StationCreator implements Runnable {
	private final DatabaseHelper helper;
	private final ProgressCallback callback;

	public StationCreator(DatabaseHelper helper, ProgressCallback callback) {
		this.helper = helper;
		this.callback = callback;
	}

	private HttpResponse downloadUrl(String url) {
		/*
		 * HttpParams params = new BasicHttpParams();
		 * HttpConnectionParams.setConnectionTimeout(params, 30000);
		 * HttpConnectionParams.setSoTimeout(params, 50000);
		 */
		HttpClient client = Container.getHttpClient();
		// = new DefaultHttpClient(params);
		HttpGet httpGet = new HttpGet(url);
		httpGet.setHeader("accept", "application/json");
		try {
			HttpResponse response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) {
				return response;
			}
			else {
				throw new IOException("Failed to load data from server.");
			}
		}
		catch (UnknownHostException e) {
			Log.e("HTTP", "Unknown host");
			e.printStackTrace();
		}
		catch (ClientProtocolException e) {
			Log.e("HTTP", "Proto error");
			e.printStackTrace();
		}
		catch (IOException e) {
			Log.e("HTTP", "IO Error");
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void run() {
		String url = Jni.GetStations();
		if (callback != null) callback.onStart(8000);
		final HttpResponse response = downloadUrl(url);
		try {
			final Dao<Station, Integer> dao = helper.getStationDao();
			dao.callBatchTasks(new Callable<Object>() {
				@Override
				public Object call() throws Exception {
					long current = 0;
					InputStream is = response.getEntity().getContent();
					BufferedReader reader = new BufferedReader(new InputStreamReader(is), 8192);
					String statement;
					long all = 7700;
					while ((statement = reader.readLine()) != null) {
						dao.executeRaw(statement);
						current++;
						if (current % 100 == 0)
							if (callback != null) callback.onProgress(current, all);
					}
					reader.close();
					response.getEntity().consumeContent();
					return null;
				}
			});
			if (callback != null) callback.onDone();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
