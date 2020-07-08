package com.patrykkwiatkowski.simplenotepad;

import java.text.SimpleDateFormat;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class NoteViewer extends Activity {
	private TextView contentTextView;
	private TextView dateTextView;
	private TextView timeTextView;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.viewnote);

		Bundle request = this.getIntent().getExtras();
		Note note = (Note) request.get("note");

		contentTextView = findViewById(R.id.viewNoteContentTextView);
		dateTextView = findViewById(R.id.viewNoteDateTextView);
		timeTextView = findViewById(R.id.viewNoteTimeTextView);

		contentTextView.setText(note.getTextContent());
		dateTextView.setText(new SimpleDateFormat("yyyy-MM-dd").format(note.getCreationDate()));
		timeTextView.setText(new SimpleDateFormat("kk:mm:ss").format(note.getCreationDate()));
	}
}