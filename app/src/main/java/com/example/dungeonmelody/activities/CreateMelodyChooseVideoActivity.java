package com.example.dungeonmelody.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.dungeonmelody.R;
import com.example.dungeonmelody.backgroundTasks.RunAsyncTask;
import com.example.dungeonmelody.configuration.ApisConfig;
import com.example.dungeonmelody.data.CreateMelodyData;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CreateMelodyChooseVideoActivity extends YouTubeBaseActivity {

    private YouTubePlayerView _youTubePlayerView;
    private YouTubePlayer _youTubePlayer;
    private EditText _videoUrlText;
    private Button _nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_melody_choose_video);

        _youTubePlayerView = findViewById(R.id.youtubePlayer);
        _videoUrlText = findViewById(R.id.videoUrlText);
        _nextButton = findViewById(R.id.separatorButton);

        _youTubePlayerView.initialize(ApisConfig.GetYoutubeApiKey(), GetPlayerOnInitListener());
        _videoUrlText.addTextChangedListener(GetVideoUrlChangeListener());

        _nextButton.setEnabled(false);
        _nextButton.setOnClickListener(GetNextButtonClickListener());
    }

    private View.OnClickListener GetNextButtonClickListener() {
        return v -> {
            String videoUrl = _videoUrlText.getText().toString();
            CreateMelodyData.VideoUrl = videoUrl;

            Intent intent = new Intent(CreateMelodyChooseVideoActivity.this, CreateMelodyEnterTabsActivity.class);
            startActivity(intent);
        };
    }

    private TextWatcher GetVideoUrlChangeListener(){
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(_youTubePlayer == null)
                {
                    return;
                }

                String videoUrl = _videoUrlText.getText().toString();
                _youTubePlayer.loadVideo(videoUrl);

                new RunAsyncTask(() -> TurnOffOnNextButtonIfVideoExist(), false).execute();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }

    private void TurnOffOnNextButtonIfVideoExist()
    {
        OkHttpClient client = new OkHttpClient();
        String url = _videoUrlText.getText().toString();
        Request request = new Request.Builder()
                .url("https://www.youtube.com/oembed?url=https://www.youtube.com/watch?v=" + url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            runOnUiThread(() -> _nextButton.setEnabled(response.code() == 200));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private YouTubePlayer.OnInitializedListener GetPlayerOnInitListener(){
        return new YouTubePlayer.OnInitializedListener(){
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                _youTubePlayer = youTubePlayer;
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
            }
        };
    }
}
