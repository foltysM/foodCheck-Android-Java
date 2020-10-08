package net.foltys.foodcheck.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface FavProdDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public void insertFav(FavProd favProd);

    @Delete
    public void deleteFav(FavProd favProd);

    @Query("SELECT * FROM fav_prod_table") //TODO ORDER BY name?
    public FavProd[] loadAllFav();

    @Query("SELECT * FROM fav_prod_table WHERE barcode=(:bar) LIMIT 1")
    public FavProd loadFavBarcode(String bar);

}
