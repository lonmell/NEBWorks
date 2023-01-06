package com.krafte.nebworks.data;

public class PlaceCheckData {
    private static String place_name = "";
    private static String place_owner_id = "";
    private static String place_owner_name = "";
    private static String registr_num = "";
    private static String store_kind = "";
    private static String place_address = "";
    private static String place_latitude = "";
    private static String place_longitude = "";
    private static String place_pay_day = "";
    private static String place_test_period = "";
    private static String place_vacation_select = "";
    private static String place_insurance = "";
    private static String place_start_time = "";
    private static String place_end_time = "";
    private static String place_save_kind = "";
    private static String place_wifi_name = "";
    private static String place_img_path = "";
    private static String place_start_date = "";
    private static String place_created_at = "";
    private static String place_icnt = "";
    private static String place_ocnt = "";
    private static String place_totalcnt = "";

    public String getPlace_name() {
        return place_name;
    }

    public void setPlace_name(String place_name) {
        this.place_name = place_name;
    }

    public String getPlace_owner_id() {
        return place_owner_id;
    }

    public void setPlace_owner_id(String place_owner_id) {
        this.place_owner_id = place_owner_id;
    }

    public String getPlace_owner_name() {
        return place_owner_name;
    }

    public void setPlace_owner_name(String place_owner_name) {
        this.place_owner_name = place_owner_name;
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

    public String getPlace_address() {
        return place_address;
    }

    public void setPlace_address(String place_address) {
        this.place_address = place_address;
    }

    public String getPlace_latitude() {
        return place_latitude;
    }

    public void setPlace_latitude(String place_latitude) {
        this.place_latitude = place_latitude;
    }

    public String getPlace_longitude() {
        return place_longitude;
    }

    public void setPlace_longitude(String place_longitude) {
        this.place_longitude = place_longitude;
    }

    public String getPlace_pay_day() {
        return place_pay_day;
    }

    public void setPlace_pay_day(String place_pay_day) {
        this.place_pay_day = place_pay_day;
    }

    public String getPlace_test_period() {
        return place_test_period;
    }

    public void setPlace_test_period(String place_test_period) {
        this.place_test_period = place_test_period;
    }

    public String getPlace_vacation_select() {
        return place_vacation_select;
    }

    public void setPlace_vacation_select(String place_vacation_select) {
        this.place_vacation_select = place_vacation_select;
    }

    public String getPlace_insurance() {
        return place_insurance;
    }

    public void setPlace_insurance(String place_insurance) {
        this.place_insurance = place_insurance;
    }

    public String getPlace_start_time() {
        return place_start_time;
    }

    public void setPlace_start_time(String place_start_time) {
        this.place_start_time = place_start_time;
    }

    public String getPlace_end_time() {
        return place_end_time;
    }

    public void setPlace_end_time(String place_end_time) {
        this.place_end_time = place_end_time;
    }

    public String getPlace_save_kind() {
        return place_save_kind;
    }

    public void setPlace_save_kind(String place_save_kind) {
        this.place_save_kind = place_save_kind;
    }

    public String getPlace_wifi_name() {
        return place_wifi_name;
    }

    public void setPlace_wifi_name(String place_wifi_name) {
        this.place_wifi_name = place_wifi_name;
    }

    public String getPlace_img_path() {
        return place_img_path;
    }

    public void setPlace_img_path(String place_img_path) {
        this.place_img_path = place_img_path;
    }

    public String getPlace_start_date() {
        return place_start_date;
    }

    public void setPlace_start_date(String place_start_date) {
        this.place_start_date = place_start_date;
    }

    public String getPlace_created_at() {
        return place_created_at;
    }

    public void setPlace_created_at(String place_created_at) {
        this.place_created_at = place_created_at;
    }

    public String getPlace_icnt() {
        return place_icnt;
    }

    public void setPlace_icnt(String place_icnt) {
        this.place_icnt = place_icnt;
    }

    public String getPlace_ocnt() {
        return place_ocnt;
    }

    public void setPlace_ocnt(String place_ocnt) {
        this.place_ocnt = place_ocnt;
    }

    public String getPlace_totalcnt() {
        return place_totalcnt;
    }

    public void setPlace_totalcnt(String place_totalcnt) {
        this.place_totalcnt = place_totalcnt;
    }

    private static PlaceCheckData instance = null;
    public static synchronized PlaceCheckData getInstance(){
        if(null == instance){
            instance = new PlaceCheckData();
        }
        return instance;
    }
}
