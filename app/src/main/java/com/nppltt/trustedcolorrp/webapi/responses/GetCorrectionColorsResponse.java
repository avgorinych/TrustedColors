package com.nppltt.trustedcolorrp.webapi.responses;

import com.google.gson.annotations.SerializedName;

public class GetCorrectionColorsResponse {

    public String error;
    public rgb rgb;

    @SerializedName("image")
    public String imageCalibrated;

    @SerializedName("score")
    public int scoreCalibrated;

    @SerializedName("time")
    public int timeCalibrated;
}


