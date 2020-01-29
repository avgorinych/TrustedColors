package com.nppltt.trustedcolorrp.webapi;

import com.nppltt.trustedcolorrp.webapi.requests.GetCorrectionColorsRequest;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface Requests {
    @POST("/api/Image/GetCorrectionColors")
    Call<ResponseBody> GetCorrectionColors(@Body GetCorrectionColorsRequest getCalibratedImageRequest);
}
