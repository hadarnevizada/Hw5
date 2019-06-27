package com.example.ivan.kotelmania;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

public class MyCustomAdapter extends ArrayAdapter<Note> {
    private ArrayList<Note> list = new ArrayList<Note>();
    private Context context;
    private TextView heading;
    private TextView content;
    private Button deleteBtn;
    private Button editBtn;
    Context mContext;


    public MyCustomAdapter(ArrayList<Note> list, Context context) {
        super(context, R.layout.activity_list_item);
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Note getItem(int pos) {
        return list.get(pos);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(final int position,
                        View recycledView,
                        ViewGroup listView) {
        if (recycledView == null) {
            recycledView = LayoutInflater.from(getContext())
                    .inflate(R.layout.activity_list_item, null);
        }

        TextView heading = recycledView.findViewById(R.id._heading);
        TextView content = recycledView.findViewById(R.id._content);
        Button deleteBtn = (Button) recycledView.findViewById(R.id.delete_btn);
        Button editBtn = (Button) recycledView.findViewById(R.id.edit_btn);
        TextView status = recycledView.findViewById(R.id._status);

        final Note note = getItem(position);
        mContext = listView.getContext();
        heading.setText(note.heading);
        content.setText(note.content);

        if (twoDaysOverdue(note.date)) {
            status.setText("recieved");
            status.setTextColor(Color.parseColor("#aaaa00"));
            deleteBtn.setEnabled(false);
            editBtn.setEnabled(false);
            recycledView.findViewById(R.id.list_item).setBackgroundColor(Color.parseColor("#bbeeeeee"));
        }
        else status.setText("sent");

        editBtn.setTag(position);
        deleteBtn.setTag(position);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Note note = list.get(position);
//                note.deleteFromDB(MainActivity.getDbHelper().getWritableDatabase());
                String uid = FirebaseAuth.getInstance().getUid();
                String dbKey = note.dbKey;
                FirebaseDatabase.getInstance().getReference().child(uid).child("notes").child(dbKey).removeValue();
//                list.remove(position);
//                notifyDataSetChanged();
                Snackbar.make(v, "note has been removed!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), EditNoteActivity.class);
                intent.putExtra("id", note.id);
                intent.putExtra("heading", note.heading);
                intent.putExtra("content", note.content);
                ((Activity) mContext).startActivityForResult(intent, 1);
                notifyDataSetChanged();
            }
        });

        return recycledView;
    }

    private boolean twoDaysOverdue(String date) {
        String today = DateFormat.getDateInstance(DateFormat.SHORT).format(Calendar.getInstance().getTime());
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        Date _today;
        Date _noteDate;
        int dif;
        try {
            _today = format.parse(today);
            _noteDate = format.parse(date);
            dif = _today.compareTo(_noteDate);
            return Math.abs(dif) > 1; //two days overdue
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }


}