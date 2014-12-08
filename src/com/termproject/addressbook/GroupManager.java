package com.termproject.addressbook;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

public class GroupManager extends Hashtable<String,GroupManager.Group> {

	/**
	 * 
	 */
	
	class Group extends ArrayList<Person>
	{
		boolean visible;
		public Group()
		{
			super();
			visible = true;
		}
	}
	private static final long serialVersionUID = 1L;
	public GroupManager()
	{
		super();
	}
	public String getKey(int idx)
	{
		int i=0;
		Iterator<String> iter = this.keySet().iterator();
		while(iter.hasNext())
		{
			String p = iter.next();
			if(i == idx)  return p;
			i++;
		}
		return null;
	}
	public void add(Person p)
	{
		for(String gname : p.myGroups)
		{
			if(gname.trim().equals("")) continue;
			ArrayList<Person> list = get(gname);
			if(list != null)
			{
				list.add(p);
			}
		}
	
	}
	public void add(ArrayList<Person> plists) {
		for (Person p : plists) {
			add(p);
		}
	}
	public boolean newGroup(String name)
	{
		if(get(name) != null) return false; // 없는 그룹일 경우 무시.
		put(name,new Group());
		
		return true;
	}
}
