package com.TuDime.ui.Model;

public class Call_model {
    String DB_CALL_RECIPIENTID,DB_CALL_RECIPIENTNAME,DB_CALL_TYPE,DB_CALL_COUNT,DB_CALL_START_TIME,DB_CALL_QBUSER,CALL_STATUS;

    public Call_model() {
    }

    public String getDB_CALL_RECIPIENTID() {
        return DB_CALL_RECIPIENTID;
    }

    public String getDB_CALL_QBUSER() {
        return DB_CALL_QBUSER;
    }

    public String getCall_status() {
        return CALL_STATUS;
    }

    public void setCall_status(String call_status) {
        CALL_STATUS = call_status;
    }

    public void setDB_CALL_QBUSER(String DB_CALL_QBUSER) {
        this.DB_CALL_QBUSER = DB_CALL_QBUSER;
    }

    public void setDB_CALL_RECIPIENTID(String DB_CALL_RECIPIENTID) {
        this.DB_CALL_RECIPIENTID = DB_CALL_RECIPIENTID;
    }

    public String getDB_CALL_RECIPIENTNAME() {
        return DB_CALL_RECIPIENTNAME;
    }

    public void setDB_CALL_RECIPIENTNAME(String DB_CALL_RECIPIENTNAME) {
        this.DB_CALL_RECIPIENTNAME = DB_CALL_RECIPIENTNAME;
    }

    public String getDB_CALL_TYPE() {
        return DB_CALL_TYPE;
    }

    public void setDB_CALL_TYPE(String DB_CALL_TYPE) {
        this.DB_CALL_TYPE = DB_CALL_TYPE;
    }

    public String getDB_CALL_COUNT() {
        return DB_CALL_COUNT;
    }

    public void setDB_CALL_COUNT(String DB_CALL_COUNT) {
        this.DB_CALL_COUNT = DB_CALL_COUNT;
    }

    public String getDB_CALL_START_TIME() {
        return DB_CALL_START_TIME;
    }

    public void setDB_CALL_START_TIME(String DB_CALL_START_TIME) {
        this.DB_CALL_START_TIME = DB_CALL_START_TIME;
    }
}
