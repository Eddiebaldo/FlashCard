package com.four.team.flashcardapp.room.database;

import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.Database;
import android.content.Context;

import com.four.team.flashcardapp.MainActivity;
import com.four.team.flashcardapp.room.dao.FolderDao;
import com.four.team.flashcardapp.room.domain.Folder;
import com.four.team.flashcardapp.room.domain.Card;
import com.four.team.flashcardapp.room.dao.CardDao;

/*
* @author Vincent Baldi
* */

@Database(entities = {Folder.class, Card.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public static final String dbName = "app_DB";
    private static AppDatabase db;

    public static AppDatabase getDatabaseInstance(Context context) {
        if (null == db) {
            db = Room.databaseBuilder(context, AppDatabase.class, AppDatabase.dbName).fallbackToDestructiveMigration().build();//instanciates the database {
            };
        return db;
    }

    public abstract FolderDao folderDao();
    public abstract CardDao cardDao();

}
