package com.example.dungeonmelody.data;

import org.json.JSONException;
import org.json.JSONObject;

public class TabPart {

    public TabPart(String tabs)
    {
        Tabs = tabs;
    }

    public final String Tabs;
    public Integer ProgressStart = null;
    public Integer ProgressEnd = null;

    public void ClearProgresses(){
        ProgressStart = null;
        ProgressEnd = null;
    }

    public boolean IsFilled() {
        return ProgressStart != null && ProgressEnd != null;
    }

    public String ToJson(String videoUrl, String melodyId, String title) {
        JSONObject jsonObject= new JSONObject();
        try {
            jsonObject.put("melodyId", melodyId);
            jsonObject.put("melodyUrl", videoUrl);
            jsonObject.put("tabs", Tabs);
            jsonObject.put("progressStart", ProgressStart);
            jsonObject.put("progressEnd", ProgressEnd);
            jsonObject.put("title", title);

            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "{}";
        }
    }

    public static TabPart FromJson(JSONObject jsonObj)
    {
        try {
            TabPart tabPart = new TabPart(jsonObj.getString("tabs"));
            tabPart.ProgressStart = jsonObj.getInt("progressStart");
            tabPart.ProgressEnd = jsonObj.getInt("progressEnd");
            return tabPart;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
