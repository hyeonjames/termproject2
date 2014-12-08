package com.termproject.addressbook;

import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.CallLog;

public class Call extends DateMessage
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int duration;

	public static void call(Context context,String number)
	{
		Intent intent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+number));
		context.startActivity(intent);
	}
	public Call(String num,int dura,int type,long date)
	{
		super(date,num,type);
		number = num;
		duration = dura;
		this.type = type;
	}
	@Override
	public int compareTo(Date obj)
	{
		return super.compareTo(obj);
	}
	
	public String getType()
	{
		switch(type)
		{
		case CallLog.Calls.MISSED_TYPE:
			return "missing";
		case CallLog.Calls.OUTGOING_TYPE:
			return "send";
		case CallLog.Calls.INCOMING_TYPE:
			return "receive";
		}
		return "other";
	}
	public String toString()
	{
		if(name!=null) return "(" + getType() + ") " + name + " (" + number + ")";
		else return "(" + getType() + ") " + number;
	}
}