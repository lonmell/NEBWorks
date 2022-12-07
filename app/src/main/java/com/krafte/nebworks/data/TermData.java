package com.krafte.nebworks.data;

public class TermData {
    public static class TermData_list {
        String id;
        String contract_id;
        String term;

        public TermData_list(String id, String contract_id, String term) {
            this.id = id;
            this.contract_id = contract_id;
            this.term = term;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getContract_id() {
            return contract_id;
        }

        public void setContract_id(String contract_id) {
            this.contract_id = contract_id;
        }

        public String getTerm() {
            return term;
        }

        public void setTerm(String term) {
            this.term = term;
        }
    }
}
