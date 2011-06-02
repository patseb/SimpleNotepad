package com.patrykkwiatkowski.simplenotepad;

import java.util.ArrayList;

import android.app.Application;

public class ApplicationController extends Application {
	private ArrayList<Note> notes;

	@Override
	public void onCreate() {
		super.onCreate();
		this.notes = NoteFileAdapter.getNotes();
	}

	public ArrayList<Note> getNotes() {
		return notes;
	}
}