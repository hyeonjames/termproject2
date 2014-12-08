package com.termproject.addressbook;

import android.os.Parcel;
import android.os.Parcelable;
import android.provider.ContactsContract;

public class Email implements Parcelable
{
	String address;
	int type;
	String id;
	String pid;
	public Email(String id,String pid,String address,int type)
	{
		this.id = id;
		this.pid = pid;
		this.address=address;
		this.type = type;
	}
	public String getType()
	{
		switch(type)
		{
		case ContactsContract.CommonDataKinds.Email.TYPE_HOME:
			return "home";
		case ContactsContract.CommonDataKinds.Email.TYPE_MOBILE:
			return "mobile";
		case ContactsContract.CommonDataKinds.Email.TYPE_WORK:
			return "work";
		case ContactsContract.CommonDataKinds.Email.TYPE_OTHER:
			return "other";
		}
		return "other";
	}
	public String toString()
	{
		return "(" + getType() + ")" + address;
	}
	public Email(Parcel par)
	{
		pid = par.readString();
		address = par.readString();
		type = par.readInt();
	}
	public boolean equals(String str)
	{
		return address.equals(str);
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
		dest.writeString(address);
		dest.writeInt(type);
	}
	public static final Parcelable.Creator<Email> CREATOR
    = new Parcelable.Creator<Email>() {
        @Override
        public Email createFromParcel(Parcel source) {
            return new Email(source);
        }

        @Override
        public Email[] newArray(int size) {
            return new Email[size];
        }

    };
}