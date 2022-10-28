package com.krafte.nebworks.data;

public class TokenData {
    public static class TokenData_list {
        private String user_id = null;
        private String token = null;
        private String channelId1 = null;
        private String channelId2 = null;
        private String channelId3 = null;
        private String channelId4 = null;

        public TokenData_list(String user_id, String token, String channelId1, String channelId2, String channelId3, String channelId4) {
            super();
            this.user_id = user_id;
            this.token = token;
            this.channelId1 = channelId1;
            this.channelId2 = channelId2;
            this.channelId3 = channelId3;
            this.channelId4 = channelId4;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getChannelId1() {
            return channelId1;
        }

        public void setChannelId1(String channelId1) {
            this.channelId1 = channelId1;
        }

        public String getChannelId2() {
            return channelId2;
        }

        public void setChannelId2(String channelId2) {
            this.channelId2 = channelId2;
        }

        public String getChannelId3() {
            return channelId3;
        }

        public void setChannelId3(String channelId3) {
            this.channelId3 = channelId3;
        }

        public String getChannelId4() {
            return channelId4;
        }

        public void setChannelId4(String channelId4) {
            this.channelId4 = channelId4;
        }
    }
}