package com.termproject.addressbook;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.QuickContactBadge;
import android.widget.Toast;

public class PModifyActivity extends Activity implements View.OnClickListener{

	Button btnAddPhone,btnAddEmail;
	Button btnOK,btnCancel;
	Bitmap image;
	int photoID=0;
	Uri imgUri;
	ArrayList<Email> del_emails;
	ArrayList<Phone> del_phones;
	public static final String[] type_phones={
			"Home",
			"Mobile",
			"Work",
			"Home Fax",
			"Work Fax",
			"Other"
		};
	public static final String[] type_emails={
				"Home",
				"Mobile",
				"Work",
				"Other"
		};
	public void delObject(Object obj)
	{
		ViewHolder vhold = (ViewHolder)obj;
		if(vhold == null) return;
		if(vhold.myobj instanceof Phone)
		{
			phones.remove(vhold);
			if(del_phones!= null && ((Phone)vhold.myobj).id != null)
			{
				del_phones.add((Phone)vhold.myobj);
			}

			layout_phone.removeView(vhold.layout);
		}
		else if(vhold.myobj instanceof Email)
		{
			emails.remove(vhold);
			if(del_emails!=null && ((Email)vhold.myobj).id != null)
			{
				del_emails.add((Email)vhold.myobj);
			}

			layout_email.removeView(vhold.layout);
		}
	}

