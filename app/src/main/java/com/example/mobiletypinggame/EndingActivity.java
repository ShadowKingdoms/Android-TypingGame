package com.example.mobiletypinggame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.Rating;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.regex.Pattern;

public class EndingActivity extends AppCompatActivity {
    GameResult result;
    Intent intent;
    TextView text_count, text_score, text_MaxCombo, text_diff, text_rank;
    RatingBar rating;
    Button saveBtn, returnBtn;
    EditText editText;
    BackPressHandler pressHandler;
    MyDBHelper helper;
    SQLiteDatabase sqlDB;

    @Override
    public void onBackPressed() {
        pressHandler.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ending);
        setTitle("글로벌 타자연습");
        intent=getIntent();
        result=(GameResult)intent.getSerializableExtra("class");
        text_count=(TextView)findViewById(R.id.ending_count);
        text_score=(TextView)findViewById(R.id.ending_score);
        text_MaxCombo=(TextView)findViewById(R.id.ending_maxCombo);
        text_diff=(TextView)findViewById(R.id.ending_diff);
        text_rank=(TextView)findViewById(R.id.ending_Rank);
        rating=(RatingBar)findViewById(R.id.ratingBar);
        editText=(EditText)findViewById(R.id.edit_NickName);
        pressHandler = new BackPressHandler(this);
        editText.setFilters( new InputFilter[]{new InputFilter.LengthFilter(7), filterAlphaNum});

        saveBtn=(Button)findViewById(R.id.btn_Saves);
        returnBtn=(Button)findViewById(R.id.btn_Return);

        text_count.setText(Integer.toString(result.getCount()));
        text_score.setText(Integer.toString(result.getScore()));
        text_MaxCombo.setText(Integer.toString(result.getMaxCombo()));
        text_diff.setText(result.getTextDifficulty());
        text_rank.setText(result.getTextRank());
        rating.setRating(result.getRank());

        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(editText.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "닉네임을 입력하세요!", Toast.LENGTH_SHORT).show();
                }
                else {
                    saveDataFunc();
                }
            }
        });

        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)))
                {
                    if(editText.getText().toString().isEmpty())
                        Toast.makeText(getApplicationContext(), "닉네임을 입력하세요!", Toast.LENGTH_SHORT).show();
                    else
                        return saveDataFunc();
                }
                    return false;
            }
        });



    }

    public boolean saveDataFunc()
    {
        helper = new MyDBHelper(getApplicationContext(),result.getLanguage());
        result.setNickName(editText.getText().toString());
        sqlDB = helper.getWritableDatabase();
        sqlDB.execSQL("INSERT INTO ListStats VALUES( '" + result.getNickName() + "' , '" + result.getScore() + "','" + result.getCount() + "','" + result.getMaxCombo() + "','" + result.getTextDifficulty() + "','" + result.getTextRank() + "');");// 삽입 동작 Querry문 생성.
        sqlDB.close();
        // id, score , count , max , diff , rank 순
        Toast.makeText(getApplicationContext(), "저장되었습니다.", Toast.LENGTH_SHORT).show();
        intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        return true;
    }

    public InputFilter filterAlphaNum = new InputFilter() {

        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) { // 영어만 되게 설정
            Pattern ps = Pattern.compile("^[a-zA-Z0-9!@#$%^&*_,.]*$");
            if (!ps.matcher(source).matches()) {
                return "";
            }
            return null;
        }
    };
}
