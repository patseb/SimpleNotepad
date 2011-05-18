package com.patrykkwiatkowski.simplenotepad;

import java.text.SimpleDateFormat;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class NoteViewer extends Activity {
	private TextView contentTextView;
	private TextView dateTextView;
	private TextView timeTextView;
	private Button closeButton;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.viewnote);

		Bundle request = this.getIntent().getExtras();
		Note note = (Note) request.get("note");

		contentTextView = (TextView) findViewById(R.id.viewNoteContentTextView);
		dateTextView = (TextView) findViewById(R.id.viewNoteDateTextView);
		timeTextView = (TextView) findViewById(R.id.viewNoteTimeTextView);

		contentTextView.setText(note.getTextContent());
		dateTextView.setText(new SimpleDateFormat("yyyy-MM-dd").format(note.getCreationDate()));
		timeTextView.setText(new SimpleDateFormat("kk:mm:ss").format(note.getCreationDate()));

		closeButton = (Button) findViewById(R.id.viewNoteCloseButton);
		closeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
}
