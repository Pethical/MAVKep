package hu.pethical.mavkep.elvira;

import hu.pethical.mavkep.data.DatabaseHelper;
import hu.pethical.mavkep.global.Constants;
import hu.pethical.mavkep.global.Container;

import java.io.IOException;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import android.app.Activity;
import android.util.Log;

import com.j256.ormlite.stmt.DeleteBuilder;

public class ElviraRequest implements Runnable {

	private String Url;
	private IElviraCallback callback;
	public boolean cacheOk = true;
	public int maxLifeTime = 120;
	protected final Activity context;
	protected Thread thread = null;

	public ElviraRequest(Activity context) {
		this.context = context;
	}

	public void Request(String url, IElviraCallback callback, int tries) {
		Url = url;
		this.callback = callback;
		thread = new Thread(this);
		if (Constants.DEBUG) Log.i("REQUEST", "Thread created");
		thread.start();
	}

	public void Request(String url, IElviraCallback callback, int tries, boolean cacheok,
			int maxLifeTime) {
		this.maxLifeTime = maxLifeTime;
		cacheOk = cacheok;
		Request(url, callback, tries);
	}

	public ElviraResponse getFromCache(String url, DatabaseHelper dbHelper) {
		ElviraResponse last = null;
		try {
			List<ElviraResponse> responses = dbHelper.getResponseDao().queryForEq("url", url);
			int c = responses.size();
			if (c == 0) {
				return null;
			}
			last = responses.get(c - 1);
			last.isfromcache = true;
			if (last.getEntityAge() < 120) {
				return last;
			}
		}
		catch (SQLException e1) {
			e1.printStackTrace();
		}
		return last;
	}

	public HttpResponse getResponseFromServer() {
		if (Constants.DEBUG) Log.i("HTTP", "Creating client");
		HttpClient client = Container.getHttpClient();
		HttpGet httpGet = new HttpGet(Url);
		httpGet.setHeader("accept", "application/json");
		try {
			if (Constants.DEBUG) Log.i("HTTP", "Exec it");
			HttpResponse response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				if (Constants.DEBUG) Log.i("HTTP", "Done");
				return response;
			}
			else if (statusCode == 400) {
				return response;
			}
			else {
				throw new IOException("Failed to load data from server.");
			}
		}
		catch (UnknownHostException e) {
			if (Constants.DEBUG) {
				Log.e("HTTP", "Unknown host");
				e.printStackTrace();
			}
		}
		catch (ClientProtocolException e) {
			if (Constants.DEBUG) {
				Log.e("HTTP", "Proto error");
				e.printStackTrace();
			}
		}
		catch (IOException e) {
			if (Constants.DEBUG) {
				Log.e("HTTP", "IO Error");
				e.printStackTrace();
			}
		}
		catch (IllegalStateException e) {
			if (Constants.DEBUG) {
				Log.e("HTTP", "Illegal state");
				e.printStackTrace();
			}
		}
		catch (Exception e) {
			if (Constants.DEBUG) {
				Log.e("HTTP", "Unknown error");
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public void run() {
		ElviraResponse last = null;
		if (cacheOk) {
			if (Constants.DEBUG) Log.i("REQUEST", "Check cache");
			if ((last = getFromCache(Url, Container.getHelper(context))) != null) {
				if (last.getEntityAge() < maxLifeTime) {
					callback.RequestDone(last);
					return;
				}
			}
		}
		if (Constants.DEBUG) Log.i("REQUEST", "Response from server");
		HttpResponse response = getResponseFromServer();
		if (Constants.DEBUG) Log.i("REQUEST", "Response from server end");
		try {
			if ((response != null) || (last == null)) {
				last = new ElviraResponse(response);
				last.url = Url;
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		if (last.getException() == null) {
			saveResponse(last);
		}
		callback.RequestDone(last);
	}

	private void saveResponse(final ElviraResponse resp) {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				DeleteBuilder<ElviraResponse, Integer> db;
				try {
					db = Container.getHelper(context).getResponseDao().deleteBuilder();
					db.setWhere(db.where().eq("url", Url));
					Container.getHelper(context).getResponseDao().delete(db.prepare());
					resp.time = System.currentTimeMillis() / 1000L;
					Container.getHelper(context).getResponseDao().create(resp);
				}
				catch (SQLException e) {
					e.printStackTrace();
				}
				catch (NullPointerException e) {
					e.printStackTrace();
				}
			}
		});
		thread.start();
	}
}
