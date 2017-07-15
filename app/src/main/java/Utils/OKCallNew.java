package Utils;

import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.callback.Callback;

import JavaBean.CheckInfo;
import okhttp3.Response;

/**
 * @Description: 对Callback的封装，可以转换成任意对象
 * @author: cyq7on
 * @date: 2016/8/2 10:06
 * @version: V1.0
 */

public abstract class OKCallNew<T> extends Callback<T> {
    private Class<T> mClass;
    private String which;

    public OKCallNew(Class<T> cls, String type) {
        mClass = cls;
        which = type;
    }


    @Override
    public T parseNetworkResponse(Response response, int id) throws Exception {
        String string = response.body().string();
        Logger.json(string);
        T t = new Gson().fromJson(string, mClass);

//        // 获取SD卡路径
//        String path = FileUtils.getPath();
//
//        switch (which) {
//            case "0":
//                // 文件名
//                path = path + "/CheckInfo1.txt";
//                FileUtils.writeFile(path, string);
//                break;
//            case "1":
//                // 文件名
//                path = path + "/" + "CheckInfo2.txt";
//                FileUtils.writeFile(path, string);
//                break;
//            case "2":
//                // 文件名
//                path = path + "/" + "CheckInfo3.txt";
//                FileUtils.writeFile(path, string);
//                break;
//        }

//        Logger.d(path);
//        Logger.d(which);
        return t;
    }
}