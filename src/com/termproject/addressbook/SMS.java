package com.termproject.addressbook;

import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class SMS extends DateMessage{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String body;
	final static int TYPE_SENT=0;
	final static int TYPE_RECEIVED=1;
	public SMS(String address,String body,long date,int type)
	{
		super(date,address,type);
		this.number = address;
		this.body = body;
		this.type = type;
		
	}
	@Override
	public int compareTo(Date date)
	{
		return super.compareTo(date);
	}
	public static void sms(Context context,String number)
	{
		Intent intent = new Intent(Intent.ACTION_SENDTO,Uri.parse("smsto:"+number));
		context.startActivity(intent);
	}
	@Override
	public String toString()
	{
		if(name== null) return number;
		else return name + " (" + number + ")";
	
	} 
}
