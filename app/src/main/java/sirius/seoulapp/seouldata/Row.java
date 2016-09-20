package sirius.seoulapp.seouldata;

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

    @SerializedName("H_KOR_CITY")
    @Expose
    private String H_KOR_CITY;
    public String getH_KOR_CITY(){ return H_KOR_CITY; }

    @SerializedName("H_KOR_GU")
    @Expose
    private String H_KOR_GU;
    public String getH_KOR_GU(){ return H_KOR_GU; }

    @SerializedName("H_KOR_DONG")
    @Expose
    private String H_KOR_DONG;
    public String getH_KOR_DONG(){ return H_KOR_DONG; }

    public String toString(){ return MAIN_KEY + "," + NAME_KOR + "," + WGS84_X + "," + WGS84_Y; }

}