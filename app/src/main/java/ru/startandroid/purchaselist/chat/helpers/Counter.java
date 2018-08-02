package ru.startandroid.purchaselist.chat.helpers;

/**
 * Created by user on 29/03/2018.
 */

public class Counter {

    private int maxValue = 0;
    private int countValue = 0;

    public Counter(int maxValue){
        this.maxValue = maxValue;
    }

    public boolean add(){
        if(countValue < maxValue) {
            countValue++;
            return true;
        }
        return false;
    }
    public boolean deduct(){
        if(countValue > 0) {
            countValue--;
            return true;
        }
        return false;
    }
}
