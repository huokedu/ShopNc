package top.yokey.nsg.share.themes.classic;

import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView.ScaleType;

import com.mob.tools.gui.ScaledImageView;

import top.yokey.nsg.share.OnekeySharePage;
import top.yokey.nsg.share.OnekeyShareThemeImpl;

@SuppressWarnings("all")
public class PicViewerPage extends OnekeySharePage implements OnGlobalLayoutListener {

    private Bitmap pic;
    private ScaledImageView sivViewer;

    public PicViewerPage(OnekeyShareThemeImpl impl) {
        super(impl);
    }

    public void setImageBitmap(Bitmap pic) {
        this.pic = pic;
    }

    public void onCreate() {
        activity.getWindow().setBackgroundDrawable(new ColorDrawable(0x4c000000));

        sivViewer = new ScaledImageView(activity);
        sivViewer.setScaleType(ScaleType.MATRIX);
        activity.setContentView(sivViewer);
        if (pic != null) {
            sivViewer.getViewTreeObserver().addOnGlobalLayoutListener(this);
        }
    }

    public void onGlobalLayout() {
        sivViewer.getViewTreeObserver().removeGlobalOnLayoutListener(this);
        sivViewer.post(new Runnable() {
            public void run() {
                sivViewer.setBitmap(pic);
            }
        });
    }

}