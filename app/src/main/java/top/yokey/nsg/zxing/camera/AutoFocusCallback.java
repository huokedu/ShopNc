package top.yokey.nsg.zxing.camera;

import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;

@SuppressWarnings("all")
public final class AutoFocusCallback implements Camera.AutoFocusCallback {

    private static final long AUTO_FOCUS_INTERVAL_MS = 1500L;

    private Handler autoFocusHandler;
    private int autoFocusMessage;

    public void setHandler(Handler autoFocusHandler, int autoFocusMessage) {
        this.autoFocusHandler = autoFocusHandler;
        this.autoFocusMessage = autoFocusMessage;
    }

    public void onAutoFocus(boolean success, Camera camera) {
        if (autoFocusHandler != null) {
            Message message = autoFocusHandler.obtainMessage(autoFocusMessage, success);
            autoFocusHandler.sendMessageDelayed(message, AUTO_FOCUS_INTERVAL_MS);
            autoFocusHandler = null;
        }
    }

}