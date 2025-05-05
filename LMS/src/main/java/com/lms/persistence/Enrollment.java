package com.lms.persistence;

public class Enrollment {
 private String eId;
 private String sId;
 private String cId;

    public Enrollment() {
    }

    public Enrollment(String eId, String sId, String cId) {
        this.eId = eId;
        this.sId = sId;
        this.cId = cId;
    }

    public String geteId() {
        return eId;
    }

    public void seteId(String eId) {
        this.eId = eId;
    }

    public String getsId() {
        return sId;
    }

    public void setsId(String sId) {
        this.sId = sId;
    }

    public String getcId() {
        return cId;
    }

    public void setcId(String cId) {
        this.cId = cId;
    }
    @Override
    public String toString() {
        return "Enrollment{" +
                "eId='" + eId + '\'' +
                ", sId='" + sId + '\'' +
                ", cId='" + cId + '\'' +
                '}';
    }
}
