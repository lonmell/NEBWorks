package com.krafte.nebworks.data;

import java.util.List;

public class CalendarSetStatusData {
    public static class CalendarSetStatusData_list {
        String day;
        String week;
        List<String> users;

        public CalendarSetStatusData_list(String day, String week, List<String> users) {
            this.day = day;
            this.week = week;
            this.users = users;
        }

        public String getDay() {
            return day;
        }

        public void setDay(String day) {
            this.day = day;
        }

        public String getWeek() {
            return week;
        }

        public void setWeek(String week) {
            this.week = week;
        }

        public List<String> getUsers() {
            return users;
        }

        public void setUsers(List<String> users) {
            this.users = users;
        }
    }
}
