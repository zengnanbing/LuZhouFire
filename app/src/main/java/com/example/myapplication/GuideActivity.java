package com.example.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import Animation.ZoomOutPageTransformer;
import Utils.FileUtils;
import Utils.mIntent;

public class GuideActivity extends AppCompatActivity {

    private int[] images = {R.mipmap.guide1, R.mipmap.guide2, R.mipmap.guide3, R.mipmap.guide4};
    private ViewPager mPager;
    private List<View> datas;
    private LinearLayout lay_point;
    private int mPointWidth;
    private ImageView red_point;
    private Button guide_but;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置全屏沉浸栏模式
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_guide);

        initUI();

        initAnimation();

        initData();

        initEvent();

    }

    private void initEvent() {

        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                //  Log.i("aaa","position"+position+"positionOffset"+positionOffset+"positionOffsetPixels"+positionOffsetPixels);

                //计算当前小红点的左边距
                int leftMargin = (int) (mPointWidth * positionOffset + position * mPointWidth);

                //修改小紅點的左邊距
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) red_point.getLayoutParams();
                params.leftMargin = leftMargin;
                red_point.setLayoutParams(params);

            }

            @Override
            public void onPageSelected(int position) {

                if (3 == position) {
                    guide_but.setVisibility(View.VISIBLE);
                } else {
                    guide_but.setVisibility(View.GONE);
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {


            }
        });


        /*************结束导航按钮***************/
        guide_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mIntent.intent(GuideActivity.this, LoginActivity.class);
//                SPUtils.put(GuideActivity.this, "IsFirst", false); //改变状态
                finish();
            }
        });

    }

    private void initAnimation() {

        //为ViewPager添加动画效果,只有3.0后才有效果，使用的是属性动画
        mPager.setPageTransformer(true, new ZoomOutPageTransformer());
        //viewPager.setPageTransformer(true, new DepthPageTransformer());

    }

    private void initData() {
//        新建缓存文件夹
        String path = FileUtils.getPath();
        if (!FileUtils.isFileExist(path)) {
            FileUtils.createFolder(FileUtils.getPath());
        }
        mPager.setAdapter(new ViewPagerAdapter());
        //页面绘制结束之后，计算两个圆点的间距
        //视图树
        lay_point.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            //layout方法执行结束（位置确定）
            public void onGlobalLayout() {

                //移除监听
                lay_point.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                //获取两个圆点的间距
                mPointWidth = lay_point.getChildAt(1).getLeft()
                        - lay_point.getChildAt(0).getLeft();
                // Log.i("bbb", mPointWidth +"");
            }
        });

    }

    private void initUI() {

        mPager = (ViewPager) findViewById(R.id.viewpager_guide);
        lay_point = (LinearLayout) findViewById(R.id.guide_point);
        red_point = (ImageView) findViewById(R.id.red_point);
        guide_but = (Button) findViewById(R.id.guide_but);


        /**********将导航页装到集合中传到数据适配器*********/
        datas = new ArrayList<View>();

        for (int i = 0; i < images.length; i++) {

            ImageView imageview = new ImageView(this);
            imageview.setBackgroundResource(images[i]);

            datas.add(imageview);

        }


        /****************导航小圆点********************/

        for (int i = 0; i < images.length; i++) {

            ImageView imagePoint = new ImageView(this);
            imagePoint.setImageResource(R.drawable.guaide_point_normal);


            // 找到布局的参数设置布局
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.leftMargin = 15;
            // 从第二个圆点开始设置左边距
            if (i != 0) {

                imagePoint.setLayoutParams(params);
                imagePoint.setEnabled(false); // 除第一个外设置不可用改变指示器颜色
            }

            lay_point.addView(imagePoint);


            //  Log.i("bbb",lay_point.getChildCount()+"个数");

        }


    }


    class ViewPagerAdapter extends PagerAdapter {


        @Override
        public int getCount() {
            return datas.size();
        }


        /*****
         * 当前加载的是否是正确的item，防止错位
         *****/
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }


        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            container.addView(datas.get(position));
            return datas.get(position);

        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {

            container.removeView(datas.get(position));   //移除对应页面
        }
    }
}
