package cl.oktech.wood.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import cl.oktech.wood.ApiClient;
import cl.oktech.wood.models.Provider;
import cl.oktech.wood.services.ProviderService;
import cl.oktech.wood.R;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.emailEditText) EditText mEmail;
    @BindView(R.id.passwordEditText) EditText mPassword;
    @BindView(R.id.logInButton) Button mLogInButton;
    @BindView(R.id.switchRemember) Switch mRememberSwitch;
    @BindView(R.id.loginToolbar) Toolbar mToolbar;

    private SharedPreferences mSharedPreferences;
    private Realm mRealm;

    AlertDialog mAlertDialog;

    public static final String TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        // Set toolbar and preferences
        setSupportActionBar(mToolbar);
        mSharedPreferences = getSharedPreferences("MaydaySharedPreferences", Context.MODE_PRIVATE);
        mRealm             = Realm.getDefaultInstance();

        setCredentialsIfExists();

        // Set onClickListener to button
        mLogInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNetworkAvailable()) {
                    String email = mEmail.getText().toString();
                    String password = mPassword.getText().toString();
                    if (isValidEmail(email) && isValidPassword(password)) {
                        showLogInDialog();
                        saveSharedPreferences(email, password);
                        loginProvider(email, password);
                    } else {
                        Toast.makeText(LoginActivity.this, R.string.valid_email_and_password, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, R.string.no_network_available, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if(networkInfo != null && networkInfo.isConnected()){
            isAvailable = true;
        }
        return isAvailable;
    }

    private void showLogInDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.login_title);
        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_login, null);
        builder.setView(viewInflated);
        mAlertDialog = builder.create();
        mAlertDialog.show();
    }

    private void dismissLogInDialog(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAlertDialog.dismiss();
            }
        });
    }

    private void setCredentialsIfExists() {
        String email    = mSharedPreferences.getString("email", "");
        String password = mSharedPreferences.getString("password", "");
        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
            mEmail.setText(email);
            mPassword.setText(password);
        }
    }

    private void saveSharedPreferences(String email, String password) {
        if (mRememberSwitch.isChecked()) {
            SharedPreferences.Editor edit = mSharedPreferences.edit();
            edit.putString("email", email);
            edit.putString("password", password);
            edit.commit();
        }
    }

    private void loginProvider(String email, String password) {
        ProviderService providerService = ApiClient.getClient().create(ProviderService.class);

        Provider provider = new Provider(email, password);

        Call<Provider> call = providerService.login(provider);
        call.enqueue(new Callback<Provider>() {
            @Override
            public void onResponse(Call<Provider>call, Response<Provider> response) {
                Provider provider = response.body();
                updateProvider(provider);
                dismissLogInDialog();
                goToDashboardActivity();
            }

            @Override
            public void onFailure(Call<Provider>call, Throwable t) {
                dismissLogInDialog();
                Toast.makeText(LoginActivity.this, R.string.on_failure_login, Toast.LENGTH_LONG).show();
                Log.e(TAG, t.toString());
            }
        });
    }

    private void updateProvider(Provider provider){
        mRealm.beginTransaction();
        provider.setId(1);
        mRealm.copyToRealmOrUpdate(provider);
        mRealm.commitTransaction();
    }

    private void goToDashboardActivity(){
        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private boolean isValidEmail(String email){
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidPassword(String password){
        return password.length() > 4;
    }
}
