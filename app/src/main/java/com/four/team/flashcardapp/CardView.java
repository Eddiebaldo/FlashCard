package com.four.team.flashcardapp;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.four.team.flashcardapp.adapters.CardAdapter;
import com.four.team.flashcardapp.room.database.AppDatabase;
import com.four.team.flashcardapp.room.domain.Card;

import java.util.ArrayList;

public class CardView extends AppCompatActivity {

    AppDatabase db;
    CardAdapter cardAdapter = new CardAdapter(new ArrayList<Card>());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_view);

        RecyclerView cardList = (RecyclerView) findViewById(R.id.cardView);
        FloatingActionButton addCard = findViewById(R.id.addCard);//button for addin cards



        cardList.setAdapter(cardAdapter);
        cardList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

    }
}
