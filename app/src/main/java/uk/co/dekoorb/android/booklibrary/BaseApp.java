package uk.co.dekoorb.android.booklibrary;

import android.app.Application;
import android.arch.persistence.room.Room;

import uk.co.dekoorb.android.booklibrary.db.AppDatabase;

/**
 * Created by ed on 04/01/18.
 */

public class BaseApp extends Application {
    private static final String DATABASE_NAME = "books_database";

    private AppDatabase mDb;

    public BaseApp() {
        super();
        createDb();
    }

    public AppDatabase getAppDb() {
        return this.mDb;
    }

    private void createDb() {
        mDb = Room.databaseBuilder(this,
                AppDatabase.class, DATABASE_NAME).build();
    }
}
