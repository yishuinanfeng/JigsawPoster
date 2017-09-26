package com.example.yishuinanfeng.jigsawapplication.activity;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Path;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yishuinanfeng.jigsawapplication.R;
import com.example.yishuinanfeng.jigsawapplication.customview.JigsawView;
import com.example.yishuinanfeng.jigsawapplication.model.HollowModel;
import com.example.yishuinanfeng.jigsawapplication.model.PictureModel;
import com.example.yishuinanfeng.jigsawapplication.util.SvgParseUtil;
import com.example.yishuinanfeng.jigsawapplication.util.BitmapUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hendricks on 2017/6/13.
 *
 */

public class JigsawActivity extends Activity {
    private ArrayList<PictureModel> mPictureModelList = new ArrayList<>();
    private JigsawView mJigsawView;

    private LinearLayout mEditPicBottomBar;
    private ImageView mClosePicBottomBar;

    private TextView mRotatePicture;
    private TextView mOverturnPicture;

    //被选择的拼图
    private PictureModel mSelectPictureModel;


    private TextView mSavePicture;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpView();
        initJigsawView();
    }

    private void setUpView() {
        mJigsawView = (JigsawView) findViewById(R.id.jigsawview);

        mEditPicBottomBar = (LinearLayout) findViewById(R.id.layout_bottom_edit);
        mClosePicBottomBar = (ImageView) findViewById(R.id.close_edit);

        mRotatePicture = (TextView) findViewById(R.id.txt_edit_rotate);
        mOverturnPicture = (TextView) findViewById(R.id.txt_edit_overturn);
        mSavePicture = (TextView) findViewById(R.id.txt_edit_save);

        mRotatePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelectPictureModel != null){
                    mSelectPictureModel.setRotate(mSelectPictureModel.getRotate() + 90);
                    mJigsawView.refreshView();
                }
            }
        });

        mOverturnPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelectPictureModel != null){
                    mSelectPictureModel.setScaleX(-mSelectPictureModel.getScaleX());
                    mJigsawView.refreshView();
                }
            }
        });


        mSavePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mJigsawView.setNeedHighlight(false);
                mJigsawView.refreshView();
                Bitmap bitmap = BitmapUtil.getBitmapFromView(mJigsawView, mJigsawView.getWidth(), mJigsawView.getHeight());
                if (BitmapUtil.saveBitmap(bitmap)){
                    Toast.makeText(JigsawActivity.this,"海报保存成功",Toast.LENGTH_SHORT).show();
                }else {
                    mJigsawView.setNeedHighlight(true);
                }
            }
        });

        mClosePicBottomBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closePicEditBar();
            }
        });

        mJigsawView.setPictureSelectListener(new JigsawView.PictureSelectListener() {
            @Override
            public void onPictureSelect(PictureModel pictureModel) {
                showPicEditBar(pictureModel);
            }
        });

        mJigsawView.setPictureNoSelectListener(new JigsawView.PictureNoSelectListener() {
            @Override
            public void onPictureNoSelect() {
                closePicEditBar();
            }
        });

        mJigsawView.setPictureCancelSelectListener(new JigsawView.PictureCancelSelectListener() {
            @Override
            public void onPictureCancelSelect() {
                closePicEditBar();
            }
        });
    }

    private void showPicEditBar(PictureModel pictureModel) {
        mSelectPictureModel = pictureModel;
        ObjectAnimator.ofFloat(mEditPicBottomBar,"translationY",
                0).setDuration(200).start();
    }

    private void closePicEditBar(){
        if (mEditPicBottomBar.getTranslationY() == 0){
            ObjectAnimator.ofFloat(mEditPicBottomBar,"translationY",
                    0, mEditPicBottomBar.getHeight()).setDuration(200).start();
        }
    }

    private void initJigsawView() {
        Bitmap mBitmapBackGround = BitmapFactory.decodeResource(getResources(), R.drawable.scene);
        Bitmap picture = BitmapFactory.decodeResource(getResources(),R.drawable.weixin);
        Bitmap picture1 = BitmapFactory.decodeResource(getResources(),R.drawable.cat);
        Bitmap picture2 = BitmapFactory.decodeResource(getResources(),R.drawable.niutuku);

        List<String> pathNameList = new ArrayList<>();
        pathNameList.add("svg_circle.svg");
        pathNameList.add("demo.svg");
        ArrayList<ArrayList<Path>> pathLists = SvgParseUtil.getHollowPaths(pathNameList);


        PictureModel pictureModel = new PictureModel();
        pictureModel.setBitmapPicture(picture);

        HollowModel hollowModel = new HollowModel();
        hollowModel.setHollowX(200);
        hollowModel.setHollowY(100);
        hollowModel.setHeight(400);
        hollowModel.setWidth(400);
        hollowModel.setPathList(pathLists.get(1));
        pictureModel.setHollowModel(hollowModel);


        PictureModel pictureModel2 = new PictureModel();
        pictureModel2.setBitmapPicture(picture1);

        HollowModel hollowModel2 = new HollowModel();
        hollowModel2.setHollowX(300);
        hollowModel2.setHollowY(500);
        hollowModel2.setHeight(500);
        hollowModel2.setWidth(500);
        hollowModel2.setPathList(pathLists.get(0));
        pictureModel2.setHollowModel(hollowModel2);

        PictureModel pictureModel3 = new PictureModel();
        pictureModel3.setBitmapPicture(picture2);

        HollowModel hollowModel3 = new HollowModel();
        hollowModel3.setHollowX(500);
        hollowModel3.setHollowY(1000);
        hollowModel3.setHeight(400);
        hollowModel3.setWidth(400);
        pictureModel3.setHollowModel(hollowModel3);

        mPictureModelList.add(pictureModel);
        mPictureModelList.add(pictureModel2);
        mPictureModelList.add(pictureModel3);

        mJigsawView.setBitmapBackGround(mBitmapBackGround).setPictureModels(mPictureModelList);
    }
}
