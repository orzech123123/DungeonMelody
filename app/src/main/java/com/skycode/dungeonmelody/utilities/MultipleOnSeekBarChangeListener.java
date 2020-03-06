package com.skycode.dungeonmelody.utilities;

import android.widget.SeekBar;

import java.util.List;

public class MultipleOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

    private final List<SeekBar.OnSeekBarChangeListener> _listeners;

    public MultipleOnSeekBarChangeListener(List<SeekBar.OnSeekBarChangeListener> listeners){

        _listeners = listeners;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        for (SeekBar.OnSeekBarChangeListener listener: _listeners) {
            listener.onProgressChanged(seekBar, progress, fromUser);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
