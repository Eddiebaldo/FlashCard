package com.four.team.flashcardapp.studyobjects;

/*
 * This class will represent the flashcards of the app.
 * They will contain a question and an answer used for studying. 
 * They will be stored in objects of class Folder
 **/
public class Flashcard {
	
	private int number; //the number of the flashcard in the folder
	private String question; //the question being asked on the flashcard
	private String answer; // the answer to the question
	
	public Flashcard(int number) {
		this.number = number;
	}
	
	public Flashcard(int number, String question, String answer) {
		this.number = number;
		this.question = question;
		this.answer = answer;
	}
	
	public void setQuestion(String question) {//set the question
		this.question = question;
	}
	
	public void setAnswer(String answer) {//set the answer
		this.answer = answer;
	}
	
	public void setNum(int number) {
		this.number = number;
	}
	
	public String getQuestion() {//retrieve the question
		return question;
	}
	
	public String getAnswer() { //retrieve the answer
		return answer;
	}
	
	public int getNum() {
		return number;
	}
	
}
