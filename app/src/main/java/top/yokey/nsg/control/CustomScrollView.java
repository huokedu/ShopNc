package top.yokey.nsg.control;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.ScrollView;
import android.widget.Scroller;

public class CustomScrollView extends ScrollView {

    private int view_height = 0;
    private int Scroll_height = 0;
    protected Field scrollView_mScroller;
    private GestureDetector mGestureDetector;

    private int lastScrollY;
    private OnScrollListener onScrollListener;

    public CustomScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mGestureDetector = new GestureDetector(context, new YScrollDetector());
        setFadingEdgeLength(0);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            stopAnim();
        }
        boolean ret = super.onInterceptTouchEvent(ev);
        boolean ret2 = mGestureDetector.onTouchEvent(ev);
        return ret && ret2;
    }

    class YScrollDetector extends SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (Math.abs(distanceY) > Math.abs(distanceX)) {
                return true;
            }
            return false;
        }
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        boolean stop = false;
        if (Scroll_height - view_height == t) {
            stop = true;
        }

        if (t == 0 || stop == true) {
            try {
                if (scrollView_mScroller == null) {
                    scrollView_mScroller = getDeclaredField(this, "mScroller");
                }
                Object ob = scrollView_mScroller.get(this);
                if (ob == null || !(ob instanceof Scroller)) {
                    return;
                }
                Scroller sc = (Scroller) ob;
                sc.abortAnimation();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.onScrollChanged(l, t, oldl, oldt);
    }

    private void stopAnim() {
        try {
            if (scrollView_mScroller == null) {
                scrollView_mScroller = getDeclaredField(this, "mScroller");
            }

            Object ob = scrollView_mScroller.get(this);
            if (ob == null) {
                return;
            }
            Method method = ob.getClass().getMethod("abortAnimation");
            method.invoke(ob);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected int computeVerticalScrollRange() {
        Scroll_height = super.computeVerticalScrollRange();
        return Scroll_height;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed == true) {
            view_height = b - t;
        }
    }

    @Override
    public void requestChildFocus(View child, View focused) {
        if (focused != null && focused instanceof WebView) {
            return;
        }
        super.requestChildFocus(child, focused);
    }

    public static Field getDeclaredField(Object object, String field_name) {
        Class<?> cla = object.getClass();
        Field field = null;
        for (; cla != Object.class; cla = cla.getSuperclass()) {
            try {
                field = cla.getDeclaredField(field_name);
                field.setAccessible(true);
                return field;
            } catch (Exception e) {

            }
        }
        return null;
    }

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (onScrollListener != null) {
            onScrollListener.onScroll(lastScrollY = this.getScrollY());
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
                handler.sendMessageDelayed(handler.obtainMessage(), 20);
                break;
        }
        return super.onTouchEvent(ev);
    }

    public interface OnScrollListener {
        void onScroll(int scrollY);
    }

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            int scrollY = CustomScrollView.this.getScrollY();
            if (lastScrollY != scrollY) {
                lastScrollY = scrollY;
                handler.sendMessageDelayed(handler.obtainMessage(), 5);
            }
            if (onScrollListener != null) {
                onScrollListener.onScroll(scrollY);
            }

        }
    };

}