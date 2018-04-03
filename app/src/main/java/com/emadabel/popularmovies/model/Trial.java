package com.emadabel.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Trial implements Parcelable {

    public static final Parcelable.Creator<Trial> CREATOR
            = new Parcelable.Creator<Trial>() {
        public Trial createFromParcel(Parcel in) {
            return new Trial(in);
        }

        public Trial[] newArray(int size) {
            return new Trial[size];
        }
    };
    private String trialImage;
    private String trialUrl;

    public Trial(String trialImage, String trialUrl) {
        this.trialImage = trialImage;
        this.trialUrl = trialUrl;
    }

    public Trial() {

    }

    /**
     * recreate object from parcel
     */
    private Trial(Parcel in) {
        trialImage = in.readString();
        trialUrl = in.readString();
    }

    public String getTrialImage() {
        return trialImage;
    }

    public void setTrialImage(String trialImage) {
        this.trialImage = trialImage;
    }

    public String getTrialUrl() {
        return trialUrl;
    }

    public void setTrialUrl(String trialUrl) {
        this.trialUrl = trialUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(trialImage);
        parcel.writeString(trialUrl);
    }
}
