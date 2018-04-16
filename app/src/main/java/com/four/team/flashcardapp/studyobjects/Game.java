package com.four.team.flashcardapp.studyobjects;
import java.util.LinkedList;
import java.util.ArrayList;
public class Game {

    private Folder subject;//the folder being played from
    private int correct;//amount of questions answered correctly
    private Flashcard current;//the current flashcard
    private LinkedList<Flashcard> play;

    public Game(Folder subject){//constructor will create a game using a provided folder
        this.subject = subject;

    }



}
