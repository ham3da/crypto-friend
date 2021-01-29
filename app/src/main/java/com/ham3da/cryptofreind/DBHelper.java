package com.ham3da.cryptofreind;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.ham3da.cryptofreind.alarm.Alarm;
import com.ham3da.cryptofreind.currencydetails.DBCoin;
import com.ham3da.cryptofreind.models.rest.CMCCoin;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper
{
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "cf_data_file";


    private static final String TABLE_ALARM = "alarms";
    private static final String AL_KEY_ID = "id";
    private static final String AL_KEY_SYMBOL = "symbol";
    private static final String AL_KEY_PRICE = "price";
    private static final String AL_KEY_RDADTE = "recoreded_date";
    private static final String AL_KEY_Status = "status";
    private static final String AL_KEY_ALARM_TYPE = "alarm_type";
    private static final String AL_KEY_ALARM_REPEAT = "alarm_repeat";

    public static final int ALARM_TYPE_MORE_THAN = 1;
    public static final int ALARM_TYPE_LESS_THAN = 2;


    public static final int ALARM_REPEAT_ONCE = 1;
    public static final int ALARM_REPEAT_ALWAYS = 2;
    String TAG = "DBHelper";

    Context mContext;
    SQLiteDatabase db;

    public DBHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
        db = this.getWritableDatabase();
    }


    public void closeDb()
    {
        db.close();
    }

    public void onCreate(SQLiteDatabase db)
    {

        String CREATE_TABLE_Alarm = "CREATE TABLE " + TABLE_ALARM + "("
                + AL_KEY_ID + " INTEGER PRIMARY KEY,"
                + AL_KEY_SYMBOL + " VARCHAR(255),"
                + AL_KEY_PRICE + " DOUBLE,"
                + AL_KEY_Status + " TINYINT Default 0,"
                + AL_KEY_RDADTE + " VARCHAR(50),"
                + AL_KEY_ALARM_TYPE + " TINYINT Default 1,"
                + AL_KEY_ALARM_REPEAT + " TINYINT Default 1)";

        db.execSQL(CREATE_TABLE_Alarm);
    }

    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)
    {
    }

    public void addAlarm(String symbol, Double price, int alarm_type, int alarm_repeat)
    {
        try
        {
            if (!checkAlarmExist(symbol, price, alarm_type))
            {
                ContentValues contentValues = new ContentValues();
                contentValues.put(AL_KEY_SYMBOL, symbol);
                contentValues.put(AL_KEY_PRICE, price);
                contentValues.put(AL_KEY_Status, 1);

                contentValues.put(AL_KEY_ALARM_TYPE, alarm_type);
                contentValues.put(AL_KEY_ALARM_REPEAT, alarm_repeat);
                db.insert(TABLE_ALARM, null, contentValues);
                Toast.makeText(mContext, mContext.getString(R.string.alarm_added), Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(mContext, mContext.getString(R.string.already_exists), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception exception)
        {
            Toast.makeText(mContext, mContext.getString(R.string.has_error), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "addAlarm: "+exception.getMessage() );

        }
    }

    public boolean checkAlarmExist(String symbol, Double price, int alarm_type)
    {
        Cursor cursor_count = db.rawQuery("SELECT Count(*) FROM " + TABLE_ALARM + " Where (symbol='" + symbol + "' AND price=" + price + " AND status=0 AND alarm_type=" + alarm_type + ") Limit 1", null);
        if (cursor_count.moveToFirst())
        {
            cursor_count.moveToFirst();
            int count = cursor_count.getInt(0);
            cursor_count.close();
            return count >= 1;
        }
        cursor_count.close();

        return false;
    }

    public void updateAlarm(String symbol, Double price, int coin_id)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(AL_KEY_SYMBOL, symbol);
        contentValues.put(AL_KEY_PRICE, price);

        db.update(TABLE_ALARM, contentValues, "id=?", new String[]{Integer.toString(coin_id)});
    }

    public void updateAlarmRDate(String date, int coin_id)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(AL_KEY_RDADTE, date);
        db.update(TABLE_ALARM, contentValues, "id=?", new String[]{Integer.toString(coin_id)});
    }

    public void updateAlarmStatus(int status, int coin_id)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(AL_KEY_Status, status);
        db.update(TABLE_ALARM, contentValues, "id=?", new String[]{Integer.toString(coin_id)});
    }

    public void updateAlarmDateStatus(String date, int status, int coin_id)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(AL_KEY_Status, status);
        contentValues.put(AL_KEY_RDADTE, date);
        db.update(TABLE_ALARM, contentValues, "id=?", new String[]{Integer.toString(coin_id)});
    }


    public void deleteAllAlarms()
    {
        db.delete(TABLE_ALARM, null, null);
    }

    public Alarm getGetAlarm(int id)
    {
        Alarm verse = null;
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ALARM + " Where (id=" + id + ") Limit 1", null);
        if (cursor.moveToFirst())
        {
            verse = new Alarm(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getDouble(2),
                    cursor.getInt(3),
                    cursor.getString(4),
                    cursor.getInt(5),
                    cursor.getInt(6)
            );
        }
        cursor.close();
        return verse;
    }

    public Alarm getGetAlarm(int id, int status)
    {
        Alarm alarm = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ALARM + " Where (id=" + id + " AND status=" + status + ") Limit 1", null);
        if (cursor.moveToFirst())
        {
            alarm = new Alarm(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getDouble(2),
                    cursor.getInt(3),
                    cursor.getString(4),
                    cursor.getInt(5),
                    cursor.getInt(6)
            );
        }
        cursor.close();
        return alarm;
    }

    public void deleteAlarm(int id)
    {
        db.delete(TABLE_ALARM, "id=?", new String[]{Integer.toString(id)});
    }


    public ArrayList<Alarm> getGetAlarms()
    {

        ArrayList<Alarm> alarmArrayList = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ALARM + " Order BY id DESC", null);
        if (cursor.moveToFirst())
        {
            do
            {
                Alarm alarm = new Alarm(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getDouble(2),
                        cursor.getInt(3),
                        cursor.getString(4),
                        cursor.getInt(5),
                        cursor.getInt(6)
                );
                alarmArrayList.add(alarm);
            } while (cursor.moveToNext());

        }
        cursor.close();
        return alarmArrayList;
    }

    public ArrayList<Alarm> getGetAlarms(int status, String symbol)
    {
        ArrayList<Alarm> alarmArrayList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ALARM + " Where (status=" + status + " AND symbol LIKE '" + symbol + "')", null);
        if (cursor.moveToFirst())
        {
            do
            {
                Alarm alarm = new Alarm(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getDouble(2),
                        cursor.getInt(3),
                        cursor.getString(4),
                        cursor.getInt(5),
                        cursor.getInt(6)
                );
                alarmArrayList.add(alarm);
            } while (cursor.moveToNext());

        }
        cursor.close();
        return alarmArrayList;
    }

}