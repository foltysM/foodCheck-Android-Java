package net.foltys.foodcheck.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "past_scans_table")
public class PastScan {
    @PrimaryKey(autoGenerate = true)
    int id;
    String barcode;
    String name;
    float weight;
    String date;
    float energy;
    float carbohydrates;
    float protein;
    float fat;
    float saturates;
    float sugars;
    float fibre;
    float salt;
}
