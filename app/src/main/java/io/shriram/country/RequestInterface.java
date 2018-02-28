package io.shriram.country;


import java.util.List;

import io.reactivex.Observable;
import io.shriram.country.common.ResponseClass;
import retrofit2.Response;
import retrofit2.http.GET;

public interface RequestInterface {

    @GET("jsonparsetutorial.txt")
    Observable<ResponseClass> getData();
}