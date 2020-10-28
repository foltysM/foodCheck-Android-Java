package net.foltys.foodcheck.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FavProdDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public void insertFav(FavProd favProd);

    @Delete
    void deleteFav(FavProd favProd);

    @Query("SELECT * FROM fav_prod_table ORDER BY `name`")
    LiveData<List<FavProd>> loadAllFav();

    @Query("SELECT * FROM fav_prod_table WHERE barcode=:bar LIMIT 1")
    FavProd loadFavBarcode(String bar);

}
