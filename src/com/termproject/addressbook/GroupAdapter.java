package com.termproject.addressbook;

import java.util.ArrayList;
import java.util.Hashtable;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.QuickContactBadge;
import android.widget.TextView;
import android.widget.Toast;

public class GroupAdapter extends BaseAdapter{ // Group ListView를 담당하는 Adapter

	LayoutInflater inflater;
	Context mContext;
	public final static int ACT_PMODIFY = 0;
	GroupManager manager;
	public GroupAdapter(Context context,GroupManager manager)
	{
		this.manager = manager;
		DBHelper.helper.read_group(manager);
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mContext=context;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return manager.size();
	}
	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return manager.get(manager.getKey(arg0));
	}
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	class GroupHolder implements View.OnClickListener
	{
		String name;
		GroupManager.Group group;
		LayoutInflater inflater;
		TextView txtGroup;
		LinearLayout layout_group;
		public GroupHolder(View v,String Name)
		{
			layout_group = (LinearLayout)v;
			layout_group.setClickable(true);
			this.name = Name;
			group = manager.get(name);
			inflater = (LayoutInflater)v.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			txtGroup = (TextView)v.findViewById(R.id.textGroup);
			txtGroup.setText(Name);
			txtGroup.setClickable(true);
			txtGroup.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(group != null) group.visible = !group.visible;
					notifyDataSetChanged();
				}
				
			});
		}
		public void setgroup(String name)
		{
			group= manager.get(name);
			txtGroup.setText(name);
			notifyDatachanged();
			
		}
		public void notifyDatachanged()
		{
			layout_group.removeAllViews();
			layout_group.addView(txtGroup);
			if(!group.visible) return;
			
			for(Person person : group)
			{
				View inflated = (View)inflater.inflate(R.layout.layout_person, layout_group,false);
				TextView txtname = (TextView) inflated.findViewById(R.id.txtName);
				QuickContactBadge badge = (QuickContactBadge) inflated.findViewById(R.id.icon);
				txtname.setText(person.name);
				badge.setImageBitmap(person.getIcon(inflated.getContext()));
				inflated.setTag(person);
				layout_group.addView(inflated);
				inflated.setOnClickListener(this);
			}
			
		}
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Person person=(Person)v.getTag();
			Intent intent = new Intent(mContext, InfoActivity.class);
			intent.putExtra("info", person);
			mContext.startActivity(intent);
		}
		
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		GroupHolder ghold;
		String key = manager.getKey(position);
		if(convertView == null)
		{
			convertView = inflater.inflate(R.layout.layout_glist, parent,false);
			ghold = new GroupHolder(convertView,key);
			convertView.setTag(ghold);
		}
		else
		{
			ghold = (GroupHolder)convertView.getTag();
			if(ghold!=null)
			{
				ghold.setgroup(key);
			}
		}
		return convertView;
	}
}
