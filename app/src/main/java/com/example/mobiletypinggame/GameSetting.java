package com.example.mobiletypinggame;

import java.io.Serializable;

public class GameSetting implements Serializable {

    public int language, words, difficulty, progress;


    public GameSetting(int d, int w, int l, int p)
    {
        difficulty=d; words=w; language=l; progress=p;
    }

    public int getLanguage() {
        return language;
    } // kor=0, eng=1

    public int getWords() {
        return words;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public int getProgress() {
        return progress;
    }

    public int getDiffSocre()
    {
        switch (getDifficulty())
        {
            case 0:
                return 1;
            case 1:
                return 4;
            case 2:
                return 7;
        }
        return 4;
    }


    public long getDiffTime()
    {
        switch (getDifficulty())
        {
            case 0:
                return 10000;
            case 1:
                return 7000;
            case 2:
                return 5000;
        }
        return 7000;
    }

    public int getDiffTimeToInt()
    {
        switch (getDifficulty())
        {
            case 0:
                return 10;
            case 1:
                return 7;
            case 2:
                return 5;
        }
        return 7;
    }
}
