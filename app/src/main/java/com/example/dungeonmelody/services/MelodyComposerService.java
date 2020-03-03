package com.example.dungeonmelody.services;

import com.example.dungeonmelody.data.TabPart;

import java.util.ArrayList;
import java.util.Optional;

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

    public void SaveMelody() {

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
