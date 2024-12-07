package ibm.mobile.appscal.ui;

import java.util.Comparator;

import ibm.mobile.appscal.data.model.HistoryMakan;

class SortByNewest implements Comparator<HistoryMakan> {
    @Override
    public int compare(HistoryMakan o1, HistoryMakan o2) {
        return o2.getTimeStampHistory().compareTo(o1.getTimeStampHistory());
    }
}

class SortByOldest implements Comparator<HistoryMakan> {
    @Override
    public int compare(HistoryMakan o1, HistoryMakan o2) {
        return o1.getTimeStampHistory().compareTo(o2.getTimeStampHistory());
    }
}
