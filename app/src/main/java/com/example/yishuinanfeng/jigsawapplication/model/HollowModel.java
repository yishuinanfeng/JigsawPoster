package com.example.yishuinanfeng.jigsawapplication.model;

import android.graphics.Path;

import java.util.ArrayList;

/**
 * Created by Hendricks on 2017/6/12.
 * 一个镂空图形的数据模型
 */

public class HollowModel {
    //Hollow的x，y坐标
    private int hollowX;
    private int hollowY;
    private int width;
    private int height;

    private ArrayList<Path> pathList;

    public int getHollowX() {
        return hollowX;
    }

    public void setHollowX(int hollowX) {
        this.hollowX = hollowX;
    }

    public int getHollowY() {
        return hollowY;
    }

    public void setHollowY(int hollowY) {
        this.hollowY = hollowY;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public ArrayList<Path> getPathList() {
        return pathList;
    }

    public void setPathList(ArrayList<Path> pathList) {
        this.pathList = pathList;
    }
}
