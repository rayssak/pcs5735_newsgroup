package com.ime.newsgroup.util;

public class NewsgroupPostTO {
	
	private String message;
	private String category;
	
	public NewsgroupPostTO(String message, String category) {
		super();
		this.message = message;
		this.category = category;
	}
	
	public NewsgroupPostTO() {
	}

	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}

}