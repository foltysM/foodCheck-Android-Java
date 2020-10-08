package net.foltys.foodcheck.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface PastScanDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public void insertScan(PastScan scan);

    @Delete
    public void deleteScan(PastScan scan);

    @Query("SELECT * FROM past_scans_table") // TODO moze jakiś order by?
    public PastScan[] loadAll();


}
