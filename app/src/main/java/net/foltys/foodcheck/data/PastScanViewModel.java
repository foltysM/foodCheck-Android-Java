package net.foltys.foodcheck.data;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class PastScanViewModel extends AndroidViewModel {
    private final PastScanRepository mPastRepository;

    private final LiveData<List<PastScan>> mAllPastScans;


    /**
     * Constructor of the PastScanViewModel class, part of Room. Its role is to provide data to the UI and survive configuration changes
     *
     * @param application Object of Application class, includes global application state
     */
    public PastScanViewModel(@NonNull Application application) {
        super(application);
        mPastRepository = new PastScanRepository(application);
        mAllPastScans = mPastRepository.getAllScans();
    }

    /**
     * Method returns all records from past scans database
     *
     * @return LiveData of the list of all past scans
     */
    public LiveData<List<PastScan>> getAllPastScans() {
        return mAllPastScans;
    }

    /**
     * Method inserts scan to past scans database
     *
     * @param scan Scan that will be inserted
     */
    public void insertPast(PastScan scan) {
        mPastRepository.insertPast(scan);
    }

    /**
     * Method deletes scan passed from the past scans database
     *
     * @param pastScan Scan that will be deleted from the database
     */
    public void deletePast(PastScan pastScan) {
        mPastRepository.deletePast(pastScan);
    }
}
