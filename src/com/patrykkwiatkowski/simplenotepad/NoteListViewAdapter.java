package com.patrykkwiatkowski.simplenotepad;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class NoteListViewAdapter extends BaseAdapter {
	private ArrayList<Note> notes;
	private LayoutInflater layout;

	public NoteListViewAdapter(Context context, ArrayList<Note> notes) {
		super();
		this.notes = notes;
		this.layout = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void setItems(ArrayList<Note> notes) {
		this.notes = notes;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return notes.size();
	}

	@Override
	public Object getItem(int position) {
		return notes.get(position);
	}

	@Override
	public long getItemId(int position) {
		return notes.get(position).getCreationDate().hashCode();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;

		if (v == null) {
			v = layout.inflate(R.layout.listitem, null);
		}

		if (position >= 0 && position < notes.size()) {
			Note note = notes.get(position);
			TextView date = (TextView) v.findViewById(R.id.listItemCreationDateTextView);
			TextView time = (TextView) v.findViewById(R.id.listItemCreationTimeTextView);
			TextView content = (TextView) v.findViewById(R.id.listItemContentTextView);
			TextView collapse = (TextView) v.findViewById(R.id.listItemCollapseTextView);
			//Layout l = content.getLayout();

			date.setText(new SimpleDateFormat("yyyy.MM.dd").format(note.getCreationDate()));
			time.setText(new SimpleDateFormat("kk:mm:ss").format(note.getCreationDate()));

			content.setText(note.getTextContent());
			if (note.getCollapsed() == 1) {
				collapse.setVisibility(View.VISIBLE);
				content.setMaxLines(1);
			}
			else {
				collapse.setVisibility(View.INVISIBLE);
				content.setMaxLines(Integer.MAX_VALUE);
			}
		}

		return v;
	}

}
