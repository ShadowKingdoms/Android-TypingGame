package com.example.mobiletypinggame;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {

    Button start_button, stats_button, exit_button;
    SeekBar seek_Bar;
    RadioGroup diff, words, lan;
    AlertDialog dialog;
    Intent intent;
    GameSetting option;
    TextView seek_Bar_Progress;
    BackPressHandler pressHandler;

    @Override
    public void onBackPressed() {
        pressHandler.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start_button=(Button)findViewById(R.id.btn_Start);
        stats_button=(Button)findViewById(R.id.btn_Stats);
        exit_button=(Button)findViewById(R.id.btn_Exits);
        stats_button=(Button)findViewById(R.id.btn_Stats);
        pressHandler = new BackPressHandler(this);
        setTitle(null);

        stats_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getApplicationContext(),StatsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        exit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                builder.setTitle("게임 종료");
                builder.setMessage("타자연습을 종료하시겠습니까?");

                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                });

                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getApplicationContext(), "취소하였습니다.",Toast.LENGTH_SHORT).show();
                    }
                });

                AlertDialog dialog=builder.create();
                dialog.show();
            }
        });

        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"게임 셋팅을 설정해주세요.",Toast.LENGTH_SHORT).show();
                final View radioView = getLayoutInflater().inflate(R.layout.activity_game_set,null);
                seek_Bar=(SeekBar)radioView.findViewById(R.id.seekBar);
                seek_Bar_Progress=(TextView)radioView.findViewById(R.id.seekBar_time);
                seek_Bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        seek_Bar_Progress.setText(Integer.toString(i));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                });

                diff =(RadioGroup)radioView.findViewById(R.id.radio_difficulty);
                words=(RadioGroup)radioView.findViewById(R.id.radio_words);
                lan =(RadioGroup)radioView.findViewById(R.id.radio_language);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setView(radioView);

                builder.setPositiveButton("시작", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int d=0,w=0,l=0;
                        if(diff.getCheckedRadioButtonId()==R.id.diff_easy) // 난이도 설정
                            d=0;
                        else if(diff.getCheckedRadioButtonId()==R.id.diff_normal)
                            d=1;
                        else if(diff.getCheckedRadioButtonId()==R.id.diff_hard)
                            d=2;
                        if(words.getCheckedRadioButtonId()==R.id.word_size5) // 단어 수 설정
                            w=0;
                        else if(words.getCheckedRadioButtonId()==R.id.word_size7)
                            w=1;
                        if(lan.getCheckedRadioButtonId()==R.id.lan_kor) // 언어 설정
                            l=0;
                        else if(lan.getCheckedRadioButtonId()==R.id.lan_eng)
                            l=1;
                        option = new GameSetting(d,w,l,Integer.parseInt(seek_Bar_Progress.getText().toString()));
                        intent = new Intent(getApplicationContext(), InGameActivity.class);
                        intent.putExtra("class",option);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });

                builder.setNeutralButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getApplicationContext(),"취소하였습니다.",Toast.LENGTH_SHORT).show();
                    }
                });

                dialog = builder.create();
                dialog.show();
            }
        });

    }
}
