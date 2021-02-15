package com.TuDime.ui.Model;

public class Contact_Model
{
   public String Contact_Name;
    public  String Contact_Number;
    public   boolean QB_User=false;
    public  String QB_user_id;

    public Contact_Model(String contact_Name, String contact_Number, boolean QB_User, String QB_user_id) {
        Contact_Name = contact_Name;
        Contact_Number = contact_Number;
        this.QB_User = QB_User;
        this.QB_user_id = QB_user_id;
    }

    public Contact_Model() {
    }

    public String getContact_Name() {
        return Contact_Name;
    }

    public void setContact_Name(String contact_Name) {
        Contact_Name = contact_Name;
    }

    public String getContact_Number() {
        return Contact_Number;
    }

    public void setContact_Number(String contact_Number) {
        Contact_Number = contact_Number;
    }

    public boolean isQB_User() {
        return QB_User;
    }

    public void setQB_User(boolean QB_User) {
        this.QB_User = QB_User;
    }

    public String getQB_user_id() {
        return QB_user_id;
    }

    public void setQB_user_id(String QB_user_id) {
        this.QB_user_id = QB_user_id;
    }
}
