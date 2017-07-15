package Utils;

import android.content.Context;
import android.content.Intent;

/**
 * Created by 澄鱼 on 2016/4/14.
 * 类之间的跳转
 */
public class mIntent {

    private  Context context;
    private  Class<?> DesClass;

    private mIntent() {

        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");


    }

    public  static  void  intent(Context context,Class<?> DesClass){
        Intent intent =  new Intent(context,DesClass);
        context.startActivity(intent);
    }
}
