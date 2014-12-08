package com.termproject.addressbook;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.MediaStore;

public class DBHelper extends SQLiteOpenHelper {
	public static DBHelper helper;

	abstract class DBTable {
		class Element {
			final String Name;
			final String type;

			public Element(String name, String type) {
				Name = name;
				this.type = type;
			}

			public String toString() {
				return Name + " " + type;
			}

			public boolean isAuto() {
				if (type.toLowerCase().indexOf("autoincrement") >= 0)
					return true;
				else
					return false;
			}
		}
		public Element[] elements;
		public String table;

		public long insert(SQLiteDatabase db, Object[] datas) {
			ContentValues cvalues = new ContentValues();
			for (int i = 0; i < elements.length; i++) {

				if (datas[i] != null) {
					if (datas[i] instanceof byte[]) {
						cvalues.put(elements[i].Name, (byte[]) datas[i]);
					} else
						cvalues.put(elements[i].Name, String.valueOf(datas[i]));
				}
			}
			return db.insert(table, null, cvalues);
		}

		public void Create(SQLiteDatabase db) {
			String sql = "CREATE TABLE " + table + "(";
			for (Element el : elements)
				sql += el.toString() + ",";
			db.execSQL(sql.substring(0, sql.length() - 1) + ")");
		}

		public int update(SQLiteDatabase db, Object[] datas, String selection,
				String[] selectionArgs) {
			ContentValues cvalues = new ContentValues();
			for (int i = 0; i < datas.length; i++) {
				if (datas[i] != null)
					cvalues.put(elements[i].Name, String.valueOf(datas[i]));
			}
			return db.update(table, cvalues, selection, selectionArgs);
		}
	}

	class DBPhoto extends DBTable {
		public DBPhoto() {

			table = "photo";
			elements = new Element[] {
					new Element(_ID, "INTEGER PRIMARY KEY AUTOINCREMENT"),
					new Element(photo_uri, "TEXT"), new Element(data, "BLOB"), };
		}

		public static final String photo_uri = "uri";
		public static final String _ID = "_id";
		public static final String data = "pdata";
	}

	class DBPerson extends DBTable {
		public DBPerson() {
			table = "info_person";
			elements = new Element[] {
					new Element(_ID, "INTEGER PRIMARY KEY AUTOINCREMENT"),
					new Element(name, "TEXT"), new Element(icon, "INTEGER"),
					new Element(connected_id, " TEXT"),
					new Element(group, "TEXT") };
			// TODO Auto-generated constructor stub
		}

		public static final String group = "groups";
		public static final String _ID = "_id";
		public static final String name = "name";
		public static final String icon = "photoid";
		public static final String connected_id = "connid";
	}

	class DBGroup extends DBTable {
		public DBGroup() {
			
			table = "groups";
			elements = new Element[] {
					new Element(_ID, "INTEGER PRIMARY KEY AUTOINCREMENT"),
					new Element(name, "TEXT") };
		}

		public static final String _ID = "_id";
		public static final String name = "name";
	}

	class DBPhone extends DBTable {
		public DBPhone() {
			table = "phones";

			elements = new Element[] {
					new Element(_ID, "INTEGER PRIMARY KEY AUTOINCREMENT"),
					new Element(PersonID, "INTEGER"),
					new Element(number, "TEXT"), new Element(type, "INTEGER"), };
			// TODO Auto-generated constructor stub
		}

		public static final String _ID = "_id";
		public static final String PersonID = "person_id";
		public static final String number = "number";
		public static final String type = "type";

		public static final int TYPE_HOME = 0;
		public static final int TYPE_MOBILE = 1;
		public static final int TYPE_WORK = 2;
		public static final int TYPE_FAX_HOME = 3;
		public static final int TYPE_FAX_WORK = 4;
		public static final int TYPE_OTHER = 5;

	}

	class DBEmail extends DBTable {
		public DBEmail() {
			table = "emails";
			elements = new Element[] {
					new Element(_ID, "INTEGER PRIMARY KEY AUTOINCREMENT"),
					new Element(PersonID, "INTEGER"),
					new Element(Address, "TEXT"), new Element(type, "INTEGER"), };
			// TODO Auto-generated constructor stub
		}

