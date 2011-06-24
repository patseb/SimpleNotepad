package com.patrykkwiatkowski.simplenotepad;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Main activity.
 * 
 * The user may manage the list of notes and create the new one.
 */
public class NoteList extends Activity {
	private Button createButton;
	private ListView notesListView;
	private ArrayList<Note> notes;
	private Note selectedNote;
	private ApplicationController AC;
	public static final int noteEditorRequest = 1;
	public static final int noteCreateReqeust = 2;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		AC = (ApplicationController)getApplicationContext();
		notes = AC.getNotes();

		selectedNote = null;

		if (notes == null) {
			Toast.makeText(this, R.string.err_read, Toast.LENGTH_LONG).show();
			notes = new ArrayList<Note>();
		}

		createButton = (Button) findViewById(R.id.mainCreateButton);
		createButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(NoteList.this, NoteEditor.class);
				intent.putExtra("request", noteCreateReqeust);
				startActivityForResult(intent, noteCreateReqeust);
			}
		});

		notesListView = (ListView) findViewById(R.id.mainNotesListView);
		notesListView.setAdapter(new NoteListViewAdapter(this, notes));
		registerForContextMenu(notesListView);
		notesListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				view.showContextMenu();
			}
		});
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		selectedNote = (Note) notesListView.getItemAtPosition(info.position);

		menu.add(R.string.contextmenu_copy).setOnMenuItemClickListener(
				new OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
						clipboardManager.setText(selectedNote.getTextContent());
						return true;
					}
				});
		
		menu.add(R.string.contextmenu_show).setOnMenuItemClickListener(
				new OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						Intent intent = new Intent(NoteList.this, NoteViewer.class);
						intent.putExtra("note", selectedNote);
						startActivity(intent);
						return true;
					}
				});

		menu.add(R.string.contextmenu_delete).setOnMenuItemClickListener(
				new OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						new AlertDialog.Builder(NoteList.this)
								.setTitle(R.string.dialog_title)
								.setMessage(R.string.dialog_question)
								.setNegativeButton(R.string.dialog_no, null)
								.setPositiveButton(R.string.dialog_yes,
										new Dialog.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog,
													int which) {

												if (!NoteFileAdapter.deleteNote(selectedNote)) {
													Toast.makeText(NoteList.this,
															R.string.err_delete,
															Toast.LENGTH_LONG).show();
													return;
												}
												notes.remove(selectedNote);
												((NoteListViewAdapter)notesListView.getAdapter()).notifyDataSetChanged();
											}
										})
								.show();
						return false;
					}
				});
	
		menu.add(R.string.contextmenu_edit).setOnMenuItemClickListener(
				new OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						Intent intent = new Intent(NoteList.this, NoteEditor.class);
						intent.putExtra("request", noteEditorRequest);
						intent.putExtra("note", selectedNote);
						startActivityForResult(intent, noteEditorRequest);
						return false;
					}
				});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
			case (noteCreateReqeust): {
				if (resultCode == Activity.RESULT_OK) {
					Note note = (Note) data.getParcelableExtra("note");
					notes.add(0, note);
					((NoteListViewAdapter)notesListView.getAdapter()).notifyDataSetChanged();
				}
			}
			break;

			case (noteEditorRequest): {
				if (resultCode == Activity.RESULT_OK) {
					Note note = data.getParcelableExtra("note");
					notes.get(notes.indexOf(note)).setTextContent(note.getTextContent());
					((NoteListViewAdapter)notesListView.getAdapter()).notifyDataSetChanged();
				}
			}
			break;
		}
	}
}