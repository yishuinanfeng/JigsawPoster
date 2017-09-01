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
    private ArrayList<PictureModel> pictureModelList = new ArrayList<>();
    private JigsawView jigsawView;

    private LinearLayout editPicBottomBar;
    private ImageView closePicBottomBar;

    private TextView rotatePicture;
    private TextView overturnPicture;

    //被选择的拼图
    private PictureModel selectPictureModel;

    private FrameLayout rootLayout;

    private TextView savePicture;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpView();
        initJigsawView();
    }

    private void setUpView() {
        jigsawView = (JigsawView) findViewById(R.id.jigsawview);

        editPicBottomBar = (LinearLayout) findViewById(R.id.layout_bottom_edit);
        closePicBottomBar = (ImageView) findViewById(R.id.close_edit);

        rotatePicture = (TextView) findViewById(R.id.txt_edit_rotate);
        overturnPicture = (TextView) findViewById(R.id.txt_edit_overturn);
        savePicture = (TextView) findViewById(R.id.txt_edit_save);

        rootLayout = (FrameLayout) findViewById(R.id.layout_root);

        rotatePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectPictureModel != null){
                    selectPictureModel.setRotate(selectPictureModel.getRotate() + 90);
                    jigsawView.refreshView();
                }
            }
        });

        overturnPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectPictureModel != null){
                    selectPictureModel.setScaleX(-selectPictureModel.getScaleX());
                    jigsawView.refreshView();
                }
            }
        });


        savePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jigsawView.setNeedHighlight(false);
                jigsawView.refreshView();
                Bitmap bitmap = BitmapUtil.getBitmapFromView(jigsawView,jigsawView.getWidth(),jigsawView.getHeight());
                if (BitmapUtil.saveBitmap(bitmap)){
                    Toast.makeText(JigsawActivity.this,"海报保存成功",Toast.LENGTH_SHORT).show();
                }else {
                    jigsawView.setNeedHighlight(true);
                }
            }
        });

        closePicBottomBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closePicEditBar();
            }
        });

        jigsawView.setmPictureSelectListener(new JigsawView.PictureSelectListener() {
            @Override
            public void onPictureSelect(PictureModel pictureModel) {
                showPicEditBar(pictureModel);
            }
        });

        jigsawView.setmPictureNoSelectListener(new JigsawView.PictureNoSelectListener() {
            @Override
            public void onPictureNoSelect() {
                closePicEditBar();
            }
        });
    }

    private void showPicEditBar(PictureModel pictureModel) {
        selectPictureModel = pictureModel;
        ObjectAnimator.ofFloat(editPicBottomBar,"translationY",
                0).setDuration(200).start();
    }

    private void closePicEditBar(){
        if (editPicBottomBar.getTranslationY() == 0){
            ObjectAnimator.ofFloat(editPicBottomBar,"translationY",
                    0,editPicBottomBar.getHeight()).setDuration(200).start();
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

        pictureModelList.add(pictureModel);
        pictureModelList.add(pictureModel2);
        pictureModelList.add(pictureModel3);

        jigsawView.setmBitmapBackGround(mBitmapBackGround).setmPictureModels(pictureModelList);
    }
}
