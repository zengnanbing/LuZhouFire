package JavaBean;

import android.os.Environment;

/**
 * Created by 澄鱼 on 2016/6/5.
 */
public class PathUrl {

    public static String  mainUri = Environment.getExternalStorageDirectory().getAbsolutePath()+"/NaxiApp";
    public static String  task = mainUri+"/task_list";   //任务文件夹
    public static String  taskImage = task+"/a.png";   //任务照片路径
    public static String  headphoto = mainUri+"/headphoto.jpg"; //头像路劲
    public static String  taskCache = mainUri+"/task_cache";  //缓存文件夹

}
