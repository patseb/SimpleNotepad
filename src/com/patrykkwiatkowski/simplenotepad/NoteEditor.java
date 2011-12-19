package com.patrykkwiatkowski.simplenotepad;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
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
	private Note note;

	private void save() {
		String text = editText.getText().toString();
		if (text.length() > 0) {
			note.setTextContent(text);
			if (!NoteStorage.INSTANCE.save(note)) {
				Toast.makeText(this, R.string.err_creation, Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.editnote);

		Bundle request = this.getIntent().getExtras();
		if (request != null && request.containsKey("note")) {
			note = (Note) request.get("note");
		}
		if (note == null) {
			note = new Note();
		}

		editText = (EditText) findViewById(R.id.editNoteContentEditText);
		editText.setText(note.getTextContent());
		save = (Button) findViewById(R.id.editNoteSaveButton);
		save.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				save();
				editText.setText("");
				note = new Note();
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			startActivity(new Intent(this, NoteList.class));
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onBackPressed() {
		save();
		super.onBackPressed();
	}
}
