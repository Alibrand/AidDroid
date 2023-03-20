package com.ksacp2022t3.aiddroid.models;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class CenterAccount extends Account{
    String services;
    int work_from;
    int work_to;
    GeoPoint location;

    public CenterAccount() {
    }


    public String getServices() {
        return services;
    }

    public void setServices(String services) {
        this.services = services;
    }

    public int getWork_from() {
        return work_from;
    }

    public void setWork_from(int work_from) {
        this.work_from = work_from;
    }

    public int getWork_to() {
        return work_to;
    }

    public void setWork_to(int work_to) {
        this.work_to = work_to;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }



}
