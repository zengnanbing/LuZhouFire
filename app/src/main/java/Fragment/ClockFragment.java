package Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;

import java.io.File;

import JavaBean.PathUrl;
import Utils.SPUtils;
import Utils.mIntent;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import widget.CircleImageView;
import widget.GestureLockView;

/**
 * Created by 澄鱼 on 2016/5/23.
 * 集中管理图案解锁密码的设置和验证
 */

@SuppressLint("ValidFragment")
public class ClockFragment extends Fragment {

    @BindView(R.id.slidingToast)
    TextView slidingToast;
    @BindView(R.id.clock)
    GestureLockView clock;
    @BindView(R.id.head_photo)
    CircleImageView headPhoto;
    private int mFlag_activity;
    private String pas = "";
    private SharedPreferences sp;
    private Unbinder unbinder;
    @SuppressLint("ValidFragment")
    public ClockFragment(int Flag_activity) {
        this.mFlag_activity = Flag_activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_clock, container,false);

        unbinder = ButterKnife.bind(this, view);

        if (mFlag_activity == 2) {

            initView2();
        } else {
            initView3();
        }

        return view;
    }

    //验证密码
    private void initView3() {

        initHeadPhoto();

        slidingToast.setText("请输入图案密码");

        clock.setOnGestureFinishListener(new GestureLockView.OnGestureFinishListener() {
            @Override
            public void OnGestureFinish(boolean success, String key) {

                if (success) {

                    if (key.equals((String) SPUtils.get(getActivity(), "pic_password", "没有"))) {
                        slidingToast.setText("密码正确");
                        mIntent.intent(getActivity(), MainActivity.class);
                        getActivity().finish();
                    } else {
                        slidingToast.setText("请输入正确密码");
                    }

                } else {
                    slidingToast.setText("密码必需大于4位");
                }

            }
        });

    }


    //设置密码
    private void initView2() {

        initHeadPhoto();

        slidingToast.setText("请设置图案密码");

        clock.setOnGestureFinishListener(new GestureLockView.OnGestureFinishListener() {
            @Override
            public void OnGestureFinish(boolean success, String key) {

                if (success) {
                    slidingToast.setText("请再次绘制图案密码");
                    pas = key;
                    clock.setOnGestureFinishListener(new GestureLockView.OnGestureFinishListener() {
                        @Override
                        public void OnGestureFinish(boolean success, String key) {

                            if (success) {

                                if (key.equals(pas)) {
                                    slidingToast.setText("密码设置成功");

                                    SPUtils.put(getActivity(), "pic_password", key);

                                    startActivity(new Intent(getActivity(), MainActivity.class));
                                    getActivity().finish();

                                } else {
                                    slidingToast.setText("两次密码不一致");
                                }

                            } else {
                                slidingToast.setText("请正确绘制密码");
                            }
                        }
                    });

                } else {
                    slidingToast.setText("密码必需大于4位");
                }

            }
        });


    }

    private void initHeadPhoto() {

        File img = new  File(PathUrl.headphoto);
        if (img.exists()){

            headPhoto.setImageBitmap(BitmapFactory.decodeFile(img.getAbsolutePath()));
        }

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
