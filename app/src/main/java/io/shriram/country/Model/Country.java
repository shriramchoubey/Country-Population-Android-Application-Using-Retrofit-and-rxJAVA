package io.shriram.country.Model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ram on 27/02/2018.
 */

public class Country {
private String rank,country,population,flag;

    public Country(String rank,String country, String population, String flag) {
        this.rank = rank;
        this.country = country;
        this.population = population;
        this.flag = flag;
    }

    public Country(){

    }

    public String getCountry() {
        return country;
    }

    public ArrayList<Country> jsonTOArraylist(String s){
        ArrayList<Country> arrayList=new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(s);
            for (int i=0;i<jsonArray.length();i++) {
                JSONObject obj = jsonArray.getJSONObject(i);

                // adding new element
                arrayList.add(new Country(obj.getString("rank"),obj.getString("country"),obj.getString("population"),obj.getString("flag")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return arrayList;
    }


    public void setCountry(String country) {
        this.country = country;
    }

    public String getPopulation() {
        return population;
    }

    public void setPopulation(String population) {
        this.population = population;
    }

    public String getFlag() {
        return flag;
    }

    public void setflag(String flag) {
        this.flag = flag;
    }
}

