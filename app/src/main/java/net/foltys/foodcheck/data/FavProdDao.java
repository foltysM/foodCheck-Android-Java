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
    /**
     * Method inserts favorite product to favorites database
     *
     * @param favProd Favorite product that will be inserted to the database
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertFav(FavProd favProd);

    /**
     * Method deletes favorite product passed, from the favorites database
     *
     * @param favProd Favorite product that will be deleted from the database
     */
    @Delete
    void deleteFav(FavProd favProd);

    /**
     * Method returns all records from favorites database
     *
     * @return LiveData of the list of all favorite products
     */
    @Query("SELECT * FROM fav_prod_table ORDER BY `name`")
    LiveData<List<FavProd>> loadAllFav();

    /**
     * Method returns the object of favorite product that has specified barcode
     *
     * @param bar Barcode of the product that is being searched for
     * @return Object of searched favorite product (if exists)
     */
    @Query("SELECT * FROM fav_prod_table WHERE barcode=:bar LIMIT 1")
    FavProd loadFavBarcode(String bar);

}
