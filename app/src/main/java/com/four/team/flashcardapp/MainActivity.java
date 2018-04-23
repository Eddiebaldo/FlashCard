package com.four.team.flashcardapp;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;

import com.four.team.flashcardapp.adapters.FolderAdapter;
import com.four.team.flashcardapp.room.database.AppDatabase;
import com.four.team.flashcardapp.room.domain.Folder;
import com.mapzen.speakerbox.Speakerbox;

import java.util.ArrayList;
import java.util.List;
/*
* @author Vincent Baldi
* */
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    AppDatabase db;//the database for the app
    FolderAdapter folderAdapter = new FolderAdapter(new ArrayList<Folder>());//adapter for connecting folder objects to the recyclert view
    Intent start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {//on create
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView folderList =  (RecyclerView) findViewById(R.id.viewFolders);//sets the recycler view
        FloatingActionButton addFolder = (FloatingActionButton) findViewById(R.id.addFolder);//button for adding folders


        /*
        * Taken from https://github.com/mapzen/speakerbox
        * */
        final Speakerbox speakerbox = new Speakerbox(getApplication());


        /*
        * This onCLickListener will send an intent to a new activity for adding a new folder
        * */
        addFolder.setOnClickListener(new View.OnClickListener() {//action taken when the addFolder button is clicked
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MainActivity.this, AddFolder.class), 0);
            }
        });

       // db = Room.databaseBuilder(this, AppDatabase.class, AppDatabase.dbName).build();//instanciates the database

        db = AppDatabase.getDatabaseInstance(this);

        start = getIntent();

        retrieveData();//retrieve the data from the DB

        deleteFolder();


        /*
        * This will populate the list of folders
        * */
        folderList.setAdapter(folderAdapter);
        folderList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        folderAdapter.setOnItemClickListener(new FolderAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(long folderId) {//this sets an on click listener for the items in the recycler view
                Intent intent = new Intent(MainActivity.this, CardView.class);
                intent.putExtra("folderID", folderId);
                startActivity(intent);
            }

            @Override
            public void OnItemLongClick(String folderName) {
                speakerbox.play(folderName);
            }
        });

    }//end on create

    @Override
    protected void onResume() {
        setTitle("Decks");
        super.onResume();
    }

    /*
    * This method will check if a folder is supposed to be deleted and remove from database if yes
    * */
    private void deleteFolder(){
        boolean delete = start.getBooleanExtra("delete", false);
        long folderId = start.getLongExtra("folderID", 0);
        if (delete){
            DataRemove deleteFolder = new DataRemove(db, folderAdapter, folderId);
            deleteFolder.execute();
            retrieveData();
        }
    }
    /*
    * this will take the information returned from the sent intent is received
    * it will enter it into the database and repopulate the list.
    * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {//when the add button retrurns the results
        Folder newFolder = new Folder();
        newFolder.setSubject(data.getStringExtra("result"));
        DataEnter enter = new DataEnter(db, folderAdapter, false);
        enter.execute(newFolder);
    }

    private void retrieveData(){
        DataAccess retrieve = new DataAccess(db, folderAdapter);
        retrieve.execute();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    static class DataAccess extends AsyncTask<Void, Void, List<Folder>>{ //for retrieving data from database

        private AppDatabase db;
        private FolderAdapter folderAdapter;

        public DataAccess(AppDatabase db, FolderAdapter folderAdapter){
            this.db = db;
            this.folderAdapter = folderAdapter;
        }
        @Override
        protected List<Folder> doInBackground(Void... voids) {
            List<Folder> folders = new ArrayList<Folder>();
            folders = db.folderDao().getAll();

            return folders;
        }

        @Override
        protected void onPostExecute(List<Folder> folders) {
            folderAdapter.setmDataset(new ArrayList<Folder>(folders));
            super.onPostExecute(folders);
        }
    }

    /*
    * This class is for entering data into the database
    * */
    static class DataEnter extends AsyncTask<Folder, Void, Folder> { //for entering data into database

        private AppDatabase db;
        private FolderAdapter folderAdapter;
        private boolean delete;

        public DataEnter(AppDatabase db, FolderAdapter folderAdapter, boolean delete){
            this.db = db;
            this.folderAdapter = folderAdapter;
            this.delete = delete;
        }
        @Override
        protected Folder doInBackground(Folder... subjects) {
           // List<Folder> folders = new ArrayList<Folder>();
            if(delete && subjects.length > 0){
                folderAdapter.removeFolder(subjects[0]);
                db.folderDao().delete(subjects[0]);
            }
            else if(subjects.length > 0) {
               long id =  db.folderDao().insertOne(subjects[0]);
               subjects[0].setId(id);
               return subjects[0];
            }
            return null;
        }

        @Override
        protected void onPostExecute(Folder aVoid) {
            if(aVoid != null && !delete){
                folderAdapter.addFolder(aVoid);
            }
            super.onPostExecute(aVoid);
        }
    }

    /*
     * This class is for entering data into the database
     * */
    static class DataRemove extends AsyncTask<Folder, Void, Folder> { //for entering data into database

        private AppDatabase db;
        private FolderAdapter folderAdapter;
        private long folderID;
        private Folder goodbye;

        public DataRemove(AppDatabase db, FolderAdapter folderAdapter, long folderID){
            this.db = db;
            this.folderAdapter = folderAdapter;
            this.folderID = folderID;

        }
        @Override
        protected Folder doInBackground(Folder... subjects) {
            // List<Folder> folders = new ArrayList<Folder>();
                if(folderID >= 0) {
                    goodbye = db.folderDao().getFolder(folderID);
                   //folderAdapter.removeFolder(goodbye);
                    db.folderDao().delete(goodbye);
                    db.cardDao().deleteFromTable(folderID);
                }
                return goodbye;
        }

        @Override
        protected void onPostExecute(Folder aVoid) {
            super.onPostExecute(aVoid);
            //folderAdapter.removeFolder(aVoid);

        }
    }
}
