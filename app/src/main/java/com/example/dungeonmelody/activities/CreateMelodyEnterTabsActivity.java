package com.example.dungeonmelody.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.dungeonmelody.R;
import com.example.dungeonmelody.data.CreateMelodyData;

public class CreateMelodyEnterTabsActivity extends AppCompatActivity {

    private EditText _tabsText;
    private Button _separatorButton;
    private Button _nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_melody_enter_tabs);

        _tabsText = findViewById(R.id.tabsText);
        _separatorButton = findViewById(R.id.separatorButton);
        _nextButton = findViewById(R.id.nextButton);

        _separatorButton.setOnClickListener(GetSeparatorButtonClickListener());
        _nextButton.setOnClickListener(GetNextButtonClickListener());
        _tabsText.addTextChangedListener(GetTabsChangedListener());

        _nextButton.setEnabled(false);
    }

    private TextWatcher GetTabsChangedListener() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String tabs = _tabsText.getText().toString();
                _nextButton.setEnabled(!TextUtils.isEmpty(tabs));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }

    private View.OnClickListener GetSeparatorButtonClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = _tabsText.getText().toString();
                int cursorPosition = _tabsText.getSelectionStart();
                text = text.substring(0, cursorPosition) + " X " + text.substring(cursorPosition);
                _tabsText.setText(text);
                _tabsText.setSelection(cursorPosition);
            }
        };
    }

    private View.OnClickListener GetNextButtonClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tabs = _tabsText.getText().toString();
                CreateMelodyData.TabsText = tabs;

                Intent intent = new Intent(CreateMelodyEnterTabsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        };
    }
}
