package sirius.seoulapp;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by SIRIUS on 2016-09-03.
 */
public class SebcTourStreetKor {

    @SerializedName("SebcTourStreetKor")
    @Expose
    private Sebc SebcTourStreetKor;
    public Sebc getSebcTourStreetKor(){ return SebcTourStreetKor; }

}