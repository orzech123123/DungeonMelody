package com.skycode.dungeonmelody.actions;

import android.widget.SeekBar;

import java.util.function.Consumer;

public class RunOnSeekProgressRewindBackAction implements SeekBar.OnSeekBarChangeListener {

    private final Consumer<Integer> _progressConsumer;
    private int _previousProgress = -1;

    public RunOnSeekProgressRewindBackAction(Consumer<Integer> progressConsumer)
    {
        _progressConsumer = progressConsumer;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (progress < _previousProgress) {
            _progressConsumer.accept(progress);
        }

        _previousProgress = progress;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}