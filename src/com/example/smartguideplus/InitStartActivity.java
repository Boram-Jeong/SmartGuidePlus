package com.example.smartguideplus;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;

public class InitStartActivity extends Activity {
	private static String senderId = "420349069439";
	private static String apiKey = "AIzaSyBabui03rvBsTdArIpv-kJwZ4i_PgMEUPo";

	private EditText et_name;
	private EditText et_phone;
	private EditText et_regitID;
	private Button gcm_push;
	private String regId;

	private Context mContext = null;

	static final Handler mHandler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.init_start);

		mContext = this;
		
		database_check();
		
		// SharedPrefences
		final SharedPreferences pref = this.getSharedPreferences("pref",
				MODE_PRIVATE);
		final SharedPreferences.Editor editor = pref.edit();

		et_name = (EditText) findViewById(R.id.et_name);
		et_phone = (EditText) findViewById(R.id.et_phone);
		gcm_push = (Button) findViewById(R.id.btn_gcmPush);

		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);
		regId = GCMRegistrar.getRegistrationId(this);

		// Registration
		if (regId.equals("")) {
			GCMRegistrar.register(this, senderId);
			regId = GCMRegistrar.getRegistrationId(this);
		} else {
			Log.d("@@@@", "regId already register");
		}

		// telephony manager
		TelephonyManager mTelephonyManager = (TelephonyManager) getSystemService(this.TELEPHONY_SERVICE);
		String phoneNumber = mTelephonyManager.getLine1Number();
		et_phone.setText(phoneNumber);
		et_phone.setEnabled(false);

		gcm_push.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (et_name.getText().toString().length() < 2) {
					Toast.makeText(getApplicationContext(),
							"이름을 최소 2글자 이상 적어주세요.", Toast.LENGTH_LONG).show();
				} else {
					editor.putString("pref", "init");
					editor.commit();
					regId = GCMRegistrar.getRegistrationId(mContext);
					Log.d("regit id : ", regId);
					new ConnectionTask().execute(SmartGuidePlusInfo.hostUrl
							+ "user");
				}
			}
		});

	}

	private void database_check() {
		String fileChk = SmartGuidePlusInfo.saveDir + "guiderManager";
		File file = new File(fileChk);
		if(file.exists() == true){
			file.delete();
		}
	}

	class ConnectionTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... params) {
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(params[0]);

				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

				String name = et_name.getText().toString();

				String encodedName = null;

				try {
					encodedName = new String(
							new String(name).getBytes("utf-8"), "iso-8859-1");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}

				nameValuePairs.add(new BasicNameValuePair("name", encodedName));
				nameValuePairs.add(new BasicNameValuePair("phone", et_phone
						.getText().toString()));
				nameValuePairs.add(new BasicNameValuePair("regitid", regId));

				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				// Execute HTTP Post Request
				HttpResponse response = httpclient.execute(httppost);
				String ret = EntityUtils.toString(response.getEntity());

				JSONObject result;
				String user_id = null;
				try {
					result = new JSONObject(ret);
					user_id = result.getString("user_id");
				} catch (JSONException e) {
					e.printStackTrace();
				}

				return user_id;

			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String user_id) {
			Toast.makeText(getApplicationContext(), "가입이 완료되었습니다.", Toast.LENGTH_LONG).show();
			SharedPreferences pref = mContext.getSharedPreferences("pref", MODE_PRIVATE);
			SharedPreferences.Editor editor = pref.edit();
			editor.putString("user_id", user_id);
			editor.commit();
			finish();
		}
	}
}