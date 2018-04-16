package com.four.team.flashcardapp.studyobjects;

import java.util.ArrayList;
import java.util.Random;
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

	public ArrayList shuffle(){ //a shuffle method for the purpose of the game class
		ArrayList<Flashcard> mixed = cards;
		Random r = new Random();
		Flashcard temp;
		for(int i = 0; i <  cards.size(); i ++){//goes through each spot in the array list and sets to a new random spot
			temp = mixed.get(i);
			int j =  r.nextInt(mixed.size() - 1);
			mixed.set(i, mixed.get(j));
			mixed.set(j, temp);
		}
		return mixed;
	}
}
