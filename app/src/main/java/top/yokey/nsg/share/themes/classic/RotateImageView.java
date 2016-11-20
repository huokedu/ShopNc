package top.yokey.nsg.share.themes.classic;

import android.content.Context;
import android.graphics.Canvas;
import android.widget.ImageView;

@SuppressWarnings("all")
public class RotateImageView extends ImageView {

    private float rotation;

    public RotateImageView(Context context) {
        super(context);
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
        invalidate();
    }

    protected void onDraw(Canvas canvas) {
        canvas.rotate(rotation, getWidth() / 2, getHeight() / 2);
        super.onDraw(canvas);
    }

}