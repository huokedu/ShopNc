package top.yokey.nsg.activity.home;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.Vector;

import top.yokey.nsg.adapter.MainPagerAdapter;
import top.yokey.nsg.control.PhotoViewPager;
import top.yokey.nsg.R;
import top.yokey.nsg.utility.DisplayUtil;
import top.yokey.nsg.utility.TextUtil;
import uk.co.senab.photoview.PhotoViewAttacher;

public class PhotoActivity extends AppCompatActivity {

    private Activity mActivity;
    private NcApplication mApplication;

    private String titleString;
    private Vector<String> imageVector;

    private ImageView backImageView;
    private TextView titleTextView;
    private ImageView moreImageView;

    private ImageView[] mImageView;
    private PhotoViewPager mViewPager;
    private PhotoViewAttacher mAttCher;

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            returnActivity();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_photo);
        initView();
        initData();
        initEven();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAttCher.cleanup();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    private void initView() {

        backImageView = (ImageView) findViewById(R.id.backImageView);
        titleTextView = (TextView) findViewById(R.id.titleTextView);
        moreImageView = (ImageView) findViewById(R.id.moreImageView);
        mViewPager = (PhotoViewPager) findViewById(R.id.mainViewPager);

    }

    private void initData() {

        mActivity = this;
        mApplication = (NcApplication) getApplication();

        titleString = mActivity.getIntent().getStringExtra("title");
        int positionInt = mActivity.getIntent().getIntExtra("position", 0);
        imageVector = new Vector<>(TextUtil.encodeImage(mActivity.getIntent().getStringExtra("image")));
        mImageView = new ImageView[imageVector.size()];

        backImageView.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.black));
        titleTextView.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.black));
        moreImageView.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.black));

        titleTextView.setText(titleString);
        titleTextView.append(" - ");
        titleTextView.append(positionInt + 1 + "");
        titleTextView.append("/");
        titleTextView.append(imageVector.size() + "");

        ArrayList<View> mArrayList = new ArrayList<>();
        for (int i = 0; i < imageVector.size(); i++) {
            final int pos = i;
            mArrayList.add(getLayoutInflater().inflate(R.layout.include_image_view, null));
            mImageView[i] = (ImageView) mArrayList.get(i).findViewById(R.id.mainImageView);
            mImageView[i].setBackgroundColor(ContextCompat.getColor(mActivity, R.color.black));
            ImageLoader.getInstance().displayImage(imageVector.get(i), mImageView[i], new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    super.onLoadingComplete(imageUri, view, loadedImage);
                    mImageView[pos].setImageBitmap(loadedImage);
                    mImageView[pos].setScaleType(ImageView.ScaleType.CENTER);
                    mImageView[pos].setBackgroundColor(ContextCompat.getColor(mActivity, R.color.black));
                    mAttCher = new PhotoViewAttacher(mImageView[pos]);
                    mAttCher.update();
                }
            });
        }

        mViewPager.setAdapter(new MainPagerAdapter(mArrayList));
        mViewPager.setCurrentItem(positionInt);
        mAttCher = new PhotoViewAttacher(mImageView[positionInt]);
        DisplayUtil.setFullScreen(mActivity);
        mAttCher.update();

    }

    private void initEven() {

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnActivity();
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                titleTextView.setText(titleString);
                titleTextView.append(" - ");
                titleTextView.append(position + 1 + "");
                titleTextView.append("/");
                titleTextView.append(imageVector.size() + "");
                mAttCher = new PhotoViewAttacher(mImageView[position]);
                mAttCher.update();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void returnActivity() {

        mApplication.finishActivity(mActivity);

    }

}