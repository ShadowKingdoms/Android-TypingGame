package com.example.mobiletypinggame;

import java.io.Serializable;

public class GameResult implements Serializable {
    int score, count, maxCombo, difficulty, playTime;
    int rank, star, language;
    String nickName;

    public int getScore() {
        return score;
    }

    public int getPlayTime() {
        return playTime;
    }

    public int getCount() {
        return count;
    }

    public int getMaxCombo() {
        return maxCombo;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getTextDifficulty() {
        switch (difficulty){

            case 0:
                return new String("-쉬움-");
            case 1:
                return new String("-보통-");
            case 2:
                return new String("-어려움-");

        }
        return new String("-쉬움-");
    }

    public String getTextRank() // 랭크 확인
    {
        if(count<=1)
            return "F";

        int result = playTime/count;
        if((result<=5)&&(playTime>=100)) {
            rank = 5;
            return "S";
        }
        else if(result<=5) {
            rank = 4;
            return "A";
        }
        else if(result<=6) {
            rank = 3;
            return "B";
        }
        else if(result<=8) {
            rank = 2;
            return "C";
        }
        else if(result<=10) {
            rank = 1;
            return "D";
        }
        else
            return "F";
    }

    public int getRank() {
        return rank;
    }

    public int getStar() {
        return star;
    }

    public String getLanguage() {
        switch (language)
        {
            case 0 :
                return "korDB";

            case 1:
                return "engDB";
        }
        return "statsDB";
    }

    public String getNickName() {
        return nickName;
    }

    public GameResult(int s, int c, int max, int d, int p, int l) {
        if(max<=2)
            max=0;
        else
            max-=2;
        score=s; count=c; maxCombo=max; difficulty=d; playTime=p; rank=0; language=l;
    }

}
