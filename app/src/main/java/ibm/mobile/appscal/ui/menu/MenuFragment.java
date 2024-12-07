package ibm.mobile.appscal.ui.menu;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import ibm.mobile.appscal.MainActivity;
import ibm.mobile.appscal.R;
import ibm.mobile.appscal.data.model.HistoryMakan;
import ibm.mobile.appscal.data.model.MenuItem;
import ibm.mobile.appscal.data.model.User;
import ibm.mobile.appscal.databinding.DialogChooseTypeBinding;
import ibm.mobile.appscal.databinding.FragmentMenuBinding;
import ibm.mobile.appscal.databinding.FragmentProfileBinding;
import ibm.mobile.appscal.ui.LoadingDialog;
import ibm.mobile.appscal.ui.MainViewModel;
import ibm.mobile.appscal.ui.adapter.HistoryMakanAdapter;
import ibm.mobile.appscal.ui.adapter.MenuItemAdapter;


public class MenuFragment extends Fragment {

    private FragmentMenuBinding binding;
    private User newestCurrentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMenuBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MainViewModel mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        LoadingDialog loadingDialog = new LoadingDialog(requireContext());

        mainViewModel.getCurrentUser().observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                newestCurrentUser = user;
            }
        });

        mainViewModel.getFilteredMenuItemList().observe(getViewLifecycleOwner(), new Observer<List<MenuItem>>() {
            @Override
            public void onChanged(List<MenuItem> menuItems) {
                MenuItemAdapter menuItemAdapter = new MenuItemAdapter(menuItems, ((gram, menuItem) -> {
                    showChooseWaktuMakanDialog(new OnWaktuPilih() {
                        @Override
                        public void onPilihWaktu(int waktuMakan) {
                            loadingDialog.startLoadingDialog();
                            DatabaseReference taskRef = FirebaseDatabase.getInstance().getReference("HistoryMakan");
                            String key = taskRef.push().getKey();
                            HistoryMakan historyMakan = new HistoryMakan(key, menuItem.getId(), newestCurrentUser.getUid(), waktuMakan, gram, System.currentTimeMillis());
                            taskRef.child(key).setValue(historyMakan).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        loadingDialog.dismissDialog();
                                        if (getActivity() instanceof MainActivity) {
                                            ((MainActivity) getActivity()).chooseHomePage();
                                        }
                                        makeToast("History added successfully.");
                                    }else{
                                        loadingDialog.dismissDialog();
                                        makeToast(task.getException().getMessage());
                                    }
                                }
                            });
                        }
                    });
                }));
                binding.rvMenu.setAdapter(menuItemAdapter);
                binding.rvMenu.setLayoutManager(new LinearLayoutManager(requireContext()));
            }
        });

        binding.menuSV.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                mainViewModel.searchMenuItem(s);
                return false;
            }
        });

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).chooseHomePage();
                }
            }
        });

    }

    public interface OnWaktuPilih {
        void onPilihWaktu(int waktuMakan);
    }

    private void showChooseWaktuMakanDialog(OnWaktuPilih onWaktuPilih){
        DialogChooseTypeBinding dialogPilihWaktuBinding = DialogChooseTypeBinding.inflate(getLayoutInflater());
        AlertDialog.Builder dialogPilihWaktuBuilder = new AlertDialog.Builder(requireContext()).setView(dialogPilihWaktuBinding.getRoot());
        AlertDialog waktuDialog = dialogPilihWaktuBuilder.create();

        dialogPilihWaktuBinding.btnSarapan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                waktuDialog.dismiss();
                onWaktuPilih.onPilihWaktu(0);
            }
        });

        dialogPilihWaktuBinding.btnLunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                waktuDialog.dismiss();
                onWaktuPilih.onPilihWaktu(1);
            }
        });

        dialogPilihWaktuBinding.btnDinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                waktuDialog.dismiss();
                onWaktuPilih.onPilihWaktu(2);
            }
        });

        waktuDialog.setCancelable(false);
        waktuDialog.show();
    }

    private void makeToast(String message){
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}