package net.foltys.foodcheck.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class FavProdRepository {

    private final FavProdDao mFavProdDao;
    private final LiveData<List<FavProd>> mAllFavProducts;


    /**
     * Constructor of FavProdRepository class which is a Room repository for favorite products database
     *
     * @param application Object of Application class, includes global application state
     */
    FavProdRepository(Application application) {
        PastScanDatabase db = PastScanDatabase.getDatabase(application);
        mFavProdDao = db.favProdDao();
        mAllFavProducts = mFavProdDao.loadAllFav();
    }

    /**
     * Method returns all records from favorites database
     *
     * @return LiveData of the list of all favorite products
     */
    LiveData<List<FavProd>> getAllFav() {
        return mAllFavProducts;
    }

    /**
     * @param barcode Barcode of the product that is being searched for
     * @return Object of searched favorite product (if exists)
     */
    FavProd getOneFav(String barcode) {
        return mFavProdDao.loadFavBarcode(barcode);
    }

    /**
     * Method inserts favorite product to favorites database
     *
     * @param favProd Favorite product that will be inserted to the database
     */
    void insertFav(FavProd favProd) {
        PastScanDatabase.databaseWriteExecutor.execute(() -> mFavProdDao.insertFav(favProd));
    }

    /**
     * Method deletes favorite product passed from the favorites database
     *
     * @param favProd Favorite product that will be deleted from the database
     */
    void deleteFav(FavProd favProd) {
        PastScanDatabase.databaseWriteExecutor.execute(() -> mFavProdDao.deleteFav(favProd));
    }
}
