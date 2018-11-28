package com.webank.mbank.facescan;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by jasoncai on 2018/11/1.
 */

public class ScanView extends View {

    public static final String TAG = "ScanView";

    /**
     * 1、各种自定义属性的默认值
     */
    private static final int DF_SCAN_STEP = 10;
    private static final int DF_SCAN_PERIOD = 17;
    private static final int DF_HORIZONTAL_SQUARE_NUMBER = 35;
    private static final int DF_VERTICAL_SQUARE_NUMBER = 15;
    private static final float DF_CIRCLE_RADIUS = 2;
    private static final int DF_PAINT_WIDTH = 1;
    private static final int DF_BACKGROUND_COLOR = Color.parseColor("#800A0834");

    /**
     * 2、竖向不同透明度的正方形数：
     * <p>
     * 正方形数的一半
     */
    private int verticalSquareNumberTop;
    /**
     * 0-10%透明度的正方形数
     */
    private int verticalSquareNumberTop1;
    /**
     * 10-90%透明度的正方形数
     */
    private int verticalSquareNumberTop2;
    /**
     * 正方形数的另外一半
     */
    private int verticalSquareNumberBottom;
    /**
     * 90-10%透明度的正方形数
     */
    private int verticalSquareNumberBottom1;
    /**
     * 10-0%透明度的正方形数
     */
    private int verticalSquareNumberBottom2;


    /**
     * 3、扫码相关的矩形信息：
     * <p>
     * 扫描线所在的矩形
     */
    private Rect currentScanRect;
    /**
     * 扫描区域的矩形
     */
    private Rect captureRect;
    /**
     * 扫描区域的矩形的备份，用于下次扫码使用，避免外部频繁设置
     */
    private Rect captureRectBackUp;


    /**
     * 4、各种可以绘制相关的自定义属性：
     * <p>
     * 扫面线改变的步长
     */
    private int scanStep;
    private float circleRadius;
    /**
     * 画笔粗细
     */
    private int paintWidth;
    /**
     * 水平方向正方形总数
     */
    private int horizontalSquareNumber;
    /**
     * 竖直方向正方形总数
     */
    private int verticalSquareNumber;
    private int backGroundColor;
    /**
     * 定时任务执行的时间间隔
     */
    private long scanPeriod;


    private boolean isStart = false;
    /**
     * 竖向画笔颜色的渐变矩阵
     */

    private int[] color;
    /**
     * 竖向画笔颜色对应的渐变位置
     */
    private float[] position;
    /**
     * 扫描线所在矩形的高度
     */
    private float scanHeight;
    /**
     * 正方形的边长
     */
    private float squareWidth;
    private Paint mVerticalPaint;
    private Paint mHorizontalPaint;
    private ScheduledExecutorService executorService;


    public ScanView(Context context) {
        super(context);
        init(context, null);
    }

