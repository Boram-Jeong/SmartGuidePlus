package com.example.smartguideplus.mylist;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.smartguideplus.R;
import com.example.smartguideplus.SmartGuidePlusInfo;

public class AddRequestActivity extends Activity {
	private EditText txtTitle_Add;
	private EditText txtBody_Add;
	private Button btnRequest_Add;
	private Button btnCancel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_request);

		txtTitle_Add = (EditText) findViewById(R.id.txtTitle_Detail);
		txtBody_Add = (EditText) findViewById(R.id.txtBody_Detail);
		btnRequest_Add = (Button) findViewById(R.id.btnRequest_Edit);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		
		btnCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		btnRequest_Add.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new ConnectionTask().execute("add_request");
			}
		});
	}

	class ConnectionTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... params) {
			try {
				if (params[0].equals("add_request")) {
					return addRequest();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		private String addRequest() throws UnsupportedEncodingException,
				IOException, ClientProtocolException, ParseException {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(SmartGuidePlusInfo.hostUrl + "request");

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			
			String title = new String(new String(txtTitle_Add.getText().toString()).getBytes("utf-8"), "iso-8859-1");
			String body = new String(new String(txtBody_Add.getText().toString()).getBytes("utf-8"), "iso-8859-1");
			
			nameValuePairs.add(new BasicNameValuePair("user_id", SmartGuidePlusInfo.getUserID(getApplicationContext())));
			nameValuePairs.add(new BasicNameValuePair("title", title));
			nameValuePairs.add(new BasicNameValuePair("body", body));
			
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);
			
			String ret = EntityUtils.toString(response.getEntity());
			
//			Log.d("test", "result : " + ret);

			return ret;
		}
		
		@Override
		protected void onPostExecute(String result) {
			Toast.makeText(getApplicationContext(), "등록 완료", Toast.LENGTH_LONG).show();
			finish();
		}
	}
}
