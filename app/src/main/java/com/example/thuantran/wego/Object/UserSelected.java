package com.example.thuantran.wego.Object;




public class UserSelected  {

    private String id;
    private String name,gender,phone, date, time, cost, infoCar;
    private float review;
    private int nreview;
    private String avatar, photoCar;


    public UserSelected(String id, String name,String phone,String gender, String date, String time,
                        String cost, String infoCar, float review,int nreview, String avatar, String photoCar) {
        this.id = id;
        this.name  = name;
        this.phone = phone;
        this.gender = gender;
        this.date = date;
        this.time = time;
        this.cost = cost;


        this.infoCar = infoCar;
        this.review = review;
        this.nreview = nreview;
        this.avatar = avatar;
        this.photoCar = photoCar;
    }




    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getPhotoCar() {
        return photoCar;
    }

    public void setPhotoCar(String photoCar) {
        this.photoCar = photoCar;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getInfoCar() {
        return infoCar;
    }

    public void setInfoCar(String infoCar) {
        this.infoCar = infoCar;
    }

    public int getNreview() {
        return nreview;
    }

    public void setNreview(int nreview) {
        this.nreview = nreview;
    }

    public float getReview() {
        return review;
    }

    public void setReview(float review) {
        this.review = review;
    }


}
