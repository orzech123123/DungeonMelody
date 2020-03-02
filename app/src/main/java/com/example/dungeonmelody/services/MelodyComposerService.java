package com.example.dungeonmelody.services;

import android.app.ActionBar;
import android.os.Build;
import android.widget.SeekBar;

import androidx.annotation.RequiresApi;

import com.example.dungeonmelody.data.TabPart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

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

    public void Start()
    {
    }

    public void SetMarker(int progress)
    {
        Optional<TabPart> recordTabPartOptional = _tabParts.stream().filter(s -> s.ProgressStart == null).findFirst();
        if(!recordTabPartOptional.isPresent())
        {
            return;
        }

        TabPart recordTabPart = recordTabPartOptional.get();

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
            _isRecording = false;
            _recordedTabPart = null;
        }
    }

    public SeekBar.OnSeekBarChangeListener GetClearTabProgressesOnProgresRewindBackListener()
    {
        return new SeekBar.OnSeekBarChangeListener(){

            private int _previousProgress = -1;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(progress < _previousProgress)
                {
                    _tabParts.stream()
                            .filter(t ->
                                    (t.ProgressStart != null && t.ProgressStart > progress) ||
                                    (t.ProgressEnd != null && t.ProgressEnd > progress && t.ProgressStart < progress)
                            )
                            .forEach(t -> t.ClearProgresses());

                    _recordedTabPart = null;
                    _isRecording = false;
                }

                _previousProgress = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        };
    }

    public SeekBar.OnSeekBarChangeListener GetFireActionOnRewindBackListener(Runnable action)
    {
        return new SeekBar.OnSeekBarChangeListener(){

            private int _previousProgress = -1;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(progress < _previousProgress)
                {
                    action.run();
                }

                _previousProgress = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        };
    }
}
