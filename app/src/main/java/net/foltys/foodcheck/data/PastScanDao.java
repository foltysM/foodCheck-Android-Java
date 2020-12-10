package net.foltys.foodcheck.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PastScanDao {
    /**
     * Method inserts scan to past scans database
     *
     * @param scan Scan that will be inserted to database
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertScan(PastScan scan);

    /**
     * Method deletes scan from past scans database that have ID equal to parameter passed
     *
     * @param scanId ID of scan to delete
     */
    @Query("DELETE FROM past_scans_table WHERE 'id'=(:scanId)")
    void deleteScan(int scanId);

    /**
     * Method returns all records from past scans database
     *
     * @return LiveData of the list of all past scans
     */
    @Query("SELECT * FROM past_scans_table ORDER BY 'minutes' ASC")
    LiveData<List<PastScan>> loadAll();

    /**
     * Method deletes scan from past scans database that is equal to parameter passed
     *
     * @param pastScan Object of scan to delete
     */
    @Delete
    void deletePast(PastScan pastScan);
}
