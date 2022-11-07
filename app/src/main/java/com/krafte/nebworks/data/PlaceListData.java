package com.krafte.nebworks.data;

public class PlaceListData {
    private static final String TAG = "ContractData";

    public static class PlaceListData_list {
        String id;
        String name;
        String owner_id;
        String owner_name;
        String registr_num;
        String store_kind;
        String address;
        String latitude;
        String longitude;
        String pay_day;
        String test_period;
        String vacation_select;
        String insurance;
        String start_time;
        String end_time;
        String save_kind;
        String img_path;
        String place_kind;
        String total_cnt;
        String i_cnt;
        String o_cnt;
        String created_at;


        public PlaceListData_list(String id, String name, String owner_id, String owner_name, String registr_num,
                                    String store_kind, String address, String latitude, String longitude, String pay_day, String test_period,
                                    String vacation_select, String insurance, String start_time, String end_time, String save_kind,
                                    String img_path, String place_kind, String total_cnt, String i_cnt, String o_cnt, String created_at) {
            this.id = id;
            this.name = name;
            this.owner_id = owner_id;
            this.owner_name = owner_name;
            this.registr_num = registr_num;
            this.store_kind = store_kind;
            this.address = address;
            this.latitude = latitude;
            this.longitude = longitude;
            this.pay_day = pay_day;
            this.test_period = test_period;
            this.vacation_select = vacation_select;
            this.insurance = insurance;
            this.start_time = start_time;
            this.end_time = end_time;
            this.save_kind = save_kind;
            this.img_path = img_path;
            this.place_kind = place_kind;
            this.total_cnt = total_cnt;
            this.i_cnt = i_cnt;
            this.o_cnt = o_cnt;
            this.created_at = created_at;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getOwner_id() {
            return owner_id;
        }

        public void setOwner_id(String owner_id) {
            this.owner_id = owner_id;
        }

        public String getOwner_name() {
            return owner_name;
        }

        public void setOwner_name(String owner_name) {
            this.owner_name = owner_name;
        }

        public String getRegistr_num() {
            return registr_num;
        }

        public void setRegistr_num(String registr_num) {
            this.registr_num = registr_num;
        }

        public String getStore_kind() {
            return store_kind;
        }

        public void setStore_kind(String store_kind) {
            this.store_kind = store_kind;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }

        public String getPay_day() {
            return pay_day;
        }

        public void setPay_day(String pay_day) {
            this.pay_day = pay_day;
        }

        public String getTest_period() {
            return test_period;
        }

        public void setTest_period(String test_period) {
            this.test_period = test_period;
        }

        public String getVacation_select() {
            return vacation_select;
        }

        public void setVacation_select(String vacation_select) {
            this.vacation_select = vacation_select;
        }

        public String getInsurance() {
            return insurance;
        }

        public void setInsurance(String insurance) {
            this.insurance = insurance;
        }

        public String getStart_time() {
            return start_time;
        }

        public void setStart_time(String start_time) {
            this.start_time = start_time;
        }

        public String getEnd_time() {
            return end_time;
        }

        public void setEnd_time(String end_time) {
            this.end_time = end_time;
        }

        public String getSave_kind() {
            return save_kind;
        }

        public void setSave_kind(String save_kind) {
            this.save_kind = save_kind;
        }

        public String getImg_path() {
            return img_path;
        }

        public void setImg_path(String img_path) {
            this.img_path = img_path;
        }

        public String getPlace_kind() {
            return place_kind;
        }

        public void setPlace_kind(String place_kind) {
            this.place_kind = place_kind;
        }

        public String getTotal_cnt() {
            return total_cnt;
        }

        public void setTotal_cnt(String total_cnt) {
            this.total_cnt = total_cnt;
        }

        public String getI_cnt() {
            return i_cnt;
        }

        public void setI_cnt(String i_cnt) {
            this.i_cnt = i_cnt;
        }

        public String getO_cnt() {
            return o_cnt;
        }

        public void setO_cnt(String o_cnt) {
            this.o_cnt = o_cnt;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }
    }


}
