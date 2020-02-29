package com.example.dungeonmelody.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.dungeonmelody.R;

public class CreateMelodyEnterTabsActivity extends AppCompatActivity {

    private EditText _tabsText;
    private Button _nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_melody_enter_tabs);

        _tabsText = findViewById(R.id.tabsText);
        _nextButton = findViewById(R.id.nextButton);

        _nextButton.setOnClickListener(GetNextButtonClickListener());
    }

    private View.OnClickListener GetNextButtonClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = _tabsText.getText().toString();
                int cursorPosition = _tabsText.getSelectionStart();
                text = text.substring(0, cursorPosition) + " X " + text.substring(cursorPosition);
                _tabsText.setText(text);
            }
        };
    }
}
