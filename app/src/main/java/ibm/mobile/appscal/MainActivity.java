package ibm.mobile.appscal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.HashSet;
import java.util.Set;

import ibm.mobile.appscal.data.model.User;
import ibm.mobile.appscal.databinding.ActivityMainBinding;
import ibm.mobile.appscal.ui.LoadingDialog;
import ibm.mobile.appscal.ui.MainViewModel;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MainViewModel mainViewModel;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        getSupportActionBar().hide();
        BottomNavigationView navView = binding.navView;

        mainViewModel = new ViewModelProvider(MainActivity.this).get(MainViewModel.class);


        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        navView.setItemIconTintList(null);
        NavigationUI.setupWithNavController(navView, navController);
//        NavigationUI.setupActionBarWithNavController(MainActivity.this, navController, appBarConfiguration);

        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController navController, @NonNull NavDestination navDestination, @Nullable Bundle bundle) {
                if(navDestination.getId() == R.id.homeFragment || navDestination.getId() == R.id.menuFragment ||  navDestination.getId() == R.id.historyFragment || navDestination.getId() == R.id.profileFragment){
                    binding.navView.setVisibility(View.VISIBLE);
                }else{
                    binding.navView.setVisibility(View.GONE);
                }
            }
        });

        LoadingDialog loadingDialog = new LoadingDialog(this);

        mainViewModel.getCurrentUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                currentUser = user;
            }
        });

        mainViewModel.getLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isLoading) {
                if (isLoading) {
                    loadingDialog.startLoadingDialog();
                } else {
                    loadingDialog.dismissDialog();
                }
            }
        });

        mainViewModel.getErrorMsg().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String errorMsg) {
                Toast.makeText(MainActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
            }
        });

        mainViewModel.getErrorResetApp().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean it) {
                if (it) {
                    restartResetAuthApp();
                }
            }
        });

    }

    public void restartResetAuthApp() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void chooseMenuPage(){
        binding.navView.setSelectedItemId(R.id.menuFragment);
    }

    public void chooseHomePage(){
        binding.navView.setSelectedItemId(R.id.homeFragment);
    }

}