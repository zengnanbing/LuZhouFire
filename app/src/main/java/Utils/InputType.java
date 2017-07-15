package Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 澄鱼 on 2016/5/8.
 */
public class InputType {

    /*************邮箱验证****************/
    public static  boolean  emails(String emails){

        String str = "^\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
        Pattern pattern = Pattern.compile(str);

        Matcher  matcher = pattern.matcher(emails);

        return  matcher.matches();

    }

    /**************电话号码验证*******************/
    public static  boolean  phone(String phone){

        String str="^((13[0-9])|(15[^4,\\D])|(18[0,1-9]))\\d{8}$";

        Pattern pattern = Pattern.compile(str);

        Matcher  matcher = pattern.matcher(phone);

        return  matcher.matches();
    }


    /**************密码验证(以字母开头，长度在6~12之间，只能包含字符、数字和下划线。)*******************/
    public static  boolean  password(String password){

        String str="^[0-9a-zA-Z]{5,13}$";

        Pattern pattern = Pattern.compile(str);

        Matcher  matcher = pattern.matcher(password);

        return  matcher.matches();
    }

    /**************非空验证*******************/
    public static  boolean  isNull(String isnull){

        String str="[^*]{1,}";

        Pattern pattern = Pattern.compile(str);

        Matcher  matcher = pattern.matcher(isnull);

        return  matcher.matches();
    }

}
