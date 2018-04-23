package com.four.team.flashcardapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddFolder extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_folder);
        Button createFolder = findViewById(R.id.createFolder);
        final EditText folderName = findViewById(R.id.folderName);

        createFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent result = new Intent();
                result.putExtra("result", folderName.getText().toString());
                setResult(0, result);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        setTitle("Add Deck");
        super.onResume();
    }
}
