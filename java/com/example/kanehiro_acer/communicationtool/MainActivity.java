package com.example.kanehiro_acer.communicationtool;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

        private Button EditBtn;
        private Button FinBtn;
        private EditText editText1;
        private EditText editText2;
        private TextView LabelName_View;
        private TextView display_name;
        private TextView displayIntro;
        private MyHelper mUserDB;
        private String[] getData;
        private String display_nameStr;
        private String displayIntroStr;
        private String user_id;
        private ImageView imageView;
        private final int REQUEST_GALLERY = 0;
        private ArrayList<Bitmap> mBitmap;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            SharedPreferences sp = getSharedPreferences("user_prof", MODE_PRIVATE);
            //ユーザIDの取得
            user_id =sp.getString("user_id", null);
           //String user_name = sp.getString("user_name" , null);
            //editor.putString("user_id" , "kazuyuki");
            //editor.commit();
            display_name = (TextView) findViewById(R.id.displayName);
            displayIntro = (TextView) findViewById(R.id.displayIntro);
            imageView = (ImageView) findViewById(R.id.image_view);

            mUserDB = new MyHelper(this);

                 //読み出して部品に描画する
            if(user_id != null){
                String resultStr = SelectValue(user_id);
                getData = resultStr.split(",");
                //mBitmap = selectImage(user_id);

                for(int i = 0 ; i < getData.length ; i++ ) {
                    Log.v( i + "番目の要素(初回)", getData[i]);
                }
                display_name.setText(getData[1]);
                displayIntro.setText(getData[2]);
                Log.v("自己紹介(DB)", getData[2]);
                //imageView.setImageBitmap(mBitmap.get(0));
            }

           display_nameStr = display_name.getText().toString();
           displayIntroStr = displayIntro.getText().toString();

            EditBtn = (Button) findViewById(R.id.Edit_Btn);
            FinBtn = (Button) findViewById(R.id.Fin_Btn);

            EditBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editText1.setVisibility(View.VISIBLE);
                    editText1.setText(display_nameStr);
                    display_name.setVisibility(View.GONE);
                    editText2.setVisibility(View.VISIBLE);
                    editText2.setText(displayIntroStr);
                    displayIntro.setVisibility(View.GONE);
                }
            });

            FinBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editText1.setVisibility(View.GONE);
                    display_name.setVisibility(View.VISIBLE);
                    editText2.setVisibility(View.GONE);
                    displayIntro.setVisibility(View.VISIBLE);

                    String name_txt = editText1.getText().toString();
                    String intro_txt = editText2.getText().toString();

                    if(user_id != null){
                        valueUpdate(user_id ,name_txt, intro_txt);
                    }else {
                        user_id = valueInsert(name_txt, intro_txt);
                    }

                  SharedPreferences sp = getSharedPreferences("user_prof" , MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("user_id" , user_id);
                    editor.putString("user_name", name_txt);
                    editor.putString("intro_txt", intro_txt);
                    editor.commit();

                    String resultStr = SelectValue(String.valueOf(user_id));
                    getData = resultStr.split(",");
                    //mBitmap = selectImage(user_id);

                    for(int i = 0 ; i < getData.length ; i++ ) {
                        Log.v( i + "番目の要素", getData[i]);
                    }
                    display_name.setText(getData[1]);
                    displayIntro.setText(getData[2]);


                    display_nameStr = display_name.getText().toString();
                    displayIntroStr = displayIntro.getText().toString();
                }
            });

            editText1 = (EditText) findViewById(R.id.editText1);
            editText2 = (EditText) findViewById(R.id.editText2);
            LabelName_View = (TextView) findViewById(R.id.Label_name);

        //もし、画像をクリックしたら
            imageView.setOnClickListener( new View.OnClickListener(){

                public void onClick(View v) {
                    //ギャラリー呼び出し
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent ,REQUEST_GALLERY);
                }
            });
        }

    private String valueUpdate(String user_id, String name,String intro){
        SQLiteDatabase db = mUserDB.getWritableDatabase();

        //列に対応する値をセットする
        ContentValues values = new ContentValues();
        values.put(MyHelper.Columns.NAME, name);
        values.put(MyHelper.Columns.INTRO, intro);
        //values.put(MyHelper.Columns.CREATE_TIME, System.currentTimeMillis());
        values.put(MyHelper.Columns.UPDATE_TIME, System.currentTimeMillis());

        //データベースに行を更新する
    long id = db.update(mUserDB.TABLE_NAME, values, "_ID =" + user_id, null);
        if (id == -1) {
            Log.v("Database" , "行の更新に失敗したよ");
        }
        //データベースを閉じる(処理の終了を伝える)
        //db.close();
        Log.v("Database" , "更新件数 " + String.valueOf(id) + "件");

        String idStr = String.valueOf(id);
        return idStr;
    }

    private String valueInsert(String name,String intro){
        SQLiteDatabase db = mUserDB.getWritableDatabase();

        //列に対応する値をセットする
        ContentValues values = new ContentValues();
        values.put(MyHelper.Columns.NAME, name);
        values.put(MyHelper.Columns.INTRO, intro);
        values.put(MyHelper.Columns.CREATE_TIME, System.currentTimeMillis());
        values.put(MyHelper.Columns.UPDATE_TIME, System.currentTimeMillis());

        //データベースに行を追加する
        long id = db.insert(mUserDB.TABLE_NAME, null, values);
        if (id == -1) {
            Log.v("Database" , "行の追加に失敗したよ");
        }

        //データベースを閉じる(処理の終了を伝える)
        //db.close();

        String idStr = String.valueOf(id);
        return idStr;
    }


    private String SelectValue(String user_id) {
        //queryメソッドでデータ取得
        String[] clos = {"_ID", "NAME", "INTRO", "CREATE_TIME", "UPDATE_TIME"};
        String selection = "_ID =?";
        String[] selectionArgs = {user_id};
        String groupBy = null;
        String having = null;
        String orderBy = null;

        SQLiteDatabase db = mUserDB.getReadableDatabase();
        try {
            Cursor cursor = db.query(mUserDB.TABLE_NAME, clos, selection, selectionArgs, groupBy, having, orderBy);
            //TextViewに表示
            StringBuilder text = new StringBuilder();
            while (cursor.moveToNext()) {
                text.append(cursor.getInt(0));
                text.append("," + cursor.getString(1));
                text.append("," + cursor.getString(2));
                text.append("," + cursor.getString(3));
                text.append("," + cursor.getString(4));
                text.append("\n");
            }

            return text.toString();

        } finally {
            db.close();
        }

    }

    public void onActivityResult (int requestCode, int resultCode, Intent data){

        if(requestCode == REQUEST_GALLERY && resultCode == RESULT_OK){

           try{
               InputStream in = getContentResolver().openInputStream(data.getData());
               Bitmap img = BitmapFactory.decodeStream(in);
               in.close();

               imageView.setImageBitmap(img);
               updateImage(user_id, img);
           }catch (Exception e){
               Log.e("onActivityResult" ,e.getMessage());
           }
       }

    }

    public void updateImage(String user_id ,Bitmap img){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        img.compress( Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bytes = baos.toByteArray();

        SQLiteDatabase db = mUserDB.getWritableDatabase();

        //列に対応する値をセットする
        ContentValues values = new ContentValues();
        values.put(MyHelper.Columns.IMG_DATA, bytes);
        values.put(MyHelper.Columns.UPDATE_TIME, System.currentTimeMillis());

        //long id = db.update(mUserDB.TABLE_NAME, values, "_ID =" + user_id, null);

        long id = db.update(mUserDB.TABLE_NAME, values, "_ID=" + user_id, null);
        if (id == -1) {
            Log.v("Database" , "画像の更新に失敗したよ");
        }
        if (id <= 1 ) {
            Log.v("Database", "画像の更新に成功したよ");
        }
        Log.v("Database", String.valueOf(id));
        Log.v("Database", user_id);
    }



    public ArrayList<Bitmap> selectImage(String user_id){
        SQLiteDatabase db = mUserDB.getReadableDatabase();

        //queryメソッドでデータ取得
        String[] clos = {"IMG_DATA"};
        String selection = "_ID =?";
        String[] selectionArgs = {user_id};
        String groupBy = null;
        String having = null;
        String orderBy = null;
        Cursor cursor = null;

        try {
            cursor = db.query(mUserDB.TABLE_NAME, clos, selection, selectionArgs, groupBy, having, orderBy);
            byte[] bytes = null;
            int i = 0;
            ArrayList<Bitmap> bmp = new ArrayList<>();
            //TextViewに表示

            if (cursor.moveToFirst() ) {
                bytes = cursor.getBlob(0);
            }

                if(bytes != null){
                    Bitmap tmpBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    bmp.add(tmpBitmap);
                }
               // i++;

           return bmp;

        } finally {
            db.close();
        }

       //Bitmap[] bmp = null;
       //return bmp;
    }

}
