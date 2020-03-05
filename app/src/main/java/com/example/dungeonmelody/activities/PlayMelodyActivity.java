package com.example.dungeonmelody.activities;

import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.dungeonmelody.R;
import com.example.dungeonmelody.actions.RunOnSeekProgressRewindBackAction;
import com.example.dungeonmelody.actions.SetSeekBarMaxProgressValueFromPlayerAction;
import com.example.dungeonmelody.actions.UpdatePlayerProgressOnSeekBarChangeAction;
import com.example.dungeonmelody.backgroundTasks.RunAsyncTask;
import com.example.dungeonmelody.configuration.ApisConfig;
import com.example.dungeonmelody.data.PlayMelodyData;
import com.example.dungeonmelody.data.TabPart;
import com.example.dungeonmelody.utilities.MultipleOnSeekBarChangeListener;
import com.example.dungeonmelody.utilities.MultiplePlayerStateChangeListener;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PlayMelodyActivity extends YouTubeBaseActivity
{
    private YouTubePlayer _youTubePlayer;
    private YouTubePlayerView _youTubePlayerView;
    private SeekBar _seekBar;
    private RunAsyncTask _uiRefreshTask;

    private ArrayList<TabPart> _tabParts = new ArrayList<>();

    @Override
    protected void onDestroy() {
        _uiRefreshTask.cancel(true);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_melody);

        _youTubePlayerView = findViewById(R.id.youtubePlayer);
        _seekBar = findViewById(R.id.seekBar);

        ((TextView) findViewById(R.id.textView)).setMovementMethod(new ScrollingMovementMethod());

        _youTubePlayerView.initialize(ApisConfig.GetYoutubeApiKey(), GetPlayerOnInitListener());

        _seekBar.setEnabled(false);

        UpdateTabsOnView();
    }

    //TODO to bedzie zastapione innym rysowaniem/wyswietlaniem
    private void UpdateTabsOnView() {
        int currentTime = _youTubePlayer != null ? _youTubePlayer.getCurrentTimeMillis() : 0;

        String text = "";
        for (TabPart tabPart: _tabParts) {
            String tabPartText = tabPart.Tabs + "<br />";
            String color = "";
            if(currentTime > tabPart.ProgressStart && currentTime < tabPart.ProgressEnd)
            {
                color = String.format("#%06X", (0xFFFFFF & getResources().getColor(R.color.colorGreen)));
            }
            else if(currentTime > tabPart.ProgressStart)
            {
                color = String.format("#%06X", (0xFFFFFF & getResources().getColor(R.color.colorPrimaryDark)));
            }

            text = text + "<font color='"+color+"'>" + tabPartText + "</font>";
        }
        ((TextView) findViewById(R.id.textView)).setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);
    }

    private YouTubePlayer.OnInitializedListener GetPlayerOnInitListener(){
        return new YouTubePlayer.OnInitializedListener(){
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                if(_youTubePlayer != null)
                {
                    return;
                }

                _youTubePlayer = youTubePlayer;

                _youTubePlayer.setPlayerStateChangeListener(new MultiplePlayerStateChangeListener(Arrays.asList(
                        new SetSeekBarMaxProgressValueFromPlayerAction(_seekBar, _youTubePlayer)
                )));

                _seekBar.setOnSeekBarChangeListener(new MultipleOnSeekBarChangeListener(Arrays.asList(
                        new UpdatePlayerProgressOnSeekBarChangeAction(_youTubePlayer),
                        new RunOnSeekProgressRewindBackAction((progress) -> UpdateTabsOnView())
                )));

                _seekBar.setEnabled(true);

                new RunAsyncTask(() -> DownloadAndDisplayMelody(), false).execute();
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        };
    }

    private void DownloadAndDisplayMelody() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://dungeonmelody-0441.restdb.io/rest/tabs?q={\"melodyId\": \""+ PlayMelodyData.MelodyId + "\"}")
                .header("x-apikey", ApisConfig.GetRestdbioApiKey())
                .header("Content-Type", "application/json")
                .get()
                .build();

        String melodyUrl = null;

        ArrayList<TabPart> tabParts = new ArrayList<>();
        try (Response response = client.newCall(request).execute()) {
            String json = response.body().string();
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++)
            {
                melodyUrl = jsonArray.getJSONObject(i).getString("melodyUrl");
                tabParts.add(TabPart.FromJson(jsonArray.getJSONObject(i)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        _tabParts = tabParts;
        Collections.sort(tabParts, (t1, t2) -> t1.ProgressStart.compareTo(t2.ProgressStart));

        _youTubePlayer.cueVideo(melodyUrl);

        _uiRefreshTask = new RunAsyncTask(() -> {
            UpdateSeekBarProgress();
            UpdateTabsOnView();

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, true);
        _uiRefreshTask.execute();
    }

    private void UpdateSeekBarProgress(){
        if(_seekBar == null || _youTubePlayer == null)
        {
            return;
        }

        runOnUiThread(() -> {
            try{
                _seekBar.setProgress(_youTubePlayer.getCurrentTimeMillis());
            }
            catch (IllegalStateException e){}//youtube released
        });
    }
}
