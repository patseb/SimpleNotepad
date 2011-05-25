package com.patrykkwiatkowski.simplenotepad;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

public class Note implements Parcelable {
	private static String dateFormat = "yyyy-MM-dd kk:mm:ss";

	private Date creationDate;
	private String textContent;

	/*
	 * Integer instead of boolean because of Parcelable
	 * 0 - not collapsed
	 * 1 - collapsed
	 */
	private int collapsed;

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
		creationDate = new Date();
		textContent = "";
		collapsed = 1;
	}

	public Note(String text) {
		creationDate = new Date();
		textContent = text;
		collapsed = 1;
	}

	public Note(String text, Date date) {
		creationDate = date;
		textContent = text;
		collapsed = 1;
	}

	protected Note(Parcel in) {
		collapsed = in.readInt();

		String date_str = in.readString();
		try {
			creationDate = new SimpleDateFormat(dateFormat).parse(date_str);
		}
		catch (Exception e) {
		}

		textContent = in.readString();
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

	public void setCollapsed(int collapsed) {
		this.collapsed = collapsed;
	}

	public int getCollapsed() {
		return collapsed;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(collapsed);
		out.writeString(new SimpleDateFormat(dateFormat).format(creationDate));
		out.writeString(textContent);
	}

	@Override
	public boolean equals(Object o) {
		return (o instanceof Note) && ((Note) o).creationDate.equals(this.creationDate);
	}

	public static boolean equals(Note a, Note b) {
		return a != null && b != null && a.creationDate.equals(b.creationDate);
	}

}
