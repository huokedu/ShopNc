package top.yokey.nsg.share.themes.classic;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

@SuppressWarnings("all")
public class XView extends View {

    private float ratio;

    public XView(Context context) {
        super(context);
    }

    public void setRatio(float ratio) {
        this.ratio = ratio;
    }

    @SuppressLint("DrawAllocation")
    protected void onDraw(Canvas canvas) {
        int width = getWidth() / 2;
        int height = getHeight() / 2;

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(0xffa0a0a0);
        canvas.drawRect(width, 0, getWidth(), height, paint);

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(3f * ratio);
        paint.setColor(0xffffffff);
        float left = 8f * ratio;
        canvas.drawLine(width + left, left, getWidth() - left, width - left, paint);
        canvas.drawLine(width + left, width - left, getWidth() - left, left, paint);
    }

}