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

import com.four.team.flashcardapp.adapters.FolderAdapter;
import com.four.team.flashcardapp.room.database.AppDatabase;
import com.four.team.flashcardapp.room.domain.Folder;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    AppDatabase db;
    FolderAdapter folderAdapter = new FolderAdapter(new ArrayList<Folder>());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView folderList =  (RecyclerView) findViewById(R.id.viewFolders);//sets the recycler view
        FloatingActionButton addFolder = (FloatingActionButton) findViewById(R.id.addFolder);

        addFolder.setOnClickListener(new View.OnClickListener() {//action taken when the addFolder button is clicked
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MainActivity.this, AddFolder.class), 0);
            }
        });

        db = Room.databaseBuilder(this, AppDatabase.class, AppDatabase.dbName).build();

        retrieveData();

        folderList.setAdapter(folderAdapter);
        folderList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

    }

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
}
