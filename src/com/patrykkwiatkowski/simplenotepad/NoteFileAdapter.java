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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.os.Environment;

public class NoteFileAdapter {
	private static String dateFormat = "yyyy.MM.dd.kk.mm.ss";

	public static ArrayList<Note> getNotes() {
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
					if (content.length() > 0)
						content = content.substring(0, content.length()-1);

					fileIS.close();
					buf.close();

					Date creationDate = new SimpleDateFormat(dateFormat).parse(noteDir.getName());

					list.add(new Note(content, creationDate));
				}
			}
		}
		catch (Exception c) {
			return null;
		}
		return list;
	}

	public static boolean saveNote(Note note) {
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
			String noteDirName = new SimpleDateFormat(dateFormat).format(note.getCreationDate());

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

	public static boolean deleteNote(Note note) {
		if (!isMounted()) {
			return false;
		}

		File notesDir = Environment.getExternalStoragePublicDirectory("Notes");
		String noteDirName = new SimpleDateFormat(dateFormat).format(note.getCreationDate());

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
