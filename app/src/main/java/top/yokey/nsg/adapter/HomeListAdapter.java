package top.yokey.nsg.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import top.yokey.nsg.activity.home.BrowserActivity;
import top.yokey.nsg.activity.mine.CollectionActivity;
import top.yokey.nsg.activity.home.MainActivity;
import top.yokey.nsg.activity.home.NcApplication;
import top.yokey.nsg.R;
import top.yokey.nsg.activity.mine.PropertyActivity;
import top.yokey.nsg.activity.order.OrderActivity;
import top.yokey.nsg.activity.mine.SignActivity;
import top.yokey.nsg.activity.store.StoreListActivity;
import top.yokey.nsg.utility.TextUtil;

public class HomeListAdapter extends RecyclerView.Adapter<HomeListAdapter.ViewHolder> {

    private Activity mActivity;
    private NcApplication mApplication;
    private CountDownTimer mCountDownTimer;
    private ArrayList<HashMap<String, String>> mArrayList;

    public HomeListAdapter(NcApplication application, Activity activity, ArrayList<HashMap<String, String>> arrayList) {
        this.mActivity = activity;
        this.mArrayList = arrayList;
        this.mCountDownTimer = null;
        this.mApplication = application;
    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.advViewPager.setVisibility(View.GONE);

        holder.nav1LinearLayout.setVisibility(View.GONE);
        holder.nav2LinearLayout.setVisibility(View.GONE);

        holder.home1LinearLayout.setVisibility(View.GONE);
        holder.home1TitleTextView.setVisibility(View.GONE);

        holder.home2LinearLayout.setVisibility(View.GONE);
        holder.home2TitleTextView.setVisibility(View.GONE);

        holder.home3LinearLayout.setVisibility(View.GONE);
        holder.home3TitleTextView.setVisibility(View.GONE);

        holder.home4LinearLayout.setVisibility(View.GONE);
        holder.home4TitleTextView.setVisibility(View.GONE);

        holder.goodsLinearLayout.setVisibility(View.GONE);
        holder.goodsTitleTextView.setVisibility(View.GONE);

        HashMap<String, String> hashMap = mArrayList.get(position);

        switch (hashMap.get("keys")) {
            /*
            case "nav":
                parseNav(holder);
                break;
            */
            case "home1":
                parseHome1(holder, hashMap.get("value"));
                break;
            case "home2":
                parseHome2(holder, hashMap.get("value"));
                break;
            case "home3":
                parseHome3(holder, hashMap.get("value"));
                break;
            case "home4":
                parseHome4(holder, hashMap.get("value"));
                break;
            case "goods":
                parseGoods(holder, hashMap.get("value"), 0);
                break;
            case "goods1":
                parseGoods(holder, hashMap.get("value"), 1);
                break;
            case "goods2":
                parseGoods(holder, hashMap.get("value"), 2);
                break;
            case "adv_list":
                parseAdvList(holder, hashMap.get("value"));
                break;
            default:
                break;
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup group, int viewType) {
        View view = LayoutInflater.from(group.getContext()).inflate(R.layout.item_list_home, group, false);
        return new ViewHolder(view);
    }

    private void parseAdvList(final ViewHolder holder, String json) {

        //大小
        final int size;

        //定时器处理
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }

        //解析数据
        try {

            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("item");
            List<View> list = new ArrayList<>();
            ImageView[] imageView = new ImageView[jsonArray.length()];
            size = jsonArray.length();
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = (JSONObject) jsonArray.get(i);
                list.add(mActivity.getLayoutInflater().inflate(R.layout.include_image_view, null));
                imageView[i] = (ImageView) list.get(i).findViewById(R.id.mainImageView);
                ImageLoader.getInstance().displayImage(jsonObject.getString("image"), imageView[i]);
                final String type = jsonObject.getString("type");
                final String data = jsonObject.getString("data");
                imageView[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (type) {
                            case "keyword":
                                mApplication.startKeyword(mActivity, data);
                                break;
                            case "special":
                                mApplication.startSpecial(mActivity, data);
                                break;
                            case "goods":
                                mApplication.startGoods(mActivity, data);
                                break;
                            case "url":
                                startUrl(data);
                                break;
                            default:
                                mApplication.startKeyword(mActivity, "");
                                break;
                        }
                    }
                });
            }

            //适配器
            holder.advViewPager.setAdapter(new ViewPagerAdapter(list));

            //自动轮播
            mCountDownTimer = new CountDownTimer(6000000, 5000) {
                @Override
                public void onTick(long l) {
                    if (holder.advViewPager.getCurrentItem() == (size - 1)) {
                        holder.advViewPager.setCurrentItem(0);
                    } else {
                        holder.advViewPager.setCurrentItem(holder.advViewPager.getCurrentItem() + 1);
                    }
                }

                @Override
                public void onFinish() {

                }
            }.start();

            //显示广告
            holder.advViewPager.setVisibility(View.VISIBLE);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void parseGoods(ViewHolder holder, String json, int t) {

        try {

            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("item");

            //处理标题
            if (!TextUtil.isEmpty(jsonObject.getString("title"))) {
                holder.goodsTitleTextView.setVisibility(View.VISIBLE);
                holder.goodsTitleTextView.setText(jsonObject.getString("title"));
            }

            //商品内容
            ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();

            for (int i = 0; i < jsonArray.length(); i += 2) {
                jsonObject = new JSONObject(jsonArray.get(i).toString());
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("model", t + "");
                hashMap.put("goods_id_1", jsonObject.getString("goods_id"));
                hashMap.put("goods_name_1", jsonObject.getString("goods_name"));
                hashMap.put("goods_price_1", jsonObject.getString("goods_price"));
                hashMap.put("goods_promotion_price_1", jsonObject.getString("goods_promotion_price"));
                hashMap.put("goods_image_1", jsonObject.getString("goods_image"));
                if ((i + 1) < jsonArray.length()) {
                    jsonObject = (JSONObject) jsonArray.get(i + 1);
                    hashMap.put("goods_id_2", jsonObject.getString("goods_id"));
                    hashMap.put("goods_name_2", jsonObject.getString("goods_name"));
                    hashMap.put("goods_price_2", jsonObject.getString("goods_price"));
                    hashMap.put("goods_promotion_price_2", jsonObject.getString("goods_promotion_price"));
                    hashMap.put("goods_image_2", jsonObject.getString("goods_image"));
                } else {
                    hashMap.put("goods_id_2", "");
                    hashMap.put("goods_name_2", "");
                    hashMap.put("goods_price_2", "");
                    hashMap.put("goods_promotion_price_2", "");
                    hashMap.put("goods_image_2", "");
                }
                arrayList.add(hashMap);
            }

            holder.goodsListView.setLayoutManager(new LinearLayoutManager(mActivity));
            holder.goodsListView.setAdapter(new GoodsHomeListAdapter(mApplication, mActivity, arrayList));

            holder.goodsLinearLayout.setVisibility(View.VISIBLE);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void parseHome1(ViewHolder holder, String json) {

        try {

            JSONObject jsonObject = new JSONObject(json);
            final String type = jsonObject.getString("type");
            final String data = jsonObject.getString("data");

            if (!TextUtil.isEmpty(jsonObject.getString("title"))) {
                holder.home1TitleTextView.setVisibility(View.VISIBLE);
                holder.home1TitleTextView.setText(jsonObject.getString("title"));
            }

            if (!TextUtil.isEmpty(jsonObject.getString("image"))) {
                holder.home1ImageView.setVisibility(View.VISIBLE);
                ImageLoader.getInstance().displayImage(jsonObject.getString("image"), holder.home1ImageView);
                holder.home1ImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        switch (type) {
                            case "keyword":
                                mApplication.startKeyword(mActivity, data);
                                break;
                            case "special":
                                mApplication.startSpecial(mActivity, data);
                                break;
                            case "goods":
                                mApplication.startGoods(mActivity, data);
                                break;
                            case "url":
                                startUrl(data);
                                break;
                            default:
                                mApplication.startKeyword(mActivity, "");
                                break;
                        }
                    }
                });
            }

            holder.home1LinearLayout.setVisibility(View.VISIBLE);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void parseHome2(ViewHolder holder, String json) {

        try {

            final JSONObject jsonObject = new JSONObject(json);
            final String square_type = jsonObject.getString("square_type");
            final String square_data = jsonObject.getString("square_data");
            final String rectangle1_type = jsonObject.getString("rectangle1_type");
            final String rectangle1_data = jsonObject.getString("rectangle1_data");
            final String rectangle2_type = jsonObject.getString("rectangle2_type");
            final String rectangle2_data = jsonObject.getString("rectangle2_data");

            //处理标题
            if (!TextUtil.isEmpty(jsonObject.getString("title"))) {
                holder.home2TitleTextView.setVisibility(View.VISIBLE);
                holder.home2TitleTextView.setText(jsonObject.getString("title"));
            }

            //图片显示
            if (!TextUtil.isEmpty(jsonObject.getString("square_image"))) {
                ImageLoader.getInstance().displayImage(jsonObject.getString("square_image"), holder.home2ImageView[0]);
                holder.home2ImageView[0].setVisibility(View.VISIBLE);
                holder.home2ImageView[0].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        switch (square_type) {
                            case "keyword":
                                mApplication.startKeyword(mActivity, square_data);
                                break;
                            case "special":
                                mApplication.startSpecial(mActivity, square_data);
                                break;
                            case "goods":
                                mApplication.startGoods(mActivity, square_data);
                                break;
                            case "url":
                                startUrl(square_data);
                                break;
                            default:
                                mApplication.startKeyword(mActivity, "");
                                break;
                        }
                    }
                });
            }

            if (!TextUtil.isEmpty(jsonObject.getString("rectangle1_image"))) {
                ImageLoader.getInstance().displayImage(jsonObject.getString("rectangle1_image"), holder.home2ImageView[1]);
                holder.home2ImageView[1].setVisibility(View.VISIBLE);
                holder.home2ImageView[1].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        switch (rectangle1_type) {
                            case "keyword":
                                mApplication.startKeyword(mActivity, rectangle1_data);
                                break;
                            case "special":
                                mApplication.startSpecial(mActivity, rectangle1_data);
                                break;
                            case "goods":
                                mApplication.startGoods(mActivity, rectangle1_data);
                                break;
                            case "url":
                                startUrl(rectangle1_data);
                                break;
                            default:
                                mApplication.startKeyword(mActivity, "");
                                break;
                        }
                    }
                });
            }

            if (!TextUtil.isEmpty(jsonObject.getString("rectangle2_image"))) {
                ImageLoader.getInstance().displayImage(jsonObject.getString("rectangle2_image"), holder.home2ImageView[2]);
                holder.home2ImageView[2].setVisibility(View.VISIBLE);
                holder.home2ImageView[2].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        switch (rectangle2_type) {
                            case "keyword":
                                mApplication.startKeyword(mActivity, rectangle2_data);
                                break;
                            case "special":
                                mApplication.startSpecial(mActivity, rectangle2_data);
                                break;
                            case "goods":
                                mApplication.startGoods(mActivity, rectangle2_data);
                                break;
                            case "url":
                                startUrl(rectangle2_data);
                                break;
                            default:
                                mApplication.startKeyword(mActivity, "");
                                break;
                        }
                    }
                });
            }

            //显示
            holder.home2LinearLayout.setVisibility(View.VISIBLE);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void parseHome3(ViewHolder holder, String json) {

        try {

            JSONObject jsonObject = new JSONObject(json);

            //处理标题
            if (!TextUtil.isEmpty(jsonObject.getString("title"))) {
                holder.home3TitleTextView.setVisibility(View.VISIBLE);
                holder.home3TitleTextView.setText(jsonObject.getString("title"));
            }

            ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
            JSONArray jsonArray = new JSONArray(jsonObject.getString("item"));

            for (int i = 0; i < jsonArray.length(); i += 2) {
                jsonObject = new JSONObject(jsonArray.get(i).toString());
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("image_1", jsonObject.getString("image"));
                hashMap.put("type_1", jsonObject.getString("type"));
                hashMap.put("data_1", jsonObject.getString("data"));
                if ((i + 1) < jsonArray.length()) {
                    jsonObject = (JSONObject) jsonArray.get(i + 1);
                    hashMap.put("image_2", jsonObject.getString("image"));
                    hashMap.put("type_2", jsonObject.getString("type"));
                    hashMap.put("data_2", jsonObject.getString("data"));
                } else {
                    hashMap.put("image_2", "");
                    hashMap.put("type_2", "");
                    hashMap.put("data_2", "");
                }
                arrayList.add(hashMap);
            }

            holder.home3ListView.setLayoutManager(new LinearLayoutManager(mActivity));
            holder.home3ListView.setAdapter(new Home3ListAdapter(mApplication, mActivity, arrayList));

            //显示
            holder.home3LinearLayout.setVisibility(View.VISIBLE);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void parseHome4(ViewHolder holder, String json) {

        try {

            JSONObject jsonObject = new JSONObject(json);
            final String square_type = jsonObject.getString("square_type");
            final String square_data = jsonObject.getString("square_data");
            final String rectangle1_type = jsonObject.getString("rectangle1_type");
            final String rectangle1_data = jsonObject.getString("rectangle1_data");
            final String rectangle2_type = jsonObject.getString("rectangle2_type");
            final String rectangle2_data = jsonObject.getString("rectangle2_data");

            //处理标题
            if (!TextUtil.isEmpty(jsonObject.getString("title"))) {
                holder.home4TitleTextView.setVisibility(View.VISIBLE);
                holder.home4TitleTextView.setText(jsonObject.getString("title"));
            }

            //图片显示
            if (!TextUtil.isEmpty(jsonObject.getString("square_image"))) {
                ImageLoader.getInstance().displayImage(jsonObject.getString("square_image"), holder.home4ImageView[0]);
                holder.home4ImageView[0].setVisibility(View.VISIBLE);
                holder.home4ImageView[0].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        switch (square_type) {
                            case "keyword":
                                mApplication.startKeyword(mActivity, square_data);
                                break;
                            case "special":
                                mApplication.startSpecial(mActivity, square_data);
                                break;
                            case "goods":
                                mApplication.startGoods(mActivity, square_data);
                                break;
                            case "url":
                                startUrl(rectangle1_data);
                                break;
                            default:
                                mApplication.startKeyword(mActivity, "");
                                break;
                        }
                    }
                });
            }

            if (!TextUtil.isEmpty(jsonObject.getString("rectangle1_image"))) {
                ImageLoader.getInstance().displayImage(jsonObject.getString("rectangle1_image"), holder.home4ImageView[1]);
                holder.home4ImageView[1].setVisibility(View.VISIBLE);
                holder.home4ImageView[1].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        switch (rectangle1_type) {
                            case "keyword":
                                mApplication.startKeyword(mActivity, rectangle1_data);
                                break;
                            case "special":
                                mApplication.startSpecial(mActivity, rectangle1_data);
                                break;
                            case "goods":
                                mApplication.startGoods(mActivity, rectangle1_data);
                                break;
                            case "url":
                                startUrl(rectangle1_data);
                                break;
                            default:
                                mApplication.startKeyword(mActivity, "");
                                break;
                        }
                    }
                });
            }

            if (!TextUtil.isEmpty(jsonObject.getString("rectangle2_image"))) {
                ImageLoader.getInstance().displayImage(jsonObject.getString("rectangle2_image"), holder.home4ImageView[2]);
                holder.home4ImageView[2].setVisibility(View.VISIBLE);
                holder.home4ImageView[2].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        switch (rectangle2_type) {
                            case "keyword":
                                mApplication.startKeyword(mActivity, rectangle2_data);
                                break;
                            case "special":
                                mApplication.startSpecial(mActivity, rectangle2_data);
                                break;
                            case "goods":
                                mApplication.startGoods(mActivity, rectangle2_data);
                                break;
                            case "url":
                                startUrl(rectangle2_data);
                                break;
                            default:
                                mApplication.startKeyword(mActivity, "");
                                break;
                        }
                    }
                });
            }

            //显示
            holder.home4LinearLayout.setVisibility(View.VISIBLE);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void parseNav(ViewHolder holder) {

        holder.nav1LinearLayout.setVisibility(View.VISIBLE);
        holder.nav2LinearLayout.setVisibility(View.VISIBLE);

        //分类
        holder.navTextView[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.mTabHost.setCurrentTab(1);
            }
        });

        //购物车
        holder.navTextView[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.mTabHost.setCurrentTab(3);
            }
        });

        //店铺
        holder.navTextView[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mApplication.startActivity(mActivity, new Intent(mActivity, StoreListActivity.class));
            }
        });

        //签到
        holder.navTextView[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mApplication.startActivityLoginSuccess(mActivity, new Intent(mActivity, SignActivity.class));
            }
        });

        //我的
        holder.navTextView[4].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.mTabHost.setCurrentTab(4);
            }
        });

        //订单
        holder.navTextView[5].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mApplication.startActivityLoginSuccess(mActivity, new Intent(mActivity, OrderActivity.class));
            }
        });

        //财产
        holder.navTextView[6].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mApplication.startActivityLoginSuccess(mActivity, new Intent(mActivity, PropertyActivity.class));
            }
        });

        //足迹
        holder.navTextView[7].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, CollectionActivity.class);
                intent.putExtra("position", 2);
                mApplication.startActivityLoginSuccess(mActivity, intent);
            }
        });

    }

    private void startUrl(String link) {

        try {
            link = URLDecoder.decode(link, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (link.contains("gc_id")) {
            String gc_id = link.substring(link.lastIndexOf("=") + 1, link.length());
            mApplication.startCategory(mActivity, gc_id);
            return;
        }

        if (link.contains("goods_id")) {
            String goods_id = link.substring(link.lastIndexOf("=") + 1, link.length());
            mApplication.startGoods(mActivity, goods_id);
            return;
        }

        if (link.contains("product_list.html") && !link.contains("?")) {
            mApplication.startKeyword(mActivity, "");
            return;
        }

        if (link.contains("product_list.html") && link.contains("keyword")) {
            mApplication.startKeyword(mActivity, link.substring(link.lastIndexOf("=") + 1, link.length()));
            return;
        }

        Intent intent = new Intent(mActivity, BrowserActivity.class);
        intent.putExtra("model", "normal");
        intent.putExtra("link", link);
        mApplication.startActivity(mActivity, intent);

    }

    class ViewHolder extends RecyclerView.ViewHolder {

        //adv
        private ViewPager advViewPager;
        //nav
        private LinearLayout nav1LinearLayout;
        private LinearLayout nav2LinearLayout;
        private TextView[] navTextView;
        //home1
        private LinearLayout home1LinearLayout;
        private ImageView home1ImageView;
        private TextView home1TitleTextView;
        //home2
        private LinearLayout home2LinearLayout;
        private TextView home2TitleTextView;
        private ImageView[] home2ImageView;
        //home3
        private LinearLayout home3LinearLayout;
        private TextView home3TitleTextView;
        private RecyclerView home3ListView;
        //home4
        private LinearLayout home4LinearLayout;
        private TextView home4TitleTextView;
        private ImageView[] home4ImageView;
        //goods
        private LinearLayout goodsLinearLayout;
        private TextView goodsTitleTextView;
        private RecyclerView goodsListView;

        private ViewHolder(View view) {
            super(view);

            advViewPager = (ViewPager) view.findViewById(R.id.advViewPager);

            nav1LinearLayout = (LinearLayout) view.findViewById(R.id.nav1LinearLayout);
            nav2LinearLayout = (LinearLayout) view.findViewById(R.id.nav2LinearLayout);
            navTextView = new TextView[8];
            navTextView[0] = (TextView) view.findViewById(R.id.categoryTextView);
            navTextView[1] = (TextView) view.findViewById(R.id.cartTextView);
            navTextView[2] = (TextView) view.findViewById(R.id.storeTextView);
            navTextView[3] = (TextView) view.findViewById(R.id.signTextView);
            navTextView[4] = (TextView) view.findViewById(R.id.mineTextView);
            navTextView[5] = (TextView) view.findViewById(R.id.orderTextView);
            navTextView[6] = (TextView) view.findViewById(R.id.propertyTextView);
            navTextView[7] = (TextView) view.findViewById(R.id.footprintTextView);

            home1LinearLayout = (LinearLayout) view.findViewById(R.id.home1LinearLayout);
            home1TitleTextView = (TextView) view.findViewById(R.id.home1TitleTextView);
            home1ImageView = (ImageView) view.findViewById(R.id.home1ImageView);

            home2LinearLayout = (LinearLayout) view.findViewById(R.id.home2LinearLayout);
            home2TitleTextView = (TextView) view.findViewById(R.id.home2TitleTextView);
            home2ImageView = new ImageView[3];
            home2ImageView[0] = (ImageView) view.findViewById(R.id.home21ImageView);
            home2ImageView[1] = (ImageView) view.findViewById(R.id.home22ImageView);
            home2ImageView[2] = (ImageView) view.findViewById(R.id.home23ImageView);

            home3LinearLayout = (LinearLayout) view.findViewById(R.id.home3LinearLayout);
            home3TitleTextView = (TextView) view.findViewById(R.id.home3TitleTextView);
            home3ListView = (RecyclerView) view.findViewById(R.id.home3ListView);

            home4LinearLayout = (LinearLayout) view.findViewById(R.id.home4LinearLayout);
            home4TitleTextView = (TextView) view.findViewById(R.id.home4TitleTextView);
            home4ImageView = new ImageView[3];
            home4ImageView[0] = (ImageView) view.findViewById(R.id.home41ImageView);
            home4ImageView[1] = (ImageView) view.findViewById(R.id.home42ImageView);
            home4ImageView[2] = (ImageView) view.findViewById(R.id.home43ImageView);

            goodsLinearLayout = (LinearLayout) view.findViewById(R.id.goodsLinearLayout);
            goodsTitleTextView = (TextView) view.findViewById(R.id.goodsTitleTextView);
            goodsListView = (RecyclerView) view.findViewById(R.id.goodsListView);

        }

    }

}