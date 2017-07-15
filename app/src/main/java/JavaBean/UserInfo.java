package JavaBean;

/**
 * @Description:
 * @author: cyq7on
 * @date: 2016/8/2 0002 10:34
 * @version: V1.0
 */
public class UserInfo extends BaseBean{

    /**
     * coverageRate : 4.41%
     * idcard :
     * password : 123456
     * policeStation : 泸州市公安局纳溪区分局护国派出所
     * state : success
     * tel : 15528330581
     * unitCount : 68
     * userid : 11
     */

    public ResultBean result;

    public static class ResultBean {
        public String coverageRate;
        public String idcard;
        public String password;
        public String policeStation;
        public String state;
        public String tel;
        public String unitCount;
        public Integer userid;
    }
}
