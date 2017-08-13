package vola.systers.com.volunteers_android.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import org.json.JSONObject;
import java.util.Arrays;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import vola.systers.com.volunteers_android.R;

 /*
  * User can SignIn manually or through social in this activity.
  */

public class SignInActivity extends AppCompatActivity implements
        View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_GOOGLE_SIGN_IN = 007;
    private static final int RC_FACEBOOK_SIGN_IN=64206;
    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;
    CallbackManager callbackManager;
    private static final String TAG = SignInActivity.class.getSimpleName();

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private EditText emailText,passwordText;
    private Button login;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        LoginButton btnFacebookLogin=(LoginButton) findViewById(R.id.btn_fb_sign_in) ;
        Button btnGoogleLogin = (Button) findViewById(R.id.btn_sign_in_google);
        TextView skipLink = (TextView)findViewById(R.id.link_skip);
        btnGoogleLogin.setOnClickListener(this);
        skipLink.setOnClickListener(this);

        emailText= (EditText)findViewById(R.id.input_email) ;
        passwordText= (EditText)findViewById(R.id.input_password);

        login =(Button)findViewById(R.id.btn_login) ;
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signin(emailText,passwordText);
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        callbackManager = CallbackManager.Factory.create();
        btnFacebookLogin.setReadPermissions(Arrays.asList("public_profile", "email"));
        btnFacebookLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.v("Response", response.toString());
                          }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields","id,name,email,gender,birthday");
                request.setParameters(parameters);
                request.executeAsync();
                Intent intent = new Intent(SignInActivity.this,MenuActivity.class);
                startActivity(intent);
            }

            @Override
            public void onCancel() {
                Toast.makeText(SignInActivity.this,R.string.cancelled_request,Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(SignInActivity.this,R.string.error,Toast.LENGTH_LONG).show();
            }
        });
    }

    public void signin(EditText email,EditText password) {

        Log.d(TAG, "SignIn");
        final ProgressDialog progressDialog = new ProgressDialog(SignInActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                // If sign in fails, display a message to the user. If sign in succeeds
                // the auth state listener will be notified and logic to handle the
                // signed in user can be handled in the listener.

                if (!task.isSuccessful()) {
                    Log.w(TAG, "signInWithEmail:failed", task.getException());
                    Toast.makeText(SignInActivity.this, R.string.auth_failed,Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(SignInActivity.this, R.string.auth_success,Toast.LENGTH_SHORT).show();
                }
                }
            });

        new android.os.Handler().postDelayed(
            new Runnable() {
                public void run() {
                    progressDialog.dismiss();
                }
            }, 3000);
    }


    private void googleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
    }

    public void onSkipClicked() {
        Intent intent = new Intent(SignInActivity.this,MenuActivity.class);
        startActivity(intent);
    }

    private void handleGoogleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully.
            Toast.makeText(this,R.string.sign_in_success,Toast.LENGTH_LONG).show();
            GoogleSignInAccount acct = result.getSignInAccount();

            Log.i(TAG, "display name: " + acct.getDisplayName());

            String personName = acct.getDisplayName();
            String personPhotoUrl;
            if(acct.getPhotoUrl()==null)
            {
                personPhotoUrl = null;
            }
            else
            {
                 personPhotoUrl = acct.getPhotoUrl().toString();
            }

            String email = acct.getEmail();

            Log.i(TAG, "Name: " + personName + ", email: " + email
                    + ", Image: " +personPhotoUrl);

            Intent intent = new Intent(SignInActivity.this,MenuActivity.class);
            startActivity(intent);

        } else {
            // UnAuthenticated.
            Toast.makeText(this,R.string.sign_in_failure,Toast.LENGTH_LONG).show();
        }

    }


    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {

            case R.id.btn_sign_in_google:
                googleSignIn();
                break;
            case R.id.link_skip:
                onSkipClicked();

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            Log.d(TAG, "signin");
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleGoogleSignInResult(result);
        }
        else if( requestCode == RC_FACEBOOK_SIGN_IN){
            super.onActivityResult(requestCode, resultCode, data);
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleGoogleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.

            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleGoogleSignInResult(googleSignInResult);
                }
            });
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }
}