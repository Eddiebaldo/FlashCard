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
                String f = folderName.getText().toString();
                if (f.isEmpty())
                    f = "empty folder name";
                result.putExtra("result", f);
                setResult(0, result);
                finish();
            }
        });
    }
}
