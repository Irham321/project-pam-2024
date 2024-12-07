package ibm.mobile.appscal.ui.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ibm.mobile.appscal.MainActivity;
import ibm.mobile.appscal.R;
import ibm.mobile.appscal.data.model.User;
import ibm.mobile.appscal.databinding.ActivityLogInBinding;
import ibm.mobile.appscal.databinding.ActivitySignUpBinding;
import ibm.mobile.appscal.ui.LoadingDialog;

public class LogInActivity extends AppCompatActivity {

    private ActivityLogInBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLogInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

//        userDb = FirebaseDatabase.getInstance().getReference("users");

        // inisialisasi elemen
//        tvDaftarSini = findViewById(R.id.tvDaftarSini);
//        etEmailLogin = findViewById(R.id.etUserLogIn);
//        etPassLogin = findViewById(R.id.etPassLogIn);
//        btLogin = findViewById(R.id.btLogIn);

        // text-link Sign-up jika belum ada akun
        binding.tvDaftarSini.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LogInActivity.this, SignUpActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        LoadingDialog loadingDialog = new LoadingDialog(LogInActivity.this);

        // button Log-In
        binding.btLogIn.setOnClickListener(view -> {
            if(isValid()){

                String email = binding.etUserLogIn.getText().toString();
                String password = binding.etPassLogIn.getText().toString();
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                makeToast("Login success");
                                loadingDialog.dismissDialog();

                                Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                loadingDialog.dismissDialog();
                                makeToast(e.getMessage());
                            }
                        });
            }
        });

    }

    private void makeToast(String msg) {
        Toast.makeText(LogInActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    private boolean isValid() {
        String email = binding.etUserLogIn.getText().toString();
        String password = binding.etPassLogIn.getText().toString();

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