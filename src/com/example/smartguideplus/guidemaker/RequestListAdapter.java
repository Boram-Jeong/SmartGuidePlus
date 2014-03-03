package com.example.smartguideplus.guidemaker;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.smartguideplus.R;
import com.example.smartguideplus.model.Request;

public class RequestListAdapter extends BaseAdapter{

	private Context mContext;
	private ArrayList<Request> requests;

	public RequestListAdapter(Context context,  int resource, ArrayList<Request> requests) {
		super();
		mContext = context;
		this.requests = requests;
	}
	
	@Override
	public int getCount() {
		return requests.size();
	}

	@Override
	public Object getItem(int position) {
		return requests.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;

		if (convertView == null) {
			LayoutInflater vi = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = vi.inflate(R.layout.request_row, null);
			
			viewHolder = new ViewHolder();
			viewHolder.txtTitle = (TextView) convertView.findViewById(R.id.txtRequest_Row);

			convertView.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		Request reqeust = requests.get(position);

		if (reqeust != null) {
			viewHolder.txtTitle.setText(reqeust.getTitle());
		}

		return convertView;
	}
	
	private class ViewHolder {
		TextView txtTitle;
	}


	

}
