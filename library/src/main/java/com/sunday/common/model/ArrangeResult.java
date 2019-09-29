package com.sunday.common.model;

import java.util.List;

public class ArrangeResult {

    private List<ArrangeDay> results;

    public List<ArrangeDay> getResults() {
        return results;
    }

    public void setResults(List<ArrangeDay> results) {
            this.results = results;
    }


    public static class ArrangeDay{
        private String date;
        private int type;
        private List<ArrangeItem> times;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public List<ArrangeItem> getTimes() {
            return times;
        }

        public void setTimes(List<ArrangeItem> times) {
            this.times = times;
        }
    }

    public static class ArrangeItem{
        private String time;
        private int type;
        private int orderType;// 0电话  1视频  2未安排
        private int count;
        private int orderCount;
        private List<subArrangeItem> sunExpertTimes;

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getOrderType() {
            return orderType;
        }

        public void setOrderType(int orderType) {
            this.orderType = orderType;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public int getOrderCount() {
            return orderCount;
        }

        public void setOrderCount(int orderCount) {
            this.orderCount = orderCount;
        }

        public List<subArrangeItem> getSunExpertTimes() {
            return sunExpertTimes;
        }

        public void setSunExpertTimes(List<subArrangeItem> sunExpertTimes) {
            this.sunExpertTimes = sunExpertTimes;
        }

        public static class subArrangeItem{
            private int id;
            private String time;
            private int isSelect;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getTime() {
                return time;
            }

            public void setTime(String time) {
                this.time = time;
            }

            public int getIsSelect() {
                return isSelect;
            }

            public void setIsSelect(int isSelect) {
                this.isSelect = isSelect;
            }
        }
    }
}
