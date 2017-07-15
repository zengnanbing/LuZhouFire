package JavaBean;


import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @Description:
 * @author: cyq7on
 * @date: 2016/8/27 18:20
 * @version: V1.0
 */
public class HistoryInfo {
    /**
     * result : {"dailyCheck":[{"checkdate":"2017-07-06","firetableid":"389"},{"checkdate":"2017-07-06","firetableid":"389"}],"troubleCheck":[{"checkdate":"2017-07-06","troubletableid":"389"},{"checkdate":"2017-07-06","troubletableid":"389"}]}
     */

    public ResultBean result;

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public static class ResultBean {
        private List<CheckBean> dailyCheck;
        private List<CheckBean> troubleCheck;

        public List<CheckBean> getDailyCheck() {
            return dailyCheck;
        }

        public void setDailyCheck(List<CheckBean> dailyCheck) {
            this.dailyCheck = dailyCheck;
        }

        public List<CheckBean> getTroubleCheck() {
            return troubleCheck;
        }

        public void setTroubleCheck(List<CheckBean> troubleCheck) {
            this.troubleCheck = troubleCheck;
        }

        public static class CheckBean {
            /**
             * checkdate : 2017-07-06
             * firetableid : 389
             */

            private String checkdate;
            private String firetableid;
            private String troubletableid;

            public String getCheckdate() {
                return checkdate;
            }

            public void setCheckdate(String checkdate) {
                this.checkdate = checkdate;
            }

            public String getFiretableid() {
                return firetableid;
            }

            public void setFiretableid(String firetableid) {
                this.firetableid = firetableid;
            }

            public String getTroubletableid() {
                return troubletableid;
            }

            public void setTroubletableid(String troubletableid) {
                this.troubletableid = troubletableid;
            }
        }


    }
}
