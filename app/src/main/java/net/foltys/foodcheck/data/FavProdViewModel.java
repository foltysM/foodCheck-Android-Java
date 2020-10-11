package net.foltys.foodcheck.data;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class FavProdViewModel extends AndroidViewModel {

    public static final String TAG = "FavProdViewModel";
    private FavProdRepository mRepo;

    private LiveData<List<FavProd>> mAllFav;

    public FavProdViewModel(@NonNull Application application) {
        super(application);
        mRepo = new FavProdRepository(application);
        mAllFav = mRepo.getAllFav();
    }

    public LiveData<List<FavProd>> getAllFav() {
        return mAllFav;
    }

    public FavProd getOneFav(String barcode) {
        return mRepo.getOneFav(barcode);
    }

    public void insertFav(FavProd fav) {
        mRepo.insertFav(fav);
    }

    public void deleteFav(FavProd fav) {
        mRepo.deleteFav(fav);
    }
}
