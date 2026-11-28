package com.gincz.telegrambot.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class OfflineQueue extends SQLiteOpenHelper {
    private static final String DB_NAME = "offline_queue.db";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "pending_messages";

    private static final String COL_ID = "id";
    private static final String COL_MESSAGE = "message";
    private static final String COL_TIMESTAMP = "timestamp";

    private static OfflineQueue instance;

    public static class QueueItem {
        public final long id;
        public final String message;
        public final long timestamp;

        public QueueItem(long id, String message, long timestamp) {
            this.id = id;
            this.message = message;
            this.timestamp = timestamp;
        }
    }

    private OfflineQueue(Context context) {
        super(context.getApplicationContext(), DB_NAME, null, DB_VERSION);
    }

    public static synchronized OfflineQueue getInstance(Context context) {
        if (instance == null) {
            instance = new OfflineQueue(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_MESSAGE + " TEXT NOT NULL, " +
                COL_TIMESTAMP + " INTEGER NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public synchronized void enqueue(String message) {
        if (message == null || message.trim().isEmpty()) return;
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_MESSAGE, message);
        cv.put(COL_TIMESTAMP, System.currentTimeMillis());
        db.insert(TABLE_NAME, null, cv);
    }

    public synchronized List<QueueItem> peekAll(int limit) {
        List<QueueItem> items = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME,
                new String[]{COL_ID, COL_MESSAGE, COL_TIMESTAMP},
                null, null, null, null,
                COL_ID + " ASC",
                String.valueOf(limit));

        if (cursor != null) {
            while (cursor.moveToNext()) {
                long id = cursor.getLong(0);
                String msg = cursor.getString(1);
                long ts = cursor.getLong(2);
                items.add(new QueueItem(id, msg, ts));
            }
            cursor.close();
        }
        return items;
    }

    public synchronized void delete(long id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME, COL_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public synchronized int getCount() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME, null);
        int count = 0;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        }
        return count;
    }
}
