package com.example.parjanya.thedeathlyhallows;

/**
 * Created by Parjanya on 2/26/2015.
 */
public class Question {
    private long Q_ID;
    private String Q_TEXT;
    private String Q_OPTION_1;
    private String Q_OPTION_2;
    private String Q_OPTION_3;
    private String Q_OPTION_4;
    private String Q_ANSWER;
    private String Q_HINT;

    public long getQ_ID(){
        return Q_ID;
    }
    public void setQ_ID(long inp){
        Q_ID = inp;
    }

    public String getQ_TEXT(){
        return Q_TEXT;
    }
    public void setQ_TEXT(String inp){
        Q_TEXT = inp;
    }

    public String getQ_OPTION_1(){
        return Q_OPTION_1;
    }
    public void setQ_OPTION_1(String inp){
        Q_OPTION_1 = inp;
    }

    public String getQ_OPTION_2(){
        return Q_OPTION_2;
    }
    public void setQ_OPTION_2(String inp){
        Q_OPTION_2 = inp;
    }

    public String getQ_OPTION_3(){
        return Q_OPTION_3;
    }
    public void setQ_OPTION_3(String inp){
        Q_OPTION_3 = inp;
    }

    public String getQ_OPTION_4(){
        return Q_OPTION_4;
    }
    public void setQ_OPTION_4(String inp){
        Q_OPTION_4 = inp;
    }

    public String getQ_ANSWER(){
        return Q_ANSWER;
    }
    public void setQ_ANSWER(String inp){
        Q_ANSWER = inp;
    }

    public String getQ_HINT(){
        return Q_HINT;
    }
    public void setQ_HINT(String inp){
        Q_HINT = inp;
    }

    @Override
    public String toString() {
        return Q_TEXT;
    }
}
