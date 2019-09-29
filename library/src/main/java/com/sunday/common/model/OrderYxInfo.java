package com.sunday.common.model;

public class OrderYxInfo {

    private YxInfo expert;
    private YxInfo member;

    public YxInfo getExpert() {
        return expert;
    }

    public void setExpert(YxInfo expert) {
        this.expert = expert;
    }

    public YxInfo getMember() {
        return member;
    }

    public void setMember(YxInfo member) {
        this.member = member;
    }

    public static class YxInfo{
        String accid;
        String token;

        public String getAccid() {
            return accid;
        }

        public void setAccid(String accid) {
            this.accid = accid;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }
}
