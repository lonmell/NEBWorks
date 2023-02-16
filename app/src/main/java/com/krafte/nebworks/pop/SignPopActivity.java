package com.krafte.nebworks.pop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.krafte.nebworks.R;
import com.krafte.nebworks.data.GetResultData;
import com.krafte.nebworks.util.CanvasIO;
import com.krafte.nebworks.util.DBConnection;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class SignPopActivity  extends Activity {
    private static final String TAG = "EmployerOptionActivity";
    Context mContext;

    //XML ID
    LinearLayout lo_canvas;
    TextView next_btn, back_btn, refresh_btn;


    // shared 저장값
    PreferenceHelper shardpref;
    private DrawCanvas drawCanvas;
    private LinearLayout canvasContainer;
    private Bitmap saveBitmap;

    //Other
    Dlog dlog = new Dlog();
    PageMoveClass pm = new PageMoveClass();
    DBConnection dbConnection = new DBConnection();
    GetResultData resultData = new GetResultData();
    Handler mHandler;

    String ResultTv = "";
    int selectPosition = 0;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 다이얼로그 화면이 투명해진다
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_signpop);

        mContext = this;
        dlog.DlogContext(mContext);
        shardpref = new PreferenceHelper(mContext);

        next_btn = findViewById(R.id.next_btn);
        back_btn = findViewById(R.id.back_btn);
        lo_canvas = findViewById(R.id.lo_canvas);
        refresh_btn = findViewById(R.id.refresh_btn);
        drawCanvas = new DrawCanvas(this);

//        Bundle extra = new Bundle();
//        Intent intent = new Intent();
//        extra.putString("data", data);
//        dlog.i("우편번호 : " + data);
//        intent.putExtras(extra);
//        setResult(RESULT_OK, intent);
//        finish();
//
        lo_canvas.addView(drawCanvas);

        refresh_btn.setOnClickListener(v -> {
            drawCanvas.init();
            drawCanvas.invalidate();
        });

        next_btn.setOnClickListener(v -> {
            drawCanvas.invalidate();
            saveBitmap = drawCanvas.getCurrentCanvas();
            CanvasIO.saveBitmap(this, saveBitmap);

            Intent intent= new Intent();
            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            saveBitmap.compress(Bitmap.CompressFormat.PNG, 50, bs);
            shardpref.remove("singing");
            intent.putExtra("signing", bs.toByteArray());
            setResult(RESULT_OK, intent);
            finish();

//            Bundle extra = new Bundle();
//            Intent intent = new Intent();
//            extra.putString("singing", String.valueOf(saveBitmap));
//            intent.putExtras(extra);
//            setResult(RESULT_OK, intent);
//            finish();
        });

    }

    /**
     * Pen을 표현할 class입니다.
     */
    class Pen {
        public static final int STATE_START = 0;        //펜의 상태(움직임 시작)
        public static final int STATE_MOVE = 1;         //펜의 상태(움직이는 중)
        float x, y;                                     //펜의 좌표
        int moveStatus;                                 //현재 움직임 여부
        int color;                                      //펜 색
        int size;                                       //펜 두께

        public Pen(float x, float y, int moveStatus, int color, int size) {
            this.x = x;
            this.y = y;
            this.moveStatus = moveStatus;
            this.color = color;
            this.size = size;
        }

        /**
         * 현재 pen의 상태가 움직이는 상태인지 반환합니다.
         */
        public boolean isMove() {
            return moveStatus == STATE_MOVE;
        }
    }

    /**
     * 그림이 그려질 canvas view
     */
    class DrawCanvas extends View {
        public static final int MODE_PEN = 1;                     //모드 (펜)
        public static final int MODE_ERASER = 0;                  //모드 (지우개)
        final int PEN_SIZE = 10;                                   //펜 사이즈
        final int ERASER_SIZE = 30;                               //지우개 사이즈

        public ArrayList<Pen> drawCommandList;                    //그리기 경로가 기록된 리스트
        Paint paint;                                              //펜
        Bitmap loadDrawImage;                                     //호출된 이전 그림
        int color;                                                //현재 펜 색상
        int size;                                                 //현재 펜 크기

        public DrawCanvas(SignPopActivity context) {
            super(context);
            init();
        }

        public DrawCanvas(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        public DrawCanvas(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            init();
        }

        /**
         * jhChoi - 201124
         * 그리기에 필요한 요소를 초기화 합니다.
         */
        private void init() {
            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            drawCommandList = new ArrayList<>();
            loadDrawImage = null;
            color = Color.BLACK;
            size = PEN_SIZE;
        }

        /**
         * jhChoi - 201124
         * 현재까지 그린 그림을 Bitmap으로 반환합니다.
         */
        public Bitmap getCurrentCanvas() {
            Bitmap bitmap = Bitmap.createBitmap(this.getWidth(), this.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            this.draw(canvas);
            return bitmap;
        }

        /**
         * jhChoi - 201124
         * Tool type을 (펜 or 지우개)로 변경합니다.
         */
        private void changeTool(int toolMode) {
            if (toolMode == MODE_PEN) {
                this.color = Color.BLACK;
                size = PEN_SIZE;
            } else {
                this.color = Color.WHITE;
                size = ERASER_SIZE;
            }
            paint.setColor(color);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawColor(Color.parseColor("#E0EAFB"));

            if (loadDrawImage != null) {
                canvas.drawBitmap(loadDrawImage, 0, 0, null);
            }

            for (int i = 0; i < drawCommandList.size(); i++) {
                Pen p = drawCommandList.get(i);
                paint.setColor(p.color);
                paint.setStrokeWidth(p.size);

                if (p.isMove()) {
                    Pen prevP = drawCommandList.get(i - 1);
                    canvas.drawLine(prevP.x, prevP.y, p.x, p.y, paint);
                }
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent e) {
            int action = e.getAction();
            int state = action == MotionEvent.ACTION_DOWN ? Pen.STATE_START : Pen.STATE_MOVE;
            drawCommandList.add(new Pen(e.getX(), e.getY(), state, color, size));
            invalidate();
            return true;
        }
    }
}