		public static final String _ID = "_id";
		public static final String PersonID = "person_id";
		public static final String Address = "address";
		public static final String type = "type";
		public static final int TYPE_HOME = 0;
		public static final int TYPE_MOBILE = 1;
		public static final int TYPE_WORK = 2;
		public static final int TYPE_OTHER = 3;

	}
	DBPerson person;
	DBPhone phone;
	DBEmail email;
	DBGroup group;
	DBPhoto photo;
	public DBHelper(Context context) {
		super(context, "ads.db", null, 1);
		// TODO Auto-generated constructor stub
		person = new DBPerson();
		phone = new DBPhone();
		email = new DBEmail();
		group = new DBGroup();
		photo = new DBPhoto();
		
	}

	public long add(String uri, byte[] data) {
		return photo.insert(getWritableDatabase(), new Object[] { null, uri,
				data });
	}
	
	public long add(Person p) {
		return person.insert(getWritableDatabase(), new Object[] { null,
				p.name, p.photoID, null, p.getGroup() });
	}

	public void add(Phone p) {
		phone.insert(getWritableDatabase(), new Object[] { null, p.pid,
				p.number, p.type });
	}

	public void add(Email e) {
		email.insert(getWritableDatabase(), new Object[] { null, e.pid,
				e.address, e.type });
	}

	public void update(Person p) {
		person.update(getWritableDatabase(), new Object[] { null, p.name,
				p.photoID, null, p.getGroup() }, DBPerson._ID + "=?",
				new String[] { p.id });
	}

	public void update(Email e) {
		email.update(getWritableDatabase(), new Object[] { null, null,
				e.address, e.type }, DBEmail._ID + "=?", new String[] { e.id });
	}

	public void update(Phone p) {
		phone.update(getWritableDatabase(), new Object[] { null, null,
				p.number, p.type }, DBPhone._ID + "=?", new String[] { p.id });
	}

	public void delete(Person p) {
		SQLiteDatabase wdb = getWritableDatabase();
		wdb.delete(person.table, DBPerson._ID + "=?", new String[] { p.id });
		wdb.delete(phone.table, DBPhone.PersonID + "=?", new String[] { p.id });
		wdb.delete(email.table, DBEmail.PersonID + "=?", new String[] { p.id });
	}

	public void delete(int photoID) {
		getWritableDatabase().delete(photo.table, DBPhoto._ID + "=?",
				new String[] { String.valueOf(photoID) });
	}

	public void delete(Phone p) {
		getWritableDatabase().delete(phone.table, DBPhone._ID + "=?",
				new String[] { p.id });
	}

	public void delete(Email e) {
		getWritableDatabase().delete(email.table, DBEmail._ID + "=?",
				new String[] { e.id });
	}
	public String getNameByNumber(String num){
		
		SQLiteDatabase rdb = getReadableDatabase();
		Cursor cursor = rdb.query(phone.table, new String[]{DBPhone.PersonID}, DBPhone.number + "=?", new String[]{num}, null,null,null);
		if(cursor.moveToFirst())
		{
			Cursor data = rdb.query(person.table,new String[]{DBPerson.name},DBPerson._ID + "=?",new String[]{cursor.getString(0)},null,null,null);
			if(data.moveToFirst()) return data.getString(0);
			else return null;
		}
		return null;
	}
	public void delete(String id) {
		SQLiteDatabase wdb = getWritableDatabase();
		wdb.delete(person.table, DBPerson._ID + "=?", new String[] { id });
		wdb.delete(phone.table, DBPhone.PersonID + "=?", new String[] { id });
		wdb.delete(email.table, DBEmail.PersonID + "=?", new String[] { id });
	}

	public void read_email(ArrayList<Email> list, String id) {
		SQLiteDatabase read_db = getReadableDatabase();
		Cursor cursor = read_db.query(email.table, null, DBEmail.PersonID + "=?",
				new String[] { id }, null, null, null);
		int addressidx = cursor.getColumnIndex(DBEmail.Address);
		int typeidx = cursor.getColumnIndex(DBEmail.type);
		int ididx = cursor.getColumnIndex(DBEmail._ID);
		while (cursor.moveToNext()) {
			list.add(new Email(cursor.getString(ididx), id, cursor
					.getString(addressidx), cursor.getInt(typeidx)));
		}
	}

	public void read_phone(ArrayList<Phone> list, String id) {
		SQLiteDatabase read_db = getReadableDatabase();
		Cursor cursor = read_db.query(phone.table, null, DBPhone.PersonID + "=?",
				new String[] { id }, null, null, null);
		int numidx = cursor.getColumnIndex(DBPhone.number);
		int typeidx = cursor.getColumnIndex(DBPhone.type);
		int ididx = cursor.getColumnIndex(DBPhone._ID);
		while (cursor.moveToNext()) {
			list.add(new Phone(cursor.getString(ididx), id, cursor
					.getString(numidx), cursor.getInt(typeidx)));
		}
	}

