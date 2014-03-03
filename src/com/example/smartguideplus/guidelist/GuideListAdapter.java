package com.example.smartguideplus.guidelist;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.smartguideplus.R;
import com.example.smartguideplus.model.Guide;

public class GuideListAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<Guide> guides;

	public GuideListAdapter(Context context, int resource,
			ArrayList<Guide> guides) {
		super();
		mContext = context;
		this.guides = guides;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(guides.size() < 1){
			LayoutInflater vi = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			return vi.inflate(R.layout.non_row, null);
		}
		
		ViewHolder viewHolder;

		if (convertView == null) {
			LayoutInflater vi = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = vi.inflate(R.layout.guide_row, null);
			
			viewHolder = new ViewHolder();
			viewHolder.txtName = (TextView) convertView.findViewById(R.id.txtName_Row);
			viewHolder.txtDescription = (TextView) convertView.findViewById(R.id.txtDescription_Row);

			convertView.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		Guide guide = guides.get(position);

		if (guide != null) {
			viewHolder.txtName.setText(guide.getName());
			viewHolder.txtDescription.setText(guide.getDescription());
		}

		return convertView;
	}

	private class ViewHolder {
		TextView txtName;
		TextView txtDescription;
	}

	@Override
	public int getCount() {
		return guides.size();
	}

	@Override
	public Object getItem(int position) {
		return guides.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
}
