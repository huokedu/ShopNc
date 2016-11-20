package top.yokey.nsg.adapter;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class MainPagerAdapter extends PagerAdapter {

    private List<View> list = new ArrayList<>();

    public MainPagerAdapter(ArrayList<View> list) {
        this.list = list;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ViewPager pViewPager = ((ViewPager) container);
        pViewPager.removeView(list.get(position));
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object instantiateItem(ViewGroup arg0, int arg1) {
        ViewPager pViewPager = ((ViewPager) arg0);
        pViewPager.addView(list.get(arg1));
        return list.get(arg1);
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

}