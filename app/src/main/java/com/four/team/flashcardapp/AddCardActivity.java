package com.four.team.flashcardapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddCardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);
        Button createCard = (Button) findViewById(R.id.createCard);
        final EditText question = findViewById(R.id.question);
        final EditText answer = findViewById(R.id.answer);

        createCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent result = new Intent();
                result.putExtra("question", question.getText().toString());
                result.putExtra("answer", answer.getText().toString());
                setResult(1, result);
                finish();
            }
        });

    }

}
