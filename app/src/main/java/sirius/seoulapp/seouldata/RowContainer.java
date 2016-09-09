package sirius.seoulapp.seouldata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by SIRIUS on 2016-09-07.
 */
public class RowContainer implements Serializable {

    private static HashMap<String, ArrayList<Row>> rowMap;

    public RowContainer(){
        rowMap = new HashMap<String, ArrayList<Row>>();
    }

    public static void setRowMap(String key, ArrayList<Row> value){
        rowMap.put(key, value);
    }

    public static HashMap<String, ArrayList<Row>>getRowMap(){ return rowMap; }

}