    public ScanView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ScanView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public ScanView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            @SuppressLint("Recycle") TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ScanView);
            scanStep = typedArray.getInteger(R.styleable.ScanView_scan_step, DF_SCAN_STEP);
            scanPeriod = typedArray.getInteger(R.styleable.ScanView_scan_period, DF_SCAN_PERIOD);
            verticalSquareNumber = typedArray.getInteger(R.styleable.ScanView_vertical_square_number, DF_VERTICAL_SQUARE_NUMBER);
            horizontalSquareNumber = typedArray.getInteger(R.styleable.ScanView_horizontal_square_number, DF_HORIZONTAL_SQUARE_NUMBER);
            circleRadius = typedArray.getFloat(R.styleable.ScanView_ciclr_radius, DF_CIRCLE_RADIUS);
            paintWidth = typedArray.getInteger(R.styleable.ScanView_paint_width, DF_PAINT_WIDTH);
            String color = typedArray.getString(R.styleable.ScanView_back_ground_color);
            if (color != null) {
                backGroundColor = Color.parseColor(color);
            } else {
                backGroundColor = DF_BACKGROUND_COLOR;
            }

            verticalSquareNumberTop = (verticalSquareNumber / 2);
            //透明度0-10%的正方形数
            verticalSquareNumberTop1 = (int) (verticalSquareNumberTop * 0.7);
            //透明度10-90%的正方形数
            verticalSquareNumberTop2 = verticalSquareNumberTop - verticalSquareNumberTop1;

            verticalSquareNumberBottom = verticalSquareNumber - verticalSquareNumberTop;
            //透明度90-10%的正方形数
            verticalSquareNumberBottom1 = verticalSquareNumberTop2;
            //透明度10-0%的正方形数
            verticalSquareNumberBottom2 = verticalSquareNumberBottom - verticalSquareNumberBottom1;

            WeBankLogger.d(TAG, "verticalSquareNumberTop=" + verticalSquareNumberTop + ",verticalSquareNumberTop1=" + verticalSquareNumberTop1 + "," +
                    "verticalSquareNumberTop2=" + verticalSquareNumberTop2);
            WeBankLogger.d(TAG, "verticalSquareNumberBottom=" + verticalSquareNumberBottom + ",verticalSquareNumberBottom1=" + verticalSquareNumberBottom1 + "," +
                    "verticalSquareNumberBottom2=" + verticalSquareNumberBottom2);

            WeBankLogger.d(TAG, "scanStep=" + scanStep + ",scanPeriod=" + scanPeriod + ",circleRadius=" + circleRadius);
        }
    }

    /**
     * 设置扫描区域的大小
     */
    public void setCaptureRect(Rect captureRect) {
        this.captureRect = captureRect;
        this.captureRectBackUp = captureRect;

        float currentScanRectWidth = captureRect.right - captureRect.left;
        squareWidth = currentScanRectWidth / horizontalSquareNumber;
        scanHeight = verticalSquareNumber * squareWidth;

        //扫面线所在矩形的宽度由传入的扫描区域所在矩形的宽度决定
        //扫面线所在矩形的高度由传入的扫描区域所在矩形的高度+扫描高度决定
        currentScanRect = new Rect(captureRect.left, captureRect.top, captureRect.right,
                captureRect.top + (int) scanHeight);

        WeBankLogger.d(TAG, "currentScanRect=" + currentScanRect.toString());
        WeBankLogger.d(TAG, "currentScanRectWidth=" + currentScanRectWidth);
        WeBankLogger.d(TAG, "scanHeight=" + scanHeight);
        WeBankLogger.d(TAG, "captureRect=" + captureRect + ",captureRectBackUp=" + captureRectBackUp);

    }

    private Timer mTimer;
    private TimerTask mTimerTask;

    /**
     * 对外暴露的扫码方法，外部调用此方法就可以开始动画
     */
    public void startScan() {

        //如果没有设置扫码区域，则默认扫码区域在当前View的中间位置
        if (captureRect == null) {
            captureRect = new Rect(getLeft() / 2, getTop() / 2, getRight() / 2, getBottom() / 2);
            setCaptureRect(captureRect);
        }
        if (!isStart) {
//            startAinmation();
            initTask();
            //即刻执行任务，每隔40ms执行一次
            mTimer.schedule(mTimerTask, 0, scanPeriod);
            isStart = true;
        }
        invalidate();
    }

    /**
     * 和定时器二选一来执行定时任务
     */
    private void startAinmation() {
        //初始化扫描区域矩阵
        setCaptureRect(captureRectBackUp);

        executorService = new ScheduledThreadPoolExecutor(1);
        executorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                System.out.println("当前线程名字为：" + Thread.currentThread().getName());
                WeBankLogger.d(TAG, "mTimerTask.run");
                currentScanRect.bottom = currentScanRect.bottom + scanStep;
                if (currentScanRect.bottom >= captureRect.bottom) {
                    //如果扫描线底部超过扫描区域底部，重新初始化扫码线的底部值
                    currentScanRect.bottom = (int) (captureRect.top + scanHeight);
                }
                //扫描线所在矩形的top=bottom-矩形高=bottom-scanHeight
                currentScanRect.top = (int) (currentScanRect.bottom - scanHeight);
                postInvalidate();
            }
        }, 0,scanPeriod, TimeUnit.MILLISECONDS);

        isStop = false;
    }

    private void initTask() {

        //初始化扫描区域矩阵
        setCaptureRect(captureRectBackUp);
        //创建定时器
        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                currentScanRect.bottom = currentScanRect.bottom + scanStep;
                if (currentScanRect.bottom >= captureRect.bottom) {
                    //如果扫描线底部超过扫描区域底部，重新初始化扫码线的底部值
                    currentScanRect.bottom = (int) (captureRect.top + scanHeight);
                }
                //扫描线所在矩形的top=bottom-矩形高=bottom-scanHeight
                currentScanRect.top = (int) (currentScanRect.bottom - scanHeight);
                postInvalidate();
            }
        };
        isStop = false;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int minWidth = 500;
        int minHeight = 500;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(minWidth, minHeight);
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(minWidth, heightSpecSize);
        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSpecSize, minHeight);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //初始化以及动画过程才会绘制背景色
        if (!isStop) {
            canvas.drawColor(backGroundColor);
        }
        if (currentScanRect != null && isStart) {
            drawVerticalLine(canvas);
            drawHorizontalLine(canvas);
            drawCircle(canvas);
        }
    }


    private void drawVerticalLine(Canvas canvas) {
        for (int i = 1; i < horizontalSquareNumber; i++) {
            //绘制竖线
            canvas.drawLine(currentScanRect.left + i * squareWidth, currentScanRect.top,
                    currentScanRect.left + i * squareWidth, currentScanRect.bottom,
                    getVerticalLinePaint(currentScanRect.left + i * squareWidth, currentScanRect.top, currentScanRect.left + i * squareWidth, currentScanRect.bottom));
        }
    }

    private void drawHorizontalLine(Canvas canvas) {
        //绘制透明度0-10%的横线
        for (int i = 1; i < verticalSquareNumberTop1; i++) {
            canvas.drawLine(currentScanRect.left, currentScanRect.top + i * squareWidth,
                    currentScanRect.right, currentScanRect.top + i * squareWidth,
                    getHorizontalLinePaint(i, Type.ALPHA_0_10));
        }
        //绘制透明度10-90%的横线
        for (int i = 1; i <= verticalSquareNumberTop2; i++) {
            canvas.drawLine(currentScanRect.left, currentScanRect.top + (i + verticalSquareNumberTop1) * squareWidth,
                    currentScanRect.right, currentScanRect.top + (i + verticalSquareNumberTop1) * squareWidth,
                    getHorizontalLinePaint(i, Type.ALPHA_10_90));

        }
        //绘制透明度90-10%的横线
        for (int i = 1; i <= verticalSquareNumberBottom1; i++) {
            canvas.drawLine(currentScanRect.left, currentScanRect.top + (i + verticalSquareNumberTop1 + verticalSquareNumberTop2) * squareWidth,
                    currentScanRect.right, currentScanRect.top + (i + verticalSquareNumberTop1 + verticalSquareNumberTop2) * squareWidth,
                    getHorizontalLinePaint(i, Type.ALPHA_90_10));
        }
        //绘制透明度10-0%的横线
        for (int i = 1; i <= verticalSquareNumberBottom2; i++) {
            canvas.drawLine(currentScanRect.left, currentScanRect.top + (i + verticalSquareNumberTop1 + verticalSquareNumberTop2 + verticalSquareNumberBottom1) * squareWidth,
                    currentScanRect.right, currentScanRect.top + (i + verticalSquareNumberTop1 + verticalSquareNumberTop2 + verticalSquareNumberBottom1) * squareWidth,
                    getHorizontalLinePaint(i, Type.ALPHA_10_0));
        }
    }

    private void drawCircle(Canvas canvas) {
        for (int i = 1; i < horizontalSquareNumber; i++) {
            //绘制透明度0-10%的圆
            for (int j = 1; j <= verticalSquareNumberTop1; j++) {
                canvas.drawCircle(currentScanRect.left + squareWidth * i, currentScanRect.top + squareWidth * j, circleRadius, getHorizontalLinePaint(j, Type.ALPHA_0_10));
            }
            //绘制透明度为10-90%的圆
            for (int j = 1; j <= verticalSquareNumberTop2; j++) {
                canvas.drawCircle(currentScanRect.left + squareWidth * i, currentScanRect.top + squareWidth * (verticalSquareNumberTop1 + j), circleRadius, getHorizontalLinePaint(j, Type.ALPHA_10_90));
            }
            //绘制透明度为90-10%的圆
            for (int j = 1; j <= verticalSquareNumberBottom1; j++) {
                canvas.drawCircle(currentScanRect.left + squareWidth * i, currentScanRect.top + squareWidth * (verticalSquareNumberTop1 + verticalSquareNumberTop2 + j), circleRadius, getHorizontalLinePaint(j, Type.ALPHA_90_10));
            }
            //绘制透明度为10-0%的圆
            for (int j = 1; j < verticalSquareNumberBottom2; j++) {
                canvas.drawCircle(currentScanRect.left + squareWidth * i, currentScanRect.top + squareWidth * (verticalSquareNumberTop1 + verticalSquareNumberTop2 + verticalSquareNumberBottom1 + j), circleRadius, getHorizontalLinePaint(j, Type.ALPHA_10_0));
            }
        }
    }

    /**
     * 这个标志位主要用于控制背景的绘制
     */
    private boolean isStop;

    public void stopScan() {
        WeBankLogger.d(TAG, "stopScan");
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
        isStop = true;
        isStart = false;
//        executorService.shutdown();
        invalidate();//清除屏幕
    }

    private Paint getVerticalLinePaint(float startX, float startY, float stopX, float stopY) {
        LinearGradient linearGradient = new LinearGradient(startX, startY, stopX, stopY, getColor(),
                getPositions(), Shader.TileMode.CLAMP);
        if (mVerticalPaint == null) {
            mVerticalPaint = new Paint();
            mVerticalPaint.setStrokeWidth(paintWidth);
            //抗锯齿
            mVerticalPaint.setAntiAlias(true);
            //防抖动
            mVerticalPaint.setDither(true);
        }
        mVerticalPaint.setShader(linearGradient);
        return mVerticalPaint;
    }


    private Paint getHorizontalLinePaint(int alphaLevel, Type type) {
        if (mHorizontalPaint == null) {
            mHorizontalPaint = new Paint();
            //这句代码调用必须放在setAlpha前面
            mHorizontalPaint.setColor(Color.parseColor("#FFFFFF"));
            mHorizontalPaint.setStrokeWidth(paintWidth);
            mHorizontalPaint.setAntiAlias(true);
            mHorizontalPaint.setDither(true);
        }
        switch (type) {
            //透明度0-10%
            case ALPHA_0_10:
                mHorizontalPaint.setAlpha(25 * alphaLevel / verticalSquareNumberTop1);
                break;
            //透明度10-90%
            case ALPHA_10_90:
                mHorizontalPaint.setAlpha(25 + 204 * alphaLevel / verticalSquareNumberTop2);
                break;
            //透明度90-10%
            case ALPHA_90_10:
                mHorizontalPaint.setAlpha(229 - 204 * alphaLevel / verticalSquareNumberBottom1);
                break;
            //透明度10-0%
            case ALPHA_10_0:
                mHorizontalPaint.setAlpha(25 - 25 * alphaLevel / verticalSquareNumberBottom2);
                break;
            default:
                break;
        }

        return mHorizontalPaint;
    }

    private int[] getColor() {
        if (color == null) {
            //0%的透明度
            int color1 = Color.parseColor("#00FFFFFF");
            //10%的透明度
            int color2 = Color.parseColor("#19FFFFFF");
            //90%的透明度
            int color3 = Color.parseColor("#E5FFFFFF");
            int color4 = Color.parseColor("#19FFFFFF");
            int color5 = Color.parseColor("#00FFFFFF");
            color = new int[]{color1, color2, color3, color4, color5};
        }
        return color;
    }

    private float[] getPositions() {
        if (position == null) {
            position = new float[]{0f, 0.3f, 0.5f, 0.7f, 1f};
        }
        return position;
    }

    /**
     * 画笔透明度
     */
    private enum Type {
        /**
         * 透明度0-10%
         */
        ALPHA_0_10,
        /**
         * 透明度10-90%
         */
        ALPHA_10_90,
        /**
         * 透明度90-10%
         */
        ALPHA_90_10,
        /**
         * 透明度10-0%
         */
        ALPHA_10_0
    }

}
