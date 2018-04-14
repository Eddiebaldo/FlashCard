package com.four.team.flashcardapp.studyobjects;

import java.util.ArrayList;
public class Folder {

	private String subject; //name of the folder
	private ArrayList<Flashcard> cards;
	
	Folder(String subject){//constructor which will set the name of the folder
		this.subject = subject;
	}
	
	//Methods:
	
	public boolean addCard(Flashcard card) {//adds a flashcard to the folder
		return cards.add(card);
	}
	
	public Flashcard getCard(int index) {//retrieve a card from the list
		return cards.get(index);
	}
	
	public void removeCard(int index) {//remove a flash card from the list
		cards.remove(index);
	} 
	
	public String getSubject() {//get the subject of the folder
		return subject;
	}
}
