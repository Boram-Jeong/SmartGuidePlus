package com.example.smartguideplus.mylist;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.example.smartguideplus.DBContactHelper;
import com.example.smartguideplus.R;
import com.example.smartguideplus.SmartGuidePlusInfo;
import com.example.smartguideplus.guidelist.GuideDetailActivity;
import com.example.smartguideplus.guidelist.GuideListAdapter;
import com.example.smartguideplus.guidemaker.RequestListAdapter;
import com.example.smartguideplus.model.Guide;
import com.example.smartguideplus.model.Request;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@SuppressLint("ValidFragment")
public class MyListFragment extends Fragment {

	Context mContext;

	private ListView downloadList;
	private GuideListAdapter downListAdapter;
	private ArrayList<Guide> downloadGuides;

	private ListView madeList;
	private GuideListAdapter madeListAdapter;
	private ArrayList<Guide> madeGuides;

	private ListView requestList;
	private RequestListAdapter requestListAdapter;
	private ArrayList<Request> requests;

	private Button btnStartRequest;

	private DBContactHelper db;

	int flag = 0;

	public MyListFragment(Context context) {
		mContext = context;
	}

	@Override
	public void onResume() {
		super.onResume();
		getData();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.my_list, null);

		downloadList = (ListView) view.findViewById(R.id.downloadList);
		downloadList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(getActivity(),
						DownloadGuideDetailActivity.class);
				Guide guide = downloadGuides.get(position);

				intent.putExtra("guide", guide);
				startActivity(intent);
			}
		});

		downloadList.setOnItemLongClickListener(new OnItemLongClickListener() {
			private Guide selectedGuide;

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				selectedGuide = downloadGuides.get(position);
				AlertDialog.Builder alertDlg = new AlertDialog.Builder(
						getActivity());
				alertDlg.setTitle(downloadGuides.get(position).getName());
				alertDlg.setItems(new String[] { "전송하기", "삭제" },
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								switch (which) {
								case 0:
									Intent intent = new Intent(getActivity(),
											UserListActivity.class);
									intent.putExtra("guide", selectedGuide);
									startActivity(intent);
									break;
								case 1:
									AlertDialog.Builder alert_confirm = new AlertDialog.Builder(
											getActivity());
									alert_confirm
											.setMessage("정말로 삭제하시겠습니까?")
											.setCancelable(false)
											.setPositiveButton(
													"확인",
													new DialogInterface.OnClickListener() {
														@Override
														public void onClick(
																DialogInterface dialog,
																int which) {
															db.deleteGuide(selectedGuide);
															delGuideDir();
															getData();
														}
													})
											.setNegativeButton(
													"취소",
													new DialogInterface.OnClickListener() {
														@Override
														public void onClick(
																DialogInterface dialog,
																int which) {
															return;
														}
													});
									AlertDialog alert = alert_confirm.create();
									alert.show();
								default:
									break;
								}

							}
						});
				alertDlg.show();
				return false;
			}

			private void delGuideDir() {
				String fileChk = SmartGuidePlusInfo.saveDir
						+ selectedGuide.getGidx();
				Log.d("test", "fileChk" + fileChk);
				DeleteDirectory(fileChk);
			}

			void DeleteDirectory(String path)
			{
				File file = new File(path);
				File[] childFileList = file.listFiles();
				// 폴더 안의 파일 및 서브 폴더에 대해 삭제
				for (File childFile : childFileList)
				{
					if (childFile.isDirectory()) { // 폴더
						DeleteDirectory(childFile.getAbsolutePath()); // 하위 폴더에
					}
					else { // 파일
						childFile.delete(); // 하위 파일삭제
					}
				}
				file.delete(); // 폴더 삭제
			}
		});

		madeList = (ListView) view.findViewById(R.id.madeList);
		madeList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(getActivity(),
						GuideDetailActivity.class);
				Guide guide;
				guide = madeGuides.get(position);
				intent.putExtra("guide", guide);
				startActivity(intent);
			}
		});

		requestList = (ListView) view.findViewById(R.id.requsetList);
		requestList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(getActivity(),
						RequestDetailActivity.class);
				Request request = requests.get(position);
				intent.putExtra("request", request);

				Log.d("test", "전달 전 " + request);
				startActivity(intent);
			}
		});

		btnStartRequest = (Button) view.findViewById(R.id.btnStartRequest);
		btnStartRequest.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),
						AddRequestActivity.class);
				startActivity(intent);
			}
		});

		return view;
	}

	private void getData() {
		db = new DBContactHelper(getActivity());
		downloadGuides = (ArrayList<Guide>) db.getDownloadedGuide();
		downListAdapter = new GuideListAdapter(mContext, R.layout.guide_row,
				downloadGuides);
		downloadList.setAdapter(downListAdapter);

		new ConnectionTask().execute("guides");
		new ConnectionTask().execute("requests");
	}

	class ConnectionTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... params) {
			try {
				if (params[0].equals("requests")) {
					return getRequests();
				} else if (params[0].equals("guides")) {
					return getMadeGuideList();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		private String getRequests() throws UnsupportedEncodingException,
				IOException, ClientProtocolException, ParseException {
			HttpClient client = new DefaultHttpClient();
			HttpGet get = new HttpGet(SmartGuidePlusInfo.hostUrl + "request/"
					+ SmartGuidePlusInfo.getUserID(getActivity()));
			HttpResponse response = client.execute(get);
			String ret = EntityUtils.toString(response.getEntity());

			flag = 0;

			return ret;
		}

		private String getMadeGuideList() throws UnsupportedEncodingException,
				IOException, ClientProtocolException, ParseException {
			HttpClient client = new DefaultHttpClient();
			HttpGet get = new HttpGet(SmartGuidePlusInfo.hostUrl
					+ "guide_by_user/"
					+ SmartGuidePlusInfo.getUserID(getActivity()));
			HttpResponse response = client.execute(get);
			String ret = EntityUtils.toString(response.getEntity());

			flag = 1;

			return ret;
		}

		@Override
		protected void onPostExecute(String result) {
			switch (flag) {
			case 0:
				requests = new Gson().fromJson(result,
						new TypeToken<ArrayList<Request>>() {
						}.getType());
				requestListAdapter = new RequestListAdapter(mContext,
						R.layout.request_row, requests);
				requestList.setAdapter(requestListAdapter);
				break;
			case 1:
				madeGuides = new Gson().fromJson(result,
						new TypeToken<ArrayList<Guide>>() {
						}.getType());
				madeListAdapter = new GuideListAdapter(mContext,
						R.layout.guide_row, madeGuides);
				madeList.setAdapter(madeListAdapter);
			}

		}
	}

}
