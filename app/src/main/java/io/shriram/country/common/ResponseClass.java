package io.shriram.country.common;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import io.shriram.country.Model.Country;

/**
 * Created by ram on 27/02/2018.
 */

public class ResponseClass {


    @SerializedName("worldpopulation")
    @Expose
    private List<Country> country ;


    public List<Country> getCountry() {
        return country;
    }

    public void setCountry(List<Country> students) {
        this.country = students;
    }
}
