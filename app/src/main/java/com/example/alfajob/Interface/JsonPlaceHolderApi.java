package com.example.alfajob.Interface;

import retrofit2.Call;
import retrofit2.http.GET;

public interface JsonPlaceHolderApi {
    @GET("exec")
    Call<Void> getPost();




}