package com.example.dungeonmelody.backgroundTasks;

import android.os.AsyncTask;
import android.widget.SeekBar;

import com.google.android.youtube.player.YouTubePlayer;

public class PlayerSeekBarRefreshTask extends AsyncTask {

    private final SeekBar _seekBar;
    private final YouTubePlayer _youTubePlayer;

    public PlayerSeekBarRefreshTask(SeekBar seekBar, YouTubePlayer youTubePlayer)
    {
        this._seekBar = seekBar;
        this._youTubePlayer = youTubePlayer;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        while (true)
        {
            if(isCancelled())
            {
                return null;
            }

            if(_seekBar == null || _youTubePlayer == null)
            {
                continue;
            }

            _seekBar.setProgress(_youTubePlayer.getCurrentTimeMillis());

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