	public byte[] getPhoto(int photoid) {
		Cursor cursor = getReadableDatabase().query(photo.table,
				new String[] { DBPhoto.data }, DBPhoto._ID + "=?",
				new String[] { String.valueOf(photoid) }, null, null, null);
		if (cursor.moveToFirst()) {
			return cursor.getBlob(0);
		}
		return null;
	}

	public int getPhotoIDfromUri(Uri uri) {
		Cursor cursor = getReadableDatabase().query(photo.table,
				new String[] { DBPhoto._ID }, DBPhoto.photo_uri + "=?",
				new String[] { uri.toString() }, null, null, null);
		if (cursor.moveToNext()) {
			return cursor.getInt(0);
		}
		return -1;
	}

	interface OnSynchronizeListener {
		void run(int progress, String text);
	}

	private static byte[] readFromStream(InputStream pInputStream) {
		try {
			if (pInputStream == null) {
				return null;
			}

			int lBufferSize = 1024;
			byte[] lByteBuffer = new byte[lBufferSize];

			int lBytesRead = 0;
			ByteArrayOutputStream lByteArrayOutputStream = new ByteArrayOutputStream(
					lBufferSize * 2);

			try {
				while ((lBytesRead = pInputStream.read(lByteBuffer)) != -1) {
					lByteArrayOutputStream.write(lByteBuffer, 0, lBytesRead);
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}

			byte[] lDataBytes = lByteArrayOutputStream.toByteArray();

			return lDataBytes;
		} catch (Exception e) {
			return null;
		}
	}



	public void synchronize(ContentResolver resolver,
			OnSynchronizeListener listener) throws Exception {
		String name, id, p_id;
		int photoID, customPID;
		SQLiteDatabase read_db = getReadableDatabase();
		SQLiteDatabase write_db = getWritableDatabase();
		Cursor cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI,
				new String[] { ContactsContract.Contacts._ID,
						ContactsContract.Contacts.DISPLAY_NAME,
						ContactsContract.Contacts.PHOTO_ID }, null, null,
				ContactsContract.Contacts.DISPLAY_NAME + " ASC");
		while (cursor.moveToNext()) {
			name = cursor.getString(1);

			if (listener != null)
				listener.run(cursor.getPosition() * 100 / cursor.getCount(),
						name);
			id = cursor.getString(0);
			/* get photo and put custom db */
			photoID = cursor.getInt(2);

			if (photoID != 0) {
				Uri uri;
				try {
					uri = ContentUris.withAppendedId(
							ContactsContract.Contacts.CONTENT_URI,
							Long.parseLong(id));
					if (uri != null) {
						customPID = getPhotoIDfromUri(uri);
						if (customPID < 0) {
							InputStream input = ContactsContract.Contacts
									.openContactPhotoInputStream(resolver, uri);
							if (input != null) {
								customPID = (int) add(uri.toString(),
										readFromStream(input));
							}
							else throw null;
						}
						photoID = customPID;
					} else {
						photoID = 0;
					}
				} catch (Exception e) {
					photoID = 0;
					e.printStackTrace();
				}

			}
			/* find person from connected id */
			Cursor find = read_db.query(person.table, new String[] {},
					DBPerson.connected_id + "=? AND " + DBPerson.name + "=?",
					new String[] { id, name }, null, null, null);
			if (find.moveToNext()) {
				p_id = find.getString(0);
			} else {
				p_id = String.valueOf(person.insert(write_db, new Object[] {
						null, name, String.valueOf(photoID), id, "" }));

			}
			/* update phone */
			Cursor data = resolver.query(
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
					new String[] {
							ContactsContract.CommonDataKinds.Phone.NUMBER,
							ContactsContract.CommonDataKinds.Phone.TYPE },
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?",
					new String[] { id }, null);
			while (data.moveToNext()) {
				String num = data.getString(0);
				int type = data.getInt(1);
				String num2 = "";
				for (int i = 0; i < num.length(); i++) {
					char k = num.charAt(i);
					if (k >= '0' && k <= '9') {
						num2 += k;
					}
				}
				find = read_db.query(phone.table, new String[] {},
						DBPhone.PersonID + "=? AND " + DBPhone.number + "=?",
						new String[] { p_id, num2 }, null, null, null);
				if (!find.moveToNext()) {

					phone.insert(write_db, new Object[] { null, p_id, num2,
							type });
				}
			}
			data = resolver.query(
					ContactsContract.CommonDataKinds.Email.CONTENT_URI,
					new String[] {
							ContactsContract.CommonDataKinds.Email.DATA,
							ContactsContract.CommonDataKinds.Email.TYPE },
					ContactsContract.CommonDataKinds.Email.CONTACT_ID + "=?",
					new String[] { id }, null);
			while (data.moveToNext()) {
				String address = data.getString(0);
				int type = data.getInt(1);
				find = read_db.query(email.table, new String[] {},
						DBEmail.PersonID + "=? AND " + DBEmail.Address + "=?",
						new String[] { p_id, address }, null, null, null);
				if (!find.moveToNext()) {
					email.insert(write_db, new Object[] { null, p_id, address,
							type });
				}
			}
			if (listener != null)
				listener.run(cursor.getPosition() * 100 / cursor.getCount(),
						name + " done.");
		}
		cursor.close();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		person.Create(db);
		email.Create(db);
		phone.Create(db);
		group.Create(db);
		photo.Create(db);

	}

