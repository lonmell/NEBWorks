package com.krafte.nebworks.data;

public class SearchAddressDataDetail {

    public static class SearchAddressDataDetaillist {
        String roadAddrPart1 = "";//ex. 대구광역시 동구 동촌로 325
        String roadAddrPart2 = "";//ex. (신평동)
        String zipNo = "";//우편번호

        public SearchAddressDataDetaillist(String roadAddrPart1, String roadAddrPart2, String zipNo) {
            this.roadAddrPart1 = roadAddrPart1;
            this.roadAddrPart2 = roadAddrPart2;
            this.zipNo = zipNo;
        }

        public String getRoadAddrPart1() {
            return roadAddrPart1;
        }

        public void setRoadAddrPart1(String roadAddrPart1) {
            this.roadAddrPart1 = roadAddrPart1;
        }

        public String getRoadAddrPart2() {
            return roadAddrPart2;
        }

        public void setRoadAddrPart2(String roadAddrPart2) {
            this.roadAddrPart2 = roadAddrPart2;
        }

        public String getZipNo() {
            return zipNo;
        }

        public void setZipNo(String zipNo) {
            this.zipNo = zipNo;
        }
    }
}