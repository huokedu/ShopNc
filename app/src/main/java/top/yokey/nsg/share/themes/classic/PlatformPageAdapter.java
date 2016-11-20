package top.yokey.nsg.share.themes.classic;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mob.tools.gui.ViewPagerAdapter;

import java.util.ArrayList;

import cn.sharesdk.framework.Platform;
import top.yokey.nsg.share.CustomerLogo;

@SuppressWarnings("all")
public abstract class PlatformPageAdapter extends ViewPagerAdapter implements OnClickListener {

    protected static final int MIN_CLICK_INTERVAL = 1000;
    public static final int DESIGN_BOTTOM_HEIGHT = 52;

    protected Object[][] cells;
    private PlatformPage page;
    private IndicatorView vInd;

    protected int bottomHeight;
    protected int panelHeight;
    protected int cellHeight;
    protected int lineSize;
    protected int sepLineWidth;
    protected int paddingTop;
    protected int logoHeight;

    private long lastClickTime;

    public PlatformPageAdapter(PlatformPage page, ArrayList<Object> cells) {
        this.page = page;
        if (cells != null && !cells.isEmpty()) {
            calculateSize(page.getContext(), cells);
            collectCells(cells);
        }
    }

    protected abstract void calculateSize(Context context, ArrayList<Object> plats);

    protected abstract void collectCells(ArrayList<Object> plats);

    public int getBottomHeight() {
        return bottomHeight;
    }

    public int getPanelHeight() {
        return panelHeight;
    }

    public int getCount() {
        return cells == null ? 0 : cells.length;
    }

    public void setIndicator(IndicatorView view) {
        vInd = view;
    }

    public void onScreenChange(int currentScreen, int lastScreen) {
        if (vInd != null) {
            vInd.setScreenCount(getCount());
            vInd.onScreenChange(currentScreen, lastScreen);
        }
    }

    public View getView(int index, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = createPanel(parent.getContext());
        }

        LinearLayout llPanel = com.mob.tools.utils.R.forceCast(convertView);
        LinearLayout[] llCells = com.mob.tools.utils.R.forceCast(llPanel.getTag());
        refreshPanel(llCells, cells[index]);
        return convertView;
    }

    private View createPanel(Context context) {
        LinearLayout llPanel = new LinearLayout(context);
        llPanel.setOrientation(LinearLayout.VERTICAL);
        llPanel.setBackgroundColor(0xfff2f2f2);

        int lineCount = panelHeight / cellHeight;
        LinearLayout[] llCells = new LinearLayout[lineCount * lineSize];
        llPanel.setTag(llCells);
        int cellBack = com.mob.tools.utils.R.getBitmapRes(context, "ssdk_oks_classic_platform_cell_back");
        LinearLayout.LayoutParams lp;
        for (int i = 0; i < lineCount; i++) {
            LinearLayout llLine = new LinearLayout(context);
            lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, cellHeight);
            llPanel.addView(llLine, lp);

            for (int j = 0; j < lineSize; j++) {
                llCells[i * lineSize + j] = new LinearLayout(context);
                llCells[i * lineSize + j].setBackgroundResource(cellBack);
                llCells[i * lineSize + j].setOrientation(LinearLayout.VERTICAL);
                lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, cellHeight);
                lp.weight = 1;
                llLine.addView(llCells[i * lineSize + j], lp);

                if (j < lineSize - 1) {
                    View vSep = new View(context);
                    lp = new LinearLayout.LayoutParams(sepLineWidth, LayoutParams.MATCH_PARENT);
                    llLine.addView(vSep, lp);
                }
            }

            View vSep = new View(context);
            lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, sepLineWidth);
            llPanel.addView(vSep, lp);
        }

        for (LinearLayout llCell : llCells) {
            ImageView ivLogo = new ImageView(context);
            ivLogo.setScaleType(ScaleType.CENTER_INSIDE);
            lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, logoHeight);
            lp.topMargin = paddingTop;
            llCell.addView(ivLogo, lp);

            TextView tvName = new TextView(context);
            tvName.setTextColor(0xff646464);
            tvName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            tvName.setGravity(Gravity.CENTER);
            lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            lp.weight = 1;
            llCell.addView(tvName, lp);
        }

        return llPanel;
    }

    private void refreshPanel(LinearLayout[] llCells, Object[] logos) {
        int cellBack = com.mob.tools.utils.R.getBitmapRes(page.getContext(), "ssdk_oks_classic_platform_cell_back");
        int disableBack = com.mob.tools.utils.R.getBitmapRes(page.getContext(), "ssdk_oks_classic_platfrom_cell_back_nor");
        for (int i = 0; i < logos.length; i++) {
            ImageView ivLogo = com.mob.tools.utils.R.forceCast(llCells[i].getChildAt(0));
            TextView tvName = com.mob.tools.utils.R.forceCast(llCells[i].getChildAt(1));
            if (logos[i] == null) {
                ivLogo.setVisibility(View.INVISIBLE);
                tvName.setVisibility(View.INVISIBLE);
                llCells[i].setBackgroundResource(disableBack);
                llCells[i].setOnClickListener(null);
            } else {
                ivLogo.setVisibility(View.VISIBLE);
                tvName.setVisibility(View.VISIBLE);
                llCells[i].setBackgroundResource(cellBack);
                llCells[i].setOnClickListener(this);
                llCells[i].setTag(logos[i]);

                if (logos[i] instanceof CustomerLogo) {
                    CustomerLogo logo = com.mob.tools.utils.R.forceCast(logos[i]);
                    if (logo.logo != null) {
                        ivLogo.setImageBitmap(logo.logo);
                    } else {
                        ivLogo.setImageBitmap(null);
                    }
                    if (logo.label != null) {
                        tvName.setText(logo.label);
                    } else {
                        tvName.setText("");
                    }
                } else {
                    Platform plat = com.mob.tools.utils.R.forceCast(logos[i]);
                    String name = plat.getName().toLowerCase();
                    int resId = com.mob.tools.utils.R.getBitmapRes(ivLogo.getContext(), "ssdk_oks_classic_" + name);
                    if (resId > 0) {
                        ivLogo.setImageResource(resId);
                    } else {
                        ivLogo.setImageBitmap(null);
                    }
                    resId = com.mob.tools.utils.R.getStringRes(tvName.getContext(), "ssdk_" + name);
                    if (resId > 0) {
                        tvName.setText(resId);
                    } else {
                        tvName.setText("");
                    }
                }
            }
        }
    }

    public void onClick(View v) {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < MIN_CLICK_INTERVAL) {
            return;
        }
        lastClickTime = time;

        if (v.getTag() instanceof CustomerLogo) {
            CustomerLogo logo = com.mob.tools.utils.R.forceCast(v.getTag());
            page.performCustomLogoClick(v, logo);
        } else {
            Platform plat = com.mob.tools.utils.R.forceCast(v.getTag());
            page.showEditPage(plat);
        }
    }

}