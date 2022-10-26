package com.krafte.kogas.data;

public class YoilList implements Comparable<YoilList>{
    int yoil = 0;


    public YoilList(int yoil){
        this.yoil = yoil;
    }

    public int getYoil() {
        return yoil;
    }

    public void setYoil(int yoil) {
        this.yoil = yoil;
    }

    @Override
    public int compareTo(YoilList o) {
        return getYoil() - o.getYoil();
    }
}