	public void read_group(GroupManager manager) {
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(group.table, null, null, null, null, null,
				DBGroup.name + " ASC");
		int nameidx = cursor.getColumnIndex(DBGroup.name);
		while (cursor.moveToNext()) {
			manager.newGroup(cursor.getString(nameidx));

		}
	}

	public boolean addGroup(String name) {
		SQLiteDatabase rdb = getReadableDatabase();
		Cursor cursor = rdb.query(group.table, new String[] {}, DBGroup.name
				+ "=?", new String[] { name }, null, null, null);
		if (cursor.moveToNext()) {
			return false;
		}
		group.insert(getWritableDatabase(), new Object[] { null, name });
		return true;
	}

	public void read(ArrayList<Person> plists) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(person.table, null, null, null, null, null,
				DBPerson.name + " ASC");
		int nameidx = cursor.getColumnIndex(DBPerson.name);
		int ididx = cursor.getColumnIndex(DBPerson._ID);
		int photoidx = cursor.getColumnIndex(DBPerson.icon);
		int groupidx = cursor.getColumnIndex(DBPerson.group);
		while (cursor.moveToNext()) {
			plists.add(new Person(cursor.getString(nameidx), cursor
					.getString(ididx), cursor.getInt(photoidx), cursor
					.getString(groupidx)));
		}
	}

	public void readLog(ContentResolver resolver, CommunicationLog log,
			ArrayList<Phone> plists) {
		String query = "IN(";
		for (Phone p : plists)
			query += "'" + p.number + "',";
		if (query.length() > 3)
			query = query.substring(0, query.length() - 1);
		query += ")";
		Cursor cursor = resolver.query(CallLog.Calls.CONTENT_URI, null,
				CallLog.Calls.NUMBER + " " + query, null, null);
		log.readCalls(cursor);
		cursor = resolver.query(Uri.parse("content://sms/inbox"), null,
				"address " + query, null, null);
		log.readSMS(cursor, SMS.TYPE_RECEIVED);
		cursor = resolver.query(Uri.parse("content://sms/sent"), null,
				"address " + query, null, null);
		log.readSMS(cursor, SMS.TYPE_SENT);

		Collections.sort(log);
		
	}

	public void readLog(ContentResolver resolver, CommunicationLog log) {
		Cursor cursor = resolver.query(CallLog.Calls.CONTENT_URI, null, null,
				null, CallLog.Calls.DATE + " DESC");
		log.readCalls(cursor);
		cursor = resolver.query(Uri.parse("content://sms/inbox"), null, null,
				null, "date DESC");
		log.readSMS(cursor, SMS.TYPE_RECEIVED);
		cursor = resolver.query(Uri.parse("content://sms/sent"), null, null,
				null, "date DESC");
		log.readSMS(cursor, SMS.TYPE_SENT);
		Collections.sort(log);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS " + person.table);
		db.execSQL("DROP TABLE IF EXISTS " + photo.table);
		db.execSQL("DROP TABLE IF EXISTS " + email.table);
		db.execSQL("DROP TABLE IF EXISTS " + group.table);
		db.execSQL("DROP TABLE IF EXISTS " + phone.table);
		onCreate(db);
	}

}

