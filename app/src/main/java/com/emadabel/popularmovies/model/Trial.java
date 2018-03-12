package com.emadabel.popularmovies.model;

/**
 * Created by lenovo on 12/03/2018.
 */

public class Trial {

    private String trialImage;
    private String trialUrl;

    public Trial(String trialImage, String trialUrl) {
        this.trialImage = trialImage;
        this.trialUrl = trialUrl;
    }

    public Trial() {

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
}
