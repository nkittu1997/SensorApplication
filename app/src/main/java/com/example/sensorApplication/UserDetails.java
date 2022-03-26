package com.example.sensorApplication;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity
public class UserDetails {
    @PrimaryKey (autoGenerate = true)
    public int id;
    public Date timestamp;
    public float heartRate;
    public float breathingRate;
    public float fever;
    public float cough;
    public float tiredness;
    public float shortnessOfBreath;
    public float muscleAches;
    public float chills;
    public float soreThroat;
    public float runnyNose;
    public float headache;
    public float chestPain;
    public double xcoord;
    public double ycoord;

    public UserDetails() {
        fever = 0;
        cough = 0;
        tiredness = 0;
        shortnessOfBreath = 0;
        muscleAches = 0;
        chills = 0;
        soreThroat = 0;
        runnyNose = 0;
        headache = 0;
        chestPain = 0;
        xcoord = 0;
        ycoord = 0;
    }

}
