package ibm.mobile.appscal.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import ibm.mobile.appscal.data.model.HistoryMakan;
import ibm.mobile.appscal.data.model.MenuItem;
import ibm.mobile.appscal.data.model.User;

public class MainViewModel extends ViewModel {

    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final DatabaseReference userRef = database.getReference("Users");
    private final DatabaseReference menuRef = FirebaseDatabase.getInstance().getReference("Menu");
    private final DatabaseReference historyRef = FirebaseDatabase.getInstance().getReference("HistoryMakan");

    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    private final MutableLiveData<User> _currentUser = new MutableLiveData<>();
    public LiveData<User> getCurrentUser() {
        return _currentUser;
    }

    private final MutableLiveData<List<MenuItem>> _menuItemList = new MutableLiveData<>();
    public LiveData<List<MenuItem>> getMenuItemList() {
        return _menuItemList;
    }

    private final MutableLiveData<List<MenuItem>> _filteredMenuItemList = new MutableLiveData<>();
    public LiveData<List<MenuItem>> getFilteredMenuItemList() {
        return _filteredMenuItemList;
    }

    private final MutableLiveData<List<HistoryMakan>> _historyMakanList = new MutableLiveData<>();
    public LiveData<List<HistoryMakan>> getHistoryMakanList() {
        return _historyMakanList;
    }

    private final MutableLiveData<List<HistoryMakan>> _filteredHistoryMakanList = new MutableLiveData<>();
    public LiveData<List<HistoryMakan>> getFilteredHistoryMakanList() {
        return _filteredHistoryMakanList;
    }

    private final MutableLiveData<Boolean> _loading = new MutableLiveData<>();
    public LiveData<Boolean> getLoading() {
        return _loading;
    }

    private final MutableLiveData<String> _errorMsg = new MutableLiveData<>();
    public LiveData<String> getErrorMsg() {
        return _errorMsg;
    }

    private final MutableLiveData<Boolean> _errorResetApp = new MutableLiveData<>();
    public LiveData<Boolean> getErrorResetApp() {
        return _errorResetApp;
    }

    private String searchMenuText = "";
    private boolean newestFilter = true;
    private Long filterTimeStamp = 0L;


    public void searchMenuItem(String query) {
        searchMenuText = query;
        applySearchMenuItem();
    }
    
    public void applyFilterToHistoryList(boolean filterBoolean, long filterTime){
        newestFilter = filterBoolean;
        filterTimeStamp = filterTime;
        applyHistoryFilter();
    }

    private void applyHistoryFilter() {
        List<HistoryMakan> sortedList = _historyMakanList.getValue();
        if(newestFilter){
            Collections.sort(sortedList, new SortByNewest());
        }else{
            Collections.sort(sortedList, new SortByOldest());
        }
        if(filterTimeStamp != 0L){
            List<HistoryMakan> sortedListWithTime = sortedList.stream()
                    .filter( historyMakan ->
                            DateUtil.isSameDay(historyMakan.getTimeStampHistory(), filterTimeStamp)
                    )
                    .collect(Collectors.toList());
            sortedList = sortedListWithTime;
        }
        _filteredHistoryMakanList.setValue(sortedList);
    }

    private void applySearchMenuItem() {
        List<MenuItem> menuItemFilteredList = _menuItemList.getValue().stream()
                .filter(menuItem ->{
                            boolean matchSearch = menuItem.getNamaMenu().toLowerCase().contains(searchMenuText.toLowerCase());
                            return matchSearch;
                        }
                )
                .collect(Collectors.toList());
        _filteredMenuItemList.setValue(menuItemFilteredList);
    }

    public MainViewModel() {
        observeCurrentUser();
        observeCurrentMenuItemList();
        observeHistoryMakanList();
    }

    private void observeCurrentUser() {
        String currentUserUid = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;
        if (currentUserUid != null) {
            _errorResetApp.setValue(false);
            _loading.setValue(true);
            userRef.child(currentUserUid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        _currentUser.setValue(user);
                    } else {
                        _errorResetApp.setValue(true);
                        _errorMsg.setValue("User not found");
                    }
                    _loading.setValue(false);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    _errorMsg.setValue(error.getMessage());
                    _loading.setValue(false);
                }
            });
        } else {
            _errorResetApp.setValue(true);
        }
    }

    private void observeCurrentMenuItemList() {
        _loading.setValue(true);
        menuRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<MenuItem> snapshotMenuItemList = new ArrayList<>();
                for (DataSnapshot menuSnapshot : snapshot.getChildren()) {
                    MenuItem menu = menuSnapshot.getValue(MenuItem.class);
                    if (menu != null) {
                        snapshotMenuItemList.add(menu);
                    }
                }
                _loading.setValue(false);
                _menuItemList.setValue(snapshotMenuItemList);
                applySearchMenuItem();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                _errorMsg.setValue(error.getMessage());
                _loading.setValue(false);
            }
        });
    }

    private void observeHistoryMakanList() {
        _loading.setValue(true);
        historyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<HistoryMakan> snapshotHistoryMakanList = new ArrayList<>();
                for (DataSnapshot historySnapshot : snapshot.getChildren()) {
                    HistoryMakan history = historySnapshot.getValue(HistoryMakan.class);
                    if (history != null) {
                        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(history.getUserUid())){
                            snapshotHistoryMakanList.add(history);
                        }
                    }
                }
                _loading.setValue(false);
                _historyMakanList.setValue(snapshotHistoryMakanList);
                applyHistoryFilter();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                _errorMsg.setValue(error.getMessage());
                _loading.setValue(false);
            }
        });
    }
}