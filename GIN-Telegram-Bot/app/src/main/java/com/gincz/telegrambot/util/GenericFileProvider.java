package com.gincz.telegrambot.util;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;
import java.io.File;
import java.io.FileNotFoundException;

/**
 * Ultra-lightweight native ContentProvider for secure file sharing without AndroidX dependencies.
 * Complies with strict zero-dependency philosophy and Android 7.0+ (API 24+) FileUriExposed rules.
 */
public class GenericFileProvider extends ContentProvider {

    public static final String AUTHORITY_SUFFIX = ".fileprovider";

    public static Uri getUriForFile(Context context, File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Uri.parse("content://" + context.getPackageName() + AUTHORITY_SUFFIX + "/" + file.getName());
        } else {
            return Uri.fromFile(file);
        }
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    private static File getUpdateFile(Context context, String fileName) {
        File ext = context.getExternalCacheDir();
        if (ext != null) {
            File extFile = new File(new File(ext, "updates"), fileName);
            if (extFile.exists()) {
                return extFile;
            }
        }
        return new File(new File(context.getCacheDir(), "updates"), fileName);
    }

    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        Context context = getContext();
        if (context == null) {
            throw new FileNotFoundException("Context is null");
        }
        String fileName = uri.getLastPathSegment();
        if (fileName == null) {
            throw new FileNotFoundException("Invalid URI: " + uri);
        }

        File file = getUpdateFile(context, fileName);
        if (!file.exists()) {
            throw new FileNotFoundException("File not found: " + file.getAbsolutePath());
        }
        return ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
    }

    @Override
    public String getType(Uri uri) {
        return "application/vnd.android.package-archive";
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Context context = getContext();
        if (context == null) return null;
        String fileName = uri.getLastPathSegment();
        if (fileName == null) return null;

        File file = getUpdateFile(context, fileName);
        if (!file.exists()) return null;

        String[] cols = projection != null ? projection : new String[]{
                OpenableColumns.DISPLAY_NAME,
                OpenableColumns.SIZE
        };
        MatrixCursor cursor = new MatrixCursor(cols, 1);
        MatrixCursor.RowBuilder row = cursor.newRow();
        for (String col : cols) {
            if (OpenableColumns.DISPLAY_NAME.equals(col)) {
                row.add(file.getName());
            } else if (OpenableColumns.SIZE.equals(col)) {
                row.add(file.length());
            } else {
                row.add(null);
            }
        }
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
