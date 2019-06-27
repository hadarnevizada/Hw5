package com.example.ivan.kotelmania;

import android.content.Intent;
import android.icu.text.DateFormat;
import android.icu.util.Calendar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class AddNoteActivity extends AppCompatActivity {
    EditText heading;
    EditText content;
    int id = 0;
    DBHelper dbHelper;
    private static int RESULT_LOAD_IMAGE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        heading = findViewById(R.id.add_heading);
        content = findViewById(R.id.add_content);

        Button buttonLoadImage = (Button) findViewById(R.id.buttonLoadPicture);
        buttonLoadImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

    }


    public void addANote(View view) {
        String headingText = heading.getText().toString();
        String contentText = content.getText().toString();

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child(FirebaseAuth.getInstance().getUid()).child("notes");
        String key = dbRef.push().getKey();
        Note note = new Note(8, key, headingText, contentText, "read", DateFormat.getDateInstance(DateFormat.SHORT).format(Calendar.getInstance().getTime()));

        dbRef.child(key).setValue(note);

        Intent intent = new Intent();
        intent.putExtra("heading", headingText);
        intent.putExtra("content", contentText);
        intent.putExtra("dbKey", key);
        intent.putExtra("activity", "add");
        intent.putExtra("date", DateFormat.getDateInstance(DateFormat.SHORT).format(Calendar.getInstance().getTime()));
        setResult(RESULT_OK, intent);
        finish();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            ImageView imageView = (ImageView) findViewById(R.id.imgView);
            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));

        }


    }
}
