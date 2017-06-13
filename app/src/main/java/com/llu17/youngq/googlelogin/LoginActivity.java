package com.llu17.youngq.googlelogin;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.llu17.youngq.googlelogin.MainActivity.mGoogleApiClient;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener{

    private static final int REQ_CODE = 9001;
    private SignInButton SignIn;
    private TextView TV_Error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SignIn = (SignInButton)findViewById(R.id.Log_In_Button);
        TV_Error = (TextView)findViewById(R.id.TV_Error);
        SignIn.setOnClickListener(this);

        TV_Error.setVisibility(View.GONE);

        //Log in
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestScopes(new Scope(Scopes.PLUS_LOGIN))
//                .requestScopes(new Scope(Scopes.PLUS_ME))
//                .requestIdToken(LoginActivity.this.getResources().getString(R.string.server_client_id))
                .requestEmail()
                .build();
        Log.e("hello: ","111111111111");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
//                .addOnConnectionFailedListener(this)
                .build();
        Log.e("hello: ","2222222222222");
    }
    private void signIn() {
        TV_Error.setVisibility(View.GONE);
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, REQ_CODE);
    }
    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // ...
                    }
                });
    }
    private void revokeAccess() {
        Log.e("hello","revokeAccess");
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // ...
                    }
                });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == REQ_CODE) {
            Log.e("hello: ","3333333333333");
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            Log.e("hello: ","44444444444444");
            handleSignInResult(result);
            Log.e("hello: ","55555555555555");
        }
    }

    private void handleSignInResult(GoogleSignInResult result ){
        if(result.isSuccess()){
            GoogleSignInAccount account = result.getSignInAccount();
            String email = account.getEmail();
            Pattern emailPattern = Pattern.compile("\\w+([-+.]\\w+)*@binghamton\\.edu");
            Matcher matcher = emailPattern.matcher(email);
            if(matcher.find()){
                revokeAccess();
                signOut();
                this.finish();
            }
            else{
                revokeAccess();
                signOut();
                TV_Error.setVisibility(View.VISIBLE);
            }
        }
        else {
            Log.e("Login failed","Failed: " + result.getStatus().getStatusMessage() + " code: " + result.getStatus().getStatusCode());
            Toast.makeText(getBaseContext(), "Failed: " + result.getStatus().getStatusMessage() + " code: " + result.getStatus().getStatusCode(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Log_In_Button:
                signIn();
                break;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getBaseContext(), "onConnectionFailed", Toast.LENGTH_LONG).show();
    }
    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }
}
