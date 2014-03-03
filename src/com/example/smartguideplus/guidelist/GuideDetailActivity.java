package com.example.smartguideplus.guidelist;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartguideplus.DBContactHelper;
import com.example.smartguideplus.R;
import com.example.smartguideplus.SmartGuidePlusInfo;
import com.example.smartguideplus.model.Guide;
import com.example.smartguideplus.util.ZipUtils;
import com.example.smartguideplus.viewer.GuideViewerService;

public class GuideDetailActivity extends Activity {
	private DBContactHelper db;

	ImageView imgDetailImage;
	TextView txtDetailName;
	TextView txtDetailOS;
	TextView txtDetailModel;

	TextView txtDetailDownload;
	TextView txtDetailDate;

	TextView txtDetailDescription;

	Button btnDownload;
	Button btnGuideStart;

	private long latestId = -1;
	private boolean download = false;

	private DownloadManager downloadManager;

	private BroadcastReceiver completeReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Toast.makeText(context, "다운로드가 완료되었습니다.", Toast.LENGTH_SHORT)
					.show();
			new ConnectionTask().execute();
			
			//
			unzip();
			//
			if(download == false){
				db.addGuide(guide);
				download = true;
			}
			
			setVisibility();

		}
	};

	private void unzip(){
		File zipFile = new File("/sdcard/SmartGuidePlus/"+guide.getGidx()+"/"+guide.getGidx()+".zip") ;
		File targetDir = new File("/sdcard/SmartGuidePlus/"+guide.getGidx()+"/"+guide.getGidx()+"/");
		try {
			ZipUtils.unzip(zipFile,targetDir);
		} catch (Exception e) {
		}
	}
	
	private Guide guide;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.guide_detail);

		Bundle bundle = getIntent().getExtras();
		guide = bundle.getParcelable("guide");
		db = new DBContactHelper(getApplicationContext());

		downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

		imgDetailImage = (ImageView) findViewById(R.id.imgDetailImage);
		txtDetailName = (TextView) findViewById(R.id.txtName_Row);
		txtDetailOS = (TextView) findViewById(R.id.txtDetailOS);
		txtDetailModel = (TextView) findViewById(R.id.txtDetailModel);

		txtDetailDownload = (TextView) findViewById(R.id.txtDownload);
		txtDetailDate = (TextView) findViewById(R.id.txtDetailDate);

		txtDetailDescription = (TextView) findViewById(R.id.txtDetailDescription);

		setInfo(guide);

		btnDownload = (Button) findViewById(R.id.btnDownload);

		btnDownload.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String dirPath = "/SmartGuidePlus/" + guide.getGidx() + "/";

				String infoFile = guide.getGidx() + ".json";
				String guideFile = guide.getGidx() + ".zip";

				String downloadUrl = SmartGuidePlusInfo.baseUrl + guide.getGidx() + "/";

				Uri guideInfoUri = Uri.parse(downloadUrl + infoFile);
				Uri guideFileUri = Uri.parse(downloadUrl + guideFile);

				DownloadManager.Request infoFileRequest = new DownloadManager.Request(
						guideInfoUri);
				DownloadManager.Request guideFileRequest = new DownloadManager.Request(
						guideFileUri);

				infoFileRequest.setDestinationInExternalPublicDir(dirPath,
						infoFile);
				guideFileRequest.setDestinationInExternalPublicDir(dirPath,
						guideFile);

				Environment.getExternalStoragePublicDirectory(dirPath).mkdirs();

				downloadManager.enqueue(infoFileRequest);
				latestId = downloadManager.enqueue(guideFileRequest);
			}

		});

		btnGuideStart = (Button) findViewById(R.id.btnStartRequest);
		btnGuideStart.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String str = Environment.getExternalStorageState();
				String dirPath = "";
				if ( str.equals(Environment.MEDIA_MOUNTED)) {

					dirPath = "/sdcard/SmartGuidePlus/"+guide.getGidx()+"/"+guide.getGidx(); 
					File file = new File(dirPath); 
					if( file.exists() ) { 
						int gidx = Integer.parseInt(guide.getGidx());
						Intent intent = new Intent(GuideDetailActivity.this,
								GuideViewerService.class);
						intent.putExtra("gidx", gidx);
						startService(intent);
					}else 
					{
						downloadGuide();
						Toast.makeText(getApplicationContext(),"가이드 파일이 없어 다시 다운로드합니다.",Toast.LENGTH_SHORT).show();
					}
				}
				
				
			}
			
			private void downloadGuide() {
	            String dirPath = "/SmartGuidePlus/" + guide.getGidx() + "/";
	            Log.d("test", "path : " + dirPath);

	            String infoFile = guide.getGidx() + ".json";
	            String guideFile = guide.getGidx() + ".zip";

	            String downloadUrl = SmartGuidePlusInfo.baseUrl + guide.getGidx() + "/";

	            Uri guideInfoUri = Uri.parse(downloadUrl + infoFile);
	            Uri guideFileUri = Uri.parse(downloadUrl + guideFile);

	            DownloadManager.Request infoFileRequest = new DownloadManager.Request(
	                  guideInfoUri);
	            DownloadManager.Request guideFileRequest = new DownloadManager.Request(
	                  guideFileUri);

	            infoFileRequest.setDestinationInExternalPublicDir(dirPath,
	                  infoFile);
	            guideFileRequest.setDestinationInExternalPublicDir(dirPath,
	                  guideFile);

	            Environment.getExternalStoragePublicDirectory(dirPath).mkdirs();

	            downloadManager.enqueue(infoFileRequest);
	            latestId = downloadManager.enqueue(guideFileRequest);
	         }
		});
		
		setVisibility();
	}

	private void setVisibility() {
		if (db.isDownloaded(guide.getIdx()) == true) {
			btnDownload.setVisibility(View.GONE);
			btnGuideStart.setVisibility(View.VISIBLE);
		} else {
			btnDownload.setVisibility(View.VISIBLE);
			btnGuideStart.setVisibility(View.GONE);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		IntentFilter completeFilter = new IntentFilter(
				DownloadManager.ACTION_DOWNLOAD_COMPLETE);
		registerReceiver(completeReceiver, completeFilter);
	}

	private void setInfo(Guide guide) {
		if (!("".equals(guide.getImage()))) {
			new BitmapWorkerTask().execute(SmartGuidePlusInfo.imageUrl
					+ guide.getImage());
		}

		txtDetailName.setText(guide.getName());
		txtDetailOS.setText(guide.getOs());
		txtDetailModel.setText(String.valueOf(guide.getDevice()));

		txtDetailDownload.setText(String.valueOf(guide.getDownload()));
		txtDetailDate.setText(guide.getDate());

		txtDetailDescription.setText(guide.getDescription());

	}

	public Bitmap getBitmapFromURL(String src) {
		try {
			URL url = new URL(src);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			Bitmap myBitmap = BitmapFactory.decodeStream(input);
			return myBitmap;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {

		@Override
		protected Bitmap doInBackground(String... params) {

			return getBitmapFromURL(params[0]);
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			imgDetailImage.setImageBitmap(bitmap);
		}

	}

	class ConnectionTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(SmartGuidePlusInfo.hostUrl + "download");
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

				nameValuePairs.add(new BasicNameValuePair("idx", String.valueOf(guide.getIdx())));
				int download = guide.getDownload();
				nameValuePairs.add(new BasicNameValuePair("download", String.valueOf(++download)));

				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				HttpResponse response = httpclient.execute(httppost);
				
//				String ret = EntityUtils.toString(response.getEntity());
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		unregisterReceiver(completeReceiver);
	}
}
