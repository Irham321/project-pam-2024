package ibm.mobile.appscal.ui;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtil {

    public static boolean isSameDay(long timestamp1, long timestamp2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();

        cal1.setTimeInMillis(timestamp1);
        cal2.setTimeInMillis(timestamp2);

        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }


    public static String formatTimestamp(Long timestamp) {
        if (timestamp == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH.mm", Locale.getDefault());
        Date date = new Date(timestamp);
        return sdf.format(date);
    }
}
