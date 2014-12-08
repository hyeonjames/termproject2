package com.termproject.addressbook;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.database.Cursor;
import android.provider.CallLog;

@SuppressWarnings("serial")
public class CommunicationLog extends ArrayList<DateMessage> {

	static SimpleDateFormat dformat = new SimpleDateFormat("MM/dd hh:mm");
			
	public CommunicationLog() {
		// TODO Auto-generated constructor stub
		super();
	}
	public static String format(Date date)
	{
		return dformat.format(date);
	}
	public void readCalls(Cursor data)
	{
		int nameidx = data.getColumnIndex(CallLog.Calls.CACHED_NAME);
		int dateidx = data.getColumnIndex(CallLog.Calls.DATE);
		int numidx = data.getColumnIndex(CallLog.Calls.NUMBER);
		int duridx = data.getColumnIndex(CallLog.Calls.DURATION);
		int typeidx = data.getColumnIndex(CallLog.Calls.TYPE);
		while(data.moveToNext())
		{
			String name = data.getString(nameidx);
			long date = data.getLong(dateidx);
			String number = data.getString(numidx);
			int dura = data.getInt(duridx);
			int type = data.getInt(typeidx);
			add(new Call(number,dura,type,date));
		}
	}
	public void readSMS(Cursor data,int type)
	{
		int nameidx = data.getColumnIndex("address");
		int dateidx = data.getColumnIndex("date");
		int bodyidx = data.getColumnIndex("body");
		while(data.moveToNext())
		{
			add(new SMS(data.getString(nameidx),data.getString(bodyidx),data.getLong(dateidx),type));
		}
	}
	
}
