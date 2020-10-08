package net.foltys.foodcheck.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "fav_prod_table")
public class FavProd {
    @NonNull
    @PrimaryKey(autoGenerate = false)
    String barcode;
    String name;
    double weight;
    float energy;
    float carbohydrates;
    float protein;
    float fat;
    float saturates;
    float sugars;
    float fibre;
    float salt;
}
