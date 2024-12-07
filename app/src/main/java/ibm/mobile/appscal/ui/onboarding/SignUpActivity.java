package ibm.mobile.appscal.ui.onboarding;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ibm.mobile.appscal.R;
import ibm.mobile.appscal.data.model.User;
import ibm.mobile.appscal.databinding.ActivityGenderBinding;
import ibm.mobile.appscal.databinding.ActivitySignUpBinding;
import ibm.mobile.appscal.databinding.DialogBeratBadanBinding;
import ibm.mobile.appscal.databinding.DialogNamaSignupBinding;
import ibm.mobile.appscal.databinding.DialogTinggiBadanBinding;
import ibm.mobile.appscal.databinding.DialogUmurSignupBinding;
import ibm.mobile.appscal.ui.LoadingDialog;


public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();


//        userDb = FirebaseDatabase.getInstance().getReference("users");

        binding.tvMasukSini.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, LogInActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        LoadingDialog loadingDialog = new LoadingDialog(SignUpActivity.this);
        // Button Sign-Up
        binding.btSignUp.setOnClickListener(view -> {
            if (isValid()) {
                loadingDialog.startLoadingDialog();
                String username = binding.etUserSignUp.getText().toString();
                String email = binding.etEmailSignUp.getText().toString();
                String password = binding.etPassSignUp.getText().toString();

                DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");
                FirebaseAuth auth = FirebaseAuth.getInstance();
                usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            loadingDialog.dismissDialog();
                            makeToast("Error : Email already exist!");
                        }else{
                            loadingDialog.dismissDialog();
                            openNamaFormDialog(stringData -> {
                                String nama = stringData;
                                openGenderFormBinding(stringData1 -> {
                                    String jenisKelamin = stringData1;
                                    openTinggiBadanFormDialog(floatData -> {
                                        Float tinggi = floatData;
                                        openBeratBadanFormDialog(floatData1 -> {
                                            Float beratBadan = floatData1;
                                            openUmurFormDialog(intData -> {
                                                int usiaUser = intData;
                                                loadingDialog.startLoadingDialog();
                                                auth.createUserWithEmailAndPassword(email, password)
                                                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                                if (task.isSuccessful()) {
                                                                    String uid = auth.getCurrentUser().getUid();

                                                                    User user = new User(uid, username, email, password, nama, jenisKelamin, tinggi, beratBadan, usiaUser);

                                                                    FirebaseDatabase.getInstance().getReference("Users")
                                                                            .child(uid)
                                                                            .setValue(user)
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> innerTask) {
                                                                                    if (innerTask.isSuccessful()) {
                                                                                        auth.signOut();
                                                                                        loadingDialog.dismissDialog();
                                                                                        makeToast("Register success.");
                                                                                        Intent intent = new Intent(SignUpActivity.this, LogInActivity.class);
                                                                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                                        startActivity(intent);
                                                                                        finish();
                                                                                    } else {
                                                                                        loadingDialog.dismissDialog();
                                                                                        makeToast("Register failed.");
                                                                                    }
                                                                                }
                                                                            });
                                                                } else {
                                                                    loadingDialog.dismissDialog();
                                                                    makeToast(task.getException().toString());
                                                                }
                                                            }
                                                        });
                                            });
                                        });
                                    });
                                });
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        loadingDialog.dismissDialog();
                        makeToast("Unexpected Error");
                    }
                });

//                String id = userDb.push().getKey(); // Generate unique key
//                User user = new User(id, username, email, password);

