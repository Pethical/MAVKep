package hu.pethical.mavkep.global;

import hu.pethical.mavkep.data.DatabaseHelper;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.app.Activity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.j256.ormlite.android.apptools.OpenHelperManager;

public final class Container {

	private static ObjectMapper objectMapper = null;
	private static HttpClient httpClient = null;

	public static DatabaseHelper getHelper(Activity context) {
		return OpenHelperManager.getHelper(context, DatabaseHelper.class);
	}

	public static ObjectMapper getMapper() {
		if (objectMapper == null) objectMapper = new ObjectMapper();
		return objectMapper;
	}

	public static HttpClient getHttpClient() {
		if (httpClient == null) {
			String version = Constants.version;
			String user_agent = android.os.Build.MANUFACTURER + " " + android.os.Build.MODEL + ";"
					+ version;

			HttpParams params = new BasicHttpParams();
			params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
			HttpConnectionParams.setConnectionTimeout(params, 30000);
			HttpConnectionParams.setSoTimeout(params, 50000);
			HttpConnectionParams.setTcpNoDelay(params, true);
			params.setParameter(CoreProtocolPNames.USER_AGENT, user_agent);
			httpClient = new DefaultHttpClient(params);
		}
		return httpClient;
	}
}
