package com.patrykkwiatkowski.simplenotepad;

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
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Main activity.
 * 
 * The user may manage the list of notes and create the new one.
 */
@SuppressWarnings("deprecation")
public class NoteList extends Activity {
	private ListView notesListView;
	private Note selectedNote;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notelist);

		selectedNote = null;

		if (NoteStorage.getNotes() == null) {
			Toast.makeText(this, R.string.err_read, Toast.LENGTH_LONG).show();
			finish();
			return;
		}

		notesListView = (ListView) findViewById(R.id.mainNotesListView);
		notesListView.setAdapter(new NoteListViewAdapter(this, NoteStorage.getNotes()));
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
											public void onClick(DialogInterface dialog, int which) {
												NoteStorage.delete(selectedNote,
														new Runnable() {
															@Override
															public void run() {
																NoteList.this
																		.runOnUiThread(new Runnable() {
																			@Override
																			public void run() {
																				Toast.makeText(
																						NoteList.this,
																						R.string.err_delete,
																						Toast.LENGTH_SHORT)
																						.show();
																			}
																		});
															}
														});
												((NoteListViewAdapter) notesListView.getAdapter())
														.notifyDataSetChanged();
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
						startActivity(new Intent(NoteList.this, NoteEditor.class).putExtra("note",
								selectedNote));
						return false;
					}
				});
	}

	@Override
	protected void onResume() {
		((NoteListViewAdapter) notesListView.getAdapter()).notifyDataSetChanged();
		super.onResume();
	}
}