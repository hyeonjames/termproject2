package com.termproject.addressbook;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.QuickContactBadge;
import android.widget.TabHost;
import android.widget.TextView;


public class InfoActivity extends Activity implements OnClickListener{
	public final static int ACT_PMODIFY=0;

	Person myPerson;
	ArrayList<Phone> plists;
	ListView loglist;
	CommunicationLog log;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_information);
		TabHost tabhost = (TabHost)findViewById(R.id.tabhost);
		tabhost.setup();
		tabhost.addTab(tabhost.newTabSpec("tab_info").setIndicator("information").setContent(R.id.layout_tab1));
		
		tabhost.addTab(tabhost.newTabSpec("tab_log").setIndicator("log").setContent(R.id.layout_tab2));
		myPerson = (Person)getIntent().getParcelableExtra("info");
		notifyDatachanged();
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
		case R.id.item_del:
			DBHelper.helper.delete(myPerson);
			this.finish();
			break;
		case R.id.item_modify:
			Intent intent = new Intent(this,PModifyActivity.class);
			intent.putExtra("info", myPerson);
			startActivityForResult(intent,ACT_PMODIFY);
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.info_menu, menu);
		return true;
	}
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}
	public void notifyDatachanged()
	{
		TextView txtName = (TextView)findViewById(R.id.txtName);
		QuickContactBadge icon = (QuickContactBadge)findViewById(R.id.info_icon);
		txtName.setText(myPerson.name);
		icon.setImageBitmap(myPerson.getIcon(this.getBaseContext()));
		LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		plists = new ArrayList<Phone>();
		DBHelper.helper.read_phone(plists, myPerson.id);
		LinearLayout layout_phone = (LinearLayout)findViewById(R.id.layout_phone);
		layout_phone.removeAllViews();
		for(Phone p : plists)
		{
			View v = (View)inflater.inflate(R.layout.layout_info_list, layout_phone,false);
			v.setClickable(true);
			TextView txt = (TextView)v.findViewById(R.id.text1);
			txt.setText(p.toString());
			txt.setClickable(false);
			
			v.setTag(p.number);
			v.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Call.call(InfoActivity.this,(String)v.getTag());
				}
			});
			layout_phone.addView(v);
		}
		
		ArrayList<Email> elists = new ArrayList<Email>();
		DBHelper.helper.read_email(elists,myPerson.id);
		LinearLayout layout_email = (LinearLayout)findViewById(R.id.layout_email);
		layout_email.removeAllViews();
		for(Email e : elists)
		{
			View v = (View)inflater.inflate(R.layout.layout_info_list, layout_email,false);
			v.setClickable(true);
			TextView txt = (TextView)v.findViewById(R.id.text1);
			txt.setText(e.toString());
			
			layout_email.addView(v);
		}
		loglist= (ListView)findViewById(R.id.loglist);
		log = new CommunicationLog();
		DBHelper.helper.readLog(getContentResolver(), log, plists);
		loglist.setAdapter(new LogAdapter(log,this));
	}
	protected void onActivityResult(int req,int res,Intent data)
	{
		switch(req)
		{
		case ACT_PMODIFY:
			if(res==RESULT_OK)
			{
				myPerson = data.getParcelableExtra("result");
				notifyDatachanged();
			}
			break;
		}
	}
}
