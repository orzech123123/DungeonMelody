package com.example.dungeonmelody.services;

import com.example.dungeonmelody.data.TabPart;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

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

        String json = "[";
        for (TabPart tabPart : _tabParts) {
            json += tabPart.ToJson(videoUrl, melodyId);
            if(_tabParts.indexOf(tabPart) != _tabParts.size() - 1)
            {
                json += ",";
            }
        }
        json += "]";

        Request request = new Request.Builder()
                .url("https://dungeonmelody-0441.restdb.io/rest/tabs")
                .header("x-apikey", "8733ef5f451ad34dbda6155cb2142c01bb423")
                .header("Content-Type", "application/json")
                .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json))
                .build();

        try (Response response = client.newCall(request).execute()) {
            Integer a = 3;
        } catch (IOException e) {
            e.printStackTrace();
        }
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
