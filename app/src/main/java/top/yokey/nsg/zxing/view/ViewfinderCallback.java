package top.yokey.nsg.zxing.view;

import com.google.zxing.ResultPoint;
import com.google.zxing.ResultPointCallback;

@SuppressWarnings("all")
public final class ViewfinderCallback implements ResultPointCallback {

    private final ViewfinderView viewfinderView;

    public ViewfinderCallback(ViewfinderView viewfinderView) {
        this.viewfinderView = viewfinderView;
    }

    public void foundPossibleResultPoint(ResultPoint point) {
        viewfinderView.addPossibleResultPoint(point);
    }

}