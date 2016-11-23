package com.algonquincollege.shee0058.doorsopenottawa.parsers;

import android.util.Log;

import com.algonquincollege.shee0058.doorsopenottawa.model.Building;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by owen sheehan on 2016-11-08.
 */

public class BuildingJSONParser {
    public static List<String> dates = new ArrayList<>();
    public static List<Building> parseFeed(String content){

        try{
            JSONObject jsonResponse = new JSONObject(content);
            JSONArray buildingArray = jsonResponse.getJSONArray("buildings");
            List<Building> buildingList = new ArrayList<>();

            for (int i =0; i < buildingArray.length(); i++){

                JSONObject obj = buildingArray.getJSONObject(i);
//                JSONArray openHoursArray = obj.getJSONArray("open_hours");
                Building building = new Building();

//                for (int d=0; d < openHoursArray.length(); d++){
//                    dates.add(openHoursArray.getJSONObject(i).getString("date"));
//                }
                building.setBuildingId(obj.getInt("buildingId"));
                building.setName(obj.getString("name"));
                building.setAddress(obj.getString("address"));
                building.setImage(obj.getString("image"));
                building.setDescription(obj.getString("description"));
                //building.setOpen_hours(dates);

                buildingList.add(building);
            }

            return buildingList;

        }catch (JSONException e){
            Log.e("TAG","JsonParsing",e);
            return null;
        }
    }
}
