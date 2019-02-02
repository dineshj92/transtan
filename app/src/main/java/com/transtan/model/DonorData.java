package com.transtan.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by djayakum on 3/25/2018.
 */

public class DonorData {

    @SerializedName("id")
    public String id;
    @SerializedName("appid")
    public String appid;
    @SerializedName("name")
    public String name;
    @SerializedName("address")
    public String address;
    @SerializedName("dob")
    public String dob;
    @SerializedName("sex")
    public String sex;
    @SerializedName("blood_group")
    public String blood_group;

    @SerializedName("organs")
    public String organs;
    @SerializedName("organs2")
    public String organs2;
    @SerializedName("organs3")
    public String organs3;
    @SerializedName("organs4")
    public String organs4;
    @SerializedName("organs5")
    public String organs5;
    @SerializedName("organs6")
    public String organs6;
    @SerializedName("organs7")
    public String organs7;
    @SerializedName("organs8")
    public String organs8;
    @SerializedName("organs9")
    public String organs9;
    @SerializedName("organs10")
    public String organs10;

    @SerializedName("id_no")
    public String id_no;
    @SerializedName("email")
    public String email;
    @SerializedName("mobile_no")
    public String mobile_no;

    @SerializedName("emg_name1")
    public String emg_name1;
    @SerializedName("emg_mobile1")
    public String emg_mobile1;
    @SerializedName("emg_address1")
    public String emg_address1;

    @SerializedName("emg_name2")
    public String emg_name2;
    @SerializedName("emg_mobile2")
    public String emg_mobile2;
    @SerializedName("emg_address2")
    public String emg_address2;

    @SerializedName("photo")
    public String photo;
    @SerializedName("district")
    public String district;
    @SerializedName("upload_date")
    public String upload_date;




    public DonorData(String id, String appid, String name, String address, String dob,String sex,String blood_group,String organs,
                     String organs2,String organs3,String organs4,String organs5,String organs6,String organs7,String organs8,
                     String organs9,String organs10,String id_no, String email,String mobile_no, String emg_name1,String emg_mobile1,
                     String emg_address1,String emg_name2,String emg_mobile2,String emg_address2, String photo, String district, String upload_date) {
        this.id = id;
        this.appid = appid;
        this.name = name;
        this.address = address;
        this.dob = dob;
        this.sex = sex;
        this.blood_group = blood_group;
        this.organs = organs;
        this.organs2 = organs2;
        this.organs3 = organs3;
        this.organs4 = organs4;
        this.organs5 = organs5;
        this.organs6 = organs6;
        this.organs7 = organs7;
        this.organs8 = organs8;
        this.organs9 = organs9;
        this.organs10 = organs10;
        this.id_no = id_no;
        this.email = email;
        this.mobile_no = mobile_no;
        this.emg_name1 = emg_name1;
        this.emg_mobile1 = emg_mobile1;
        this.emg_address1 = emg_address1;
        this.emg_name2 = emg_name2;
        this.emg_mobile2 = emg_mobile2;
        this.emg_address2 = emg_address2;
        this.photo = photo;
        this.district = district;
        this.upload_date = upload_date;
    }
}
