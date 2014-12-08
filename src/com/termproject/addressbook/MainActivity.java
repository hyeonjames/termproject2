package com.termproject.addressbook;

import java.util.ArrayList;
import java.util.Iterator;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.QuickContactBadge;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnItemClickListener,
		OnClickListener, OnItemLongClickListener, OnTabChangeListener {
	PersonAdapter adapter;
	CommunicationLog log;
	ListView loglist, grouplist;
	GroupManager gmanager;
	GroupAdapter gadapter;
	int currentTab = 1;

	public final static int ACT_PMODIFY = 0;

	class PersonAdapter extends BaseAdapter {
		ArrayList<Person> plists;
		LayoutInflater inflater;

		public PersonAdapter(Context context) {
			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if (plists == null)
				return 0;
			return plists.size();
		}

		public void setVisible(int arg, boolean visible) {
			((Person) getItem(arg)).visible = visible;
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return plists.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		class ViewHolder {
			TextView txtname;
			QuickContactBadge badge;

			public ViewHolder(View view) {
				txtname = (TextView) view.findViewById(R.id.txtName);
				badge = (QuickContactBadge) view.findViewById(R.id.icon);
			}

			public void setPerson(Person p) {
				txtname.setText(p.name);
				badge.setImageBitmap(p.getIcon(MainActivity.this));
			}

			public void setVisible(boolean visible) {
				if (visible) {
					txtname.setVisibility(View.VISIBLE);
					badge.setVisibility(View.VISIBLE);
				} else {
					txtname.setVisibility(View.INVISIBLE);
					badge.setVisibility(View.INVISIBLE);
				}
			}
		}

		@Override
		public View getView(int pos, View view, ViewGroup arg2) {
			// TODO Auto-generated method stub
			ViewHolder vhold;
			Person person = (Person) getItem(pos);
			if (view == null) {
				view = inflater.inflate(R.layout.layout_person, arg2, false);
				vhold = new ViewHolder(view);
				view.setTag(vhold);
			} else {
				vhold = (ViewHolder) view.getTag();
			}
			if (vhold != null) {
				vhold.setPerson(person);
				vhold.setVisible(person.visible);
			}
			return view;
		}

	}

	public DBHelper dbhelp;
	public EditText editFindPerson, editFindGroup;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		TabHost tabhost = (TabHost) findViewById(R.id.tabhost);
		tabhost.setup();
		TabSpec spec = tabhost.newTabSpec("tab1");
		spec.setContent(R.id.tablayout1);
		spec.setIndicator("List");
		tabhost.addTab(spec);
		tabhost.addTab(tabhost.newTabSpec("tab2")
				.setContent(R.id.tablayout_grouplist)
				.setIndicator("group list"));

		spec = tabhost.newTabSpec("tab3").setContent(R.id.tablayout2)
				.setIndicator("LastLog");
		tabhost.addTab(spec);

		spec = tabhost.newTabSpec("tab4").setContent(R.id.tablayout3)
				.setIndicator("Settings");
		tabhost.addTab(spec);
		tabhost.setOnTabChangedListener(this);

		gmanager = new GroupManager();

		ListView listview = (ListView) findViewById(R.id.personlist);
		listview.setOnItemClickListener(this);
		listview.setOnItemLongClickListener(this);
		adapter = new PersonAdapter(this);
		listview.setAdapter(adapter);
		adapter.plists = new ArrayList<Person>();
		grouplist = (ListView) findViewById(R.id.grouplist);

		dbhelp = new DBHelper(this);
		dbhelp.read(adapter.plists);
		DBHelper.helper = dbhelp;
		gadapter = new GroupAdapter(this, gmanager);
		grouplist.setAdapter(gadapter);
		log = new CommunicationLog();
		loglist = (ListView) findViewById(R.id.loglist);
		loglist.setAdapter(new LogAdapter(log, this));
		Button btnSynchronize = (Button) findViewById(R.id.btnSynchronize);
		btnSynchronize.setOnClickListener(this);
		editFindPerson = ((EditText) findViewById(R.id.editFind));
		editFindPerson.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				search_person(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}
		});
		editFindGroup = ((EditText) findViewById(R.id.editFindGroup));
		editFindGroup.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				search_group(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

		});
	}

	public void search_person(String text) {
		int i = 0;
		adapter.plists.clear();
		dbhelp.read(adapter.plists);
		while (i < adapter.plists.size()) {
			if (search(adapter.plists.get(i).name, text) < 0) {
				adapter.plists.remove(i);
			} else
				i++;
		}
		adapter.notifyDataSetChanged();
	}

	public void search_group(String text) {
		adapter.plists.clear();
		int i = 0;
		dbhelp.read(adapter.plists);
		gmanager.clear();
		dbhelp.read_group(gmanager);
		gmanager.add(adapter.plists);
		Iterator<String> keys = gmanager.keySet().iterator();
		while (keys.hasNext()) {
			int j = 0;
			boolean check = false;
			String key = keys.next();
			if (search(key, text) < 0) {
				check = true;
			}
			ArrayList<Person> plist = gmanager.get(key);
			while (j < plist.size()) {
				if (search(plist.get(j).name, text) < 0) {
					plist.remove(j);
				} else
					j++;
			}
			if (plist.size() > 0)
				check = false;
			if (check) {
				gmanager.remove(key);
			} else
				i++;
		}
		gadapter.notifyDataSetChanged();
	}

	private static final char[] Cho = { 'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ',
			'ㅃ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ' };

	public static int search(String obj, String k) // 검색 ( 초성까지 )
	{
		int i, n = 0, j = 0;
		if (k.length() == 0)
			return 0;
		char m = k.charAt(j);
		for (i = 0; i < obj.length(); i++) {
			char m2 = obj.charAt(i);
			if (m == m2 || m == getChosung(m2)) {
				if (j == 0)
					n = i;
				j++;
				if (j == k.length())
					return n;
				m = k.charAt(j);
			} else {
				if (j != 0)
					i = n + 1;
				j = 0;
				m = k.charAt(j);
			}
		}
		return -1;
	}

	public static char getChosung(char k) // 초성 가져옴 한글이 아니면 0 반환
	{
		char m = (char) ((k - 0xAC00));
		m = (char) ((m - (m % 28)) / 28);
		m = (char) ((m - (m % 21)) / 21);
		if (m < Cho.length && m >= 0)
			return Cho[m];
		else
			return 0;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_itemadd:
			if (currentTab == 1) {
				Intent intent = new Intent(this, PModifyActivity.class);
				startActivityForResult(intent, ACT_PMODIFY);
			} else if (currentTab == 2) {
				final EditText txtName = new EditText(this);
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("group name : ");
				txtName.setWidth(LayoutParams.MATCH_PARENT);
				builder.setView(txtName);
				builder.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								String name = txtName.getText().toString().replace(",","");
								if (dbhelp.addGroup(name)) {
									dialog.dismiss();
									notifyGroupchanged();
								} else {
									Toast.makeText(MainActivity.this,
											"already group exists",
											Toast.LENGTH_LONG).show();
								}
							}

						});
				builder.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								dialog.dismiss();
							}
						});
				AlertDialog dialog = builder.create();
				dialog.show();
			}
			else break;
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View arg1, int pos, long arg3) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, InfoActivity.class);
		intent.putExtra("info", adapter.plists.get(pos));
		startActivity(intent);

	}

	public void notifyPersonchanged() {
		search_person(editFindPerson.getText().toString());

	}

	public void notifyGroupchanged() {
		search_group(editFindGroup.getText().toString());

	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		final Person person = adapter.plists.get(arg2);
		final String[] menu = { "call", "send", "modify", "delete" };
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		builder.setItems(menu, new DialogInterface.OnClickListener() {
	
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				switch (which) {
				case 0: {
					ArrayList<Phone> phonelist = new ArrayList<Phone>();
					dbhelp.read_phone(phonelist, person.id);
					final String[] menu = new String[phonelist.size()];
					for (int i = 0; i < menu.length; i++)
						menu[i] = phonelist.get(i).number;
					AlertDialog.Builder builder = new AlertDialog.Builder(
							MainActivity.this);
					builder.setItems(menu,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									Call.call(MainActivity.this, menu[which]);

								}
							});
					builder.create().show();
				}
					break;
				case 1: {
					ArrayList<Phone> phonelist = new ArrayList<Phone>();

					dbhelp.read_phone(phonelist, person.id);
					final String[] menu = new String[phonelist.size()];
					for (int i = 0; i < menu.length; i++)
						menu[i] = phonelist.get(i).number;
					AlertDialog.Builder builder = new AlertDialog.Builder(
							MainActivity.this);
					builder.setItems(menu,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									SMS.sms(MainActivity.this, menu[which]);
									dialog.dismiss();

								}
							});
					builder.create().show();
				}
					break;
				case 2: {
					Intent intent = new Intent(MainActivity.this,
							PModifyActivity.class);
					intent.putExtra("info", person);
					intent.putExtra("opt", false);
					startActivityForResult(intent, ACT_PMODIFY);
				}
					break;
				case 3:
					dbhelp.delete(person);
					notifyPersonchanged();
					break;
				}
				dialog.dismiss();
			}
		});
		builder.create().show();
		return false;
	}

	@Override
	protected void onActivityResult(int req, int res, Intent data) {
		switch (req) {
		case ACT_PMODIFY:
			if (res == RESULT_OK) {
				notifyPersonchanged();
			}
			break;
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnSynchronize:
			final ProgressDialog pdialog = new ProgressDialog(MainActivity.this);
			pdialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			pdialog.setTitle("Synchronizing...");
			pdialog.show();
			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub

					try {
						dbhelp.synchronize(getContentResolver(), new DBHelper.OnSynchronizeListener() {
							
							@Override
							public void run(int progress, String text) {
								// TODO Auto-generated method stub
								pdialog.setProgress(progress);
							}
						});
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					pdialog.dismiss();

				}

			}).start();

			break;
		
			
		}
	}

	@Override
	public void onTabChanged(String arg0) {
		// TODO Auto-generated method stub
		if (arg0.equals("tab1")) {
			notifyPersonchanged();
			currentTab = 1;
		} else if (arg0.equals("tab2")) {
			notifyGroupchanged();
			currentTab = 2;
		} else if (arg0.equals("tab3")){
			currentTab=3;
			log.clear();
			dbhelp.readLog(getContentResolver(), log);
		}
		else currentTab=4;
		
		
	}

}
