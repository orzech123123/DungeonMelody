package com.example.dungeonmelody.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.dungeonmelody.R;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Button createMelodyButton = findViewById(R.id.createMelodyButton);
        createMelodyButton.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, CreateMelodyChooseVideoActivity.class);
            startActivity(intent);
        });
        Button melodyListButton = findViewById(R.id.melodyListButton);
        melodyListButton.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, MelodyListActivity.class);
            startActivity(intent);
        });
    }
}
