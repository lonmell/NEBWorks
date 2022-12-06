package com.krafte.nebworks.data;

public class ContractData {
    public static class ContractData_list {
        String id;
        String place_id;
        String user_id;
        String name;
        String contract_yn;
        String img_path;
        String jumin;
        String kind;
        String join_date;
        String owner_sign_id;
        String worker_sign_id;

        public ContractData_list(String id, String place_id, String user_id,
                                 String name, String contract_yn, String img_path, String jumin,
                                 String kind, String join_date, String owner_sign_id, String worker_sign_id) {
            this.id = id;
            this.place_id = place_id;
            this.user_id = user_id;
            this.name = name;
            this.contract_yn = contract_yn;
            this.img_path = img_path;
            this.jumin = jumin;
            this.kind = kind;
            this.join_date = join_date;
            this.owner_sign_id = owner_sign_id;
            this.worker_sign_id = worker_sign_id;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPlace_id() {
            return place_id;
        }

        public void setPlace_id(String place_id) {
            this.place_id = place_id;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getContract_yn() {
            return contract_yn;
        }

        public void setContract_yn(String contract_yn) {
            this.contract_yn = contract_yn;
        }

        public String getImg_path() {
            return img_path;
        }

        public void setImg_path(String img_path) {
            this.img_path = img_path;
        }

        public String getJumin() {
            return jumin;
        }

        public void setJumin(String jumin) {
            this.jumin = jumin;
        }

        public String getKind() {
            return kind;
        }

        public void setKind(String kind) {
            this.kind = kind;
        }

        public String getJoin_date() {
            return join_date;
        }

        public void setJoin_date(String join_date) {
            this.join_date = join_date;
        }

        public String getOwner_sign_id() {
            return owner_sign_id;
        }

        public void setOwner_sign_id(String owner_sign_id) {
            this.owner_sign_id = owner_sign_id;
        }

        public String getWorker_sign_id() {
            return worker_sign_id;
        }

        public void setWorker_sign_id(String worker_sign_id) {
            this.worker_sign_id = worker_sign_id;
        }
    }
}
