package com.example.yishuinanfeng.jigsawapplication.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.example.yishuinanfeng.jigsawapplication.model.HollowModel;
import com.example.yishuinanfeng.jigsawapplication.model.PictureModel;

import java.util.ArrayList;


/**
 * Created by Hendricks on 2017/6/8.
 * 操作多个拼图处理的自定义View
 */

public class JigsawView extends View {
    //绘制图片的画笔
    Paint mMaimPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    //绘制高亮边框的画笔
    Paint mSelectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    PorterDuffXfermode mPorterDuffXfermodeClear = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);

    Bitmap mBitmapBackGround;

    Matrix mMatrix = new Matrix();

    float mLastX;
    float mLastY;

    float mDownX;
    float mDownY;


    double mLastFingerDistance;
    double mLastDegree;

    private boolean mIsDoubleFinger;

    Path mPath = new Path();


    private ArrayList<PictureModel> mPictureModels;

    //触摸点对应的图片模型
    private PictureModel mPicModelTouch;

    private PictureSelectListener mPictureSelectListener;
    private PictureNoSelectListener mPictureNoSelectListener;

    private PictureCancelSelectListener mPictureCancelSelectListner;

    private boolean mIsNeedHighlight = true;

    public void setPictureCancelSelectListner(PictureCancelSelectListener pictureCancelSelectListner) {
        mPictureCancelSelectListner = pictureCancelSelectListner;
    }

    public void setmPictureSelectListener(PictureSelectListener mPictureSelectListener) {
        this.mPictureSelectListener = mPictureSelectListener;
    }

    public void setmPictureNoSelectListener(PictureNoSelectListener mPictureNoSelectListener) {
        this.mPictureNoSelectListener = mPictureNoSelectListener;
    }

    public JigsawView setmPictureModels(ArrayList<PictureModel> mPictureModels) {
        this.mPictureModels = mPictureModels;
        makePicFillHollow();
        return this;
    }


    public JigsawView setmBitmapBackGround(Bitmap mBitmapBackGround) {
        this.mBitmapBackGround = mBitmapBackGround;
        return this;
    }

    public JigsawView(Context context) {
        super(context);
        init();
    }


    public JigsawView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public JigsawView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //关闭硬件加速
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        setBackgroundColor(Color.WHITE);
        mSelectPaint.setColor(Color.RED);
        mSelectPaint.setStyle(Paint.Style.STROKE);
        mSelectPaint.setStrokeWidth(6);
    }

    public void setNeedHighlight(boolean needHighlight) {
        mIsNeedHighlight = needHighlight;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mPictureModels != null && mPictureModels.size() > 0 && mBitmapBackGround != null) {
            //循环遍历画要处理的图片
            for (PictureModel pictureModel : mPictureModels) {
                Bitmap bitmapPicture = pictureModel.getBitmapPicture();
                int pictureX = pictureModel.getPictureX();
                int pictureY = pictureModel.getPictureY();
                float scaleX = pictureModel.getScaleX();
                float scaleY = pictureModel.getScaleY();
                float rotateDelta = pictureModel.getRotate();

                HollowModel hollowModel = pictureModel.getHollowModel();
                ArrayList<Path> paths = hollowModel.getPathList();
                if (paths != null && paths.size() > 0) {
                    for (Path tempPath : paths) {
                        mPath.addPath(tempPath);
                    }
                    drawPicture(canvas, bitmapPicture, pictureX, pictureY, scaleX, scaleY, rotateDelta, hollowModel, mPath);
                } else {
                    drawPicture(canvas, bitmapPicture, pictureX, pictureY, scaleX, scaleY, rotateDelta, hollowModel, null);
                }
            }
            //新建一个layer，新建的layer放置在canvas默认layer的上部，当我们执行了canvas.saveLayer()之后，我们所有的绘制操作都绘制到了我们新建的layer上，而不是canvas默认的layer。
            int layerId = canvas.saveLayer(0, 0, canvas.getWidth(), canvas.getHeight(), null, Canvas.ALL_SAVE_FLAG);

            drawBackGround(canvas);

            //循环遍历画镂空部分
            for (PictureModel pictureModel : mPictureModels) {
                int hollowX = pictureModel.getHollowModel().getHollowX();
                int hollowY = pictureModel.getHollowModel().getHollowY();
                int hollowWidth = pictureModel.getHollowModel().getWidth();
                int hollowHeight = pictureModel.getHollowModel().getHeight();
                ArrayList<Path> paths = pictureModel.getHollowModel().getPathList();
                if (paths != null && paths.size() > 0) {
                    for (Path tempPath : paths) {
                        mPath.addPath(tempPath);
                    }
                    drawHollow(canvas, hollowX, hollowY, hollowWidth, hollowHeight, mPath);
                    mPath.reset();
                } else {
                    drawHollow(canvas, hollowX, hollowY, hollowWidth, hollowHeight, null);
                }
            }

            //把这个layer绘制到canvas默认的layer上去
            canvas.restoreToCount(layerId);

            //绘制选择图片高亮边框
            for (PictureModel pictureModel : mPictureModels) {
                if (pictureModel.isSelect() && mIsNeedHighlight) {
                    canvas.drawRect(getSelectRect(pictureModel), mSelectPaint);
                }
            }
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mBitmapBackGround == null) {
            throw new RuntimeException("mBitmapBackGround is null!");
        }
        int resultWidth = 0;
        int resultHeight = 0;

        int specWidthSize = MeasureSpec.getSize(widthMeasureSpec);
        int specHeightSize = MeasureSpec.getSize(heightMeasureSpec);
        int specWidthMode = MeasureSpec.getMode(widthMeasureSpec);
        int specHeightMode = MeasureSpec.getMode(heightMeasureSpec);

        switch (specWidthMode) {
            case MeasureSpec.EXACTLY:
            case MeasureSpec.UNSPECIFIED:
                resultWidth = specWidthSize;
                break;
            case MeasureSpec.AT_MOST:
                resultWidth = mBitmapBackGround.getWidth() < specWidthSize ? mBitmapBackGround.getWidth() : specWidthSize;
                break;
        }

        switch (specHeightMode) {
            case MeasureSpec.EXACTLY:
            case MeasureSpec.UNSPECIFIED:
                resultHeight = specHeightSize;
                break;
            case MeasureSpec.AT_MOST:
                resultHeight = mBitmapBackGround.getHeight() < specHeightSize ? mBitmapBackGround.getHeight() : specHeightSize;
                break;
        }
        //如果是wrap_content,就让View的大小和背景图一样
        setMeasuredDimension(resultWidth, resultHeight);
    }


    private void drawBackGround(Canvas canvas) {
        canvas.drawBitmap(mBitmapBackGround, 0, 0, null);
    }

    /**
     * 画需要处理的图片
     *
     * @param canvas
     * @param bitmapPicture
     * @param coordinateX
     * @param coordinateY
     * @param scaleX
     * @param
     * @param rotateDelta
     * @param hollowModel
     * @param path
     */
    private void drawPicture(Canvas canvas, Bitmap bitmapPicture, int coordinateX, int coordinateY, float scaleX, float scaleY, float rotateDelta
            , HollowModel hollowModel, Path path) {
        int picCenterWidth = bitmapPicture.getWidth() / 2;
        int picCenterHeight = bitmapPicture.getHeight() / 2;
        mMatrix.postTranslate(coordinateX, coordinateY);
        mMatrix.postScale(scaleX, scaleY, coordinateX + picCenterWidth, coordinateY + picCenterHeight);
        mMatrix.postRotate(rotateDelta, coordinateX + picCenterWidth, coordinateY + picCenterHeight);
        canvas.save();

        //以下是对应镂空部分相交的处理，需要完善，或者不需要
        if (path != null) {
            Matrix matrix1 = new Matrix();
            RectF rect = new RectF();
            path.computeBounds(rect, true);

            int width = (int) rect.width();
            int height = (int) rect.height();

            float hollowScaleX = hollowModel.getWidth() / (float) width;
            float hollowScaleY = hollowModel.getHeight() / (float) height;

            matrix1.postScale(hollowScaleX, hollowScaleY);
            path.transform(matrix1);
            //平移path
            path.offset(hollowModel.getHollowX(), hollowModel.getHollowY());
            //让图片只能绘制在镂空内部，防止滑动到另一个拼图的区域中
            canvas.clipPath(path);
            path.reset();
        } else {
            int hollowX = hollowModel.getHollowX();
            int hollowY = hollowModel.getHollowY();
            int hollowWidth = hollowModel.getWidth();
            int hollowHeight = hollowModel.getHeight();
            //让图片只能绘制在镂空内部，防止滑动到另一个拼图的区域中
            canvas.clipRect(hollowX, hollowY, hollowX + hollowWidth, hollowY + hollowHeight);
        }
        canvas.drawBitmap(bitmapPicture, mMatrix, null);
        canvas.restore();
        mMatrix.reset();
    }

    /**
     * 画底图和镂空部分
     *
     * @param canvas
     * @param hollowX
     * @param hollowY
     * @param hollowWidth
     * @param hollowHeight
     * @param
     */
    private void drawHollow(Canvas canvas, int hollowX, int hollowY, int hollowWidth, int hollowHeight, Path path) {
        mMaimPaint.setXfermode(mPorterDuffXfermodeClear);
        //画镂空
        if (path != null) {
            canvas.save();
            canvas.translate(hollowX, hollowY);
            //缩放镂空部分大小
            scalePathRegion(canvas, hollowWidth, hollowHeight, path);
            canvas.drawPath(path, mMaimPaint);
            canvas.restore();
            mMaimPaint.setXfermode(null);
        } else {
            Rect rect = new Rect(hollowX, hollowY, hollowX + hollowWidth, hollowY + hollowHeight);
            canvas.save();
            canvas.drawRect(rect, mMaimPaint);
            canvas.restore();
            mMaimPaint.setXfermode(null);
        }
    }


    /**
     * 缩放镂空部分大小
     *
     * @param canvas
     * @param hollowWidth
     * @param hollowHeight
     * @param path
     */
    private void scalePathRegion(Canvas canvas, int hollowWidth, int hollowHeight, Path path) {
        //使得不规则的镂空图形填充指定的Rect区域
        RectF rect = new RectF();
        path.computeBounds(rect, true);

        int width = (int) rect.width();
        int height = (int) rect.height();

        float scaleX = hollowWidth / (float) width;
        float scaleY = hollowHeight / (float) height;

        canvas.scale(scaleX, scaleY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mPictureModels == null || mPictureModels.size() == 0) {
            return true;
        }
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_POINTER_DOWN:

                //双指模式
                if (event.getPointerCount() == 2) {
                    mPicModelTouch = getHandlePicModel(event);
                    if (mPicModelTouch != null) {
                        // mPicModelTouch.setSelect(true);
                        resetNoTouchPicsState();


                        mPicModelTouch.setSelect(true);

                        mLastFingerDistance = distanceBetweenFingers(event);
                        mLastDegree = rotation(event);
                        mIsDoubleFinger = true;

                        invalidate();
                    }
                }
                break;

            //单指模式
            case MotionEvent.ACTION_DOWN:

                mLastX = event.getX();
                mLastY = event.getY();

                mDownX = event.getX();
                mDownY = event.getY();

                mPicModelTouch = getHandlePicModel(event);
                if (mPicModelTouch != null) {

                    //每次down重置其他picture选中状态
                    resetNoTouchPicsState();
                    mPicModelTouch.setSelect(true);

                    invalidate();
                }

                // getSelectRect();
                break;
            //单双一起处理
            case MotionEvent.ACTION_MOVE:
                switch (event.getPointerCount()) {
                    //单指模式
                    case 1:
                        if (!mIsDoubleFinger) {
                            if (mPicModelTouch != null) {
                                int dx = (int) (event.getX() - mLastX);
                                int dy = (int) (event.getY() - mLastY);
                                int tempX = mPicModelTouch.getPictureX() + dx;
                                int tempY = mPicModelTouch.getPictureY() + dy;


                                if (checkPictureLocation(mPicModelTouch, tempX, tempY)) {

                                    mPicModelTouch.setPictureX(tempX);
                                    mPicModelTouch.setPictureY(tempY);

                                    mLastX = event.getX();
                                    mLastY = event.getY();
                                    invalidate();
                                }
                            }

                        }

                        break;

                    //双指模式
                    case 2:
                        if (mPicModelTouch != null) {
                            //算出两根手指的距离
                            double fingerDistance = distanceBetweenFingers(event);
                            //当前的旋转角度
                            double currentDegree = rotation(event);
                            //当前手指距离和上一次的手指距离的比即为图片缩放比
                            float scaleRatioDelta = (float) (fingerDistance / mLastFingerDistance);
                            float rotateDelta = (float) (currentDegree - mLastDegree);

                            float tempScaleX = scaleRatioDelta * mPicModelTouch.getScaleX();
                            float tempScaleY = scaleRatioDelta * mPicModelTouch.getScaleY();
                            //对缩放比做限制
                            if (Math.abs(tempScaleX) < 3 && Math.abs(tempScaleX) > 0.3 &&
                                    Math.abs(tempScaleY) < 3 && Math.abs(tempScaleY) > 0.3) {
                                mPicModelTouch.setScaleX(tempScaleX);
                                mPicModelTouch.setScaleY(tempScaleY);
                                mPicModelTouch.setRotate(mPicModelTouch.getRotate() + rotateDelta);
                                invalidate();
                                mLastFingerDistance = fingerDistance;
                            }
                            mLastDegree = currentDegree;
                        }
                        break;
                }
                break;
            //两手指都离开屏幕
            case MotionEvent.ACTION_UP:
//                for (PictureModel pictureModel : mPictureModels) {
//                    pictureModel.setSelect(false);
//                }
                mIsDoubleFinger = false;
                double distance = getDisBetweenPoints(event);

                if (mPicModelTouch != null) {
                    //是否属于滑动，非滑动则有选中状态
                    if (distance < ViewConfiguration.getTouchSlop()) {

                        if (mPicModelTouch.isLastSelect()) {
                            mPicModelTouch.setSelect(false);
                            mPicModelTouch.setLastSelect(false);
                            if (mPictureCancelSelectListner != null) {
                                mPictureCancelSelectListner.onPictureCancelSelect();
                            }

                        } else {
                            mPicModelTouch.setSelect(true);
                            mPicModelTouch.setLastSelect(true);
                            //选中的回调
                            if (mPictureSelectListener != null) {
                                mPictureSelectListener.onPictureSelect(mPicModelTouch);
                            }
                        }
                        invalidate();
                    } else {
                        //滑动则取消所有选择的状态
                        mPicModelTouch.setSelect(false);
                        mPicModelTouch.setLastSelect(false);
                        invalidate();
                    }


                } else {
                    for (PictureModel pictureModel : mPictureModels) {
                        pictureModel.setLastSelect(false);
                    }
                    //没有拼图被选中的回调
                    if (mPictureNoSelectListener != null) {
                        mPictureNoSelectListener.onPictureNoSelect();
                    }

                    invalidate();
                    //return false;//false是将事件交给父View
                }

                break;

            //双指模式中其中一手指离开屏幕
            case MotionEvent.ACTION_POINTER_UP:
                if (mPicModelTouch != null) {
                    mPicModelTouch.setSelect(false);
                    invalidate();
                }
        }
        return true;
    }

    private void resetNoTouchPicsState() {
        //每次down重置其他picture选中状态
        for (PictureModel model : mPictureModels) {
            if (model != mPicModelTouch) {
                model.setSelect(false);
                model.setLastSelect(false);
            }
        }
    }


    /**
     * 外部刷新该View所用
     */
    public void refreshView() {
        invalidate();
    }

    /**
     * 设置选中图片的高亮边框
     */
    private Rect getSelectRect(PictureModel picModel) {
        int hollowX = picModel.getHollowModel().getHollowX();
        int hollowY = picModel.getHollowModel().getHollowY();
        int hollowWidth = picModel.getHollowModel().getWidth();
        int hollowHeight = picModel.getHollowModel().getHeight();

        return new Rect(hollowX, hollowY, hollowX + hollowWidth, hollowY + hollowHeight);
    }


    /**
     * 根据事件点击区域得到对应的PictureModel，如果没有点击到图片所在区域则返回null
     *
     * @param event
     * @return
     */
    private PictureModel getHandlePicModel(MotionEvent event) {
        switch (event.getPointerCount()) {
            case 1:
                int x = (int) event.getX();
                int y = (int) event.getY();
                for (PictureModel picModel : mPictureModels) {
                    int hollowX = picModel.getHollowModel().getHollowX();
                    int hollowY = picModel.getHollowModel().getHollowY();
                    int hollowWidth = picModel.getHollowModel().getWidth();
                    int hollowHeight = picModel.getHollowModel().getHeight();

                    Rect rect = new Rect(hollowX, hollowY, hollowX + hollowWidth, hollowY + hollowHeight);
                    //点在矩形区域中
                    if (rect.contains(x, y)) {
                        return picModel;
                    }
                }
                break;
            case 2:
                int x0 = (int) event.getX(0);
                int y0 = (int) event.getY(0);
                int x1 = (int) event.getX(1);
                int y1 = (int) event.getY(1);
                for (PictureModel picModel : mPictureModels) {
                    int hollowX = picModel.getHollowModel().getHollowX();
                    int hollowY = picModel.getHollowModel().getHollowY();
                    int hollowWidth = picModel.getHollowModel().getWidth();
                    int hollowHeight = picModel.getHollowModel().getHeight();

                    Rect rect = new Rect(hollowX, hollowY, hollowX + hollowWidth, hollowY + hollowHeight);
                    //两个点都在该矩形区域
                    if (rect.contains(x0, y0) || rect.contains(x1, y1)) {
                        return picModel;
                    }
                }
                break;
            default:
                break;

        }
        return null;
    }


    /**
     * 检查图片范围是否超出窗口,此方法还要完善
     *
     * @param mPictureModel
     * @param tempX
     * @param tempY
     * @return
     */
    private boolean checkPictureLocation(PictureModel mPictureModel, int tempX, int tempY) {
        HollowModel hollowModel = mPictureModel.getHollowModel();
        Bitmap picture = mPictureModel.getBitmapPicture();
        return (tempY < hollowModel.getHollowY() + hollowModel.getHeight()) && (tempY + picture.getHeight() > hollowModel.getHollowY())
                && (tempX < hollowModel.getHollowX() + hollowModel.getWidth()) && (tempX + picture.getWidth() > hollowModel.getHollowX());
    }


    /**
     * 使图片尺寸居中填充镂空部分对应的矩形
     */
    private void makePicFillHollow() {
        for (PictureModel jigsawPictureModel : mPictureModels) {
            HollowModel hollow = jigsawPictureModel.getHollowModel();
            Bitmap bitmapPicture = jigsawPictureModel.getBitmapPicture();
            if (bitmapPicture != null) {
                int hollowX = hollow.getHollowX();
                int hollowY = hollow.getHollowY();
                int hollowWidth = hollow.getWidth();
                int hollowHeight = hollow.getHeight();
                int hollowCenterX = hollowX + hollowWidth / 2;
                int hollowCenterY = hollowY + hollowHeight / 2;

                int pictureWidth = bitmapPicture.getWidth();
                int pictureHeight = bitmapPicture.getHeight();

                float scaleX = hollowWidth / (float) pictureWidth;
                float scaleY = hollowHeight / (float) pictureHeight;
                //取大者
                float scale = (scaleX > scaleY) ? scaleX : scaleY;

                Bitmap sourceBitmap = jigsawPictureModel.getBitmapPicture();
                Bitmap dstBitmap = Bitmap.createScaledBitmap(sourceBitmap, (int) (sourceBitmap.getWidth() * scale)
                        , (int) (sourceBitmap.getHeight() * scale), true);
                //   sourceBitmap.recycle();

                //jigsawPictureModel.setScale(scale);
                jigsawPictureModel.setBitmapPicture(dstBitmap);
                //图片位置由镂空部分位置决定
                jigsawPictureModel.setPictureX(hollowCenterX - dstBitmap.getWidth() / 2);
                jigsawPictureModel.setPictureY(hollowCenterY - dstBitmap.getHeight() / 2);
            }
        }
    }

    /**
     * 计算两个手指之间的距离。
     *
     * @param event
     * @return 两个手指之间的距离
     */
    private double distanceBetweenFingers(MotionEvent event) {
        float disX = Math.abs(event.getX(0) - event.getX(1));
        float disY = Math.abs(event.getY(0) - event.getY(1));
        return Math.sqrt(disX * disX + disY * disY);
    }


    // 取旋转角度
    private float rotation(MotionEvent event) {
        double disX = (event.getX(0) - event.getX(1));
        double disY = (event.getY(0) - event.getY(1));
        //弧度
        double radians = Math.atan2(disY, disX);
        return (float) Math.toDegrees(radians);
    }

    private double getDisBetweenPoints(MotionEvent event) {
        float disX = Math.abs(event.getX() - mDownX);
        float disY = Math.abs(event.getY() - mDownY);
        return Math.sqrt(disX * disX + disY * disY);
    }

    /**
     * 图片选中的回调接口
     */
    public interface PictureSelectListener {
        void onPictureSelect(PictureModel pictureModel);
    }

    public interface PictureNoSelectListener {
        void onPictureNoSelect();
    }

    /**
     * 某个图片取消了选中状态
     */
    public interface PictureCancelSelectListener {
        void onPictureCancelSelect();
    }
}

