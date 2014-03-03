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
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.smartguideplus.R;
import com.example.smartguideplus.SmartGuidePlusInfo;
import com.example.smartguideplus.model.Request;

public class RequestDetailActivity extends Activity {
	private Request request;
	
	private EditText txtTitle_Detail;
	private EditText txtBody_Detail;
	private Button btnRequest_Edit;
	private Button btnCancel;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.request_detail);
		
		Bundle bundle = getIntent().getExtras();
		request = bundle.getParcelable("request");
		
		txtTitle_Detail = (EditText) findViewById(R.id.txtTitle_Detail);
		txtBody_Detail = (EditText) findViewById(R.id.txtBody_Detail);
		btnRequest_Edit = (Button) findViewById(R.id.btnRequest_Edit);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		
		Log.d("test", request.toString());
		
		btnRequest_Edit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new ConnectionTask().execute("edit_request");
			}
		});
		
		btnCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		setInfo(request);
	
	}
	
	private void setInfo(Request request) {
		txtTitle_Detail.setText(request.getTitle());
		txtBody_Detail.setText(request.getBody());
	}
	
	class ConnectionTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... params) {
			try {
				if (params[0].equals("edit_request")) {
					return editRequest();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		private String editRequest() throws UnsupportedEncodingException,
				IOException, ClientProtocolException, ParseException {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(SmartGuidePlusInfo.hostUrl + "editRequest");

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			
			
			String title = new String(new String(txtTitle_Detail.getText().toString()).getBytes("utf-8"), "iso-8859-1");
			String body = new String(new String(txtBody_Detail.getText().toString()).getBytes("utf-8"), "iso-8859-1");
			
			nameValuePairs.add(new BasicNameValuePair("rid", String.valueOf(request.getRid())));
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
			Toast.makeText(getApplicationContext(), "수정 완료", Toast.LENGTH_LONG).show();
			finish();
		}
	}
}
