package com.krafte.nebworks.data;

public class PlaceListData {
    private static final String TAG = "ContractData";

    public static class PlaceListData_list {
        private String id;
        private String name;
        private String owner_id;
        private String owner_name;
        private String management_office;
        private String address;
        private String latitude;
        private String longitude;
        private String start_time;
        private String end_time;
        private String img_path;
        private String start_date;
        private String total_cnt;
        private String i_cnt;
        private String o_cnt;
        private String created_at;


        public PlaceListData_list(String id, String name, String owner_id, String owner_name,
                                  String management_office, String address, String latitude,
                                  String longitude, String start_time, String end_time,
                                  String img_path, String start_date, String total_cnt,
                                  String i_cnt, String o_cnt, String created_at) {
            this.id = id;
            this.name = name;
            this.owner_id = owner_id;
            this.owner_name = owner_name;
            this.management_office = management_office;
            this.address = address;
            this.latitude = latitude;
            this.longitude = longitude;
            this.start_time = start_time;
            this.end_time = end_time;
            this.img_path = img_path;
            this.start_date = start_date;
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

        public String getManagement_office() {
            return management_office;
        }

        public void setManagement_office(String management_office) {
            this.management_office = management_office;
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

        public String getImg_path() {
            return img_path;
        }

        public void setImg_path(String img_path) {
            this.img_path = img_path;
        }

        public String getStart_date() {
            return start_date;
        }

        public void setStart_date(String start_date) {
            this.start_date = start_date;
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
