package net.foltys.foodcheck.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {PastScan.class, FavProd.class}, version = 1)

public abstract class PastScanDatabase extends RoomDatabase {
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS); // for running database operations asynchronously
    private volatile static PastScanDatabase INSTANCE; // Creating a singleton

    static PastScanDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (PastScanDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), PastScanDatabase.class, "scan_database")
                            .allowMainThreadQueries() //TODO moze jednak nie main thread
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract PastScanDao pastScanDao();

    public abstract FavProdDao favProdDao();
}
