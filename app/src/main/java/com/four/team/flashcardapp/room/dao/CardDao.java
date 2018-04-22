package com.four.team.flashcardapp.room.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.RoomWarnings;
import android.arch.persistence.room.Update;

import com.four.team.flashcardapp.room.domain.Card;

import java.util.List;

@Dao
public interface CardDao {

    @Insert
    void insertAll(Card... cards);

    @Insert
    long insertOne(Card card);


    @Update
    void updateAll(Card... cards);

    @Query("SELECT * FROM card")
    List<Card> getAll();

    @Query("SELECT * FROM card WHERE folderID = :name")
    List<Card> findCardsByFolder(long name);

    @Query("DELETE FROM card WHERE folderID = :name")
    void deleteFromTable(long name);

    @Query("SELECT * FROM card WHERE id =:name")
    Card getCard(long name);

    @Delete
    void delete(Card card);

    @Delete
    void delete(Card... cards);

}
