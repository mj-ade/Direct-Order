package com.example.direct_order.ordersheet;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.direct_order.R;

import static com.example.direct_order.ordersheet.OrderSheetActivity.dpToPx;

public abstract class StickerView extends FrameLayout {
    private final static int BUTTON_SIZE_DP = 30;
    private final static int SELF_SIZE_DP = 100;

    private BorderView iv_border;
    private ImageView iv_scale;
    private ImageView iv_delete;
    private ImageView iv_width;
    private ImageView iv_height;

    private LayoutParams params, iv_scale_params, iv_delete_params, iv_width_parmas, iv_height_parmas;
    private float centerX, centerY;
    // For scalling
    private float scale_orgX = -1, scale_orgY = -1;

    // For moving
    private float move_orgX =-1, move_orgY = -1;

    private int size = dpToPx(getContext(), SELF_SIZE_DP);
    private int margin = dpToPx(getContext(), BUTTON_SIZE_DP) / 2;

    private boolean isDeleted;

    public StickerView(Context context) {
        super(context);
        init(context);
    }

    public StickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public StickerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context){
        iv_border = new BorderView(context);
        iv_scale = new ImageView(context);
        iv_delete = new ImageView(context);
        iv_width = new ImageView(context);
        iv_height = new ImageView(context);

        iv_scale.setImageResource(R.drawable.zoominout);
        iv_delete.setImageResource(R.drawable.delete);
        iv_width.setImageResource(R.drawable.width_scale);
        iv_height.setImageResource(R.drawable.height_scale);

        this.setTag("DraggableViewGroup");
        iv_border.setTag("sticker_border");
        iv_scale.setTag("iv_scale");
        iv_delete.setTag("iv_delete");
        iv_width.setTag("iv_width");
        iv_height.setTag("iv_height");

        int buttonSize = dpToPx(getContext(), BUTTON_SIZE_DP);

        LayoutParams this_params = new LayoutParams(size, size);
        this_params.gravity = Gravity.CENTER;
        this.setLayoutParams(this_params);

        params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        params.setMargins(margin, margin, margin, margin);

        iv_scale_params = new LayoutParams(buttonSize, buttonSize);
        iv_scale_params.gravity = Gravity.BOTTOM | Gravity.RIGHT;

        iv_delete_params = new LayoutParams(buttonSize, buttonSize);
        iv_delete_params.gravity = Gravity.TOP | Gravity.RIGHT;

        iv_width_parmas = new LayoutParams(buttonSize, buttonSize);
        iv_width_parmas.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;

        iv_height_parmas = new LayoutParams(buttonSize, buttonSize);
        iv_height_parmas.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;

        this.addView(getMainView(), params);
        this.addView(iv_border, params);
        this.addView(iv_scale, iv_scale_params);
        this.addView(iv_delete, iv_delete_params);
        this.addView(iv_width, iv_width_parmas);
        this.addView(iv_height, iv_height_parmas);

        OrderSheetActivity.isFocus = true;

        this.setOnTouchListener(touchListener);
        iv_scale.setOnTouchListener(touchListener);
        iv_width.setOnTouchListener(touchListener);
        iv_height.setOnTouchListener(touchListener);
        iv_delete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(StickerView.this.getParent()!=null){
                    ViewGroup myCanvas = ((ViewGroup) StickerView.this.getParent());
                    myCanvas.removeView(StickerView.this);
                    isDeleted = true;
                }
            }
        });
    }

    protected abstract View getMainView();

    private OnTouchListener touchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            if(view.getTag().equals("DraggableViewGroup")) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if(!OrderSheetActivity.isFocus) {
                            StickerView.this.setControlItemsHidden(false);
                            OrderSheetActivity.isFocus = true;
                        }
                        move_orgX = event.getRawX();
                        move_orgY = event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float offsetX = event.getRawX() - move_orgX;
                        float offsetY = event.getRawY() - move_orgY;
                        StickerView.this.setX(StickerView.this.getX() + offsetX);
                        StickerView.this.setY(StickerView.this.getY() + offsetY);
                        move_orgX = event.getRawX();
                        move_orgY = event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }
            }
            else if(view.getTag().equals("iv_scale")){
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        scale_orgX = event.getRawX();
                        scale_orgY = event.getRawY();

                        centerX = StickerView.this.getX()
                                + ((View) StickerView.this.getParent()).getX()
                                + (float) StickerView.this.getWidth()/2;

                        int result = 0;
                        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
                        if (resourceId > 0) {
                            result = getResources().getDimensionPixelSize(resourceId);
                        }
                        centerY = StickerView.this.getY()
                                + ((View) StickerView.this.getParent()).getY()
                                + result    // statusBarHeight
                                + (float) StickerView.this.getHeight()/2;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        double length1 = getLength(centerX, centerY, scale_orgX, scale_orgY);
                        double length2 = getLength(centerX, centerY, event.getRawX(), event.getRawY());

                        float offsetX = getLength(scale_orgX, event.getRawX());
                        float offsetY = getLength(scale_orgY, event.getRawY());
                        int offset = Math.round(Math.max(offsetX, offsetY));

                        if (length2 > length1) {
                            //scale up
                            if (StickerView.this instanceof StickerImageView ||
                                    (StickerView.this instanceof StickerView
                                            && ((StickerTextView) StickerView.this).getTv_main().getTextSize() < 299)) {
                                StickerView.this.getLayoutParams().width += offset;
                                StickerView.this.getLayoutParams().height += offset;
                                onScaling(true);
                            }
                        }
                        else if(length2 < length1
                                && StickerView.this.getLayoutParams().height > size/2
                                && StickerView.this.getLayoutParams().width > size/2) {
                            //scale down
                            StickerView.this.getLayoutParams().width -= offset;
                            StickerView.this.getLayoutParams().height -= offset;
                            onScaling(false);
                        }

                        scale_orgX = event.getRawX();
                        scale_orgY = event.getRawY();

                        postInvalidate();
                        requestLayout();
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }
            }
            else if(view.getTag().equals("iv_width")) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        scale_orgX = event.getRawX();

                        centerX = StickerView.this.getX()
                                + ((View) StickerView.this.getParent()).getX()
                                + (float) StickerView.this.getWidth()/2;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float length1 = getLength(centerX, scale_orgX);
                        float length2 = getLength(centerX, event.getRawX());

                        float offsetX = getLength(scale_orgX, event.getRawX());
                        offsetX = Math.round(offsetX);

                        if (length2 > length1) {
                            //scale up
                            StickerView.this.getLayoutParams().width += offsetX;
                            onScaling(true);
                        }
                        else if (length2 < length1 && StickerView.this.getLayoutParams().width > size/2) {
                            //scale down
                            StickerView.this.getLayoutParams().width -= offsetX;
                            onScaling(false);
                        }

                        scale_orgX = event.getRawX();

                        postInvalidate();
                        requestLayout();
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }
            }
            else if(view.getTag().equals("iv_height")) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        scale_orgY = event.getRawY();

                        int result = 0;
                        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
                        if (resourceId > 0) {
                            result = getResources().getDimensionPixelSize(resourceId);
                        }
                        centerY = StickerView.this.getY()
                                + ((View) StickerView.this.getParent()).getY()
                                + result    // statusBarHeight
                                + (float) StickerView.this.getHeight()/2;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float length1 = getLength(centerY, scale_orgY);
                        float length2 = getLength(centerY, event.getRawY());

                        float offsetY = getLength(scale_orgY, event.getRawY());
                        offsetY = Math.round(offsetY);

                        if (length2 > length1) {
                            //scale up
                            StickerView.this.getLayoutParams().height += offsetY;
                            onScaling(true);
                        }
                        else if (length2 < length1 && StickerView.this.getLayoutParams().height > size/2) {
                            //scale down
                            StickerView.this.getLayoutParams().height -= offsetY;
                            onScaling(false);
                        }

                        scale_orgY = event.getRawY();

                        postInvalidate();
                        requestLayout();
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }
            }
            return true;
        }
    };

    public void setControlItemsHidden(boolean isHidden) {
        if (isHidden) {
            iv_border.setVisibility(INVISIBLE);
            iv_scale.setVisibility(INVISIBLE);
            iv_delete.setVisibility(INVISIBLE);
            iv_width.setVisibility(INVISIBLE);
            iv_height.setVisibility(INVISIBLE);
        }
        else {
            iv_border.setVisibility(VISIBLE);
            iv_scale.setVisibility(VISIBLE);
            iv_delete.setVisibility(VISIBLE);
            iv_width.setVisibility(VISIBLE);
            iv_height.setVisibility(VISIBLE);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    protected void onScaling(boolean scaleUp) {}

    private float getLength(float a, float b) {
        return Math.abs(a - b);
    }

    private double getLength(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(y2-y1, 2) + Math.pow(x2-x1, 2));
    }

    private class BorderView extends View {
        public BorderView(Context context) {
            super(context);
        }

        public BorderView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public BorderView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            // Draw sticker border
            LayoutParams layoutParams = (LayoutParams) this.getLayoutParams();

            Rect border = new Rect();
            border.left = (int) this.getLeft() - layoutParams.leftMargin;
            border.top = (int) this.getTop() - layoutParams.topMargin;
            border.right = (int) this.getRight() - layoutParams.rightMargin;
            border.bottom = (int) this.getBottom() - layoutParams.bottomMargin;
            Paint borderPaint = new Paint();
            borderPaint.setStrokeWidth(6);
            borderPaint.setColor(Color.WHITE);
            borderPaint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(border, borderPaint);
        }
    }

    public ImageView getIv_width() {
        return iv_width;
    }

    public ImageView getIv_height() {
        return iv_height;
    }

    public boolean isDeleted() {
        return isDeleted;
    }
}
