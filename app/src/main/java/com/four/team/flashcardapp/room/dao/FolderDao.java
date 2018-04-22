package com.four.team.flashcardapp.room.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.RoomWarnings;
import android.arch.persistence.room.Update;

import com.four.team.flashcardapp.room.domain.Folder;

import java.util.List;

@Dao
public interface FolderDao {

    @Query("SELECT * FROM folder")
    List<Folder> getAll();

    @Query("SELECT * FROM folder WHERE id = :name")
    Folder getFolder(long name);

    @Query("DELETE from folder WHERE id = :name")
    void deleteFromTable(long name);

    @Insert
    void insertAll(Folder... folders);

    @Insert
    long insertOne(Folder folder);

    @Update
    void updateAll(Folder... folders);

    @Delete
    void delete(Folder folder);

}
