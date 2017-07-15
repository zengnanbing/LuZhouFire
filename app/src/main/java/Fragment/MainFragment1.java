package Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.navisdk.adapter.BNOuterTTSPlayerCallback;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BNaviSettingManager;
import com.baidu.navisdk.adapter.BaiduNaviManager;
import com.example.myapplication.BNDemoGuideActivity;
import com.example.myapplication.MainActivity;
import com.example.myapplication.MyApplication;
import com.example.myapplication.R;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.Manifest;

import MyInterface.AdressLocation;
import MyInterface.OnMapSelect;
import Utils.FileUtils;
import Utils.MyOrientationListener;
import Utils.ToastUtil;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;
import widget.PoiOverlay;

import static com.baidu.navisdk.adapter.PackageUtil.getSdcardDir;


/**
 * Created by 澄鱼 on 2016/4/25.
 * 地图控件
 */
@SuppressLint("ValidFragment")
//@RuntimePermissions
public class MainFragment1 extends Fragment implements
        OnGetPoiSearchResultListener {

    private TextureMapView mMapView;
    private BaiduMap mBaiduMap;
    private View view;
    private MainActivity mActivity;
    private PoiSearch mPoiSearch = null;
    private SuggestionSearch mSuggestionSearch = null;
    private InfoWindow mInfoWindow;
    private String result;

    //定位相关
    private LocationClient mLocationClient;
    private MyLocationListener myLocationListener;
    private boolean isFirstIn = true;
    private LatLng mLastLocationData;
    private LatLng mDestLocationData;

    // 自定义定位图标
    private BitmapDescriptor mIconLocation;
    private MyOrientationListener myOrientationListener;
    private float mCurrentX;


    //导航相关
    private FloatingActionButton mNavigation;
    //    private Button mNav;
    public static List<Activity> activityList = new LinkedList<>();
    private static final String APP_FOLDER_NAME = "BNSDKSimpleDemo";
    private String mSDCardPath = null;
    public static final String ROUTE_PLAN_NODE = "routePlanNode";


    @SuppressLint("ValidFragment")
    public MainFragment1(MainActivity mActivity) {
        this.mActivity = mActivity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.d("onCreate");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Logger.d("onCreateView");
        view = inflater.inflate(R.layout.fragment1, container, false);


        initview(); // 初始化地图相关内容
        initLocation();//初始化定位
        // 开启定位
//        MainFragment1PermissionsDispatcher.requestLocationWithCheck(MainFragment1.this);//初始化定位
        requestLocation();
        initEvent();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        isFirstIn=true;
        Logger.d("onResume");
        mMapView.setVisibility(View.VISIBLE);
        if (mMapView != null) {
            mMapView.onResume(); // 使百度地图地图控件和Fragment的生命周期保持一致
            mBaiduMap.setMyLocationEnabled(true);
            mLocationClient.start();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Logger.d("onPause");
        mMapView.setVisibility(View.INVISIBLE);
        if (mMapView != null) {
            mMapView.onPause(); // 使百度地图地图控件和Fragment的生命周期保持一致
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Logger.d("onStop");
        mBaiduMap.setMyLocationEnabled(false);
        mLocationClient.stop();
        myOrientationListener.stop();
    }

    @Override
    public void onDestroy() {
        Logger.d("onDestroy");
        super.onDestroy();
        // 停止定位
        mBaiduMap.setMyLocationEnabled(false);
        mLocationClient.stop();
        // 停止方向传感器
        myOrientationListener.stop();
        if (mMapView != null) {
            mMapView.onDestroy(); // 使百度地图地图控件和Fragment的生命周期保持一致
        }
        mPoiSearch.destroy();
        mSuggestionSearch.destroy();
    }

    /*********** 百度地图相关 ***************/
    /**
     * 初始化地图控件
     */
    private void initview() {

        mMapView = (TextureMapView) view.findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        mNavigation = (FloatingActionButton) view.findViewById(R.id.btn_navigation);

//        设定中心点坐标及比例尺(默认纳溪区政府)
        final LatLng latLng = new LatLng(28.7793870000, 105.3777190000);
        setLocationCenter(latLng, 14.7f);

        //添加覆盖物
//        MyOverlay(new LatLng(28.7902470000, 105.4045110000));


        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                mDestLocationData = marker.getPosition();
                Toast.makeText(getActivity(), "设置目的地成功", Toast.LENGTH_SHORT).show();
                return false;
            }

        });

        // 初始化搜索模块，注册搜索事件监听
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);
        mSuggestionSearch = SuggestionSearch.newInstance();
//        mSuggestionSearch.setOnGetSuggestionResultListener();

        //初始化导航相关
        if (initDirs()) {
            initNavi();
        }
    }

    /**
     * 定位相关
     */
