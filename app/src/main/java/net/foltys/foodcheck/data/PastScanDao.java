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
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertScan(PastScan scan);

    @Query("DELETE FROM past_scans_table WHERE 'id'=(:scanId)")
    void deleteScan(int scanId);

    @Query("SELECT * FROM past_scans_table ORDER BY 'date', 'hour' DESC")
    LiveData<List<PastScan>> loadAll();

    @Delete
    void deletePast(PastScan pastScan);
}
