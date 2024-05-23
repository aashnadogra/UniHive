package com.example.uniapp.utilities;

import android.content.Context;
import android.text.format.DateFormat;
import android.widget.Toast;


import com.example.uniapp.R;

import java.util.Calendar;
import java.util.Locale;

public class Utils {
    public static final String AD_STATUS_AVAILABLE = "AVAILABLE";
    public static final String AD_STATUS_SOLD = "SOLD";
    public static final String[] categories = {
            "Stationary",
            "Novels",
            "Fashion & Beauty",
            "Sports",
            "Electronics and Appliances",
            "Home decor"
    };

    public static final int[] categoryIcons = {
            R.drawable.ic_category_stationary,
            R.drawable.ic_category_novel,
            R.drawable.ic_category_fashion,
            R.drawable.ic_category_sports,
            R.drawable.ic_category_electronics,
            R.drawable.ic_category_home_decor
    };

    public static final String[] conditions = {"New", "Used", "Refurbished"};

    public static void toast(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static long getTimestamp(){
        return System.currentTimeMillis();
    }

    public static String formatTimeDate(Long timestamp){
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(timestamp);
        String date = DateFormat.format("dd/MM/yyyy", calendar).toString();

        return date;
        
    }

}
