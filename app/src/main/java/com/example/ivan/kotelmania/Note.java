package com.example.ivan.kotelmania;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.Date;

//enum Status {sent, accepted};
public class Note {
   
    public int id;
    public String dbKey;
    public String heading;
    public String status;
    public String content;
    String date;


    public Note(int id, String dbKey, String heading, String content, String status, String date) {
        this.id      = id;
        this.heading = heading;
        this.content = content;
        this.status  = status;
        this.date    = date;
        this.dbKey = dbKey;
    }
    
    public static void createTable(SQLiteDatabase db){
        db.execSQL("CREATE TABLE Notes (id INTEGER PRIMARY KEY AUTOINCREMENT, heading TEXT, content TEXT, status TEXT, date TEXT)");
    }
    
    public void addToDB(SQLiteDatabase db){
        db.execSQL("INSERT INTO Notes (heading, content, status, date) VALUES('" + heading + "','" + content + "','" + status + "','" + date + "')");
    }

    public void updateDB(SQLiteDatabase db) {
        db.execSQL("UPDATE Notes SET heading='" + heading + "', content='" + content + "', status='" + status + "', date='" + status + "' WHERE id=" + id);
    }

    public void deleteFromDB(SQLiteDatabase db){
        db.execSQL("DELETE FROM Notes WHERE id=" + id);
    }
    
    public static ArrayList<Note> getAllNotes(SQLiteDatabase db){
        ArrayList<Note> notes = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT * FROM Notes", null);
        c.moveToFirst(); //IMPORTANT!!!!
        while(!c.isAfterLast()){
            notes.add(new Note(c.getInt(0), c.getString(1), c.getString(2), c.getString(3), c.getString(4), c.getString(5)));
            c.moveToNext();
        }
        c.close();
        return notes;
    }


    @Override
    public String toString(){
        return heading + ", " + content;
    }

    public void setId(int id) {
        this.id = id;
    }
    public void setHeading(String heading) {
        this.heading = heading;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }
    public String getHeading() {
        return heading;
    }
    public String getContent() {
        return content;
    }
    public String getStatus() {
        return status;
    }
}
