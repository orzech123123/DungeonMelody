package com.example.dungeonmelody.services;

import android.widget.SeekBar;

import com.example.dungeonmelody.data.TabPart;

import java.util.ArrayList;

public class MelodyComposerService {

    private final ArrayList<TabPart> _tabParts = new ArrayList<TabPart>();

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
    }

    public void SetBreak(int progress)
    {
    }

    public SeekBar.OnSeekBarChangeListener GetOnPlayerProgressChangeListener()
    {
        return new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

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
