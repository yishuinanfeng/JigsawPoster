package com.example.yishuinanfeng.jigsawapplication.model;

import android.graphics.Bitmap;

/**
 * Created by Hendricks on 2017/6/12.
 * 一个要处理的图片的数据模型，包含镂空部分数据
 */

public class PictureModel {
    private Bitmap bitmapPicture;
    private HollowModel hollowModel;

    private int pictureX;
    private int pictureY;

    private float scaleX = 1;
    private float scaleY = 1;

    public float getScaleX() {
        return scaleX;
    }

    public void setScaleX(float scaleX) {
        this.scaleX = scaleX;
    }

    public float getScaleY() {
        return scaleY;
    }

    public void setScaleY(float scaleY) {
        this.scaleY = scaleY;
    }

    private float rotateDelta;

    boolean isSelect;

    public boolean isLastSelect() {
        return isLastSelect;
    }

    public void setLastSelect(boolean lastSelect) {
        isLastSelect = lastSelect;
    }

    boolean isLastSelect;


    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }



    public void setScale(float scale) {
        this.scaleX = scale;
        this.scaleY = scale;
    }

    public float getRotate() {
        return rotateDelta;
    }

    public void setRotate(float rotateDelta) {
        this.rotateDelta = rotateDelta;
    }

    public int getPictureX() {
        return pictureX;
    }

    public void setPictureX(int pictureX) {
        this.pictureX = pictureX;
    }

    public int getPictureY() {
        return pictureY;
    }

    public void setPictureY(int pictureY) {
        this.pictureY = pictureY;
    }

    public Bitmap getBitmapPicture() {
        return bitmapPicture;
    }

    public void setBitmapPicture(Bitmap bitmapPciture) {
        this.bitmapPicture = bitmapPciture;
    }

    public HollowModel getHollowModel() {
        return hollowModel;
    }

    public void setHollowModel(HollowModel hollowModel) {
        this.hollowModel = hollowModel;
    }
}
