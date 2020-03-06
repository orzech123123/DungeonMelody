package com.skycode.dungeonmelody.services;

import com.skycode.dungeonmelody.configuration.ApisConfig;
import com.skycode.dungeonmelody.data.TabPart;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MelodyComposerService {

    private final ArrayList<TabPart> _tabParts = new ArrayList<TabPart>();

    private boolean _isRecording;
    private TabPart _recordedTabPart;

    public MelodyComposerService(String tabs)
    {
        String[] tabsArray = tabs.split("\\[X\\]");
        for (String tabPart : tabsArray) {
            tabPart = tabPart.trim();
            _tabParts.add(new TabPart(tabPart));
        }
    }

    public ArrayList<TabPart> GetTabParts()
    {
        return _tabParts;
    }

    private TabPart GetNextUnstartedTabPart(){
        Optional<TabPart> tabPart = _tabParts.stream().filter(s -> s.ProgressStart == null).findFirst();
        if(tabPart.isPresent())
        {
            return tabPart.get();
        }
        return null;
    }

    public void SetMarker(int progress)
    {
        TabPart recordTabPart = GetNextUnstartedTabPart();
        if(recordTabPart == null)
        {
            return;
        }

        if(!_isRecording)
        {
            recordTabPart.ProgressStart = progress;
            _isRecording = true;
            _recordedTabPart = recordTabPart;
        }
        else
        {
            _recordedTabPart.ProgressEnd = progress;
            recordTabPart.ProgressStart = progress;
            _recordedTabPart = recordTabPart;
        }
    }

    public void SetBreak(int progress)
    {
        if(_isRecording)
        {
            _recordedTabPart.ProgressEnd = progress;
            StopRecording();
        }
    }

    private void StopRecording()
    {
        _isRecording = false;
        _recordedTabPart = null;
    }

    public void ClearTabProgressesAfterProgress(int progress)
    {
        _tabParts.stream()
                .filter(t ->
                        (t.ProgressStart != null && t.ProgressStart > progress) ||
                                (t.ProgressEnd != null && t.ProgressEnd > progress && t.ProgressStart < progress)
                )
                .forEach(t -> t.ClearProgresses());
    }

    public void HandleRewindBack(Integer progress) {
        if(_isRecording && progress < _recordedTabPart.ProgressStart)
        {
            _recordedTabPart.ClearProgresses();
            StopRecording();
        }
    }

    public void SaveMelody(String videoUrl) {
        String melodyId = UUID.randomUUID().toString();

        OkHttpClient client = new OkHttpClient();

        String title = GetVideoTitle(videoUrl);

        String json = "[";
        for (TabPart tabPart : _tabParts) {
            json += tabPart.ToJson(videoUrl, melodyId, title);
            if(_tabParts.indexOf(tabPart) != _tabParts.size() - 1)
            {
                json += ",";
            }
        }
        json += "]";

        Request request = new Request.Builder()
                .url("https://dungeonmelody-0441.restdb.io/rest/tabs")
                .header("x-apikey", ApisConfig.GetRestdbioApiKey())
                .header("Content-Type", "application/json")
                .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json))
                .build();

        try (Response response = client.newCall(request).execute()) {
            Integer a = 3;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //TODO move it somewhere
    private String GetVideoTitle(String videoUrl) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://www.youtube.com/oembed?format=json&url=https://www.youtube.com/watch?v=" + videoUrl)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            String json = response.body().string();
            JSONObject jsonObj = new JSONObject(json);
            return jsonObj.getString("title");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean AreAllTabPartFilled() {
        return _tabParts.stream().allMatch(t -> t.IsFilled());
    }

    public boolean HasAnyUnstartedTabPart() {
        return GetNextUnstartedTabPart() != null;
    }

    public boolean IsRecording() {
        return _isRecording;
    }
}
