package com.termproject.addressbook;

import java.util.Date;

public class DateMessage extends Date implements Comparable<Date> {

	/**
	 * 
	 */
	String name;
	private static final long serialVersionUID = 1L;
	int type;
	String number;
	public DateMessage(long date,String num,int type) {
		super(date);
		// TODO Auto-generated constructor stub
		this.number = num;
		this.type = type;
		name = DBHelper.helper.getNameByNumber(num);
	}
	@Override
	public int compareTo(Date another) {
		// TODO Auto-generated method stub
		return -super.compareTo(another);
	}

}
