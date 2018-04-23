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
                String q = question.getText().toString();
                String a = answer.getText().toString();
                if (q.isEmpty())
                    q = "empty question";
                if (a.isEmpty())
                    a= "empty answer";
                result.putExtra("question", q);
                result.putExtra("answer", a);
                setResult(1, result);
                finish();
            }
        });

    }

}
