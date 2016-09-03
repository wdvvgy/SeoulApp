package sirius.seoulapp;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by SIRIUS on 2016-09-03.
 */
public class Row implements Serializable{
    @SerializedName("MAIN_KEY")
    @Expose
    private String MAIN_KEY;

    @SerializedName("NM_DP")
    @Expose
    private String NM_DP;
    public String getNM_DP(){ return NM_DP; }

    @SerializedName("WGS84_X")
    @Expose
    private String WGS84_X;
    public String getWGS84_X(){ return WGS84_X; }

    @SerializedName("WGS84_Y")
    @Expose
    private String WGS84_Y;
    public String getWGS84_Y(){ return WGS84_Y; }

    public String toString(){ return MAIN_KEY + "," + NM_DP + "," + WGS84_X + "," + WGS84_Y; }

}