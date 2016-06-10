package hu.pethical.mavkep.activities;

import hu.pethical.mavkep.R;
import hu.pethical.mavkep.adapters.LocalTransferAdapter;
import hu.pethical.mavkep.elvira.ElviraRequest;
import hu.pethical.mavkep.elvira.ElviraResponse;
import hu.pethical.mavkep.elvira.IElviraCallback;
import hu.pethical.mavkep.entities.LocalTravel;
import hu.pethical.mavkep.global.Container;
import hu.pethical.mavkep.global.Jni;
import hu.pethical.mavkep.global.PethicalActivity;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class LocalTransferActivity extends PethicalActivity implements IElviraCallback {

	private static String url;
	private static String From = null;
	private static String To = null;
	private static String When = null;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_local_transfer);
		Intent intent = getIntent();
		From = intent.getStringExtra("from");
		To = intent.getStringExtra("to");
		When = intent.getStringExtra("when");
		url = String.format(Jni.GetLocalTransportUrl(), URLEncoder.encode(From),
				URLEncoder.encode(To), When);
		((ExpandableListView) findViewById(R.id.local)).setGroupIndicator(null);
		findViewById(R.id.localProgress).setVisibility(ProgressBar.VISIBLE);
		setupActionBar();
		// Thread thread = new Thread(this);
		// thread.start();
		ElviraRequest request = new ElviraRequest(this);
		request.Request(url, this, 0);
	}

	@Override
	public String getSubTitle() {
		if (From == null) {
			From = getIntent().getStringExtra("from");
			To = getIntent().getStringExtra("to");
			When = getIntent().getStringExtra("when");
		}
		return String.format("%s - %s", From, To);
	}

	private void parseResponse(ElviraResponse eresponse) {
		String response = eresponse.getResponse();
		ObjectMapper mapper = Container.getMapper();
		ArrayList<LocalTravel> list;
		try {
			// @formatter:off
			list = mapper.readValue(response, new TypeReference<List<LocalTravel>>() {
			});
			// @formatter:on

			final LocalTransferAdapter adapter = new LocalTransferAdapter(list,
					LocalTransferActivity.this);
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					findViewById(R.id.localProgress).setVisibility(ProgressBar.GONE);
					((ExpandableListView) findViewById(R.id.local)).setAdapter(adapter);
					ExpandableListView listView = (ExpandableListView) findViewById(R.id.local);
					int count = adapter.getGroupCount();
					for (int position = 1; position <= count; position++)
						listView.expandGroup(position - 1);
				}
			});
		}
		catch (JsonParseException e) {
			e.printStackTrace();
		}
		catch (JsonMappingException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.local_transfer, menu);
		return true;
	}

	@Override
	public void RequestDone(ElviraResponse response) {
		parseResponse(response);
	}
}
