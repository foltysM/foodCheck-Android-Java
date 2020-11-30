package net.foltys.foodcheck.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class FavProdRepository {

    private final FavProdDao mFavProdDao;
    private final LiveData<List<FavProd>> mAllFavProducts;

    FavProdRepository(Application application) {
        PastScanDatabase db = PastScanDatabase.getDatabase(application);
        mFavProdDao = db.favProdDao();
        mAllFavProducts = mFavProdDao.loadAllFav();
    }

    LiveData<List<FavProd>> getAllFav() {
        return mAllFavProducts;
    }

    FavProd getOneFav(String barcode) {
        return mFavProdDao.loadFavBarcode(barcode);
    }

    void insertFav(FavProd favProd) {
        PastScanDatabase.databaseWriteExecutor.execute(() -> mFavProdDao.insertFav(favProd));
    }

    void deleteFav(FavProd favProd) {
        PastScanDatabase.databaseWriteExecutor.execute(() -> mFavProdDao.deleteFav(favProd));
    }
}
