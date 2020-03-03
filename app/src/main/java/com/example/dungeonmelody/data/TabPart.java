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

    public String ToJson(String videoUrl, String melodyId) {
        JSONObject jsonObject= new JSONObject();
        try {
            jsonObject.put("melodyId", melodyId);
            jsonObject.put("melodyUrl", videoUrl);
            jsonObject.put("tabs", Tabs);
            jsonObject.put("progressStart", ProgressStart);
            jsonObject.put("progressEnd", ProgressEnd);

            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "{}";
        }
    }
}
