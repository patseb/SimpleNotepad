package com.patrykkwiatkowski.simplenotepad;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.os.Environment;

public class NoteStorage {
	private static ArrayList<Note> notes;
	private static Thread preloader;

	static {
		preloader = new Thread(new Runnable() {
			@Override
			public void run() {
				getNotes();
			}
		});
		preloader.start();
	}

	private static class Operation extends Thread {
		private Type t;
		private Note n;
		private Runnable onfail;
		private static LinkedList<Operation> operations = new LinkedList<Operation>();

		public enum Type {
			SAVE, DELETE
		}

		public static void add(Type t, Note n, Runnable r) {
			synchronized (operations) {
				operations.add(new Operation(t, n, r));
				if (operations.getFirst().getState() == State.NEW) operations.getFirst().start();
			}
		}

		public Operation(Type t, Note n, Runnable onfail) {
			super();
			this.t = t;
			this.n = n;
			this.onfail = onfail;
		}

		@Override
		public void run() {
			switch (t) {
				case SAVE:
					if (!NoteStorage.saveNote(n)) onfail.run();
					break;
				case DELETE:
					if (!NoteStorage.deleteNote(n)) onfail.run();
					break;
			}
			synchronized (operations) {
				operations.remove(this);
				if (operations.size() > 0) {
					Operation op = operations.getFirst();
					if (op.getState() == State.NEW) op.start();
				}
			}
		}
	}

	public static void save(Note note, Runnable r) {
		int idx = 0;
		if (getNotes().contains(note)) {
			idx = getNotes().indexOf(note);
			getNotes().remove(idx);
		}
		getNotes().add(idx, note);
		Operation.add(Operation.Type.SAVE, note, r);
	}

	public static void delete(Note note, Runnable r) {
		getNotes().remove(note);
		Operation.add(Operation.Type.DELETE, note, r);
	}

	public static ArrayList<Note> getNotes() {
		synchronized (preloader) {
			if (notes == null) notes = getNotesFromMedium();
		}
		return notes;
	}

	private static String getDateFormat() {
		return "yyyy.MM.dd.kk.mm.ss";
	}

	private static ArrayList<Note> getNotesFromMedium() {
		ArrayList<Note> list = new ArrayList<Note>();

		if (!isMounted()) {
			return null;
		}

		File notesDir = Environment.getExternalStoragePublicDirectory("Notes");

		if (!notesDir.exists()) {
			if (!notesDir.mkdir()) {
				return null;
			}
			return list;
		}

		try {
			String[] noteDirList = notesDir.list();
			Arrays.sort(noteDirList, Collections.reverseOrder());

			Pattern pattern = Pattern
					.compile("\\d{4}.(0[1-9]|1[012]).([012][0-9]|3[01]).(0[0-9]|1[0-9]|2[0-4]).([0-5][0-9]).([0-5][0-9])");

			for (int i = 0; i < noteDirList.length; ++i) {
				File noteDir = new File(notesDir.getPath() + "/" + noteDirList[i]);

				Matcher matcher = pattern.matcher(noteDir.getName());
				if (matcher.find()) {
					File noteText = new File(noteDir.getAbsolutePath() + "/text.txt");
					FileInputStream fileIS = new FileInputStream(noteText);
					BufferedReader buf = new BufferedReader(new InputStreamReader(fileIS));

					String line = new String();
					String content = new String();

					while ((line = buf.readLine()) != null) {
						content += line + "\n";
					}
					if (content.length() > 0) content = content.substring(0, content.length() - 1);

					fileIS.close();
					buf.close();

					Date creationDate = new SimpleDateFormat(getDateFormat()).parse(noteDir
							.getName());

					list.add(new Note(content, creationDate));
				}
			}
		}
		catch (Exception c) {
			return null;
		}
		return list;
	}

	private static boolean saveNote(Note note) {
		if (!isMounted()) {
			return false;
		}

		File notesDir = Environment.getExternalStoragePublicDirectory("Notes");

		if (!notesDir.exists()) {
			if (!notesDir.mkdir()) {
				return false;
			}
		}

		try {
			String noteDirName = new SimpleDateFormat(getDateFormat()).format(note
					.getCreationDate());

			File noteDir = new File(notesDir.getPath() + "/" + noteDirName);

			if (!noteDir.exists()) {
				if (!noteDir.mkdir()) {
					return false;
				}
			}

			File noteFile = new File(noteDir.getAbsolutePath() + "/text.txt");
			FileOutputStream fileOS = new FileOutputStream(noteFile);
			BufferedWriter buf = new BufferedWriter(new OutputStreamWriter(fileOS));

			buf.write(note.getTextContent());
			buf.flush();

			fileOS.close();
			buf.close();

		}
		catch (Exception c) {
			return false;
		}

		return true;
	}

	private static boolean deleteNote(Note note) {
		if (!isMounted()) {
			return false;
		}

		File notesDir = Environment.getExternalStoragePublicDirectory("Notes");
		String noteDirName = new SimpleDateFormat(getDateFormat()).format(note.getCreationDate());

		try {
			File noteDir = new File(notesDir + "/" + noteDirName);
			File[] noteFiles = noteDir.listFiles();
			for (int i = 0; i < noteFiles.length; ++i)
				if (!noteFiles[i].delete()) return false;

			if (!noteDir.delete()) return false;
		}
		catch (Exception ex) {
			return false;
		}
		return true;
	}

	private static boolean isMounted() {
		if (android.os.Environment.getExternalStorageState().compareTo(
				android.os.Environment.MEDIA_MOUNTED) == 1) {
			return false;
		}
		return true;
	}
}