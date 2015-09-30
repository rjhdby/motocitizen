package motocitizen.draw;

import motocitizen.main.R;

public class Resources {
    //TODO Избавиться от порнографии
    public static int[] getAccidentRowSetCommon() {
        return new int[]{R.layout.accident_row, R.drawable.accident_row_ended, R.drawable.accident_row_hidden};
    }

    public static int[] getAccidentRowSetOwner() {
        return new int[]{R.layout.accident_row_i_was_here, R.drawable.owner_accident_ended, R.drawable.owner_accident_hidden};
    }
}

