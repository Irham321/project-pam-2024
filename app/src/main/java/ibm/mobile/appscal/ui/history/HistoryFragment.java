package ibm.mobile.appscal.ui.history;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.DatePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import ibm.mobile.appscal.MainActivity;
import ibm.mobile.appscal.R;
import ibm.mobile.appscal.SplashActivity;
import ibm.mobile.appscal.data.model.HistoryMakan;
import ibm.mobile.appscal.data.model.MenuItem;
import ibm.mobile.appscal.data.model.User;
import ibm.mobile.appscal.databinding.FragmentHistoryBinding;
import ibm.mobile.appscal.databinding.FragmentHomeBinding;
import ibm.mobile.appscal.ui.CalorieCalculator;
import ibm.mobile.appscal.ui.LoadingDialog;
import ibm.mobile.appscal.ui.MainViewModel;
import ibm.mobile.appscal.ui.adapter.HistoryMakanAdapter;


public class HistoryFragment extends Fragment {

    private FragmentHistoryBinding binding;
    private User newestCurrentUser;
    private boolean newestFilter = true;
    private long filterWaktuTimestamp = 0L;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHistoryBinding.inflate(inflater, container, false);
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
                newestCurrentUser = user;
            }
        });

        HistoryMakanAdapter historyMakanAdapter = new HistoryMakanAdapter(requireContext(), new HistoryMakanAdapter.OnHistoryItemCallback() {
            @Override
            public void onSimpan(HistoryMakan historyMakan) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle("SIMPAN");
                builder.setMessage("Apakah kamu yakin untuk menyimpan perubahan?");

                builder.setPositiveButton("Iya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        loadingDialog.startLoadingDialog();
                        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                        DatabaseReference historyRef = rootRef.child("HistoryMakan").child(historyMakan.getId());

                        HashMap<String, Object> updatedValues = new HashMap<>();
                        updatedValues.put("gramMenu", historyMakan.getGramMenu());

                        historyRef.updateChildren(updatedValues).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    loadingDialog.dismissDialog();
                                    makeToast("History data updated.");
                                } else {
                                    loadingDialog.dismissDialog();
                                    makeToast("Failed to update history data.");
                                }
                            }
                        });
                    }
                });

                builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }

            @Override
            public void onHapus(HistoryMakan historyMakan) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle("HAPUS");
                builder.setMessage("Apakah kamu yakin akan menghapus data ini?");

                builder.setPositiveButton("Iya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        loadingDialog.startLoadingDialog();
                        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                        DatabaseReference historyRef = rootRef.child("HistoryMakan").child(historyMakan.getId());
                        historyRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    loadingDialog.dismissDialog();
                                    makeToast("History data deleted.");
                                } else {
                                    loadingDialog.dismissDialog();
                                    makeToast("Failed to delete history data.");
                                }
                            }
                        });
                    }
                });

                builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        binding.rvHistory.setAdapter(historyMakanAdapter);
        binding.rvHistory.setLayoutManager(new LinearLayoutManager(requireContext()));

        mainViewModel.getFilteredMenuItemList().observe(getViewLifecycleOwner(), new Observer<List<MenuItem>>() {
            @Override
            public void onChanged(List<MenuItem> menuItems) {
                historyMakanAdapter.submitMenuList(menuItems);
            }
        });

        mainViewModel.getFilteredHistoryMakanList().observe(getViewLifecycleOwner(), new Observer<List<HistoryMakan>>() {
            @Override
            public void onChanged(List<HistoryMakan> historyMakans) {
                historyMakanAdapter.submitHistoryList(historyMakans);
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

        binding.btnUrutkanWaktu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newestFilter = !newestFilter;
                mainViewModel.applyFilterToHistoryList(newestFilter, filterWaktuTimestamp);
                String urutanFilterString = newestFilter? "Terbaru" : "Terlama";
                binding.tvFilterUrutan.setText(urutanFilterString);
            }
        });

        binding.btnFilterWaktu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();

                // on below line we are getting
                // our day, month and year.
                if(filterWaktuTimestamp!=0L) c.setTimeInMillis(filterWaktuTimestamp);
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        requireContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                Calendar newDate = Calendar.getInstance();
                                newDate.set(year, monthOfYear, dayOfMonth);
                                filterWaktuTimestamp = newDate.getTimeInMillis();
                                mainViewModel.applyFilterToHistoryList(newestFilter, filterWaktuTimestamp);
                                binding.tvFilterWaktu.setText(dayOfMonth + "/" + monthOfYear + "/"+ year);
                                binding.btnClearFilterWaktu.setVisibility(View.VISIBLE);
                            }
                        },
                        year, month, day);
                datePickerDialog.show();
            }
        });

        binding.btnClearFilterWaktu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterWaktuTimestamp = 0L;
                mainViewModel.applyFilterToHistoryList(newestFilter, filterWaktuTimestamp);
                binding.tvFilterWaktu.setText("Semua Tanggal");
                binding.btnClearFilterWaktu.setVisibility(View.GONE);
            }
        });

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