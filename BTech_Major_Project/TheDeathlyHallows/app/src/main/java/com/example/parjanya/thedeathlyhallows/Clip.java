package com.example.parjanya.thedeathlyhallows;

/**
 * Created by Parjanya on 4/5/2015.
 */
public class Clip {
    private long CLIP_ID;
    private String CLIP_DATA;

    public long getCLIP_ID(){
        return CLIP_ID;
    }
    public void setCLIP_ID(long id){
        CLIP_ID=id;
    }

    public String getCLIP_DATA(){
        return CLIP_DATA;
    }
    public void setCLIP_DATA(String data){
        CLIP_DATA=data;
    }

    @Override
    public String toString() {
        return CLIP_DATA;
    }
}