//    @NeedsPermission({android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.READ_PHONE_STATE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void requestLocation() {
//        initLocation();
        // 开启定位
        mBaiduMap.setMyLocationEnabled(true);
        if (!mLocationClient.isStarted())
            mLocationClient.start();
        myOrientationListener.start();

    }

    private void initLocation() {

        mLocationClient = new LocationClient(getActivity());
        myLocationListener=new MyLocationListener();
        mLocationClient.registerLocationListener(myLocationListener);

        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度
        option.setCoorType("bd09ll");//设置返回的定位结果坐标系
        option.setIsNeedAddress(true);//设置是否需要地址信息
        option.setOpenGps(true);//设置是否使用GPS
        option.setScanSpan(1000);//设置定位请求间隔1s
        mLocationClient.setLocOption(option);

        //注册监听函数
//        mLocationClient.registerLocationListener(new BDLocationListener() {
//            @Override
//            public void onReceiveLocation(BDLocation bdLocation) {
//                if (bdLocation == null || mMapView == null)
//                    return;
//                //构造定位数据
//                MyLocationData data = new MyLocationData.Builder()//
//                        .direction(mCurrentX)//
//                        .accuracy(bdLocation.getRadius())
//                        //此设置开发者获取道的方向信息，顺时针0-360
//                        .latitude(bdLocation.getLatitude())//
//                        .longitude(bdLocation.getLongitude())//
//                        .build();
//                //设置定位数据
//                mBaiduMap.setMyLocationData(data);
//                // 设置自定义图标
//                mIconLocation = BitmapDescriptorFactory
//                        .fromResource(R.mipmap.navi_map_gps_locked);
//                MyLocationConfiguration config = new MyLocationConfiguration(
//                        MyLocationConfiguration.LocationMode.NORMAL, true, mIconLocation);
//                mBaiduMap.setMyLocationConfigeration(config);
//
//                LatLng ll = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
//                mLastLocationData = ll;
//
//                if (isFirstIn) {
//                    isFirstIn = false;
//                    MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(ll);
//                    mBaiduMap.animateMapStatus(msu);
//
//                }
//            }
//        });

        //监听方向
        myOrientationListener = new MyOrientationListener(getActivity());
        myOrientationListener
                .setOnOrientationListener(new MyOrientationListener.OnOrientationListener() {
                    @Override
                    public void onOrientationChanged(float x) {
                        mCurrentX = x;
                    }
                });



    }


    private void initEvent() {

        mBaiduMap.setOnMapLongClickListener(new BaiduMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                Toast.makeText(getActivity(), "设置目的地成功", Toast.LENGTH_SHORT).show();
                mDestLocationData = latLng;
                MyOverlay(latLng);
            }
        });


        mActivity.SetOnMapSelect(new OnMapSelect() {
            @Override
            public void onMapSelect(int Type) {

                switch (Type) {
                    case 1:
                        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                        break;
                    case 2:
                        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                        break;

                    case 4:
                        mBaiduMap.setTrafficEnabled(true);
                        break;
                    case 5:
                        mBaiduMap.setTrafficEnabled(false);
                        break;
                    default:
                        break;
                }
            }
        });

        //检索地址设置为中心点监听
        mActivity.SetAdressLocation(new AdressLocation() {
            @Override
            public void AdressLocation(LatLng point) {

                setLocationCenter(point, 18.0f);
                //插入覆盖物
                MyOverlay(point);
            }
        });
//        导航
        mNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDestLocationData == null) {
                    Toast.makeText(getActivity(), "长按地图设置目标地点", Toast.LENGTH_SHORT).show();
                    return;
                }
                routeplanToNavi(true);
            }
        });


    }

    public void search(String keyWords) {
        String city = "泸州";
        int loadIndex = 0;
        mPoiSearch.searchInCity((new PoiCitySearchOption())
                .city(city)
                .keyword(keyWords)
                .pageNum(loadIndex));
    }


    /***********
     * 设置定位中心及定位比例尺
     ****************/
    public void setLocationCenter(LatLng latLng, float v) {

        //设定中心点坐标及比例尺(默认纳溪区政府)
        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLngZoom(latLng, v);
        mBaiduMap.animateMapStatus(msu);
    }

    /*********
     * 添加覆盖物方法
     **********/
    private void MyOverlay(final LatLng point) {

        mBaiduMap.clear();
        final LatLng pt = point;

        /**************覆盖物动画**************/
        //构建Marker图标
        ArrayList<BitmapDescriptor> giflist = new ArrayList<BitmapDescriptor>();
        giflist.add(BitmapDescriptorFactory.fromResource(R.mipmap.icon_marki));

        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(point)
                .icons(giflist);

        MarkerOptions ooD = new MarkerOptions().position(point).icons(giflist)
                .zIndex(0).period(10);
        // 生长动画
        ooD.animateType(MarkerOptions.MarkerAnimateType.none);
        Marker mMarkerD = (Marker) (mBaiduMap.addOverlay(ooD));


        /***************点击覆盖物的详细信息的监听*****************/
//        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
//            @Override
//            public boolean onMarkerClick(Marker marker) {
//
//                Detail(pt);
//
//                return false;
//            }
//        });


    }


    /***********
     * 点击覆盖物跳转的方法
     ***********/
