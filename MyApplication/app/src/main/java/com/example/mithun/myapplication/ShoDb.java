package com.example.mithun.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ShoDb extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 4;
    private static final String DATABASE_NAME = "shodb.db";
    public static final String TABLE_NAME = "bills";
    public static final String COLUMN_item_id = "item_id";
    public static final String COLUMN_bill_no = "bill_no";
    public static final String COLUMN_item_type = "item_type";
    public static final String COLUMN_item_comp = "item_comp";
    public static final String COLUMN_item_qnty = "item_qnty";
    public static final String COLUMN_item_price = "item_price";
    public static final String COLUMN_total = "total";




    public ShoDb(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String DT="DROP TABLE "+TABLE_NAME ;
        db.execSQL(DT);
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + COLUMN_item_id+ " INTEGER PRIMARY KEY," + COLUMN_bill_no + " TEXT,"+COLUMN_item_type + " TEXT,"+ COLUMN_item_comp + " TEXT,"+COLUMN_item_qnty+ " TEXT,"+COLUMN_item_price + " TEXT,"+COLUMN_total + "TEXT )";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        String DT="DROP TABLE "+TABLE_NAME ;
        db.execSQL(DT);
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + COLUMN_item_id+ " INTEGER PRIMARY KEY," + COLUMN_bill_no + " TEXT,"+COLUMN_item_type + " TEXT,"+ COLUMN_item_comp + " TEXT,"+COLUMN_item_qnty+ " TEXT,"+COLUMN_item_price + " TEXT,"+COLUMN_total + "TEXT )";
        db.execSQL(CREATE_TABLE);
    }
    //public String loadHandler() {}
    public void addHandler(String bno,String comp,String item,String qnty, String price ) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_bill_no, bno);
        values.put(COLUMN_item_comp, comp);
        values.put(COLUMN_item_type, item);
        values.put(COLUMN_item_qnty, qnty);
        values.put(COLUMN_item_price, price);
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_NAME, null, values);
        db.close();
    }
    public Cursor findHandler(String bno) {
        String query = "Select * FROM " + TABLE_NAME + " WHERE " + COLUMN_bill_no + " = " + "'" + bno + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        return cursor;
    }
    public void deleteHandler(String bno) {
        String query = "delete from " + TABLE_NAME + " WHERE " + COLUMN_bill_no + " = " + "'" + bno + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
    }
   // public boolean updateHandler(int ID, String name) {}
   public void deleteHandler(String bno,String id) {
      // String query = "delete from " + TABLE_NAME + " WHERE " + COLUMN_bill_no + " = " + "'" + bno + "' and "+COLUMN_item_type + " = "+ "'"+it+"'";
       SQLiteDatabase db = this.getWritableDatabase();
       db.delete(TABLE_NAME,COLUMN_bill_no + " = " + "'" + bno + "' and "+COLUMN_item_id + " = "+id,null);
      // Cursor cursor = db.rawQuery(query, null);
   }
    public void updateHandler(String id,String q) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_item_qnty,q); //These Fields should be your String values of actual column names

        SQLiteDatabase db = this.getWritableDatabase();
       db.update(TABLE_NAME, cv, COLUMN_item_id+" = "+id, null);
    }
}
