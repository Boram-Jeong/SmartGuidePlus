package com.example.smartguideplus.mylist;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

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
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.smartguideplus.R;
import com.example.smartguideplus.SmartGuidePlusInfo;
import com.example.smartguideplus.model.Guide;
import com.example.smartguideplus.model.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class UserListActivity extends Activity {
	private ListView userList;
	private UserListAdapter userListAdapter;
	private ArrayList<User> users;

	private ArrayList<User> allUsers;
	private HashMap<String, String> contactList;

	private ProgressBar loadingUserList;
	
	private User selectedUser;
	
	private int type = 0;
	
	private Guide guide;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_list);
		
		Bundle bundle = getIntent().getExtras();
		guide = bundle.getParcelable("guide");
		
		new ConnectionTask().execute("users");

		loadingUserList = (ProgressBar) findViewById(R.id.loadingUserList);

		userList = (ListView) findViewById(R.id.userList);
		userList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				selectedUser = users.get(position);
//				Log.d("test", "selected " + selectedPhone);
				
				AlertDialog.Builder alert_confirm = new AlertDialog.Builder(UserListActivity.this);
				alert_confirm.setMessage("가이드를 보내겠습니까?").setCancelable(false).setPositiveButton("확인",
				new DialogInterface.OnClickListener() {
				    @Override
				    public void onClick(DialogInterface dialog, int which) {
				    	new ConnectionTask().execute("push");
				    	finish();
				    }
				}).setNegativeButton("취소",
				new DialogInterface.OnClickListener() {
				    @Override
				    public void onClick(DialogInterface dialog, int which) {
				        // 'No'
				    return;
				    }
				});
				AlertDialog alert = alert_confirm.create();
				alert.show();
			}
		});
		
	}

	private HashMap<String, String> getContactList() {

		Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

		String[] projection = new String[] {
				ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
				ContactsContract.CommonDataKinds.Phone.NUMBER,
				ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME };

		String[] selectionArgs = null;

		String sortOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
				+ " COLLATE LOCALIZED ASC";

		Cursor contactCursor = managedQuery(uri, projection, null,
				selectionArgs, sortOrder);

		HashMap<String, String> contactlist = new HashMap<String, String>();

		String phonenumber, name;
		if (contactCursor.moveToFirst()) {
			do {
				phonenumber = contactCursor.getString(1).replaceAll("-", "");
				name = contactCursor.getString(2);

				contactlist.put(phonenumber, name);
			} while (contactCursor.moveToNext());
		}

		return contactlist;

	}

	class ConnectionTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... params) {
			try {
				if (params[0].equals("users")) {
					type = 0;
					return getUsers();
				}else if(params[0].equals("push")){
					type = 1;
					sendPush();
					return insertPush();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		private String getUsers() throws UnsupportedEncodingException,
				IOException, ClientProtocolException, ParseException {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(SmartGuidePlusInfo.hostUrl + "users");

			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);

			String ret = EntityUtils.toString(response.getEntity());

			return ret;
		}
		
		private String sendPush() throws UnsupportedEncodingException,
		IOException, ClientProtocolException, ParseException {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(SmartGuidePlusInfo.hostUrl + "sendGCM");
		
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			
			nameValuePairs.add(new BasicNameValuePair("user_id", SmartGuidePlusInfo.getUserID(getApplicationContext())));
			nameValuePairs.add(new BasicNameValuePair("phone", selectedUser.getPhone()));
			
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		
			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);
			
			String ret = EntityUtils.toString(response.getEntity());
			
		//	Log.d("test", "result : " + ret);
		
			return ret;
		}
		
		private String insertPush() throws UnsupportedEncodingException,
		IOException, ClientProtocolException, ParseException {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(SmartGuidePlusInfo.hostUrl + "push");
		
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			
			nameValuePairs.add(new BasicNameValuePair("sender", SmartGuidePlusInfo.getUserID(getApplicationContext())));
			nameValuePairs.add(new BasicNameValuePair("receiver", selectedUser.getUser_id()));
			nameValuePairs.add(new BasicNameValuePair("gidx", guide.getGidx()));
			
			SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat ( "yyyy.MM.dd HH:mm:ss", Locale.KOREA );
			Date currentTime = new Date ( );
			String mTime = mSimpleDateFormat.format ( currentTime );
			
			nameValuePairs.add(new BasicNameValuePair("time", mTime));
			
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		
			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);
			
			String ret = EntityUtils.toString(response.getEntity());
			
			return ret;
		}


		@Override
		protected void onPostExecute(String result) {
			
			switch (type) {
			case 0:
				allUsers = new Gson().fromJson(result,
						new TypeToken<ArrayList<User>>() {
						}.getType());
				contactList = getContactList();

				findRegisteredUsers();

				loadingUserList.setVisibility(View.GONE);
				break;

			default:
				break;
			}
		}

		private void findRegisteredUsers() {
			users = new ArrayList<User>();
			for (int index = 0; index < allUsers.size(); index++) {
				if (contactList.get(allUsers.get(index).getPhone()) != null) {
					users.add(allUsers.get(index));
				}
			}

			userListAdapter = new UserListAdapter(getApplicationContext(),
					R.layout.user_row, users);
			userList.setAdapter(userListAdapter);
		}
	}

}
