package Utils;

import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.callback.Callback;

import okhttp3.Response;

/**
 * @Description: 对Callback的封装，可以转换成任意对象
 * @author: cyq7on
 * @date: 2016/8/2 10:06
 * @version: V1.0
 */

public abstract class OKCall<T> extends Callback<T> {
    private Class<T> mClass;

    public OKCall(Class<T> cls) {
        mClass = cls;
    }

    @Override
    public T parseNetworkResponse(Response response, int id) throws Exception {
        String string = response.body().string();
        Logger.json(string);
        T t = new Gson().fromJson(string, mClass);
        return t;
    }
}