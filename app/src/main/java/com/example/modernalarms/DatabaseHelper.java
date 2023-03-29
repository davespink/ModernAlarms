package com.example.modernalarms;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;

class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "modernData";

    public static final String ALARM_TABLE_NAME = "alarm";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table "
                        + ALARM_TABLE_NAME + "(id integer primary key,start integer,description string,sound string)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ALARM_TABLE_NAME);
        onCreate(db);
    }

    public boolean readAll(ArrayList<Alarm> alarmList, Context context) {

        SQLiteDatabase db = getReadableDatabase();

        try {
            String sql = "Select * from alarm where 1 order by  description  COLLATE NOCASE ASC";
            Cursor cursor =
                    db.rawQuery(sql, null);

            cursor.moveToFirst();

            alarmList.clear();
            /*
            while (!cursor.isAfterLast()) {
                Alarm record = new Alarm(Integer.parseInt(cursor.getString(0)), Integer.parseInt(cursor.getString(1)),
                        cursor.getString(2), cursor.getString(3));

                alarmList.add(record);
                cursor.moveToNext();
            }
            */

            cursor.close();
        } catch (Exception e) {
            Toast.makeText(context, "NOT Read", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public boolean writeAll(ArrayList<Alarm> alarmList, Context context) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues newValues = new ContentValues();
        Alarm alarm;

        for(int i = 0; i < alarmList.size();i++)
        {
            alarm = alarmList.get(i);
            newValues.put("id", alarm.getId());
            newValues.put("start", alarm.getStart());
            newValues.put("description", alarm.getDescription());
            newValues.put("sound", alarm.getSound());

            db.insert("alarm", null, newValues);
        }


      db.close();



        return true;
    }}








