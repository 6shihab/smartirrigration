package com.example.smartirrigation;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "agriDatabase.db";
    public static final String data_TABLE_NAME = "agridatabasetable";
    public static final String ID="id";
    public static final String CURRENT_DATE="date";
    public static final String CURRENT_TIME="time";
    public static final String LATITUDE="latitude";
    public static final String LONGITUDE="longitude";
    public static final String MOISTURE="moisture";
    public static final String PH="ph";
    public static final String TEMPERATURE="temperature";
    public static final int version = 1;
    private HashMap hp;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+data_TABLE_NAME+"("+ID+" integer primary key AUTOINCREMENT, "+CURRENT_DATE+" text, "+CURRENT_TIME+" text, "+MOISTURE+" text, "+PH+" text, "+TEMPERATURE+" text, "+LATITUDE+" text,"+LONGITUDE+" text);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS "+data_TABLE_NAME+"");
        onCreate(db);
    }

    public boolean insertData (String moisture, String ph, String temperature,String latitude, String longitude) {
        String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CURRENT_DATE,currentDate);
        contentValues.put(CURRENT_TIME,currentTime);
        contentValues.put(MOISTURE, moisture);
        contentValues.put(PH, ph);
        contentValues.put(TEMPERATURE, temperature);
        contentValues.put(LATITUDE, latitude);
        contentValues.put(LONGITUDE, longitude);
        db.insert(data_TABLE_NAME, null, contentValues);
        return true;
    }

    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+data_TABLE_NAME+" where id="+id+"", null );
        return res;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, data_TABLE_NAME);
        return numRows;
    }

    public boolean updateData (Integer id, String latitude, String longitude, String moisture, String ph, String temperature) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put( "latitude", latitude);
        contentValues.put("longitude", longitude);
        contentValues.put("tds", moisture);
        contentValues.put("ph", ph);
        contentValues.put("temperature", temperature);
        db.update(data_TABLE_NAME, contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public Integer deleteData (Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(data_TABLE_NAME,
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    public ArrayList<String> getAllData() {
        ArrayList<String> all_data = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+data_TABLE_NAME+"", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            all_data.add(res.getString(0) +"-" + res.getString(1) + "-" + res.getString(2) +"-" + res.getString(3) + "-" + res.getString(4) + "-" + res.getString(5));
            res.moveToNext();
        }
        return all_data;
    }

    public void deleteAll() {
        SQLiteDatabase db=this.getReadableDatabase();
        db.execSQL("DELETE FROM "+data_TABLE_NAME+"");

    }
}