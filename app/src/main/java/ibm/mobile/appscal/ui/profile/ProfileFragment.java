package ibm.mobile.appscal.ui.profile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import ibm.mobile.appscal.MainActivity;
import ibm.mobile.appscal.R;
import ibm.mobile.appscal.SplashActivity;
import ibm.mobile.appscal.data.model.User;
import ibm.mobile.appscal.databinding.FragmentProfileBinding;
import ibm.mobile.appscal.ui.LoadingDialog;
import ibm.mobile.appscal.ui.MainViewModel;


public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private User newestCurrentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
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
                binding.tvUserEmail.setText(user.getEmail());
                binding.tvUserGender.setText(user.getJenisKelamin());
                binding.tvUserName.setText(user.getNama());
                binding.etBeratBadan.setText(user.getBeratBadan()+"");
                binding.etTinggiBadan.setText(user.getTinggiBadan()+"");
                binding.etUmur.setText(user.getUmur()+"");

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

        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLogoutDialog();
            }
        });

        binding.btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.etTinggiBadan.getText().toString().isEmpty()){
                    makeToast("Tinggi cannot be empty");
                }else if(binding.etBeratBadan.getText().toString().isEmpty()){
                    makeToast("Berat cannot be empty");
                }else if(binding.etUmur.getText().toString().isEmpty()){
                    makeToast("Umur cannot be empty");
                }else{
                    int umur = Integer.parseInt(binding.etUmur.getText().toString());
                    float beratBadan = Float.parseFloat(binding.etBeratBadan.getText().toString());
                    float tinggiBadan = Float.parseFloat(binding.etTinggiBadan.getText().toString());

                    if(umur != newestCurrentUser.getUmur() || beratBadan != newestCurrentUser.getBeratBadan() || tinggiBadan!= newestCurrentUser.getTinggiBadan()){
                        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                        builder.setTitle("SIMPAN");
                        builder.setMessage("Apakah kamu yakin untuk menyimpan perubahan?");

                        builder.setPositiveButton("Iya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                HashMap<String, Object> updatedValues = new HashMap<>();
                                updatedValues.put("tinggiBadan", tinggiBadan);
                                updatedValues.put("beratBadan", beratBadan);
                                updatedValues.put("umur", umur);
                                FirebaseDatabase.getInstance().getReference("Users").child(newestCurrentUser.getUid()).updateChildren(updatedValues)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> taskDatabase) {
                                                if (taskDatabase.isSuccessful()) {
                                                    loadingDialog.dismissDialog();
                                                    makeToast("Perubahan data tersimpan.");
                                                } else {
                                                    loadingDialog.dismissDialog();
                                                    makeToast(taskDatabase.getException().getMessage());
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
                }
            }
        });



    }

    private void makeToast(String message){
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("LOGOUT");
        builder.setMessage("Apakah kamu yakin akan logout?");

        builder.setPositiveButton("Iya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), SplashActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                if (getActivity() != null) {
                    getActivity().finish();
                }
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
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}