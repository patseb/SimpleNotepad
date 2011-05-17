package com.patrykkwiatkowski.simplenotepad;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Note editor activity.
 *
 * The user may edit or create text content for note.
 */
public class NoteEditor extends Activity {
	private EditText editText;
	private Button save;
	private Button cancel;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.editnote);

		Bundle request = this.getIntent().getExtras();

		editText = (EditText) findViewById(R.id.editNoteContentEditText);

		Note note;
		if (request.getInt("request") == Main.noteEditorRequest) {
			note = (Note) request.get("note");
			editText.setText(note.getTextContent());
		}
		else note = new Note();

		save = (Button) findViewById(R.id.editNoteSaveButton);
		save.setTag(note);
		save.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Note note = (Note) v.getTag();
				note.setTextContent(editText.getText().toString());

				Intent intent = new Intent(NoteEditor.this, Main.class);
				intent.putExtra("note", note);
				NoteEditor.this.setIntent(intent);

				if (!NoteFileAdapter.saveNote(note)) {
					Toast.makeText(NoteEditor.this, "lol?", Toast.LENGTH_LONG).show();
					setResult(Activity.RESULT_CANCELED);
				}
				else {
					setResult(Activity.RESULT_OK, intent);
				}

				finish();
			}
		});

		cancel = (Button) findViewById(R.id.editNoteCancelButton);
		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(Activity.RESULT_CANCELED);
				finish();
			}
		});

	}
}
