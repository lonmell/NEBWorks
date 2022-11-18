package com.krafte.nebworks.data;

public class PaymentData {
    private static final String TAG = "PaymentData2";

    public static class PaymentData_list {
        String store_no;
        String user_id;
        String user_name;
        String jikgup;
        String basic_pay;
        String second_pay;
        String overwork_pay;
        String meal_allowance_yn;
        String store_insurance_yn;
        String gongjeynpay;
        String total_payment;
        String meal_pay;
        String workday;
        String workhour;
        String total_workday;
        String payment;

        public PaymentData_list(String store_no, String user_id, String user_name, String jikgup, String basic_pay, String second_pay, String overwork_pay
                , String meal_allowance_yn, String store_insurance_yn, String gongjeynpay
                , String total_payment, String meal_pay, String workday, String workhour, String total_workday, String payment) {
            this.store_no = store_no;
            this.user_id = user_id;
            this.user_name = user_name;
            this.jikgup = jikgup;
            this.basic_pay = basic_pay;
            this.second_pay = second_pay;
            this.overwork_pay = overwork_pay;
            this.meal_allowance_yn = meal_allowance_yn;
            this.store_insurance_yn = store_insurance_yn;
            this.gongjeynpay = gongjeynpay;
            this.total_payment = total_payment;
            this.meal_pay = meal_pay;
            this.workday = workday;
            this.workhour = workhour;
            this.total_workday = total_workday;
            this.payment = payment;
        }

        public String getStore_no() {
            return store_no;
        }

        public void setStore_no(String store_no) {
            this.store_no = store_no;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getUser_name() {
            return user_name;
        }

        public void setUser_name(String user_name) {
            this.user_name = user_name;
        }

        public String getJikgup() {
            return jikgup;
        }

        public void setJikgup(String jikgup) {
            this.jikgup = jikgup;
        }

        public String getBasic_pay() {
            return basic_pay;
        }

        public void setBasic_pay(String basic_pay) {
            this.basic_pay = basic_pay;
        }

        public String getSecond_pay() {
            return second_pay;
        }

        public void setSecond_pay(String second_pay) {
            this.second_pay = second_pay;
        }

        public String getOverwork_pay() {
            return overwork_pay;
        }

        public void setOverwork_pay(String overwork_pay) {
            this.overwork_pay = overwork_pay;
        }

        public String getMeal_allowance_yn() {
            return meal_allowance_yn;
        }

        public void setMeal_allowance_yn(String meal_allowance_yn) {
            this.meal_allowance_yn = meal_allowance_yn;
        }

        public String getStore_insurance_yn() {
            return store_insurance_yn;
        }

        public void setStore_insurance_yn(String store_insurance_yn) {
            this.store_insurance_yn = store_insurance_yn;
        }

        public String getGongjeynpay() {
            return gongjeynpay;
        }

        public void setGongjeynpay(String gongjeynpay) {
            this.gongjeynpay = gongjeynpay;
        }

        public String getTotal_payment() {
            return total_payment;
        }

        public void setTotal_payment(String total_payment) {
            this.total_payment = total_payment;
        }

        public String getMeal_pay() {
            return meal_pay;
        }

        public void setMeal_pay(String meal_pay) {
            this.meal_pay = meal_pay;
        }

        public String getWorkday() {
            return workday;
        }

        public void setWorkday(String workday) {
            this.workday = workday;
        }

        public String getWorkhour() {
            return workhour;
        }

        public void setWorkhour(String workhour) {
            this.workhour = workhour;
        }

        public String getTotal_workday() {
            return total_workday;
        }

        public void setTotal_workday(String total_workday) {
            this.total_workday = total_workday;
        }

        public String getPayment() {
            return payment;
        }

        public void setPayment(String payment) {
            this.payment = payment;
        }
    }
}