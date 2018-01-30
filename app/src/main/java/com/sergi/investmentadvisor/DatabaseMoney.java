package com.sergi.investmentadvisor;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Sergi on 18/01/2018.
 */

public class DatabaseMoney extends SQLiteOpenHelper {

    private static final String name = "databasemoney.db";
    private static final String table_name = "money";

    private static final int version = 1;

    private static final String createQuery = "create table " + table_name + " (name text, amount numeric, type text)";

    public DatabaseMoney(Context context) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("drop table if exists " + table_name);
        onCreate(db);
    }

    public void createMoney(Money money) {
        SQLiteDatabase db = getWritableDatabase();

        if (db != null) {
            ContentValues values = new ContentValues();

            values.put("name", money.getName());
            values.put("amount", money.getAmount());
            values.put("type", money.getType());

            db.insert(table_name, null, values);
            db.close();
        }
    }

    public void deleteMoney(Money money) {
        SQLiteDatabase db = getWritableDatabase();

        if (db != null) {

            db.delete(table_name, "name='" + money.getName() + "'", null);
            db.close();
        }
    }

    public ArrayList<Money> getMoneyData() {

        ArrayList<Money> aListMoney = new ArrayList<>();

        SQLiteDatabase db = getWritableDatabase();

        Cursor c = db.rawQuery(" SELECT * FROM " + table_name, null);

        if (c.moveToFirst()) {
            do {

                String name = c.getString(0);
                float amount = c.getFloat(1);
                String type = c.getString(2);

                Money m = new Money(name, amount, type);

                aListMoney.add(m);

            } while (c.moveToNext());
        }

        c.close();

        return aListMoney;
    }
}
