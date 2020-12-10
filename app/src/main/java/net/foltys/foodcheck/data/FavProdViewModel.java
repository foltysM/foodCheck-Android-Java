package net.foltys.foodcheck.data;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class FavProdViewModel extends AndroidViewModel {

    private final FavProdRepository mRepo;

    private final LiveData<List<FavProd>> mAllFav;

    /**
     * Constructor of the FavProdViewModel class, part of Room. Its role is to provide data to the UI and survive configuration changes
     *
     * @param application Object of Application class, includes global application state
     */
    public FavProdViewModel(@NonNull Application application) {
        super(application);
        mRepo = new FavProdRepository(application);
        mAllFav = mRepo.getAllFav();
    }

    /**
     * Method returns all records from favorites database
     *
     * @return LiveData of the list of all favorite products
     */
    public LiveData<List<FavProd>> getAllFav() {
        return mAllFav;
    }

    /**
     * @param barcode Barcode of the product that is being searched for
     * @return Object of searched favorite product (if exists)
     */
    public FavProd getOneFav(String barcode) {
        return mRepo.getOneFav(barcode);
    }

    /**
     * Method inserts favorite product to favorites database
     *
     * @param fav Favorite product that will be inserted to the database
     */
    public void insertFav(FavProd fav) {
        mRepo.insertFav(fav);
    }

    /**
     * Method deletes favorite product passed from the favorites database
     *
     * @param fav Favorite product that will be deleted from the database
     */
    public void deleteFav(FavProd fav) {
        mRepo.deleteFav(fav);
    }
}