	public boolean modify(DBHelper help)
	{
		if(me == null) return false;
		String name = editName.getText().toString();
		me.name = name;
		if(imgUri!= null)
		{
			photoID = help.getPhotoIDfromUri(imgUri);
			if(photoID < 0)
			{
				ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
				image.compress(CompressFormat.PNG, 100, byteArray);
				photoID=(int)help.add(imgUri.toString(),byteArray.toByteArray());
			}
		}
		me.photoID = photoID;
		String group="";
		Iterator<String> iter = groups.keySet().iterator();
		while(iter.hasNext())
		{
			String key = iter.next();
			Boolean obj = groups.get(key);
			if(obj!=null && obj == true) group+=key + ",";	
		}
		if(group.length() > 0) 
		{
			me.myGroups = group.substring(0,group.length()-1).split(",");
		}
		help.update(me);
		for(ViewHolder vhold : phones)
		{
			Phone phone;
			vhold.refreshObject(me.id);
			phone = (Phone)vhold.myobj;
			if(phone.id == null)
			{
				help.add(phone);
			}
			else help.update(phone);
		}
		
		for(ViewHolder vhold : emails)
		{
			Email email;
			vhold.refreshObject(me.id);
			email = (Email)vhold.myobj;
			if(email.id == null)
			{
				help.add(email);
			}
			else help.update(email);
		}
		for(Email e : del_emails) help.delete(e);
		for(Phone p : del_phones) help.delete(p);
		
		return true;
	}
	public boolean make(DBHelper help)
	{
		
		String name = editName.getText().toString();
		String group="";
		Iterator<String> iter = groups.keySet().iterator();
		while(iter.hasNext())
		{
			group+=iter.next() + ",";	
		}
		if(imgUri!= null)
		{
			photoID = help.getPhotoIDfromUri(imgUri);
			if(photoID < 0)
			{
				ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
				
				image.compress(CompressFormat.PNG, 100, byteArray);
				photoID=(int)help.add(imgUri.toString(),byteArray.toByteArray());
			}
		}
		Person p = new Person(name,null,photoID,group);
		String pid = String.valueOf(help.add(p));
		for(ViewHolder vhold : phones)
		{
			vhold.refreshObject(pid);
			help.add((Phone)vhold.myobj);
		}
		for(ViewHolder vhold : emails)
		{
			vhold.refreshObject(pid);
			help.add((Email)vhold.myobj);
		}

		me = p;
		return true;
	}
	class ViewHolder implements View.OnClickListener, DialogInterface.OnClickListener
	{
		EditText txtContext;
		Button btnDel;
		Button btnType;
		int type;
		LinearLayout layout;
		Object myobj;
		public ViewHolder(LinearLayout layout)
		{
			this.layout = layout;
			txtContext = (EditText)layout.findViewById(R.id.editContext);
			btnDel = (Button)layout.findViewById(R.id.btnDel);
			btnDel.setOnClickListener(this);
			btnType = (Button)layout.findViewById(R.id.btnType);
			btnType.setOnClickListener(this);
		}
		public void refreshObject(String pid)
		{
			if(myobj instanceof Email)
			{
				Email e = (Email)myobj;
				e.address = txtContext.getText().toString();
				e.type = type;
				e.pid = pid;
			}
			else if(myobj instanceof Phone)
			{
				Phone p = (Phone)myobj;
				p.number = txtContext.getText().toString();
				p.type = type;
				p.pid = pid;
			}
		}
		public ViewHolder(LinearLayout layout,Object obj)
		{
			this(layout);
			setObject(obj);
		}
		public void setObject(Object obj)
		{
			myobj = obj;
			if(myobj instanceof Email)
			{
				txtContext.setInputType(InputType.TYPE_CLASS_TEXT);
				txtContext.setText(((Email)obj).address);
				type = ((Email)myobj).type;
				btnType.setText(type_emails[type]);
				
			}
			else if(myobj instanceof Phone)
			{
				txtContext.setInputType(InputType.TYPE_CLASS_NUMBER);
				txtContext.setText(((Phone)obj).number);
				type = ((Phone)myobj).type;
				btnType.setText(type_phones[type]);
				
			}
		}
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId())
			{
			case R.id.btnDel:
				delObject(this);
				break;
			case R.id.btnType:
				if(myobj instanceof Email)
				{
					AlertDialog.Builder builder = new AlertDialog.Builder(PModifyActivity.this);
					builder.setTitle("Select type");
					builder.setSingleChoiceItems(type_emails,type, new DialogInterface.OnClickListener()
					{

						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							// TODO Auto-generated method stub
							type = which;
						}

						
					});
					builder.create().show();
				}
				else if(myobj instanceof Phone)
				{
					AlertDialog.Builder builder = new AlertDialog.Builder(PModifyActivity.this);
					builder.setTitle("Select type");
					builder.setSingleChoiceItems(type_phones,type, new DialogInterface.OnClickListener(){

						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							// TODO Auto-generated method stub
							type = which;
						}
						
					});
					builder.setPositiveButton("Apply", this);
					builder.setNegativeButton("Cancel",this);
					builder.create().show();
				}
				break;
			}
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			if(which == Dialog.BUTTON_POSITIVE)
			{
				if(myobj instanceof Email)
				{
					btnType.setText(type_emails[type]);
					((Email)myobj).type = type;
				}
				else if(myobj instanceof Phone)
				{
					btnType.setText(type_phones[type]);
					((Phone)myobj).type = type;
				}
			}
			
			dialog.dismiss();
		}
	}
	public PModifyActivity() {
		super();
		// TODO Auto-generated constructor stub
		phones = new ArrayList<ViewHolder>();
		emails = new ArrayList<ViewHolder>();
	}
	Person me;
	boolean option;
	ArrayList<ViewHolder> phones;
	ArrayList<ViewHolder> emails;
	QuickContactBadge icon;
	int first_height;
	ListView lview_email;
	LinearLayout layout_phone,layout_email;
	EditText editName;
	Hashtable<String,Boolean> groups;
	LayoutInflater inflater;
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_add);
		me = getIntent().getParcelableExtra("info");
		option = getIntent().getBooleanExtra("opt", true);
		if(option==true)
			setTitle("Add");
		else if(option==false)
			setTitle("Modify");
		
		btnAddPhone = (Button)findViewById(R.id.btnPAdd);
		btnAddEmail = (Button)findViewById(R.id.btnEAdd);
		btnAddPhone.setOnClickListener(this);
		btnAddEmail.setOnClickListener(this);
		layout_phone = (LinearLayout)findViewById(R.id.layout_phone);
		layout_email = (LinearLayout)findViewById(R.id.layout_email);
		icon = (QuickContactBadge) findViewById(R.id.badge_icon);
		editName = (EditText)findViewById(R.id.editName);
		inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		groups = new Hashtable<String,Boolean>();
		
		if(me == null)
		{
			BitmapDrawable bd = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_default_person);
			image = bd.getBitmap();
			addObject(new Phone(null,null,"",0));
		}
		else
		{
			editName.setText(me.name);
			image = me.getIcon(this);
			photoID = me.photoID;
			ArrayList<Email> emails = new ArrayList<Email>();
			DBHelper.helper.read_email(emails, me.id);
			ArrayList<Phone> phones = new ArrayList<Phone>();
			DBHelper.helper.read_phone(phones, me.id);
			for(Email e : emails)
				addObject(e);
			for(Phone p : phones)
				addObject(p);
			del_phones = new ArrayList<Phone>();
			del_emails = new ArrayList<Email>();
			for(String group : me.myGroups)
				groups.put(group,true);
		}
		icon.setImageBitmap(image);
		icon.setOnClickListener(this);
		btnOK = (Button)findViewById(R.id.btnOK);
		btnCancel = (Button)findViewById(R.id.btnCancel);
		btnOK.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		((Button)findViewById(R.id.btnGroup)).setOnClickListener(this);
	}
	public void addObject(Object obj)
	{
		ViewHolder inflated = null;
		LinearLayout layout;
		if(obj instanceof Phone)
		{
			layout = (LinearLayout)inflater.inflate(R.layout.layout_add_lview, layout_phone,false);
			inflated = new ViewHolder(layout,obj);
			
			phones.add(inflated);
			layout_phone.addView(layout);
		}
		else if(obj instanceof Email)
		{
			
			layout = (LinearLayout)inflater.inflate(R.layout.layout_add_lview, layout_email,false);
			inflated = new ViewHolder(layout,obj);
			
			emails.add(inflated);
			
			layout_email.addView(layout);
		}
	}
	
	
	public final static int CAMERA_PIC_REQUEST = 1337;
	public final static int REQ_CODE_PIC_PICTURE = 0;
	@Override
	protected void onActivityResult(int req,int res,Intent data)
	{
		if(req == REQ_CODE_PIC_PICTURE || req == CAMERA_PIC_REQUEST)
		{
			if(res == RESULT_OK)
			{
				imgUri = data.getData();
				Cursor c = getContentResolver().query(Uri.parse(imgUri.toString()), null,null,null,null);
				if(c.moveToNext())
				{
					String absolutePath = c.getString(c.getColumnIndex(MediaStore.MediaColumns.DATA));
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inSampleSize = 1;
					try
					{
						image = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(absolutePath,options), (int) getResources().getDimension(R.dimen.activity_icon_maxwidth), (int) getResources().getDimension(R.dimen.activity_icon_maxheight), false);
					}catch(OutOfMemoryError e)
					{
						options.inSampleSize = 4;
						image = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(absolutePath,options), (int) getResources().getDimension(R.dimen.activity_icon_maxwidth), (int) getResources().getDimension(R.dimen.activity_icon_maxheight), false);
					}
					icon.setImageBitmap(image);
				}
			}
		}
	}
	public boolean check_phone_repeat()
	{
		int i,j;
		for(i=0;i<phones.size();i++) phones.get(i).refreshObject(null);
		
		for(i=0;i<phones.size();i++)
		{
			Phone a = (Phone)phones.get(i).myobj;
			for(j=i+1;j<phones.size();j++)
			{
				Phone b = (Phone)phones.get(j).myobj;
				if(a.number.equals(b.number)) return true;
			}
		}
		return false;
	}
	public boolean check_email_repeat()
	{
		int i,j;
		for(i=0;i<emails.size();i++) emails.get(i).refreshObject(null);
		for(i=0;i<emails.size();i++)
		{
			Email a = (Email)emails.get(i).myobj;
			for(j=i+1;j<emails.size();j++)
			{
				Email b = (Email)emails.get(j).myobj;
				if(a.address.equals(b.address)) return true;
			}
		}
		return false;	
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		case R.id.btnOK:
			if(editName.getText().toString().length() == 0)
			{
				Toast.makeText(this, "name should be longer than 1 byte", Toast.LENGTH_SHORT).show();
				break;
			}
			if(check_phone_repeat())
			{
				Toast.makeText(this, "phone should not be repeat", Toast.LENGTH_SHORT).show();
				break;
			}
			if(check_email_repeat())
			{
				Toast.makeText(this, "email should not be repeat", Toast.LENGTH_SHORT).show();
				break;
			}
			if(me == null) make(DBHelper.helper);
			else if(me != null) modify(DBHelper.helper);
			Intent intent = new Intent();
			intent.putExtra("result", me);
			setResult(RESULT_OK,intent);
			finish();
			break;
		case R.id.btnCancel:
			setResult(RESULT_CANCELED);
			finish();
			break;
		case R.id.btnPAdd:
			addObject(new Phone(null,null,"",0));
			
			break;
		case R.id.btnEAdd:
			addObject(new Email(null,null,"",0));
			break;
		case R.id.badge_icon:
			Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
			galleryIntent.setType("image/*");
			
			startActivityForResult(galleryIntent,CAMERA_PIC_REQUEST);
			break;
		case R.id.btnGroup:
			int i=0;
			GroupManager gmanager = new GroupManager();
			DBHelper.helper.read_group(gmanager);
			
			final String[] menu = new String[gmanager.size()];
			Iterator<String> keys = gmanager.keySet().iterator();
			while(keys.hasNext())
			{
				menu[i++] = keys.next();
			}
			boolean[] checked= new boolean[menu.length];
			for(i=0;i<menu.length;i++)
			{
				Boolean obj = groups.get(menu[i]);
				if(obj == null) checked[i] = false;
				else checked[i] = obj;
			}
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMultiChoiceItems(menu, checked,new DialogInterface.OnMultiChoiceClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which, boolean isChecked) {
					// TODO Auto-generated method stub
					groups.put(menu[which], isChecked);
				}
			});
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dialog.dismiss();
				}
			});
			builder.create().show();
			break;
		}
	}
}
