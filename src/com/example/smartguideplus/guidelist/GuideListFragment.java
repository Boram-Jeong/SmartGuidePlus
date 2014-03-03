package com.example.smartguideplus.guidelist;

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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.smartguideplus.R;
import com.example.smartguideplus.SmartGuidePlusInfo;
import com.example.smartguideplus.model.Guide;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

@SuppressLint("ValidFragment")
public class GuideListFragment extends Fragment{
	Context mContext;
	
	private ListView guideList;
	
	ArrayList<Guide> guides;
	ArrayList<Guide> searchGuides;

	private GuideListAdapter adapter;

	private EditText txtSearch;

	private Button btnSearch;
	
	private boolean mLockListView;

	private View footerView;
	
	private int type;
	
	public GuideListFragment(Context context) {
		mContext = context;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, 
		ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.guide_list, null);

		// initial member variable
		mLockListView = true;
		guides = new ArrayList<Guide>();
		
		
		guideList = (ListView) view.findViewById(R.id.guideList);
		guideList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(getActivity(), GuideDetailActivity.class);
				Guide guide;
				if(type == 0){
					guide = guides.get(position);
				}else{
					guide = searchGuides.get(position);
				}
				intent.putExtra("guide", guide);
				startActivity(intent);
			}
		});
		
		footerView = inflater.inflate(R.layout.guide_footer, null);
		
		guideList.addFooterView(footerView);
		
		guideList.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
			    int count = totalItemCount - visibleItemCount;
			    
			    if(firstVisibleItem >= count && totalItemCount != 0 && mLockListView == false)
			    {
			    	getGuideList();
			    }  
			
			}
		});
		
		adapter = new GuideListAdapter(getActivity().getApplicationContext(), R.layout.guide_row, guides);
		guideList.setAdapter(adapter);
		
		txtSearch = (EditText) view.findViewById(R.id.txtSearch);
		txtSearch.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				if(s.length() == 0){
					adapter = new GuideListAdapter(getActivity().getApplicationContext(), R.layout.guide_row, guides);
					guideList.setAdapter(adapter);	
					type = 0;
				}
			}
		});
		
		
		btnSearch = (Button) view.findViewById(R.id.btnSearch);
		
		btnSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String guideName = txtSearch.getText().toString();
				new ConnectionTask().execute("search", guideName);
			}
		});
		
    	return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		getGuideList();
	}
	
	private void getGuideList() {
		new ConnectionTask().execute("guides");
	}
	
	class ConnectionTask extends AsyncTask<String, Void, String> {
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}
		
		@Override
		protected String doInBackground(String... params) {
			try {
				if(params[0].equals("guides")){
					return getGuides();
				}else if(params[0].equals("search")){
					return getGuidesByName(params[1]);
				}else if(params[0].equals("reset")){
					return getGuidesByName(params[1]);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		private String getGuides()
				throws UnsupportedEncodingException, IOException,
				ClientProtocolException, ParseException {
			
			type = 0;
			mLockListView = true;
			
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(SmartGuidePlusInfo.hostUrl + "guides");
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			
			nameValuePairs.add(new BasicNameValuePair("os", SmartGuidePlusInfo.OS));
			nameValuePairs.add(new BasicNameValuePair("device", SmartGuidePlusInfo.MODEL));
			nameValuePairs.add(new BasicNameValuePair("limit", String.valueOf(guides.size())));
			
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);

			String ret = EntityUtils.toString(response.getEntity());
			return ret;
		}
		
		private String getGuidesByName(String guideName)
				throws UnsupportedEncodingException, IOException,
				ClientProtocolException, ParseException {
			
			type = 1;
			
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(SmartGuidePlusInfo.hostUrl + "search");
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			
			nameValuePairs.add(new BasicNameValuePair("os", SmartGuidePlusInfo.OS));
			nameValuePairs.add(new BasicNameValuePair("device", SmartGuidePlusInfo.MODEL));
			nameValuePairs.add(new BasicNameValuePair("name", "%" + guideName + "%"));
			
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf-8"));

			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);

			String ret = EntityUtils.toString(response.getEntity());
			return ret;
		}
		
		@Override
		protected void onPostExecute(String result) {
			switch (type) {
			case 0:
				
				//ArrayList<Guide> newGuide = new Gson().fromJson(result, new TypeToken<ArrayList<Guide>>(){}.getType());
				
				ArrayList<Guide> newGuide = new ArrayList<Guide>();
				JsonParser parser = new JsonParser();

				JsonArray array = parser.parse(result).getAsJsonArray();
				
				Guide guide;
				for(JsonElement element : array)
				{
					guide = new Gson().fromJson(element, Guide.class);
					newGuide.add(guide);
				}
				
				guides.addAll(newGuide);
				adapter.notifyDataSetChanged();
				if(newGuide.size() < 1){
					guideList.removeFooterView(footerView);
				}
				mLockListView = false;
				break;
			case 1:
				searchGuides = new Gson().fromJson(result, new TypeToken<ArrayList<Guide>>(){}.getType());
				adapter = new GuideListAdapter(getActivity().getApplicationContext(), R.layout.guide_row, searchGuides);
				guideList.setAdapter(adapter);
				break;
			}
		}
	}
}

