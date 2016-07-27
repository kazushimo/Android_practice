package com.example.kanehiro_acer.communicationtool;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.provider.SyncStateContract;
import android.util.Log;

import java.sql.Blob;

/**
 * Created by kazuyuki on 2016/07/24.
 */
public class MyHelper extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "memo";

    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "(" +
                    Columns._ID + " INTEGER primary key autoincrement," +
                    Columns.NAME + " TEXT," +
                    Columns.INTRO + " TEXT," +
                    Columns.IMG_DATA + " BLOB," +
                    Columns.CREATE_TIME + " INTEGER," +
                    Columns.UPDATE_TIME + " INTEGER)";

    private static final String SQL_ALTER_TABLE =
            "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " +
                    Columns.IMG_DATA + " BLOB";


    public MyHelper(Context context){
        super(context, "DB_COMUTOOL", null, 2);
    }

    public interface Columns extends BaseColumns {
        public static final String NAME = "name";
        public static final String INTRO = "intro";
        public static final String IMG_DATA = "img_data";
        public static final String CREATE_TIME = "create_time";
        public static final String UPDATE_TIME = "update_time";
    }

    //データベースを作成するときに呼ぶ
    public void onCreate(SQLiteDatabase db){
        //Create文を実行する
        db.execSQL(SQL_CREATE_TABLE);
    }

    //データベースのバージョン(コンストラクタの第4引数)が変化した時に呼ばれる
    public void onUpgrade(SQLiteDatabase db, int oldVersion,int newVersion){
        //現時点では何もしない
        db.execSQL(SQL_ALTER_TABLE);
    }

}