//    private void Detail(LatLng point) {
//
//        //创建InfoWindow展示的view
//        Button button = new Button(getActivity());
//        button.setBackgroundResource(R.mipmap.ic_account);
//        //定义用于显示该InfoWindow的坐标点
//        LatLng pt = point;
//        //创建InfoWindow , 传入 view， 地理坐标， y 轴偏移量
//        InfoWindow mInfoWindow = new InfoWindow(button, pt, -47);
//        //显示InfoWindow
//        mBaiduMap.showInfoWindow(mInfoWindow);
//
//
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Toast.makeText(getActivity(), "点击了", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
    @Override
    public void onGetPoiResult(PoiResult result) {
        if (result == null
                || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
            Toast.makeText(getContext(), "未找到结果", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            mBaiduMap.clear();
            PoiOverlay overlay = new MyPoiOverlay(mBaiduMap);
            mBaiduMap.setOnMarkerClickListener(overlay);
            overlay.setData(result);
            overlay.addToMap();
            overlay.zoomToSpan();
            return;
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {

            // 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
            String strInfo = "在";
            for (CityInfo cityInfo : result.getSuggestCityList()) {
                strInfo += cityInfo.city;
                strInfo += ",";
            }
            strInfo += "找到结果";
            Toast.makeText(getContext(), strInfo, Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult result) {
        if (result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(getContext(), "抱歉，未找到结果", Toast.LENGTH_SHORT)
                    .show();
        } else {
//            Toast.makeText(this, result.getName() + ": " + result.getAddress(), Toast.LENGTH_SHORT)
//                    .show();
            this.result = result.getName() + "\n" + result.getAddress();
        }
    }

    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult result) {

    }

    private class MyPoiOverlay extends PoiOverlay {

        public MyPoiOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public boolean onPoiClick(int index) {
            super.onPoiClick(index);
            PoiInfo poi = getPoiResult().getAllPoi().get(index);
            // if (poi.hasCaterDetails) {
            mPoiSearch.searchPoiDetail((new PoiDetailSearchOption())
                    .poiUid(poi.uid));
            // }
            return true;
        }
    }


    //导航
    private boolean initDirs() {
//        mSDCardPath = getSdcardDir();
        mSDCardPath = MyApplication.getInstance().getExternalFilesDir(null).getPath();
        Logger.d(mSDCardPath);
        if (mSDCardPath == null) {
            return false;
        }
        File f = new File(mSDCardPath, APP_FOLDER_NAME);
        if (!f.exists()) {
            try {
                f.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    String authinfo = null;

    /**
     * 内部TTS播报状态回传handler
     */
    private Handler ttsHandler = new Handler() {
        public void handleMessage(Message msg) {
            int type = msg.what;
            switch (type) {
                case BaiduNaviManager.TTSPlayMsgType.PLAY_START_MSG: {
//                    showToastMsg("Handler : TTS play start");
                    break;
                }
                case BaiduNaviManager.TTSPlayMsgType.PLAY_END_MSG: {
//                    showToastMsg("Handler : TTS play end");
                    break;
                }
                default:
                    break;
            }
        }
    };

    /**
     * 内部TTS播报状态回调接口
     */
    private BaiduNaviManager.TTSPlayStateListener ttsPlayStateListener = new BaiduNaviManager.TTSPlayStateListener() {

        @Override
        public void playEnd() {
//            showToastMsg("TTSPlayStateListener : TTS play end");
        }

        @Override
        public void playStart() {
//            showToastMsg("TTSPlayStateListener : TTS play start");
        }
    };

//    public void showToastMsg(final String msg) {
//        getActivity().runOnUiThread(new Runnable() {
//
//            @Override
//            public void run() {
//                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    private void initNavi() {

        BNOuterTTSPlayerCallback ttsCallback = null;

        BaiduNaviManager.getInstance().init(getActivity(), mSDCardPath, APP_FOLDER_NAME, new BaiduNaviManager.NaviInitListener() {
            @Override
            public void onAuthResult(int status, String msg) {
                if (0 == status) {
                    authinfo = "key校验成功!";
                } else {
                    authinfo = "key校验失败, " + msg;
                }
                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
//                        Toast.makeText(getActivity(), authinfo, Toast.LENGTH_LONG).show();
                    }
                });
            }

            public void initSuccess() {
//                Toast.makeText(getActivity(), "百度导航引擎初始化成功", Toast.LENGTH_SHORT).show();
                initSetting();
            }

            public void initStart() {
//                Toast.makeText(getActivity(), "百度导航引擎初始化开始", Toast.LENGTH_SHORT).show();
            }

            public void initFailed() {
                Toast.makeText(getActivity(), "百度导航引擎初始化失败", Toast.LENGTH_SHORT).show();
            }


        }, null, ttsHandler, ttsPlayStateListener);

    }


    private void routeplanToNavi(boolean mock) {

        BNRoutePlanNode.CoordinateType coType = BNRoutePlanNode.CoordinateType.GCJ02;
        BNRoutePlanNode sNode = null;
        BNRoutePlanNode eNode = null;

        //mLastLocationData
        //mDestLocationData

//        sNode = new BNRoutePlanNode(116.30142, 40.05087, "百度大厦", null, coType);
//        eNode = new BNRoutePlanNode(116.39750, 39.90882, "北京天安门", null, coType);

        sNode = new BNRoutePlanNode(mLastLocationData.longitude, mLastLocationData.latitude, "我的位置", null, coType);
        eNode = new BNRoutePlanNode(mDestLocationData.longitude, mDestLocationData.latitude, "目的地", null, coType);


        if (sNode != null && eNode != null)

        {
            List<BNRoutePlanNode> list = new ArrayList<BNRoutePlanNode>();
            list.add(sNode);
            list.add(eNode);
            BaiduNaviManager.getInstance().launchNavigator(getActivity(), list, 1, mock, new DemoRoutePlanListener(sNode));
        }
    }

    public class DemoRoutePlanListener implements BaiduNaviManager.RoutePlanListener {

        private BNRoutePlanNode mBNRoutePlanNode = null;

        public DemoRoutePlanListener(BNRoutePlanNode node) {
            mBNRoutePlanNode = node;
        }

        @Override
        public void onJumpToNavigator() {
            /*
             * 设置途径点以及resetEndNode会回调该接口
			 */

            for (Activity ac : activityList) {

                if (ac.getClass().getName().endsWith("BNDemoGuideActivity")) {

                    return;
                }
            }
            Intent intent = new Intent(getActivity(), BNDemoGuideActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(ROUTE_PLAN_NODE, (BNRoutePlanNode) mBNRoutePlanNode);
            intent.putExtras(bundle);
            startActivity(intent);

        }

        @Override
        public void onRoutePlanFailed() {
            // TODO Auto-generated method stub
            Toast.makeText(getActivity(), "算路失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void initSetting() {
        // 设置是否双屏显示
        BNaviSettingManager.setShowTotalRoadConditionBar(BNaviSettingManager.PreViewRoadCondition.ROAD_CONDITION_BAR_SHOW_ON);
        // 设置导航播报模式
        BNaviSettingManager.setVoiceMode(BNaviSettingManager.VoiceMode.Veteran);
        // 是否开启路况
        BNaviSettingManager.setRealRoadCondition(BNaviSettingManager.RealRoadCondition.NAVI_ITS_ON);
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        MainFragment1PermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
//        switch (requestCode) {
//            case 1:
//                if (grantResults.length > 0) {
//                    for (int result : grantResults) {
//                        ToastUtil toastUtil = new ToastUtil();
//                        toastUtil.Short(mActivity, "必须同意所有权限才能使用本程序").
//                                setToastColor(Color.WHITE, 0xffffc107).show();
//                        return;
//                    }
//                    requestLocation();
//                } else {
//                    ToastUtil toastUtil = new ToastUtil();
//                    toastUtil.Short(mActivity, "发生未知错误").
//                            setToastColor(Color.WHITE, 0xffffc107).show();
//                }
//                break;
//            default:
//        }
//    }

    private class MyLocationListener implements BDLocationListener{

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (bdLocation == null || mMapView == null)
                return;
            //构造定位数据
            MyLocationData data = new MyLocationData.Builder()//
                    .direction(mCurrentX)//
                    .accuracy(bdLocation.getRadius())
                    //此设置开发者获取道的方向信息，顺时针0-360
                    .latitude(bdLocation.getLatitude())//
                    .longitude(bdLocation.getLongitude())//
                    .build();
            //设置定位数据
            mBaiduMap.setMyLocationData(data);
            mBaiduMap.setMyLocationEnabled(true);
            // 设置自定义图标
            mIconLocation = BitmapDescriptorFactory
                    .fromResource(R.mipmap.navi_map_gps_locked);
            MyLocationConfiguration config = new MyLocationConfiguration(
                    MyLocationConfiguration.LocationMode.NORMAL, true, mIconLocation);
            mBaiduMap.setMyLocationConfigeration(config);

            LatLng ll = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
            mLastLocationData = ll;

            if (isFirstIn) {
                MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(ll);
                mBaiduMap.animateMapStatus(msu);
                isFirstIn = false;
            }
        }
    }
}
