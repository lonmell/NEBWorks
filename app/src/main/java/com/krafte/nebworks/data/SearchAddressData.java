package com.krafte.nebworks.data;

import java.util.List;

public class SearchAddressData {
    public static class SearchAddressData_list {
        List<String> juso;
        public SearchAddressData_list(List<String> juso) {
            this.juso = juso;
        }

        public List<String> getJuso() {
            return juso;
        }

        public void setJuso(List<String> juso) {
            this.juso = juso;
        }
    }
}

