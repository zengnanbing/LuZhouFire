package JavaBean;

import java.io.Serializable;
import java.util.List;

/**
 * @Description:
 * @author: cyq7on
 * @date: 2016/8/3 0003 10:05
 * @version: V1.0
 */
public class CheckInfo extends BaseBean {

    /**
     * type : 加油站
     * children : [{"address":"四川省泸州市纳溪区打鼓镇紫微村1组56号","checkType":1,"id":590,"master":"","policeid":7,"rectifyid":0,"report":0,"reportid":0,"state":"0","title":"泸州市纳溪区打古加油站"}]
     */

    private List<ResultBean> result;

    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List<ResultBean> result) {
        this.result = result;
    }

    public static class ResultBean {
        private String type;
        /**
         * address : 四川省泸州市纳溪区打鼓镇紫微村1组56号
         * checkType : 1
         * id : 590
         * master :
         * policeid : 7
         * rectifyid : 0
         * report : 0
         * reportid : 0
         * state : 0
         * title : 泸州市纳溪区打古加油站
         */

        private List<ChildrenBean> children;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public List<ChildrenBean> getChildren() {
            return children;
        }

        public void setChildren(List<ChildrenBean> children) {
            this.children = children;
        }

        public static class ChildrenBean implements Serializable{
            private String address;
            private int checkType;
            private int id;
            private String master;
            private String policeid;
            private String rectifyid;
            private String report;
            private String reportid;
            private String state;
            private String title;
            private int day;

            public String getAddress() {
                return address;
            }

            public void setAddress(String address) {
                this.address = address;
            }

            public int getCheckType() {
                return checkType;
            }

            public void setCheckType(int checkType) {
                this.checkType = checkType;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getMaster() {
                return master;
            }

            public void setMaster(String master) {
                this.master = master;
            }

            public String getPoliceid() {
                return policeid;
            }

            public void setPoliceid(String policeid) {
                this.policeid = policeid;
            }

            public String getRectifyid() {
                return rectifyid;
            }

            public void setRectifyid(String rectifyid) {
                this.rectifyid = rectifyid;
            }

            public String getReport() {
                return report;
            }

            public void setReport(String report) {
                this.report = report;
            }

            public String getReportid() {
                return reportid;
            }

            public void setReportid(String reportid) {
                this.reportid = reportid;
            }

            public String getState() {
                return state;
            }

            public void setState(String state) {
                this.state = state;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public int getDay() {
                return day;
            }

            public void setDay(int day) {
                this.day = day;
            }
        }
    }
}
