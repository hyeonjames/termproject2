package com.termproject.addressbook;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;



public class Phone implements Parcelable
{
	String number;
	int type;
	String id;
	String pid;
	public Phone(String id,String pid,String num,int t)
	{
		int i=0;
		this.id = id;
		this.pid = pid;
		number="";
		while(i<num.length())
		{
			char k=num.charAt(i);
			if(k >= '0' && k <= '9')
			{
				number += k;
			}
			i++;
		}
		type = t;
	}
	
	public boolean equals(Object obj)
	{
		
		if(obj instanceof Phone) return toString().equals(obj.toString());
		else return false;
	}
	public void Dial(Context contxt)
	{
		Intent intent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:" + number));
		contxt.startActivity(intent);
	}
	
	
	public void Sms(Context contxt,String text)
	{
		Intent intent = new Intent(Intent.ACTION_SENDTO,Uri.parse("smsto:" + number));
		intent.putExtra("sms_body", text);
		contxt.startActivity(intent);
	}
	public void Sms(String text)
	{
		
	}
	public String gettype()
	{
		switch(type)
		{
		
		}			
		return "other";
	}
	public String toString()
	{
		return "(" + gettype() + ")" + number;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(pid);
		dest.writeString(number);
		dest.writeInt(type);
		
	}
	public Phone(Parcel par)
	{
		pid = par.readString();
		number = par.readString();
		type = par.readInt();
	}
	public static final Parcelable.Creator<Phone> CREATOR
    = new Parcelable.Creator<Phone>() {
        @Override
        public Phone createFromParcel(Parcel source) {
            return new Phone(source);
        }

        @Override
        public Phone[] newArray(int size) {
            return new Phone[size];
        }

    };
}