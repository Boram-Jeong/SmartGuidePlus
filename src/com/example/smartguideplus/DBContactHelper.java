package com.example.smartguideplus;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.smartguideplus.model.Guide;

public class DBContactHelper extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 1;

	private static final String DATABASE_NAME = "guiderManager";

	private static final String TABLE_NAME = "guide";

	public DBContactHelper(Context context) {
		super(context, SmartGuidePlusInfo.saveDir + DATABASE_NAME, null,
				DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_CONTACTS_TABLE = "CREATE TABLE "
				+ TABLE_NAME
				+ " ( idx INTEGER PRIMARY KEY, creator TEXT, gidx TEXT, date TEXT, name TEXT, image TEXT, os TEXT, device TEXT, description TEXT, download INTEGER);";
		db.execSQL(CREATE_CONTACTS_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

		onCreate(db);
	}

	public void addGuide(Guide guide) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("creator", guide.getCreator());
		values.put("gidx", guide.getGidx());
		values.put("date", guide.getDate());
		values.put("name", guide.getName());
		values.put("image", guide.getImage());
		values.put("os", guide.getOs());
		values.put("device", guide.getDevice());
		values.put("description", guide.getDescription());
		values.put("download", guide.getDownload());

		db.insert(TABLE_NAME, null, values);
		db.close();

		Log.d("db", "insert guide");
	}

	public boolean isDownloaded(int idx) {
		SQLiteDatabase db = this.getReadableDatabase();

		String[] columns = { "idx", "name" };
		String[] params = { String.valueOf(idx) };

		Cursor cursor = db.query(TABLE_NAME, columns, "idx=?", params, null,
				null, null, null);
		if (cursor.getCount() > 0) {
			return true;
		}

		return false;

	}

	public List<Guide> getDownloadedGuide() {
		List<Guide> guideList = new ArrayList<Guide>();
		String selectQuery = "SELECT  * FROM " + TABLE_NAME;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			do {
				Guide guide = new Guide();

				guide.setIdx(Integer.parseInt(cursor.getString(0)));
				guide.setCreator(cursor.getString(1));
				guide.setGidx(cursor.getString(2));
				guide.setDate(cursor.getString(3));
				guide.setName(cursor.getString(4));
				guide.setImage(cursor.getString(5));
				guide.setOs(cursor.getString(6));
				guide.setDevice(cursor.getString(7));
				guide.setDescription(cursor.getString(8));
				guide.setDownload(Integer.parseInt(cursor.getString(9)));

				guideList.add(guide);
			} while (cursor.moveToNext());
		}

		return guideList;
	}

	public void deleteGuide(Guide guide) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_NAME, "gidx = ?", new String[] { guide.getGidx() });
		db.close();
	}

}