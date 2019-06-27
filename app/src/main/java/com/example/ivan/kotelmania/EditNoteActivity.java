package com.example.ivan.kotelmania;

import android.content.Intent;
import android.icu.text.DateFormat;
import android.icu.util.Calendar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class EditNoteActivity extends AppCompatActivity {
    EditText heading;
    EditText content;
    int id = 0;
    DBHelper dbHelper;
    Note note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        heading = findViewById(R.id.edit_heading);
        content = findViewById(R.id.edit_content);

        String existing_heading = getIntent().getStringExtra("heading");
        String existing_content = getIntent().getStringExtra("content");

        heading.setText(existing_heading);
        content.setText(existing_content);

    }

    public void editNote(View view) {

        String headingText = heading.getText().toString();
        String contentText = content.getText().toString();

        Intent intent = new Intent();
        id = getIntent().getIntExtra("id", -1);

        intent.putExtra("id", id);
        intent.putExtra("heading", headingText);
        intent.putExtra("content", contentText);
        intent.putExtra("activity", "edit");
        intent.putExtra("date", DateFormat.getDateInstance(DateFormat.SHORT).format(Calendar.getInstance().getTime()));

        setResult(RESULT_OK, intent);
        finish();
    }
}
