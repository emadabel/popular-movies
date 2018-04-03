package com.emadabel.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.emadabel.popularmovies.data.FavoritesContract.FavoritesEntry;

public class FavoritesDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "favorites.db";
    private static final int DATABASE_VERSION = 1;

    public FavoritesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_FAVORITES_TABLE =

                "CREATE TABLE " + FavoritesEntry.TABLE_NAME + " (" +

                        FavoritesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        FavoritesEntry.COLUMN_MOVIE_ID + " INTEGER, " +

                        FavoritesEntry.COLUMN_POSTER + " TEXT, " +

                        FavoritesEntry.COLUMN_TITLE + " TEXT, " +

                        FavoritesEntry.COLUMN_ORIGINAL_TITLE + " TEXT, " +

                        FavoritesEntry.COLUMN_RELEASE_DATE + " TEXT, " +

                        FavoritesEntry.COLUMN_OVERVIEW + " TEXT, " +

                        FavoritesEntry.COLUMN_VOTE_AVERAGE + " TEXT, " +

                        FavoritesEntry.COLUMN_VOTE_COUNT + " TEXT, " +

                        " UNIQUE (" + FavoritesEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavoritesEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
