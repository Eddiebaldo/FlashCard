package com.four.team.flashcardapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ViewAnswerActivity extends AppCompatActivity {

    Intent start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_answer);

        TextView answer = findViewById(R.id.answerView);
        Button done = findViewById(R.id.done);

        start = getIntent();

        answer.setText(start.getStringExtra("answer"));

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
