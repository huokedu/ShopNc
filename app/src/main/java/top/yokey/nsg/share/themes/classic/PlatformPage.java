package top.yokey.nsg.share.themes.classic;

import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mob.tools.gui.MobViewPager;

import java.util.ArrayList;
import java.util.HashMap;

import cn.sharesdk.framework.CustomPlatform;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.framework.ShareSDK;
import top.yokey.nsg.share.CustomerLogo;
import top.yokey.nsg.share.OnekeySharePage;
import top.yokey.nsg.share.OnekeyShareThemeImpl;

@SuppressWarnings("all")
public abstract class PlatformPage extends OnekeySharePage {

    private ClassicTheme impl;
    private Runnable beforeFinish;
    private Animation animShow;
    private Animation animHide;
    private LinearLayout llPanel;
    private boolean finished;

    protected PlatformPage(OnekeyShareThemeImpl impl) {
        super(impl);
        this.impl = com.mob.tools.utils.R.forceCast(impl);
    }

    public void onCreate() {
        activity.getWindow().setBackgroundDrawable(new ColorDrawable(0x4c000000));
        initAnims();

        LinearLayout llPage = new LinearLayout(activity);
        llPage.setOrientation(LinearLayout.VERTICAL);
        activity.setContentView(llPage);

        TextView vTop = new TextView(activity);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        lp.weight = 1;
        vTop.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
        llPage.addView(vTop, lp);

        llPanel = new LinearLayout(activity);
        llPanel.setOrientation(LinearLayout.VERTICAL);
        lp = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        llPanel.setAnimation(animShow);
        llPage.addView(llPanel, lp);

        MobViewPager mvp = new MobViewPager(activity);
        ArrayList<Object> cells = collectCells();
        PlatformPageAdapter adapter = newAdapter(cells);
        lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, adapter.getPanelHeight());
        llPanel.addView(mvp, lp);

        IndicatorView vInd = new IndicatorView(activity);
        lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, adapter.getBottomHeight());
        llPanel.addView(vInd, lp);

        vInd.setScreenCount(adapter.getCount());
        vInd.onScreenChange(0, 0);
        adapter.setIndicator(vInd);
        mvp.setAdapter(adapter);
    }

    protected abstract PlatformPageAdapter newAdapter(ArrayList<Object> cells);

    private ArrayList<Object> collectCells() {
        ArrayList<Object> cells = new ArrayList<>();

        Platform[] platforms = ShareSDK.getPlatformList();
        if (platforms == null) {
            platforms = new Platform[0];
        }
        HashMap<String, String> hides = getHiddenPlatforms();
        if (hides == null) {
            hides = new HashMap<>();
        }
        for (Platform p : platforms) {
            if (!hides.containsKey(p.getName())) {
                cells.add(p);
            }
        }

        ArrayList<CustomerLogo> customers = getCustomerLogos();
        if (customers != null && customers.size() > 0) {
            cells.addAll(customers);
        }

        return cells;
    }

    public final void showEditPage(final Platform platform) {
        beforeFinish = new Runnable() {
            public void run() {
                boolean isSilent = isSilent();
                boolean isCustomPlatform = platform instanceof CustomPlatform;
                boolean isUseClientToShare = isUseClientToShare(platform);
                if (isSilent || isCustomPlatform || isUseClientToShare) {
                    shareSilently(platform);
                } else {
                    ShareParams sp = formateShareData(platform);
                    if (sp != null) {
                        // 编辑分享内容的统计
                        ShareSDK.logDemoEvent(3, null);
                        if (getCustomizeCallback() != null) {
                            getCustomizeCallback().onShare(platform, sp);
                        }
                        impl.showEditPage(activity, platform, sp);
                    }
                }
            }
        };
        finish();
    }

    public final void performCustomLogoClick(final View v, final CustomerLogo logo) {
        beforeFinish = new Runnable() {
            public void run() {
                logo.listener.onClick(v);
            }
        };
        finish();
    }

    private void initAnims() {
        animShow = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 1,
                Animation.RELATIVE_TO_SELF, 0);
        animShow.setDuration(300);

        animHide = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 1);
        animHide.setDuration(300);
    }

    public boolean onFinish() {
        if (finished) {
            finished = false;
            return false;
        }

        animHide.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {

            }

            public void onAnimationRepeat(Animation animation) {

            }

            public void onAnimationEnd(Animation animation) {
                if (beforeFinish == null) {
                    // 取消分享菜单的统计
                    ShareSDK.logDemoEvent(2, null);
                } else {
                    beforeFinish.run();
                    beforeFinish = null;
                }

                finished = true;
                finish();
            }
        });
        llPanel.clearAnimation();
        llPanel.setAnimation(animHide);
        llPanel.setVisibility(View.GONE);
        return true;
    }

}