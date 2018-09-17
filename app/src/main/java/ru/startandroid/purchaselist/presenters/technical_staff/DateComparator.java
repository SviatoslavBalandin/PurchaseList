package ru.startandroid.purchaselist.presenters.technical_staff;

import java.util.Comparator;

import ru.startandroid.purchaselist.model.GoodsList;

public class DateComparator implements Comparator<GoodsList> {

    @Override
    public int compare(GoodsList list1, GoodsList list2){

        String date1 = list1.getDate();
        String date2 = list2.getDate();

        if(!date1.equals(date2)) {

            String[] splittedDate1 = date1.split("\\.");
            String[] splittedDate2 = date2.split("\\.");

            int d1_date = Integer.parseInt(splittedDate1[0]);
            int d2_date = Integer.parseInt(splittedDate2[0]);
            int d1_month = Integer.parseInt(splittedDate1[1]);
            int d2_month = Integer.parseInt(splittedDate2[1]);
            int d1_year = Integer.parseInt(splittedDate1[2]);
            int d2_year = Integer.parseInt(splittedDate2[2]);

            if(d1_year != d2_year)
                return d1_year > d2_year ? -1 : 1;
            else if(d1_month != d2_month)
                return d1_month > d2_month ? -1 : 1;
            else if(d1_date != d2_date)
                return d1_date > d2_date ? -1 : 1;
        }
        return 0;
    }

}
