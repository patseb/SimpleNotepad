package com.patrykkwiatkowski.simplenotepad;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

public class Note implements Parcelable {
	private static int nextId = 0;
	private static String dateFormat = "yyyy-MM-dd kk:mm:ss";

	private int id; // wonder if need, creationDate is also unique
	private Date creationDate;
	private String textContent;

	public static final Parcelable.Creator<Note> CREATOR = new Creator<Note>() {
		@Override
		public Note createFromParcel(Parcel source) {
			return new Note(source);
		}

		@Override
		public Note[] newArray(int size) {
			return new Note[size];
		}
	};

	public Note() {
		id = nextId++;
		creationDate = new Date();
		textContent = "";
	}

	public Note(String text) {
		id = nextId++;
		creationDate = new Date();
		textContent = text;
	}

	public Note(String text, Date date) {
		id = nextId++;
		creationDate = date;
		textContent = text;
	}

	protected Note(Parcel in) {
		id = in.readInt();
		String date_str = in.readString();
		try {
			creationDate = new SimpleDateFormat(dateFormat).parse(date_str);
		}
		catch (Exception e) {
		}

		textContent = in.readString();
	}

	public int getId() {
		return id;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public String getTextContent() {
		return textContent;
	}

	public void setTextContent(String textContent) {
		this.textContent = textContent;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(id);
		out.writeString(new SimpleDateFormat(dateFormat).format(creationDate));
		out.writeString(textContent);
	}

	@Override
	public boolean equals(Object o) {
		return (o instanceof Note) && ((Note) o).id == this.id;
	}

	public static boolean equals(Note a, Note b) {
		return a != null && b != null && a.getId() == b.getId();
	}

}
