package com.four.team.flashcardapp.room.database;

import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.Database;

import com.four.team.flashcardapp.room.dao.FolderDao;
import com.four.team.flashcardapp.room.domain.Folder;
import com.four.team.flashcardapp.room.domain.Card;
import com.four.team.flashcardapp.room.dao.CardDao;

/*
* @author Vincent Baldi
* */

@Database(entities = {Folder.class, Card.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public static final String dbName = "app_DB";

    public abstract FolderDao folderDao();
    public abstract CardDao cardDao();

}