//                userDb.child(id).setValue(user)
//                        .addOnSuccessListener(aVoid -> {
//                            Toast.makeText(this, "Sign Up Berhasil!", Toast.LENGTH_SHORT).show();
//                            Intent intent = new Intent(SignUpActivity.this, GenderActivity.class);
//                            startActivity(intent);
//                            finish();
//                        })
//                        .addOnFailureListener(e -> {
//                            Toast.makeText(this, "Sign Up Gagal: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                        });
            }
        });

    }

    private void makeToast(String msg) {
        Toast.makeText(SignUpActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    public interface OnStringDataSubmit {
        void onStringSubmit(String stringData);
    }

    public interface OnFloatDataSubmit {
        void onFloatSubmit(Float floatData);
    }

    public interface OnIntDataSubmit {
        void onIntSubmit(int intData);
    }

    private void openGenderFormBinding(OnStringDataSubmit onStringDataSubmit){
        ActivityGenderBinding activityGenderBinding = ActivityGenderBinding.inflate(getLayoutInflater());
        Dialog genderFormDialog = new Dialog(SignUpActivity.this,  R.style.FullscreenDialog);
        genderFormDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        genderFormDialog.setContentView(activityGenderBinding.getRoot());
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(genderFormDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        genderFormDialog.getWindow().setAttributes(lp);
        genderFormDialog.setCancelable(false);

        activityGenderBinding.btPria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                genderFormDialog.dismiss();
                onStringDataSubmit.onStringSubmit("Pria");
            }
        });

        activityGenderBinding.btWanita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                genderFormDialog.dismiss();
                onStringDataSubmit.onStringSubmit("Wanita");
            }
        });

        genderFormDialog.show();
    }

    private void openNamaFormDialog(OnStringDataSubmit onStringDataSubmit){
        DialogNamaSignupBinding dialogNamaSignupBinding = DialogNamaSignupBinding.inflate(getLayoutInflater());
        Dialog namaFormDialog = new Dialog(SignUpActivity.this,  R.style.FullscreenDialog);
        namaFormDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        namaFormDialog.setContentView(dialogNamaSignupBinding.getRoot());
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(namaFormDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        namaFormDialog.getWindow().setAttributes(lp);
        namaFormDialog.setCancelable(false);

        dialogNamaSignupBinding.btnSelanjutnya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nama = dialogNamaSignupBinding.etNamaSignUp.getText().toString().trim();
                if(nama.isEmpty()){
                    Toast.makeText(SignUpActivity.this, "Nama cannot be empty", Toast.LENGTH_SHORT).show();
                }else{
                    namaFormDialog.dismiss();
                    onStringDataSubmit.onStringSubmit(nama);
                }

            }
        });
        namaFormDialog.show();
    }

    private void openTinggiBadanFormDialog(OnFloatDataSubmit onFloatDataSubmit){
        DialogTinggiBadanBinding dialogTinggiBadanBinding = DialogTinggiBadanBinding.inflate(getLayoutInflater());
        Dialog tinggiFormDialog = new Dialog(SignUpActivity.this,  R.style.FullscreenDialog);
        tinggiFormDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        tinggiFormDialog.setContentView(dialogTinggiBadanBinding.getRoot());
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(tinggiFormDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        tinggiFormDialog.getWindow().setAttributes(lp);
        tinggiFormDialog.setCancelable(false);

        dialogTinggiBadanBinding.btnSelanjutnya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tinggiBadan = dialogTinggiBadanBinding.etTinggiBadan.getText().toString().trim();
                if(tinggiBadan.isEmpty()){
                    Toast.makeText(SignUpActivity.this, "Tinggi cannot be empty", Toast.LENGTH_SHORT).show();
                }else{
                    tinggiFormDialog.dismiss();
                    Float tinggiToFloat = Float.parseFloat(tinggiBadan);
                    onFloatDataSubmit.onFloatSubmit(tinggiToFloat);
                }
            }
        });

        tinggiFormDialog.show();
    }

    private void openBeratBadanFormDialog(OnFloatDataSubmit onFloatDataSubmit){
        DialogBeratBadanBinding dialogBeratBadanBinding = DialogBeratBadanBinding.inflate(getLayoutInflater());
        Dialog beratFormDialog = new Dialog(SignUpActivity.this,  R.style.FullscreenDialog);
        beratFormDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        beratFormDialog.setContentView(dialogBeratBadanBinding.getRoot());
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(beratFormDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        beratFormDialog.getWindow().setAttributes(lp);
        beratFormDialog.setCancelable(false);


        dialogBeratBadanBinding.btnSelanjutnya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String beratBadan = dialogBeratBadanBinding.etBeratBadan.getText().toString().trim();
                if(beratBadan.isEmpty()){
                    Toast.makeText(SignUpActivity.this, "Berat cannot be empty", Toast.LENGTH_SHORT).show();
                }else{
                    beratFormDialog.dismiss();
                    Float beratToFloat = Float.parseFloat(beratBadan);
                    onFloatDataSubmit.onFloatSubmit(beratToFloat);
                }
            }
        });
        beratFormDialog.show();
    }

    private void openUmurFormDialog(OnIntDataSubmit onIntDataSubmit){
        DialogUmurSignupBinding dialogUmurSignupBinding = DialogUmurSignupBinding.inflate(getLayoutInflater());
        Dialog umurFormDialog = new Dialog(SignUpActivity.this,  R.style.FullscreenDialog);
        umurFormDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        umurFormDialog.setContentView(dialogUmurSignupBinding.getRoot());
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(umurFormDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        umurFormDialog.getWindow().setAttributes(lp);
        umurFormDialog.setCancelable(false);


        dialogUmurSignupBinding.btnSelanjutnya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String umur = dialogUmurSignupBinding.etUmur.getText().toString().trim();
                if(umur.isEmpty()){
                    Toast.makeText(SignUpActivity.this, "Umur cannot be empty", Toast.LENGTH_SHORT).show();
                }else{
                    umurFormDialog.dismiss();
                    int umurToInt = Integer.parseInt(umur);
                    onIntDataSubmit.onIntSubmit(umurToInt);
                }
            }
        });

        umurFormDialog.show();
    }

    private boolean isValid() {
        String username = binding.etUserSignUp.getText().toString().trim();
        String email = binding.etEmailSignUp.getText().toString().trim();
        String password = binding.etPassSignUp.getText().toString();

        if (username.isEmpty()) {
            showToast("Username cannot be empty");
            return false;
        }

        if (email.isEmpty()) {
            showToast("Email cannot be empty");
            return false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Invalid email format");
            return false;
        }

        if (password.isEmpty()) {
            showToast("Password cannot be empty");
            return false;
        }

        return true;
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}