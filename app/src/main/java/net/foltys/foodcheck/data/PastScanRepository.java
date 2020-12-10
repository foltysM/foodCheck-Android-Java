package net.foltys.foodcheck.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

class PastScanRepository {

    private final PastScanDao mPastScanDao;
    private final LiveData<List<PastScan>> mAllScans;

    /**
     * Constructor of PastScanRepository class which is a Room repository for past scans database
     *
     * @param application Object of Application class, includes global application state
     */
    PastScanRepository(Application application) {
        PastScanDatabase db = PastScanDatabase.getDatabase(application);
        mPastScanDao = db.pastScanDao();
        mAllScans = mPastScanDao.loadAll();
    }

    /**
     * Method returns all records from past scans database
     *
     * @return LiveData of the list of all past scans
     */
    LiveData<List<PastScan>> getAllScans() {
        return mAllScans;
    }

    /**
     * Method inserts past scan to past scans database
     *
     * @param pastScan Scan that will be inserted to the database
     */
    void insertPast(PastScan pastScan) {
        PastScanDatabase.databaseWriteExecutor.execute(() -> mPastScanDao.insertScan(pastScan));
    }

    /**
     * Method deletes scan from the database
     *
     * @param pastScan Scan that will be deleted from the database
     */
    void deletePast(PastScan pastScan) {
        PastScanDatabase.databaseWriteExecutor.execute(() -> mPastScanDao.deletePast(pastScan));
    }
}
