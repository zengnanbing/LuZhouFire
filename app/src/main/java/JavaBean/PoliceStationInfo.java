package JavaBean;

import java.util.List;

/**
 * Created by zeng on 2017/6/6.
 */

public class PoliceStationInfo extends BaseBean {

    private List<PoliceStation> policeStationList;

    public List<PoliceStation> getPoliceStationList() {
        return policeStationList;
    }

    public void setPoliceStationList(List<PoliceStation> policeStationList) {
        this.policeStationList = policeStationList;
    }

    public static class PoliceStation {
        private String policeid;
        private String policeStation;
        private String unit;

        public String getPoliceid() {
            return policeid;
        }

        public void setPoliceid(String policeid) {
            this.policeid = policeid;
        }

        public String getPoliceStation() {
            return policeStation;
        }

        public void setPoliceStation(String policeStation) {
            this.policeStation = policeStation;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

    }

}
