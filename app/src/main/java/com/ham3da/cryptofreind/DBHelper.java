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
import com.ham3da.cryptofreind.portfolio.Portfolio;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DBHelper extends SQLiteOpenHelper
{
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "cf_data_file";


    private static final String TABLE_ALARM = "alarms", TABLE_PORTFOLIO = "portfolio";
    private static final String AL_KEY_ID = "id";
    private static final String AL_KEY_SYMBOL = "symbol";
    private static final String AL_KEY_PRICE = "price", PF_KEY_AMOUNT = "amount";
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


    @Override
    public void onCreate(SQLiteDatabase db)
    {
        createTableAlarm(db);
        createTablePortfolio(db);
    }


    private void createTableAlarm(SQLiteDatabase db)
    {
        String CREATE_TABLE_Alarm = "CREATE TABLE IF NOT EXISTS " + TABLE_ALARM + "("
                + AL_KEY_ID + " INTEGER PRIMARY KEY,"
                + AL_KEY_SYMBOL + " VARCHAR(255),"
                + AL_KEY_PRICE + " DOUBLE,"
                + AL_KEY_Status + " TINYINT Default 0,"
                + AL_KEY_RDADTE + " VARCHAR(50),"
                + AL_KEY_ALARM_TYPE + " TINYINT Default 1,"
                + AL_KEY_ALARM_REPEAT + " TINYINT Default 1)";

        db.execSQL(CREATE_TABLE_Alarm);
    }

    private void createTablePortfolio(SQLiteDatabase db)
    {
        String CREATE_TABLE_Alarm = "CREATE TABLE IF NOT EXISTS " + TABLE_PORTFOLIO + "("
                + AL_KEY_ID + " INTEGER PRIMARY KEY,"
                + AL_KEY_SYMBOL + " VARCHAR(255),"
                + PF_KEY_AMOUNT + " DOUBLE,"
                + AL_KEY_RDADTE + " VARCHAR(50) )";

        db.execSQL(CREATE_TABLE_Alarm);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

        if (oldVersion == 1)
        {
            createTablePortfolio(db);
        }

    }

    public void addPortFolio(String symbol, Double amount)
    {
        try
        {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            String now_date = dateFormat.format(Calendar.getInstance().getTime());

            if (!checkPortfolioExist(symbol, amount))
            {
                ContentValues contentValues = new ContentValues();
                contentValues.put(AL_KEY_SYMBOL, symbol);
                contentValues.put(PF_KEY_AMOUNT, amount);
                contentValues.put(AL_KEY_RDADTE, now_date);

                db.insert(TABLE_PORTFOLIO, null, contentValues);
                Toast.makeText(mContext, mContext.getString(R.string.item_added), Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(mContext, mContext.getString(R.string.already_exists), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception exception)
        {
            Toast.makeText(mContext, mContext.getString(R.string.has_error), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "addPortFolio: " + exception.getMessage());

        }
    }

    public void updatePortFolio(String symbol, Double amount, int id)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(AL_KEY_SYMBOL, symbol);
        contentValues.put(PF_KEY_AMOUNT, amount);

        db.update(TABLE_PORTFOLIO, contentValues, "id=?", new String[]{Integer.toString(id)});
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
            Log.e(TAG, "addAlarm: " + exception.getMessage());

        }
    }



    public boolean checkPortfolioExist(String symbol, Double amount)
    {
        Cursor cursor_count = db.rawQuery("SELECT Count(*) FROM " + TABLE_PORTFOLIO + " Where (symbol='" + symbol + "' AND amount=" + amount + " ) Limit 1", null);
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


    public void deletePortfolio(int id)
    {
        db.delete(TABLE_PORTFOLIO, "id=?", new String[]{Integer.toString(id)});
    }


    public ArrayList<Alarm> getAlarms()
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

    public ArrayList<Portfolio> getPortfolios()
    {

        ArrayList<Portfolio> portfolioArrayList = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PORTFOLIO + " Order BY id DESC", null);
        if (cursor.moveToFirst())
        {

            do
            {
                Portfolio portfolio = new Portfolio(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getDouble(2),
                        cursor.getString(3)
                );
                portfolioArrayList.add(portfolio);
            } while (cursor.moveToNext());

        }
        cursor.close();
        return portfolioArrayList;
    }

    public ArrayList<Alarm> getAlarms(int status, String symbol)
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