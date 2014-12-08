package com.termproject.addressbook;

import java.util.Date;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.QuickContactBadge;
import android.widget.TextView;


public class LogAdapter extends BaseAdapter
{
	LayoutInflater inflater;
	CommunicationLog log;
	Context mcontext;
	Bitmap ic_call,ic_sms;
	public LogAdapter(CommunicationLog log,Context context)
	{
		inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.log = log;
		mcontext = context;
		BitmapDrawable bd = (BitmapDrawable) mcontext.getResources().getDrawable(R.drawable.ic_call);
		ic_call = bd.getBitmap();
		bd = (BitmapDrawable) mcontext.getResources().getDrawable(R.drawable.ic_sms);
		ic_sms = bd.getBitmap();
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return log.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return log.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	class ViewHolder
	{
		QuickContactBadge icon;
		TextView txtAddress, txtDate;
		public ViewHolder(View v)
		{
			txtAddress= (TextView)v.findViewById(R.id.txtAddress);
			txtDate = (TextView)v.findViewById(R.id.txtDate);
			icon = (QuickContactBadge)v.findViewById(R.id.badge_calltype);
		}
		public void setData(String adr,Date date,Bitmap img)
		{
			txtAddress.setText(adr);
			txtDate.setText(CommunicationLog.format(date));
			if(img!=null) icon.setImageBitmap(img);
		}
		
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder vhold;
		if(convertView == null)
		{
			convertView = (View)inflater.inflate(R.layout.layout_info_log, parent,false);
			vhold = new ViewHolder(convertView);
			convertView.setTag(vhold);
		}
		else
		{
			vhold = (ViewHolder)convertView.getTag();
		}
		if(vhold!=null)
		{
			Object call = getItem(position);
			Bitmap icon=null;
			if(call instanceof Call)
			{
				icon = ic_call;
			}
			else if(call instanceof SMS)
			{
				icon = ic_sms;
			}
			vhold.setData(((Date)call).toString(),(Date)call,icon);
		}
		return convertView;
	}
	
	
}