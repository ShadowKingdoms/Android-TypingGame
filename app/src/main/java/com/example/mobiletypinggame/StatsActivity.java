package com.example.mobiletypinggame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

public class StatsActivity extends AppCompatActivity {

    final static int KOR=0;
    final static int ENG=1;
    EditText[] editID, editScore, editCount, editMax, editRank;
    TextView language;
    LinearLayout linear_kor, linear_eng;
    Intent intent;
    SQLiteDatabase sqlDB;
    MyDBHelper[] dbHelper;
    Cursor cursor;

    int formSwitch;

    public boolean alertWarning(int v) // 전적 초기화 시 경고창
    {
        final int i=v;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("초기화 확인");
        builder.setMessage("정말 초기화 하시겠습니까?");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int j) {
                sqlDB = dbHelper[i].getWritableDatabase(); // 쓰기전용으로 db를 불러옴.
                dbHelper[i].onUpgrade(sqlDB, 1, 2); // 숫자 : 버전확인용(필요x)
                sqlDB.close(); // 닫음.
                String strID = "  닉네임"+"\r\n"+"_________"+"\r\n";
                String strScore =  "    점수"+"\r\n"+"_________"+"\r\n";
                String strCount = "맞춘단어"+"\r\n"+"_________"+"\r\n";
                String strMax =  "최대콤보"+"\r\n"+"_________"+"\r\n";
                String strRank =  "    랭크"+"\r\n"+"_________"+"\r\n";
                editID[i].setText(strID);
                editScore[i].setText(strScore);
                editCount[i].setText(strCount);
                editMax[i].setText(strMax);
                editRank[i].setText(strRank);
                Toast.makeText(getApplicationContext(),"초기화되었습니다.",Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog dialog=builder.create();
        dialog.show();
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0,1,0,"한글 전적");
        menu.add(0,2,0,"영어 전적");
        menu.add(0,3,0,"전적 초기화");
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        switch(item.getItemId()) {

            case 1: // 한국 전적
                linear_eng.setVisibility(View.INVISIBLE);
                linear_kor.setVisibility(View.VISIBLE);
                formSwitch=0;
                language.setText("-한글 전적-");
                return true;

            case 2: // 영어 전적
                linear_kor.setVisibility(View.INVISIBLE);
                linear_eng.setVisibility(View.VISIBLE);
                formSwitch=1;
                language.setText("-영어 전적-");
                return true;
            case 3: // 초기화
                 return alertWarning(formSwitch);
        }
    return false;
    }

    public void setStatslist(int i)
    {

        sqlDB=dbHelper[i].getReadableDatabase();
        cursor=sqlDB.rawQuery("SELECT * FROM ListStats",null);
        String strID = "  닉네임"+"\r\n"+"_________"+"\r\n";
        String strScore =  "    점수"+"\r\n"+"_________"+"\r\n";
        String strCount = "맞춘단어"+"\r\n"+"_________"+"\r\n";
        String strMax =  "최대콤보"+"\r\n"+"_________"+"\r\n";
        String strRank =  "    랭크"+"\r\n"+"_________"+"\r\n";

        while (cursor.moveToNext()){
            strID+=cursor.getString(0)+"\r\n";
            strScore+=cursor.getString(1)+"\r\n";
            strCount+=cursor.getString(2)+"\r\n";
            strMax+=cursor.getString(3)+"\r\n";
            strRank+="      "+cursor.getString(5)+"\r\n";
        }

        editID[i].setText(strID);
        editScore[i].setText(strScore);
        editCount[i].setText(strCount);
        editMax[i].setText(strMax);
        editRank[i].setText(strRank);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        setTitle("글로벌 타자연습");
        intent=getIntent();
        editID = new EditText[2];
        editScore = new EditText[2];
        editCount = new EditText[2];
        editMax = new EditText[2];
        editRank = new EditText[2];
        dbHelper = new MyDBHelper[2];
        formSwitch=0;

        linear_kor = (LinearLayout)findViewById(R.id.linear_kor);
        linear_eng = (LinearLayout)findViewById(R.id.linear_eng);

        dbHelper[KOR] = new MyDBHelper(this,"korDB");
        dbHelper[ENG] = new MyDBHelper(this,"engDB");
        language=(TextView)findViewById(R.id.text_language);
        editID[0]=(EditText)findViewById(R.id.editID);
        editScore[0]=(EditText)findViewById(R.id.editScore);
        editCount[0]=(EditText)findViewById(R.id.editCount);
        editMax[0]=(EditText)findViewById(R.id.editMax);
        editRank[0]=(EditText)findViewById(R.id.editRank);
        editID[1]=(EditText)findViewById(R.id.editID2);
        editScore[1]=(EditText)findViewById(R.id.editScore2);
        editCount[1]=(EditText)findViewById(R.id.editCount2);
        editMax[1]=(EditText)findViewById(R.id.editMax2);
        editRank[1]=(EditText)findViewById(R.id.editRank2);

        setStatslist(KOR);
        setStatslist(ENG);

    }




}
