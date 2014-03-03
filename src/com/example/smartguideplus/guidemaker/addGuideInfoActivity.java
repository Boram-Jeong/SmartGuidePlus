package com.example.smartguideplus.guidemaker;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.smartguideplus.DBContactHelper;
import com.example.smartguideplus.R;
import com.example.smartguideplus.SmartGuidePlusInfo;
import com.example.smartguideplus.maker.GuideMakerService;
import com.example.smartguideplus.model.Guide;
import com.example.smartguideplus.model.Request;
import com.example.smartguideplus.util.Data;
import com.example.smartguideplus.util.RootHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class addGuideInfoActivity extends Activity {
	private DBContactHelper db;
	private EditText txtGuideName_Add;

	private LinearLayout requestLayout;
	private EditText txtRequestBody;
	private CheckBox checkRequest;

	private Spinner spinRequests;

	private EditText txtModel_Add;
	private EditText txtAnroidOS_Add;
	private EditText txt_Description_Add;
	private EditText txtFileName_Add;
	private Button btnStartMakeGuide;
	private Button btnStartRequest;
	private ImageView imgCameraOption;
	private Dialog dialog;
	
	private Guide guide;
	private int type = 0;
	
	private static final String TEMP_PHOTO_FILE = "temp.jpg";
	private static final int REQ_CODE_PICK_IMAGE = 0;

	private ArrayList<Request> requests;
	RequestListAdapter requestListAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_guide);
		db = new DBContactHelper(getApplicationContext());
		new ConnectionTask().execute("requests");

		
		txtGuideName_Add = (EditText) findViewById(R.id.txtGuideName_Add);

		checkRequest = (CheckBox) findViewById(R.id.checkRequest);

		requestLayout = (LinearLayout) findViewById(R.id.requestLayout);
		spinRequests = (Spinner) findViewById(R.id.spinRequests);
		txtRequestBody = (EditText) findViewById(R.id.txtRequestBody);

		txtModel_Add = (EditText) findViewById(R.id.txtModel_Add);
		txtAnroidOS_Add = (EditText) findViewById(R.id.txtAndroidOS_Add);
		txt_Description_Add = (EditText) findViewById(R.id.txtDescription_Add);
		txtFileName_Add = (EditText)findViewById(R.id.txtFileName_Add);
		btnStartMakeGuide = (Button) findViewById(R.id.btnStartMakeGuide);
		btnStartRequest = (Button) findViewById(R.id.btnStartRequest);
		
		imgCameraOption = (ImageView) findViewById(R.id.ImgGuide_Add);

		txtModel_Add.setHint(SmartGuidePlusInfo.MODEL);
		txtAnroidOS_Add.setHint(SmartGuidePlusInfo.OS);

		
		if (checkRequest.isChecked() == true) {
			requestLayout.setVisibility(View.VISIBLE);
		} else {
			requestLayout.setVisibility(View.GONE);
		}

		checkRequest.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					requestLayout.setVisibility(View.VISIBLE);
				} else {
					requestLayout.setVisibility(View.GONE);
				}
			}
		});
		
		spinRequests.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override

			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				txtRequestBody.setText(requests.get(position).getBody());
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			
			}
		});

		btnStartMakeGuide.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(RootHelper.execAsRoot()) {
					
					if(txtGuideName_Add.getText().toString().equals("") || 
						txt_Description_Add.getText().toString().equals((null))){
						Toast.makeText(addGuideInfoActivity.this, "가이드 이름이 등록되지 않았습니다.", Toast.LENGTH_LONG).show();
						return;
					}
					else if(txt_Description_Add.getText().toString().equals("") ||
						txt_Description_Add.getText().toString().equals(null)) {
						Toast.makeText(addGuideInfoActivity.this, "내용을 적어주세요", Toast.LENGTH_LONG).show();
						return;
					}
					else {
						int gidx = (int)System.currentTimeMillis();
						Intent intent = new Intent(addGuideInfoActivity.this,
								GuideMakerService.class);
						Data.name = txtGuideName_Add.getText().toString();
						Data.model = txtModel_Add.getText().toString();
						Data.os = txtAnroidOS_Add.getText().toString();
						Data.gidx = gidx;
						Data.desc = txt_Description_Add.getText().toString();
						Data.activity = addGuideInfoActivity.this;
						startService(intent);
					}
				}else {
					Toast.makeText(getApplicationContext(), "루트 권한이 필요합니다.", Toast.LENGTH_LONG).show();
				}
				
			}
		});
		
		btnStartRequest.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				if(txtGuideName_Add.getText().toString().equals("") || 
					txt_Description_Add.getText().toString().equals((null))){
					Toast.makeText(addGuideInfoActivity.this, "가이드 이름이 등록되지 않았습니다.", Toast.LENGTH_LONG).show();
					return;
				}
				else if(txt_Description_Add.getText().toString().equals("") ||
						txt_Description_Add.getText().toString().equals(null)) {
					Toast.makeText(addGuideInfoActivity.this, "내용을 적어주세요", Toast.LENGTH_LONG).show();
					return;
				}else if(txtFileName_Add.getText().toString().equals("") ||
						txtFileName_Add.getText().toString().equals(null) ||
						txtFileName_Add.getText().toString().equals("0")) {
					Toast.makeText(addGuideInfoActivity.this, "가이드 파일이 등록되지 않았습니다.", Toast.LENGTH_LONG).show();
					return;
				}
				else {
					setGuideInfo();
					new ConnectionTask().execute("upload");
				}
			}

			private void setGuideInfo() {
				
				
				
				guide = new Guide();
				String gidx = txtFileName_Add.getText().toString();
				guide.setCreator(SmartGuidePlusInfo.getUserID(getApplicationContext()));
				
				SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat ( "yyyy.MM.dd HH:mm:ss", Locale.KOREA );
				Date currentTime = new Date ( );
				String mTime = mSimpleDateFormat.format ( currentTime );
				
				guide.setDate(mTime);
				guide.setGidx(String.valueOf(Data.gidx));
				
				String name = txtGuideName_Add.getText().toString();
				String encodedName = null;
				try {
					encodedName = new String(new String(name).getBytes("utf-8"), "iso-8859-1");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				guide.setName(encodedName);
				guide.setImage(gidx);
				guide.setOs(SmartGuidePlusInfo.OS);
				guide.setDevice(SmartGuidePlusInfo.MODEL);

				String description = txt_Description_Add.getText().toString();
				String encodedDescription = null;
				try {
					encodedDescription = new String(new String(description).getBytes("utf-8"), "iso-8859-1");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				guide.setDescription(encodedDescription);
				guide.setDownload(0);
			}
		});
		
		/** 갤러리 가져오기 **/
		try{
			imgCameraOption.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dialog = new Dialog(addGuideInfoActivity.this, 0);
					
					dialog.setContentView(R.layout.camera_option);						
					dialog.setTitle("사진");						
					dialog.show();
					
					Button gallery = (Button) dialog.findViewById(R.id.bt_gallery);
					Button del = (Button) dialog.findViewById(R.id.bt_del);
				
					gallery.setBackgroundColor(Color.alpha(0));
					del.setBackgroundColor(Color.alpha(0));
					
					//이미지 로드
					gallery.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							
							Intent intent = new Intent(
	                        Intent.ACTION_GET_CONTENT,      // 또는 ACTION_PICK
	                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			                intent.setType("image/*");              // 모든 이미지
			                intent.putExtra("crop", "true");        // Crop기능 활성화
			                intent.putExtra(MediaStore.EXTRA_OUTPUT, getTempUri());     // 임시파일 생성
			                intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());  // 포맷방식	                					
			 
			                startActivityForResult(intent, REQ_CODE_PICK_IMAGE);
							//Toast.makeText(getActivity(), "카메라앨범.", Toast.LENGTH_LONG).show();							
						}
					});
				
					//이미지 제거
					del.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							 ImageView imgCameraOption = (ImageView)findViewById(R.id.ImgGuide_Add);
			                 imgCameraOption.setImageResource(R.drawable.ic_launcher);
			                
			                 dialog.dismiss();
						}
					});
				}
			});
		}catch(Exception e){};

	
		Intent intent = new Intent(addGuideInfoActivity.this, GuideMakerService.class);
	
		txtGuideName_Add.setText(Data.name);
		txtModel_Add.setText(Data.model);
		txt_Description_Add.setText(Data.desc);
		txtAnroidOS_Add.setText(Data.os);
		txtFileName_Add.setText(Integer.toString(Data.gidx));
		stopService(intent);
	}
	private Uri getTempUri() {
	    return Uri.fromFile(getTempFile());
	}
	
	/** 외장메모리에 임시 이미지 파일을 생성하여 그 파일의 경로를 반환  */
	private File getTempFile() {
	    if (isSDCARDMOUNTED()) {
	        File f = new File(Environment.getExternalStorageDirectory(), // 외장메모리 경로
	                TEMP_PHOTO_FILE);
	        try {
	            f.createNewFile();      // 외장메모리에 temp.jpg 파일 생성
	        } catch (IOException e) {
	        }
	
	        return f;
	    } else
	        return null;
	}
	/** SD카드가 마운트 되어 있는지 확인 */
	private boolean isSDCARDMOUNTED() {
	    String status = Environment.getExternalStorageState();
	    if (status.equals(Environment.MEDIA_MOUNTED))
	        return true;
	
	    return false;
	}
	
	
	/** 다시 액티비티로 복귀하였을때 이미지를 셋팅 */
    public void onActivityResult(int requestCode, int resultCode,
            Intent imageData) {
        super.onActivityResult(requestCode, resultCode, imageData);
 
        switch (requestCode) {
        case REQ_CODE_PICK_IMAGE:
            if (resultCode == RESULT_OK) {
                if (imageData != null) {
                    String filePath = Environment.getExternalStorageDirectory()
                            + "/temp.jpg";
 
                    System.out.println("path" + filePath); // logCat으로 경로확인.
 
                    Bitmap selectedImage = BitmapFactory.decodeFile(filePath);
                    // temp.jpg파일을 Bitmap으로 디코딩한다.
 
                    ImageView imgCameraOption = (ImageView)findViewById(R.id.ImgGuide_Add);
                    imgCameraOption.setImageBitmap(selectedImage); 
                    // temp.jpg파일을 이미지뷰에 씌운다.
                    dialog.dismiss();
                    
                }
            }
            break;
        }
    }

	class ConnectionTask extends AsyncTask<String, Void, String> {

		private DataOutputStream dataStream = null;
		String CRLF = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****b*o*u*n*d*a*r*y*****";

		@Override
		protected String doInBackground(String... params) {
			try {
				if (params[0].equals("requests")) {
					type = 0;	
					return getRequests();
				}else if(params[0].equals("upload")){
					type = 1;
					// 현우오빠
					// 이거 주석만 풀면 되용!!!!
					//uploadJSON();
					insertGuide();
					return uploadZip();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		private String insertGuide()
				throws UnsupportedEncodingException, IOException,
				ClientProtocolException, ParseException {
			
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(SmartGuidePlusInfo.hostUrl + "guide");
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			
			nameValuePairs.add(new BasicNameValuePair("name", guide.getName()));
			nameValuePairs.add(new BasicNameValuePair("creator", guide.getCreator()));
			nameValuePairs.add(new BasicNameValuePair("date", guide.getDate()));
			nameValuePairs.add(new BasicNameValuePair("gidx", guide.getGidx()));
			nameValuePairs.add(new BasicNameValuePair("name", guide.getName()));
			nameValuePairs.add(new BasicNameValuePair("image", guide.getImage()));
			nameValuePairs.add(new BasicNameValuePair("os", guide.getOs()));
			nameValuePairs.add(new BasicNameValuePair("device", guide.getDevice()));
			nameValuePairs.add(new BasicNameValuePair("description", guide.getDescription()));
			
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);

			String ret = EntityUtils.toString(response.getEntity());
			return ret;
		}
		
		private String getRequests() throws UnsupportedEncodingException,
				IOException, ClientProtocolException, ParseException {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(SmartGuidePlusInfo.hostUrl	+ "requests");

			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);

			String ret = EntityUtils.toString(response.getEntity());

			Log.d("test", "get request list " + ret);

			return ret;
		}

		public String uploadZip() {

			File uploadFile = new File(SmartGuidePlusInfo.saveDir + guide.getGidx() + "/" + guide.getGidx() + ".zip");

			if(uploadFile.exists()){
				try {
					FileInputStream fileInputStream = new FileInputStream(
							uploadFile);
					URL connectURL = new URL(SmartGuidePlusInfo.hostUrl + "upload");
					HttpURLConnection conn = (HttpURLConnection) connectURL
							.openConnection();
	
					conn.setDoInput(true);
					conn.setDoOutput(true);
					conn.setUseCaches(false);
					conn.setRequestMethod("POST");
	
					// conn.setRequestProperty("User-Agent", "myFileUploader");
	
					conn.setRequestProperty("Connection", "Keep-Alive");
	
					conn.setRequestProperty("Content-Type",
							"multipart/form-data;boundary=" + boundary);
	
					conn.connect();
	
					dataStream = new DataOutputStream(conn.getOutputStream());
	
					writeFormField("name", guide.getGidx() + ".zip");
					writeFileField("fileData", guide.getGidx() + ".zip", "zip", fileInputStream);
	
					// final closing boundary line
	
					dataStream
							.writeBytes(twoHyphens + boundary + twoHyphens + CRLF);
	
					fileInputStream.close();
	
					dataStream.flush();
	
					dataStream.close();
	
					dataStream = null;
	
					Log.d("업로드 테스트", "***********전송완료***********");
	
					int response = conn.getResponseCode();
					String ret = String.valueOf(response);
					
					return ret;
	
				}
	
				catch (MalformedURLException mue) {
					mue.printStackTrace();
				} catch (IOException ioe) {
					ioe.printStackTrace();
	
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return CRLF;
		}

		public String uploadJSON() {

			File uploadFile = new File(SmartGuidePlusInfo.saveDir + guide.getGidx() + "/" + guide.getGidx() + ".json");

			try {
				FileInputStream fileInputStream = new FileInputStream(
						uploadFile);
				URL connectURL = new URL(SmartGuidePlusInfo.hostUrl + "upload");
				HttpURLConnection conn = (HttpURLConnection) connectURL
						.openConnection();

				conn.setDoInput(true);
				conn.setDoOutput(true);
				conn.setUseCaches(false);
				conn.setRequestMethod("POST");

				// conn.setRequestProperty("User-Agent", "myFileUploader");

				conn.setRequestProperty("Connection", "Keep-Alive");

				conn.setRequestProperty("Content-Type",
						"multipart/form-data;boundary=" + boundary);

				conn.connect();

				dataStream = new DataOutputStream(conn.getOutputStream());

				writeFormField("name", guide.getGidx() + ".json");
				writeFileField("fileData", guide.getGidx() + ".json", "json", fileInputStream);

				// final closing boundary line

				dataStream
						.writeBytes(twoHyphens + boundary + twoHyphens + CRLF);

				fileInputStream.close();

				dataStream.flush();

				dataStream.close();

				dataStream = null;

				Log.d("업로드 테스트", "***********전송완료***********");

				int response = conn.getResponseCode();
				String ret = String.valueOf(response);

			}

			catch (MalformedURLException mue) {
				mue.printStackTrace();
			} catch (IOException ioe) {
				ioe.printStackTrace();

			} catch (Exception e) {
				e.printStackTrace();
			}
			return "";
		}
		
		private void writeFormField(String fieldName, String fieldValue) {

			try {

				dataStream.writeBytes(twoHyphens + boundary + CRLF);
				dataStream.writeBytes("Content-Disposition: form-data; name=\""
						+ fieldName + "\"" + CRLF);
				dataStream.writeBytes(CRLF);
				dataStream.writeBytes(fieldValue);

				dataStream.writeBytes(CRLF);

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		private void writeFileField(

		String fieldName,

		String fieldValue,

		String type,

		FileInputStream fis) {

			try {

				dataStream.writeBytes(twoHyphens + boundary + CRLF);

				dataStream.writeBytes("Content-Disposition: form-data; name=\""

				+ fieldName

				+ "\";filename=\""

				+ fieldValue

				+ "\""

				+ CRLF);

				dataStream.writeBytes("Content-Type: " + type + CRLF);

				dataStream.writeBytes(CRLF);

				int bytesAvailable = fis.available();

				int maxBufferSize = 1024;

				int bufferSize = Math.min(bytesAvailable, maxBufferSize);

				byte[] buffer = new byte[bufferSize];

				int bytesRead = fis.read(buffer, 0, bufferSize);

				while (bytesRead > 0) {
					dataStream.write(buffer, 0, bufferSize);
					bytesAvailable = fis.available();
					bufferSize = Math.min(bytesAvailable, maxBufferSize);
					bytesRead = fis.read(buffer, 0, bufferSize);

				}
				dataStream.writeBytes(CRLF);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		@Override
		protected void onPostExecute(String result) {
			if(type == 0){
				if(!(result.equals("") || result == null)){
					requests = new Gson().fromJson(result, new TypeToken<ArrayList<Request>>() {}.getType());
					requestListAdapter = new RequestListAdapter(
							getApplicationContext(), R.layout.request_row, requests);
					
					spinRequests.setAdapter(requestListAdapter);
				}
				
			}else{
				Toast.makeText(getApplicationContext(), "등록이 완료되었습니다.", Toast.LENGTH_LONG).show();
				finish();
			}
		}
	}
}

