package com.example.myapplication;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.example.myapplication.base.BaseActivity;
import com.example.myapplication.fragment.HomeFragment;
import com.example.myapplication.fragment.SlidingFragment;
import com.pgyersdk.activity.FeedbackActivity;
import com.pgyersdk.feedback.PgyFeedbackShakeManager;
import com.pgyersdk.update.PgyUpdateManager;

import java.util.ArrayList;

import Fragment.MainFragment1;
import Fragment.SuperAwesomeCardFragment;
import Utils.SPUtils;
import Utils.ToastUtil;
import widget.MyViewPager;
import widget.PagerSlidingTabStrip;

public class MainActivity extends BaseActivity {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;   //侧边栏开关
    private PagerSlidingTabStrip mPagerSlidingTabStrip;  //滑动标题栏
    private MyViewPager mViewPager;
    private Toolbar mToolbar;
    private ArrayList<Fragment> list;
    private MainFragment1 mainFragment1;     //地图页
    private HomeFragment mainFragment2;     //检查页1
    private HomeFragment mainFragment3;     //检查页2
    private HomeFragment mainFragment4;    //检查页3
    private SearchView searchView;  //搜索框
    private GeoCoder mSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PgyUpdateManager.register(this);
        initData();
        initViews();
        initEvent();

    }


    private void initViews() {


        /***************toolbar设置*********************/
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("检查覆盖率：" + SPUtils.get(this,"coverageRate",""));// 标题的文字需在setSupportActionBar之前，不然会无效
        //mToolbar.setLogo(R.drawable.ic_launcher);
        //mToolbar.setSubtitle("副标题");
        setSupportActionBar(mToolbar);

		/* 这些通过ActionBar来设置也是一样的，注意7777777要在setSupportActionBar(toolbar);之后，不然就报错了 */
        // getSupportActionBar().setTitle("标题");
        // getSupportActionBar().setSubtitle("副标题");
        // getSupportActionBar().setLogo(R.drawable.ic_launcher);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /* findView */
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open,
                R.string.drawer_close);
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mPagerSlidingTabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        mViewPager = (MyViewPager) findViewById(R.id.pager);


        /**********侧边栏布局初始化*****************/
        getSupportFragmentManager().beginTransaction().add(R.id.drawer_view, new SlidingFragment()).commit();


        /*******************初始化地图检索对象**********************/
        mSearch = GeoCoder.newInstance();

    }

    private void initData() {
        list = new ArrayList<>();
        Bundle bundle = new Bundle();
        bundle.putString("which","0");
        mainFragment1 = new MainFragment1(getMyActivity());
        mainFragment2 = HomeFragment.newInstance(bundle);
        bundle = new Bundle();
        bundle.putString("which","1");
        mainFragment3 = HomeFragment.newInstance(bundle);
        bundle = new Bundle();
        bundle.putString("which","2");
        mainFragment4 = HomeFragment.newInstance(bundle);

        list.add(mainFragment1);
        list.add(mainFragment2);
        list.add(mainFragment3);
        list.add(mainFragment4);
    }


    private void initEvent() {

        /* 菜单的监听可以在toolbar里设置，也可以像ActionBar那样，通过下面的两个回调方法来处理 */
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if (mListener != null) {

                    switch (item.getItemId()) {
                        case R.id.normal:
                            mListener.onMapSelect(1);

                            break;
                        case R.id.satelite:
                            mListener.onMapSelect(2);
                            break;

                        case R.id.traffic_on:
                            mListener.onMapSelect(4);
                            break;
                        case R.id.traffic_off:
                            mListener.onMapSelect(5);
                            break;
                        default:
                            break;
                    }
                }

                return true;
            }
        });


        /****************导航栏和viewpager绑定********************/
        mViewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        mPagerSlidingTabStrip.setViewPager(mViewPager);
        mPagerSlidingTabStrip.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                colorChange(arg0);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
        initTabsValue();
    }

    public void changeFragment(int index) {
        mViewPager.setCurrentItem(index);
    }

    /**
     * mPagerSlidingTabStrip默认值配置
     */
    private void initTabsValue() {
        // 底部游标颜色
        mPagerSlidingTabStrip.setIndicatorColor(Color.BLUE);
        // tab的分割线颜色
        mPagerSlidingTabStrip.setDividerColor(Color.TRANSPARENT);
        // tab背景
        mPagerSlidingTabStrip.setBackgroundColor(Color.parseColor("#4876FF"));
        // tab底线高度
        mPagerSlidingTabStrip.setUnderlineHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                1, getResources().getDisplayMetrics()));
        // 游标高度
        mPagerSlidingTabStrip.setIndicatorHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                5, getResources().getDisplayMetrics()));
        // 选中的文字颜色
        mPagerSlidingTabStrip.setSelectedTextColor(Color.WHITE);
        // 正常文字颜色
        mPagerSlidingTabStrip.setTextColor(Color.BLACK);
    }

    /**
     * 界面颜色的更改
     */
    @SuppressLint("NewApi")
    private void colorChange(int position) {
        // 用来提取颜色的Bitmap
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                SuperAwesomeCardFragment.getBackgroundBitmapPosition(position));
        // Palette的部分
        Palette.generateAsync(bitmap, new Palette.PaletteAsyncListener() {
            /**
             * 提取完之后的回调方法
             */
            @Override
            public void onGenerated(Palette palette) {
                Palette.Swatch vibrant = palette.getVibrantSwatch();
                /* 界面颜色UI统一性处理,看起来更Material一些 */
                if (vibrant != null) {

                    mPagerSlidingTabStrip.setBackgroundColor(vibrant.getRgb());
                    mPagerSlidingTabStrip.setTextColor(vibrant.getTitleTextColor());
                    // 其中状态栏、游标、底部导航栏的颜色需要加深一下，也可以不加，具体情况在代码之后说明
                    mPagerSlidingTabStrip.setIndicatorColor(colorBurn(vibrant.getRgb()));

                    mToolbar.setBackgroundColor(vibrant.getRgb());
                    if (android.os.Build.VERSION.SDK_INT >= 21) {
                        Window window = getWindow();
                        // 很明显，这两货是新API才有的。
                        window.setStatusBarColor(colorBurn(vibrant.getRgb()));
                        window.setNavigationBarColor(colorBurn(vibrant.getRgb()));
                    }
                }

            }
        });
    }

    /**
     * 颜色加深处理
     *
     * @param RGBValues RGB的值，由alpha（透明度）、red（红）、green（绿）、blue（蓝）构成，
     *                  Android中我们一般使用它的16进制，
     *                  例如："#FFAABBCC",最左边到最右每两个字母就是代表alpha（透明度）、
     *                  red（红）、green（绿）、blue（蓝）。每种颜色值占一个字节(8位)，值域0~255
     *                  所以下面使用移位的方法可以得到每种颜色的值，然后每种颜色值减小一下，在合成RGB颜色，颜色就会看起来深一些了
     * @return
     */
    private int colorBurn(int RGBValues) {
        int alpha = RGBValues >> 24;
        int red = RGBValues >> 16 & 0xFF;
        int green = RGBValues >> 8 & 0xFF;
        int blue = RGBValues & 0xFF;
        red = (int) Math.floor(red * (1 - 0.1));
        green = (int) Math.floor(green * (1 - 0.1));
        blue = (int) Math.floor(blue * (1 - 0.1));
        return Color.rgb(red, green, blue);
    }


    /***********
     * 可以拿到toolsbar的一些Action按钮的控键对象
     *****************/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        //拿到搜索框的实例对象
        searchView = (SearchView) menu.findItem(R.id.ab_search).getActionView();
        //搜索框搜索结果监听
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

