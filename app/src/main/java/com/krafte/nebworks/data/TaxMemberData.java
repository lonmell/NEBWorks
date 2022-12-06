package com.krafte.nebworks.data;

public class TaxMemberData {
    private static final String TAG = "TaxMemberData";

    public static class TaxMemberData_list {
        String id;
        String place_id;
        String name;
        String img_path;
        String address;
        String contact_num;
        String kind;
        String created_at;
        String updated_at;

        public TaxMemberData_list(String id, String place_id, String name, String img_path,
                                  String address, String contact_num, String kind, String created_at, String updated_at) {
            this.id          = id;
            this.place_id    = place_id;
            this.name        = name;
            this.img_path    = img_path;
            this.address     = address;
            this.contact_num = contact_num;
            this.kind        = kind;
            this.created_at  = created_at;
            this.updated_at  = updated_at;
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

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getImg_path() {
            return img_path;
        }

        public void setImg_path(String img_path) {
            this.img_path = img_path;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getContact_num() {
            return contact_num;
        }

        public void setContact_num(String contact_num) {
            this.contact_num = contact_num;
        }

        public String getKind() {
            return kind;
        }

        public void setKind(String kind) {
            this.kind = kind;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(String updated_at) {
            this.updated_at = updated_at;
        }
    }
}
