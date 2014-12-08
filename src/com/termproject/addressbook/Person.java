package com.termproject.addressbook;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Parcel;
import android.os.Parcelable;



public class Person implements Parcelable{
	/**
	 * 
	 */
	public String name;
	public String id;
	public Bitmap icon;
	public int photoID;
	public String[] myGroups;
	public boolean visible;
	public Bitmap getIcon(Context contxt)
	{
		if(icon==null && photoID>=0)
		{
			byte[] data = DBHelper.helper.getPhoto(photoID);
			if(data == null)
			{
				BitmapDrawable bd = (BitmapDrawable) contxt.getResources().getDrawable(R.drawable.ic_default_person);
				icon = bd.getBitmap();
			}
			else icon= BitmapFactory.decodeByteArray(data, 0, data.length);
		}
		return icon;
	}
	public Person(String name,String id,int photoid,String groups)
	{
		this.name = name;
		this.id = id;
		this.photoID = photoid;
		icon = null;
		visible = true;
		myGroups = groups.split(",");
	}
	public Person(Parcel par)
	{
		name = par.readString();
		id = par.readString();
		photoID = par.readInt();
		myGroups = par.readString().split(",");
		visible = false;
	}
	@Override
	public boolean equals(Object obj)
	{
		if(obj instanceof Person)
		{
			Person p = (Person)obj;
			if(p.id.equals(id)) return true;
			return false;
		}
		else return false;
	}
	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		return name.compareTo(((Person)arg0).name);
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	public void groupAdd(String group)
	{
		myGroups = (getGroup() + "," + group).split(",");
		
	}
	public String getGroup()
	{
		String join = "";
		if(myGroups == null || myGroups.length==0) return ""; 
		for(String p : myGroups)
			join += p + ",";
		return join.substring(0,join.length()-1);
	}
	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		// TODO Auto-generated method stub
		arg0.writeString(name);
		arg0.writeString(id);
		arg0.writeInt(photoID);
		arg0.writeString(getGroup());
		
		
	}
	public static final Parcelable.Creator<Person> CREATOR
    = new Parcelable.Creator<Person>() {
        @Override
        public Person createFromParcel(Parcel source) {
            return new Person(source);
        }

        @Override
        public Person[] newArray(int size) {
            return new Person[size];
        }

    };
}
