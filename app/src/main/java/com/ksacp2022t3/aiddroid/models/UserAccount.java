package com.ksacp2022t3.aiddroid.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class UserAccount extends Account {

    String age;
    String blood_group;
    String gender;
    String chronic_diseases;

    String instant_help_center;


    public UserAccount() {
    }

    public String getInstant_help_center() {
        return instant_help_center;
    }

    public void setInstant_help_center(String instant_help_center) {
        this.instant_help_center = instant_help_center;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getBlood_group() {
        return blood_group;
    }

    public void setBlood_group(String blood_group) {
        this.blood_group = blood_group;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getChronic_diseases() {
        return chronic_diseases;
    }

    public void setChronic_diseases(String chronic_diseases) {
        this.chronic_diseases = chronic_diseases;
    }



}
