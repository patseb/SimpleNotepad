package com.patrykkwiatkowski.simplenotepad;

import java.util.ArrayList;

import android.app.Application;

public class ApplicationController extends Application {
	private ArrayList<Note> notes;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		setNotes(NoteFileAdapter.getNotes());
	}

	public void setNotes(ArrayList<Note> notes) {
		this.notes = notes;
	}

	public ArrayList<Note> getNotes() {
		return notes;
	}
}
