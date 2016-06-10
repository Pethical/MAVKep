package hu.pethical.mavkep.elvira;

import hu.pethical.mavkep.global.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import android.util.Log;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "elviraresponse")
public class ElviraResponse {

	public Exception	exception	= null;

	@DatabaseField(generatedId = true, columnName = "_id")
	public int			id;

	@DatabaseField(dataType = DataType.LONG_STRING, columnName = "url")
	public String		url;

	@DatabaseField(columnName = "response", dataType = DataType.LONG_STRING)
	public String		response	= null;

	public boolean		isfromcache	= false;

	@DatabaseField(columnName = "time")
	public long			time		= 0;

	public ElviraResponse() {

	}

	public ElviraResponse(HttpResponse response) throws IOException {
		StringBuilder builder = new StringBuilder();
		if (response == null)
		{
			this.exception = new NullPointerException("Response is null");
			return;
		}
		HttpEntity entity = response.getEntity();
		InputStream content;
		try
		{
			content = entity.getContent();
		}
		catch (IllegalStateException e)
		{
			e.printStackTrace();
			this.exception = e;
			return;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			this.exception = e;
			return;
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(content), 8192);
		String line;
		while ((line = reader.readLine()) != null)
		{
			builder.append(line);
		}
		reader.close();
		if (response.getStatusLine().getStatusCode() == 400)
		{
			this.exception = new ElviraException(builder.toString());
			this.response = builder.toString();
			response.getEntity().consumeContent();
			return;
		}
		response.getEntity().consumeContent();
		this.response = builder.toString();
		this.exception = null;
		time = System.currentTimeMillis() / 1000L;
		if (Constants.DEBUG) Log.i("PARSER", "Done Parsing");
	}

	public ElviraResponse(String response, Exception exception) {
		this.response = response;
		this.exception = exception;
		time = System.currentTimeMillis() / 1000L;
	}

	public Boolean getIsFromCache() {
		return isfromcache;
	}

	public void setIsFromCache(boolean fromcache) {
		this.isfromcache = fromcache;
	}

	public long getEntityAge() {
		return System.currentTimeMillis() / 1000L - time;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public Exception getException() {
		return exception;
	}

	public void setException(Exception exception) {
		this.exception = exception;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}
}
