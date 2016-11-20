package top.yokey.nsg.activity.order;

import android.support.v7.app.AppCompatActivity;

import com.umeng.analytics.MobclickAgent;

public class OrderVrActivity extends AppCompatActivity {

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

}