package widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by 澄鱼 on 2016/4/27.
 */
public class MyViewPager extends ViewPager {

    private GestureDetector mDetector;
    private int startX;
    private WindowManager wm;
    private int width;

    public MyViewPager(Context context) {
        super(context);
        initView();

    }

    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {

        wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        width = wm.getDefaultDisplay().getWidth();

        mDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {

            /*********** 监听滑动的事件 **********/

			/*
             * (non-Javadoc)
			 *
			 * @see
			 * android.view.GestureDetector.SimpleOnGestureListener#onScroll(
			 * android.view.MotionEvent, android.view.MotionEvent, float, float)
			 * distanceX：相对位移量
			 */
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                // TODO Auto-generated method stub

                scrollBy((int) distanceX, 0); // 相对位移，移动view的位置
                // scrollTo(x, y); 绝对位移，移动到X Y坐标的位置

                return super.onScroll(e1, e2, distanceX, distanceY);
            }

        });

    }



    /***************通过触摸位置判断，如果不禁用滑动，黑边问题依然无法解决***************/
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        int x = (int) ev.getRawX();
        switch (ev.getAction()) {

            case MotionEvent.ACTION_DOWN:

                if (0 == this.getCurrentItem()) {
                   return x > (width * 0.9) || x < (width * 0.2);
                }
        }

        return super.onInterceptTouchEvent(ev);
    }

    /*******禁止掉第一页地图的滑动事件***********/
  /*  @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {

            case MotionEvent.ACTION_DOWN:
                startX = (int) ev.getX();
                return  true;
            case MotionEvent.ACTION_MOVE:

                int endX = (int) ev.getX();
                int dx = endX - startX;
                    //判斷事件是向左滑动
                    if (dx < 0) {
                        if (0 == this.getCurrentItem()) {
                            return false;
                        }
                    }

                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;

        }
        return super.onTouchEvent(ev);
    }*/

}





