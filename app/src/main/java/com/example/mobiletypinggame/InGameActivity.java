package com.example.mobiletypinggame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class InGameActivity extends AppCompatActivity {
    boolean gameEnd;
    int size, count;
    double score;
    int length;
    long temp;
    double combo, maxCombo;
    final int MAX_KOR_LINE = 512;
    final int MAX_ENG_LINE = 512;
    final int MAX_BTN_NUMBER = 15;
    final int[] btnIds = {R.id.view1,R.id.view2,R.id.view3,R.id.view4,R.id.view5,R.id.view6,R.id.view7,R.id.view8,R.id.view9,R.id.view10,
                        R.id.view11,R.id.view12,R.id.view13,R.id.view14,R.id.view15,R.id.view16};
    GameSetting option; Intent intent, intent_ending;
    TimerTask timerTask, counterTask, diffTask, startTask;
    Timer task_Time, counter_Time, diff_Time, start_Time;
    TextView time, text_count, text_score, diff_count;
    Button[] buttons;
    Random random;
    String [] data; String str; // 사용할 단어 데이터
    EditText editText;
    InputMethodManager imm;
    BackPressHandler pressHandler;


    private void stopTime(TimerTask timerTask, int i) // 시간 초기화
    {
        if(timerTask != null)
        {
            time.setText(i);
            timerTask.cancel();
        }
    }

    private void startCounter() // 플레이 전 준비시간 3초 설정
    {
        final int D=3;
        stopTime(startTask,D);
        startTask = new TimerTask() {
            int count = D;
            @Override
            public void run() {
                if(count==0)
                    cancel();
                time.post(new Runnable() {
                    @Override
                    public void run() {
                        time.setText(Integer.toString(count--));
                    }
                });
            }
        };
        start_Time.schedule(startTask,500, 1000);
    }

    private void timeCounter(int i) // 플레이 시간 설정, 게임 엔딩 로직
    {
        stopTime(counterTask, i);
        final int j=i;
        counterTask = new TimerTask() {
            int counts = j;

            @Override
            public boolean cancel() { // 게임 종료 시
                intent_ending = new Intent(getApplicationContext(), EndingActivity.class);
                int s=Integer.parseInt(text_score.getText().toString());
                int m = (int)maxCombo;
                intent_ending.putExtra("class",new GameResult(s,count,m,option.getDifficulty(),option.progress,option.getLanguage()));
                imm.hideSoftInputFromWindow(editText.getWindowToken(),0);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
                startActivity(intent_ending);
                return super.cancel();
            }

            @Override
            public void run() {

                if(counts<=0)
                    cancel();

                time.post(new Runnable() {
                    @Override
                    public void run() {
                        time.setText(Integer.toString(counts--));
                    }
                });
            }
        };
        counter_Time.schedule(counterTask,3500,1000);
    }

    private void diffCounter(int i) // 난이도 별 단어 시간 설정
    {
        switch (i)
        {
            case 0:
                i = 10;
                break;
            case 1:
                i = 7;
                break;
            case 2:
                i = 5;
                break;
        }
        stopTime(diffTask, i);
        final int j=i;
        diffTask = new TimerTask() {
            int counts = j;

            @Override
            public void run() {
                if(counts==0) {
                    if(gameEnd==true)
                        cancel();
                    else
                        counts = j;
                }
                diff_count.post(new Runnable() {
                    @Override
                    public void run() {
                        diff_count.setText(Integer.toString(counts--));
                    }
                });
            }
        };
        counter_Time.schedule(diffTask,3500,1000);
    }

    private int set_size(int size) // 청크 수 설정
    {
        switch (size){
            case 0 :
                return 5;
            case 1:
                return 7;
        }
        return 5;
    }

    private LineNumberReader set_language(int index) // 언어 설정
    {
        switch (index){
            case 0:  // korean
                data = new String[MAX_KOR_LINE];
                return new LineNumberReader(new InputStreamReader(getResources().openRawResource(R.raw.data_kor)));
            case 1: // english
                data = new String[MAX_ENG_LINE];
                return new LineNumberReader(new InputStreamReader(getResources().openRawResource(R.raw.data_eng)));
        }
        return new LineNumberReader(new InputStreamReader(getResources().openRawResource(R.raw.data_kor)));
    }

    private int gameSet(GameSetting setter) // 게임 플레이 전 셋팅
    {
        int line_index=0;
        LineNumberReader reader;
        size = set_size(setter.getWords()); // 동시에 출력할 단어
        reader = set_language(setter.getLanguage()); // 단어 타입 설정
        try{
            while((str=reader.readLine())!=null) // 언어타입에 맞는 데이터를 str 배열에 불러옴
            {
                data[line_index]=str;
                line_index++;
            }
        }catch (Exception e) {}
        return setter.getProgress();
    }

    private void gamePlay(long diff, GameSetting s) // 인게임 플레이 로직
    {

        int chunk_i = s.getProgress()/s.getDiffTimeToInt(); // 난이도별 청크 갱신 횟수
        int chunk_per = s.getProgress()%s.getDiffTimeToInt();

        if(chunk_per!=0)
            chunk_i++;

        stopTime(timerTask,chunk_i);
        final int temp_time=chunk_i;


        timerTask = new TimerTask()
        {
            int count = temp_time;

            @Override
            public boolean cancel() {
                gameEnd=true;
                return super.cancel();
            }

            @Override
            public void run()
            {
                if(count<=0) {
                    cancel();
                }
                count--;
                refreshChunk();
            }
        };
        task_Time.schedule(timerTask,3500 ,diff);


    }

    private void refreshChunk() // 청크 초기화(난이도 간걱)
    {
        String[] list = new String[size];
        int [] button_chunk = new int[size];
        int [] data_chunk = new int[size];

        for(int i=0; i<=MAX_BTN_NUMBER; i++) // 버튼 초기화
            buttons[i].setText(" ");


        for(int i=0; i<size; i++) { // 가져올 데이터를 size 수 만큼 고르기
            data_chunk[i]=random.nextInt(data.length-1);
            for(int j=0; j<i; j++) {
                if (data_chunk[i] == data_chunk[j]) {
                    i--;
                    break;
                }
            }
        }

        for(int i=0; i<size; i++) { // 활성화활 버튼을 size 수 만큼 고르기
            button_chunk[i] = random.nextInt(MAX_BTN_NUMBER);
            for(int j=0; j<i; j++){
                if(button_chunk[i]==button_chunk[j]){
                    i--;
                    break;
                }
            }
        }

        for(int i=0; i<size; i++) // 리스트에 데이터 삽입
            list[i]=data[data_chunk[i]];
        for(int i=0; i<size; i++) // 버튼에 리스트[i] 삽입
            buttons[button_chunk[i]].setText(list[i]);
    }

    private void keyBoardLanFocus(int i) // 키보드 셋팅
    {
        switch (i)
        {
            case 0:
                editText.setPrivateImeOptions("defaultInputmode=korean;");
                editText.requestFocus();
                imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                break;


            case 1:
                editText.setPrivateImeOptions("defaultInputmode=english;");
                editText.requestFocus();
                imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        pressHandler.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_game);
        setTitle("글로벌 타자연습");
        gameEnd=false;
        count=0; score=0.0; combo=1.0; length=1; maxCombo=0.0; temp=0;
        task_Time = new Timer(); counter_Time = new Timer(); diff_Time=new Timer(); start_Time = new Timer();
        buttons = new Button[MAX_BTN_NUMBER+1];
        time = (TextView)findViewById(R.id.timer);
        editText=(EditText)findViewById(R.id.game_input);
        text_count=(TextView)findViewById(R.id.game_count);
        text_score=(TextView)findViewById(R.id.game_score);
        diff_count=(TextView)findViewById(R.id.diff_count);
        intent=getIntent();
        option=(GameSetting)intent.getSerializableExtra("class");
        pressHandler = new BackPressHandler(this);
        random = new Random();
        for(int i=0; i<=MAX_BTN_NUMBER; i++)
            buttons[i]=(Button)findViewById(btnIds[i]);
        keyBoardLanFocus(option.getLanguage());
        startCounter();
        timeCounter(gameSet(option));
        diffCounter(option.getDifficulty());
        gamePlay(option.getDiffTime(),option);


        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)){
                    for(int j=0; j<=MAX_BTN_NUMBER; j++) {
                        if ((editText.getText().toString().equals(buttons[j].getText().toString())|editText.getText().toString().equals(buttons[j].getText().toString()+"."))){
                            if(!(editText.getText().toString().equals("")|editText.getText().toString().equals(""+".") |
                                    editText.getText().toString().equals(" ")|editText.getText().toString().equals(" "+"."))){
                                text_count.setText(Integer.toString(++count));
                                length=buttons[j].getText().toString().length();
                                score+=temp=Math.round((length+option.getDiffSocre())*Math.log((++combo+1.0))*(length+Integer.parseInt(diff_count.getText().toString())));
                                text_score.setText(Integer.toString((int)score));
                                if(combo>maxCombo)
                                    maxCombo=combo;
                                editText.getText().clear();
                                buttons[j].setText("");
                                Toast.makeText(getApplicationContext(), "+"+temp+"!", Toast.LENGTH_SHORT).show();
                                break;
                            }
                        }
                        else if(j==MAX_BTN_NUMBER){
                            combo = 1;
                            editText.getText().clear();
                        }
                    }
                    return true;
                }
                return  false;
            }
        });



    }

}

