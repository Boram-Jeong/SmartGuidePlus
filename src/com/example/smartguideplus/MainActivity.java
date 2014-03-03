package com.example.smartguideplus;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.widget.Toast;

import com.example.smartguideplus.guidelist.GuideDetailActivity;
import com.example.smartguideplus.guidelist.GuideListFragment;
import com.example.smartguideplus.guidemaker.GuideMakerFragment;
import com.example.smartguideplus.model.FilePush;
import com.example.smartguideplus.model.Guide;
import com.example.smartguideplus.mylist.MyListFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class MainActivity extends FragmentActivity implements
		ActionBar.TabListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	private int type = 0;
	private FilePush selectedFilePush;

	// handler GCM 실행하기 위함
	static final Handler mHandler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		viewRotateSetting();
		checkInitStart();
		
		new ConnectionTask().execute("check");

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
	}

	private void checkInitStart() {
		SharedPreferences pref = this
				.getSharedPreferences("pref", MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		String str = pref.getString("pref", "");
		if (str.equals("")) {
			Intent intent = new Intent(getApplicationContext(),
					InitStartActivity.class);
			startActivity(intent);
		}
	}

	// 자동회전 막기
	private void viewRotateSetting()
	{
		try{
			if  (android.provider.Settings.System.getInt(getContentResolver(),Settings.System.ACCELEROMETER_ROTATION, 0) == 1){
				android.provider.Settings.System.putInt(getContentResolver(),Settings.System.ACCELEROMETER_ROTATION, 0);
			}
			/*else{
				android.provider.Settings.System.putInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 1);
			}*/
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			// Fragment fragment = new DummySectionFragment();
			// Bundle args = new Bundle();
			// args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position +
			// 1);
			// fragment.setArguments(args);

			Fragment fragment = null;

			switch (position) {
			case 0:
				fragment = new GuideListFragment(getApplicationContext());
				break;
			case 1:
				fragment = new GuideMakerFragment(getApplicationContext());
				break;
			case 2:
				fragment = new MyListFragment(getApplicationContext());
			default:
				break;
			}

			return fragment;
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);
			}
			return null;
		}
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
				if (params[0].equals("check")) {
					type = 0;
					return check();
				}else if (params[0].equals("guide")) {
					type = 1;
					return getGuideByGidx();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		private String check() throws UnsupportedEncodingException,
		IOException, ClientProtocolException, ParseException {
			HttpClient client = new DefaultHttpClient();
			HttpGet get = new HttpGet(SmartGuidePlusInfo.hostUrl + "check/"
					+ SmartGuidePlusInfo.getUserID(getApplicationContext()));
			HttpResponse response = client.execute(get);
			String ret = EntityUtils.toString(response.getEntity());
			
			return ret;
		}
		
		private String getGuideByGidx() throws UnsupportedEncodingException,
		IOException, ClientProtocolException, ParseException {
			HttpClient client = new DefaultHttpClient();
			HttpGet get = new HttpGet(SmartGuidePlusInfo.hostUrl + "guide/" + selectedFilePush.getGidx());
			HttpResponse response = client.execute(get);
			String ret = EntityUtils.toString(response.getEntity());
		
			return ret;
		}

		@Override
		protected void onPostExecute(String result) {
			switch (type) {
			case 0:
				try{
				
					ArrayList<FilePush> filePushes = new Gson().fromJson(result, new TypeToken<ArrayList<FilePush>>(){}.getType());
					
					if(filePushes.size() > 0){
						selectedFilePush = filePushes.get(0);
						new ConnectionTask().execute("guide");
					}
				}catch(Exception e){
					e.printStackTrace();
				}

				break;
			case 1:
				Guide guide = new Gson().fromJson(result, Guide.class);
				Intent intent = new Intent(getApplicationContext(), GuideDetailActivity.class);
				intent.putExtra("guide", guide);
				startActivity(intent);
			}
		}
	}
}
