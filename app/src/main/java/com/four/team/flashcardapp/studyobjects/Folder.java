package com.four.team.flashcardapp.studyobjects;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
public class Folder {

	private String subject; //name of the folder
	private ArrayList<Flashcard> cards;
	private LinkedList<Flashcard> quiz;//will be the random order of cards toi show like a quiz
	private Flashcard current;
	
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

	public Flashcard getCurrent(){
		return current;
	}

	public LinkedList<Flashcard> getQuiz() {
		return quiz;
	}

	public void setQuiz(){//sets the linked list quiz to the shuffled cards
		ArrayList<Flashcard> temp = shuffle();
		LinkedList<Flashcard> newQuiz = new LinkedList<>();
		for(int i = 0; i < temp.size(); i++){
			newQuiz.add(temp.get(i));
		}
		quiz = newQuiz;
	}

	private ArrayList shuffle(){ // shuffles the cards for the linked list
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
