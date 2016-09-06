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

    @SerializedName("NAME_KOR")
    @Expose
    private String NAME_KOR;
    public String getNAME_KOR(){ return NAME_KOR; }

    @SerializedName("WGS84_X")
    @Expose
    private String WGS84_X;
    public String getWGS84_X(){ return WGS84_X; }

    @SerializedName("WGS84_Y")
    @Expose
    private String WGS84_Y;
    public String getWGS84_Y(){ return WGS84_Y; }

    @SerializedName("LAW_SGG")
    @Expose
    private String LAW_SSG;
    public String getLAW_SSG(){ return LAW_SSG; }

    @SerializedName("LAW_HEMD")
    @Expose
    private String LAW_HEMD;
    public String getLLAW_HEMD(){ return LAW_HEMD; }

    public String toString(){ return MAIN_KEY + "," + NAME_KOR + "," + WGS84_X + "," + WGS84_Y; }

}