package net.foltys.foodcheck.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

class PastScanRepository {

    private final PastScanDao mPastScanDao;
    private final LiveData<List<PastScan>> mAllScans;

    PastScanRepository(Application application) {
        PastScanDatabase db = PastScanDatabase.getDatabase(application);
        mPastScanDao = db.pastScanDao();
        mAllScans = mPastScanDao.loadAll();
    }

    LiveData<List<PastScan>> getAllScans() {
        return mAllScans;
    }

    void insertPast(PastScan pastScan) {
        PastScanDatabase.databaseWriteExecutor.execute(() -> mPastScanDao.insertScan(pastScan));
    }

    void deletePast(PastScan pastScan) {
        PastScanDatabase.databaseWriteExecutor.execute(() -> mPastScanDao.deletePast(pastScan));
    }
}
