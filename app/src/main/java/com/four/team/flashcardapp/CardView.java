package com.four.team.flashcardapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.four.team.flashcardapp.adapters.CardAdapter;
import com.four.team.flashcardapp.room.database.AppDatabase;
import com.four.team.flashcardapp.room.domain.Card;

import java.util.ArrayList;
import java.util.List;

public class CardView extends AppCompatActivity {

    AppDatabase db;
    CardAdapter cardAdapter = new CardAdapter(new ArrayList<Card>());
    long folderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_view);

        RecyclerView cardList = (RecyclerView) findViewById(R.id.cardView);
        FloatingActionButton addCard = findViewById(R.id.addCard);//button for addin cards
        Button delete = findViewById(R.id.deleteFolder);//button for deleting folder

        folderId = getIntent().getLongExtra("folderID", 0);
        Log.d("Intent", "onCreate: folder ID is " + folderId);

        db = AppDatabase.getDatabaseInstance(this);

        retrieveData();

        //populate the list of cards
        cardList.setAdapter(cardAdapter);
        cardList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        cardAdapter.setOnItemClickListener(new CardAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(String answer) {//when a card is clicked (short)
                Intent intent = new Intent(CardView.this, ViewAnswerActivity.class);
                intent.putExtra("answer", answer);
                startActivity(intent);
            }
        });




        /*
         * This onCLickListener will send an intent to a new activity for adding a new card
         * */
        addCard.setOnClickListener(new View.OnClickListener() {//action taken when the addFolder button is clicked
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(CardView.this, AddCardActivity.class), 1);
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {//action taken when the delete button is clicked
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CardView.this, MainActivity.class);
                intent.putExtra("delete", true);
                intent.putExtra("folderID", folderId);
                startActivity(intent);
            }
        });


    }

    private void retrieveData(){
        DataAccess retrieve = new DataAccess(db, cardAdapter, folderId);
        retrieve.execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Card newCard = new Card();
        newCard.setQuestion(data.getStringExtra("question"));
        newCard.setAnswer(data.getStringExtra("answer"));
        newCard.setFolderID(folderId);
        DataEnter enter = new DataEnter(db, cardAdapter, false);
        enter.execute(newCard);
    }

    static class DataAccess extends AsyncTask<Void, Void, List<Card>>{ //for retrieving data from database

        private AppDatabase db;
        private CardAdapter cardAdapter;
        private long folderId;

        public DataAccess(AppDatabase db, CardAdapter cardAdapter, long folderId){
            this.db = db;
            this.cardAdapter = cardAdapter;
            this.folderId = folderId;
        }
        @Override
        protected List<Card> doInBackground(Void... voids) {
            List<Card> cards = new ArrayList<Card>();
            cards = db.cardDao().findCardsByFolder(folderId);

            return cards;
        }

        @Override
        protected void onPostExecute(List<Card> cards) {
            cardAdapter.setmDataset(new ArrayList<Card>(cards));
            super.onPostExecute(cards);
        }
    }

    /*
     * This class is for entering data into the database
     * */
    static class DataEnter extends AsyncTask<Card, Void, Card> { //for entering data into database

        private AppDatabase db;
        private CardAdapter cardAdapter;
        private boolean delete;

        public DataEnter(AppDatabase db, CardAdapter cardAdapter, boolean delete){
            this.db = db;
            this.cardAdapter = cardAdapter;
            this.delete = delete;
        }
        @Override
        protected Card doInBackground(Card... cards) {
            // List<Folder> folders = new ArrayList<Folder>();
            if(delete && cards.length > 0){
                cardAdapter.removeFolder(cards[0]);
                db.cardDao().delete(cards[0]);
            }
            else if(cards.length > 0) {
                long id =  db.cardDao().insertOne(cards[0]);
                cards[0].setId(id);
                return cards[0];
            }
            return null;
        }

        @Override
        protected void onPostExecute(Card aVoid) {
            if(aVoid != null && !delete){
                cardAdapter.addCard(aVoid);
            }
            super.onPostExecute(aVoid);
        }
    }
}
