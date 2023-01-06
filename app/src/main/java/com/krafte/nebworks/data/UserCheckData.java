package com.krafte.nebworks.data;

public class UserCheckData {
    private static String user_id = "0";
    private static String user_password = "0";
    private static String place_name = "0";
    private static String user_account = "0";
    private static String user_name = "0";
    private static String user_nick_name = "0";
    private static String user_phone = "0";
    private static String user_gender = "0";
    private static String user_img_path = "0";
    private static String user_jumin = "0";
    private static String user_kind = "0";
    private static String user_join_date = "0";
    private static String user_state = "0";
    private static String user_jikgup = "0";
    private static String user_pay = "0";
    private static String user_worktime = "0";
    private static String user_inoutstate = "0";
    private static String user_sieob = "0";
    private static String user_jongeob = "0";
    private static String user_platform = "0";
    private static String user_contract_cnt = "0";
    private static String user_auth = "";

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_password() {
        return user_password;
    }

    public void setUser_password(String user_password) {
        this.user_password = user_password;
    }

    public String getPlace_name() {
        return place_name;
    }

    public void setPlace_name(String place_name) {
        this.place_name = place_name;
    }

    public String getUser_account() {
        return user_account;
    }

    public void setUser_account(String user_account) {
        this.user_account = user_account;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_nick_name() {
        return user_nick_name;
    }

    public void setUser_nick_name(String user_nick_name) {
        this.user_nick_name = user_nick_name;
    }

    public String getUser_phone() {
        return user_phone;
    }

    public void setUser_phone(String user_phone) {
        this.user_phone = user_phone;
    }

    public String getUser_gender() {
        return user_gender;
    }

    public void setUser_gender(String user_gender) {
        this.user_gender = user_gender;
    }

    public String getUser_img_path() {
        return user_img_path;
    }

    public void setUser_img_path(String user_img_path) {
        this.user_img_path = user_img_path;
    }

    public String getUser_jumin() {
        return user_jumin;
    }

    public void setUser_jumin(String user_jumin) {
        this.user_jumin = user_jumin;
    }

    public String getUser_kind() {
        return user_kind;
    }

    public void setUser_kind(String user_kind) {
        this.user_kind = user_kind;
    }

    public String getUser_join_date() {
        return user_join_date;
    }

    public void setUser_join_date(String user_join_date) {
        this.user_join_date = user_join_date;
    }

    public String getUser_state() {
        return user_state;
    }

    public void setUser_state(String user_state) {
        this.user_state = user_state;
    }

    public String getUser_jikgup() {
        return user_jikgup;
    }

    public void setUser_jikgup(String user_jikgup) {
        this.user_jikgup = user_jikgup;
    }

    public String getUser_pay() {
        return user_pay;
    }

    public void setUser_pay(String user_pay) {
        this.user_pay = user_pay;
    }

    public String getUser_worktime() {
        return user_worktime;
    }

    public void setUser_worktime(String user_worktime) {
        this.user_worktime = user_worktime;
    }

    public String getUser_inoutstate() {
        return user_inoutstate;
    }

    public void setUser_inoutstate(String user_inoutstate) {
        this.user_inoutstate = user_inoutstate;
    }

    public String getUser_sieob() {
        return user_sieob;
    }

    public void setUser_sieob(String user_sieob) {
        this.user_sieob = user_sieob;
    }

    public String getUser_jongeob() {
        return user_jongeob;
    }

    public void setUser_jongeob(String user_jongeob) {
        this.user_jongeob = user_jongeob;
    }

    public String getUser_platform() {
        return user_platform;
    }

    public void setUser_platform(String user_platform) {
        this.user_platform = user_platform;
    }

    public String getUser_contract_cnt() {
        return user_contract_cnt;
    }

    public void setUser_contract_cnt(String user_contract_cnt) {
        this.user_contract_cnt = user_contract_cnt;
    }

    public String getUser_auth() {
        return user_auth;
    }

    public void setUser_auth(String user_auth) {
        this.user_auth = user_auth;
    }

    private static UserCheckData instance = null;
    public static synchronized UserCheckData getInstance(){
        if(null == instance){
            instance = new UserCheckData();
        }
        return instance;
    }
}
