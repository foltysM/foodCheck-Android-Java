package net.foltys.foodcheck.data;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class PastScanViewModel extends AndroidViewModel {
    private PastScanRepository mPastRepository;

    private LiveData<List<PastScan>> mAllPastScans;


    public PastScanViewModel(@NonNull Application application) {
        super(application);
        mPastRepository = new PastScanRepository(application);
        mAllPastScans = mPastRepository.getAllScans();
    }

    public LiveData<List<PastScan>> getAllPastScans() {
        return mAllPastScans;
    }

    public void insertPast(PastScan scan) {
        mPastRepository.insertPast(scan);
    }

    public void deletePast(PastScan pastScan) {
        mPastRepository.deletePast(pastScan);
    }
}
