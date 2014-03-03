package com.example.smartguideplus.mylist;
import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.smartguideplus.R;
import com.example.smartguideplus.model.Request;
import com.example.smartguideplus.model.User;

public class UserListAdapter extends BaseAdapter{

	private Context mContext;
	private ArrayList<User> users;

	public UserListAdapter(Context context,  int resource, ArrayList<User> users) {
		super();
		mContext = context;
		this.users = users;
	}
	
	@Override
	public int getCount() {
		return users.size();
	}

	@Override
	public Object getItem(int position) {
		return users.get(position);
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
			convertView = vi.inflate(R.layout.user_row, null);
			
			viewHolder = new ViewHolder();
			viewHolder.txtName = (TextView) convertView.findViewById(R.id.txtName_User);
			viewHolder.txtPhone = (TextView) convertView.findViewById(R.id.txtPhone_User);

			convertView.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		User user = users.get(position);

		if (user != null) {
			viewHolder.txtName.setText(user.getName());
			viewHolder.txtPhone.setText(user.getPhone());
		}

		return convertView;
	}
	
	private class ViewHolder {
		TextView txtName;
		TextView txtPhone;
	}

}
