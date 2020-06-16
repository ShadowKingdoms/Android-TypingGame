package com.example.mobiletypinggame;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDBHelper extends SQLiteOpenHelper {
    public MyDBHelper(Context context, String name){
        super(context, name,null,1); // 그룹db 이름(statsDB, engDB) , factory, versionNnumber
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) { // 테이블 생성
        sqLiteDatabase.execSQL("CREATE TABLE ListStats (ID CHAR(20) PRIMARY KEY, Score INTEGER, Count INTEGER, Max INTEGER, Diff CHAR(20), Rank CHAR(5));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) { // 테이블 재생성(초기화후 생성)
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS ListStats"); // TBL 이 존재하면 삭제함.
        onCreate(sqLiteDatabase); // 제생성

    }
}