//                getAdressPoint(query);
                switch (mViewPager.getCurrentItem()) {
                    case 0:
                        mainFragment1.search(query);
                        break;
                    case 1:
                        mainFragment2.search(query);
                        break;
                    case 2:
                        mainFragment3.search(query);
                        break;
                    case 3:
                        mainFragment4.search(query);
                        break;
                    default:
                        break;
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    public void search(String query) {
        searchView.setQuery(query,true);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // switch (item.getItemId()) {
        // case R.id.action_settings:
        // Toast.makeText(MainActivity.this, "action_settings", 0).show();
        // break;
        // case R.id.action_share:
        // Toast.makeText(MainActivity.this, "action_share", 0).show();
        // break;
        // default:
        // break;
        // }
        return super.onOptionsItemSelected(item);
    }

    /* ***************FragmentPagerAdapter***************** */
    public class MyPagerAdapter extends FragmentPagerAdapter {

        private final String[] TITLES = {"地图", "常规检查", "整改检查","举报检查"};

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }


        public Fragment getItem(int position) {
            SuperAwesomeCardFragment.newInstance(position);
            return list.get(position);
        }
    }


    /***************
     * 根据实际地址检索地图坐标（经纬度）
     ******************/

    public void getAdressPoint(String adress) {

        mSearch.geocode(new GeoCodeOption()
                .city("泸州市")
                .address(adress));

        mSearch.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
                if (geoCodeResult == null || geoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
                    //没有检索到结果
                    ToastUtil toastUtil = new ToastUtil();
                    toastUtil.Short(MainActivity.this, "没有查询到结果").
                            setToastColor(Color.WHITE, 0xffffc107).show();
                } else {
                    if (mAdressListener != null) {

                        //Log.i("bbb",geoCodeResult.getLocation().toString());
                        //地址检索监听回调
                        mAdressListener.AdressLocation(geoCodeResult.getLocation());

                    }

                }
            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {

            }
        });


    }


    /***************
     * 地图类型选择回调
     ****************/
    private MyInterface.OnMapSelect mListener;

    public void SetOnMapSelect(MyInterface.OnMapSelect Listener) {

        mListener = Listener;

    }


    /***********
     * 地图地址检索回调
     *******************/
    private MyInterface.AdressLocation mAdressListener;

    public void SetAdressLocation(MyInterface.AdressLocation AdressListener) {

        mAdressListener = AdressListener;
    }


    //获取activity对象，传递给fragment，用于地图接口的调用
    public MainActivity getMyActivity() {

        return MainActivity.this;  //返回此服务对象本身
    }
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        // 自定义摇一摇的灵敏度，默认为950，数值越小灵敏度越高。
        PgyFeedbackShakeManager.setShakingThreshold(1000);

        // 以Activity的形式打开，这种情况下必须在AndroidManifest.xml配置FeedbackActivity
        // 打开沉浸式,默认为false
        FeedbackActivity.setBarImmersive(true);
        PgyFeedbackShakeManager.register(MainActivity.this, false);

    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        PgyFeedbackShakeManager.unregister();
    }

}
