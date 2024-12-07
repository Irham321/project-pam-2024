package ibm.mobile.appscal.ui.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import ibm.mobile.appscal.MainActivity;
import ibm.mobile.appscal.R;
import ibm.mobile.appscal.data.model.HistoryMakan;
import ibm.mobile.appscal.data.model.MenuItem;
import ibm.mobile.appscal.data.model.User;
import ibm.mobile.appscal.databinding.FragmentHomeBinding;
import ibm.mobile.appscal.databinding.FragmentProfileBinding;
import ibm.mobile.appscal.ui.CalorieCalculator;
import ibm.mobile.appscal.ui.DateUtil;
import ibm.mobile.appscal.ui.LoadingDialog;
import ibm.mobile.appscal.ui.MainViewModel;


public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private List<HistoryMakan> todaySarapanList = new ArrayList<>();
    private List<HistoryMakan> todayLunchList = new ArrayList<>();
    private List<HistoryMakan> todayDinnerList = new ArrayList<>();
    private List<MenuItem> infoSemuaMakananList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MainViewModel mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        LoadingDialog loadingDialog = new LoadingDialog(requireContext());

        mainViewModel.getCurrentUser().observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                binding.tvUserMain.setText(user.getNama());
                int caloriesNeeded = CalorieCalculator.calculateBMR(user.getBeratBadan(), user.getTinggiBadan(), user.getUmur(), user.getJenisKelamin());
                binding.CaloriesLeftNumb.setText(""+ caloriesNeeded);
                int caloriesNeededSarapan = Math.round((17f/100f) * caloriesNeeded);
                int caloriesNeededLunch = Math.round((46f/100f) * caloriesNeeded);
                int caloriesNeededDinner = Math.round((37f/100f) * caloriesNeeded);
                binding.TargetFixSarapan.setText(""+caloriesNeededSarapan);
                binding.TargetFixLunch.setText(""+caloriesNeededLunch);
                binding.TargetFixDinner.setText(""+caloriesNeededDinner);

                binding.progressBar.setMax(caloriesNeeded);
            }
        });

        mainViewModel.getLoading().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isLoading) {
                if (isLoading) {
                    loadingDialog.startLoadingDialog();
                } else {
                    loadingDialog.dismissDialog();
                }
            }
        });

        mainViewModel.getErrorMsg().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String errorMsg) {
                Toast.makeText(requireActivity(), errorMsg, Toast.LENGTH_SHORT).show();
            }
        });

        mainViewModel.getErrorResetApp().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean it) {
                if (it) {
                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).restartResetAuthApp();
                    }
                }
            }
        });

        binding.buttonDinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).chooseMenuPage();
                }
            }
        });
        binding.buttonSarapan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).chooseMenuPage();
                }
            }
        });
        binding.buttonLunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).chooseMenuPage();
                }
            }
        });

        mainViewModel.getMenuItemList().observe(getViewLifecycleOwner(), new Observer<List<MenuItem>>() {
            @Override
            public void onChanged(List<MenuItem> menuItems) {
                infoSemuaMakananList = menuItems;
                generateInfoKalori();
            }
        });

        mainViewModel.getHistoryMakanList().observe(getViewLifecycleOwner(), new Observer<List<HistoryMakan>>() {
            @Override
            public void onChanged(List<HistoryMakan> historyMakans) {
                Long todayTimeStamp = System.currentTimeMillis();
                List<HistoryMakan> todayHistoryMakanList = filterBySameDay(historyMakans, todayTimeStamp);
                todaySarapanList = todayHistoryMakanList.stream()
                        .filter( historyMakan ->
                                historyMakan.getWaktuMakan() == 0
                        )
                        .collect(Collectors.toList());

                todayLunchList = todayHistoryMakanList.stream()
                        .filter( historyMakan ->
                                historyMakan.getWaktuMakan() == 1
                        )
                        .collect(Collectors.toList());

                todayDinnerList = todayHistoryMakanList.stream()
                        .filter( historyMakan ->
                                historyMakan.getWaktuMakan() == 2
                        )
                        .collect(Collectors.toList());
                generateInfoKalori();
            }
        });
    }

    private void generateInfoKalori() {
        float jumlahKaloriSarapan = calculateTotalCalories(todaySarapanList);
        float jumlahKaloriLunch = calculateTotalCalories(todayLunchList);
        float jumlahKaloriDinner = calculateTotalCalories(todayDinnerList);
        float eatenKalori = jumlahKaloriSarapan + jumlahKaloriLunch + jumlahKaloriDinner;
        binding.CaloriesEatenNumb.setText(""+eatenKalori);
        binding.TargetSarapan.setText(""+ jumlahKaloriSarapan);
        binding.TargetLunch.setText(""+ jumlahKaloriLunch);
        binding.TargetDinner.setText(""+ jumlahKaloriDinner);
        if(jumlahKaloriSarapan > 0f) binding.InfoMakan.setText("Anda sudah menambahkan makanan.");
        if(jumlahKaloriLunch > 0f) binding.InfoMakanLunch.setText("Anda sudah menambahkan makanan.");
        if(jumlahKaloriDinner > 0f) binding.InfoMakanDinner.setText("Anda sudah menambahkan makanan.");

        binding.progressBar.setProgress(Math.round(eatenKalori), true);
    }

    private float calculateCalories(int gram, float caloriesPerGram) {
        return gram * caloriesPerGram;
    }

    private float calculateTotalCalories(List<HistoryMakan> historyList) {
        float totalCalories = 0;
        for (HistoryMakan historyMakan : historyList) {
            MenuItem menuItem = getMenuItemById(historyMakan.getMenuId());
            if (menuItem != null) {
                float caloriesPerGram = (float)menuItem.getKalori() / (float)menuItem.getPerGram();
                totalCalories += calculateCalories(historyMakan.getGramMenu(), caloriesPerGram);
            }
        }
        return totalCalories;
    }

    private MenuItem getMenuItemById(String menuId) {
        for (MenuItem menuItem : infoSemuaMakananList) {
            if (menuItem.getId().equals(menuId)) {
                return menuItem;
            }
        }
        return null;
    }

    private void makeToast(String message){
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    private List<HistoryMakan> filterBySameDay(List<HistoryMakan> historyMakanList, long inputTimestamp) {
        List<HistoryMakan> filteredList = new ArrayList<>();
        for (HistoryMakan historyMakan : historyMakanList) {
            if (DateUtil.isSameDay(historyMakan.getTimeStampHistory(), inputTimestamp)) {
                filteredList.add(historyMakan);
            }
        }
        return filteredList;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}