package vola.systers.com.android.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import vola.systers.com.android.R;

public class SignUpActivity extends AppCompatActivity {

    private EditText editTextEmail,editTextPassword,editTextCnfPassword;
    private Button buttonSignup;
    private ProgressDialog progressDialog;

    //defining firebaseauth object
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //initializing firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();

        editTextEmail = (EditText) findViewById(R.id.input_email);
        editTextPassword = (EditText) findViewById(R.id.input_password);
        editTextCnfPassword = (EditText)findViewById(R.id.input_cnf_password);
        buttonSignup = (Button) findViewById(R.id.btn_signup);
        progressDialog = new ProgressDialog(this);

    }

    private void registerUser(){

        String email = editTextEmail.getText().toString().trim();
        String password  = editTextPassword.getText().toString().trim();
        String cnfPassword = editTextCnfPassword.getText().toString().trim();

        if (!isValidEmail(email)) {
            editTextEmail.setError(getText(R.string.invalid_username));
        }

        else if (!isValidPassword(password)) {
            editTextPassword.setError(getText(R.string.invalid_password));
        }

        else if (!cnfPassword.equals(password)) {
            editTextCnfPassword.setError(getText(R.string.password_mismatch));
        }

        else
        {

            progressDialog.setMessage("Registering Please Wait...");
            progressDialog.show();

        //creating a new user
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        //checking if success
                        if(task.isSuccessful()){
                            //display some message here
                            Toast.makeText(SignUpActivity.this,"Successfully registered",Toast.LENGTH_LONG).show();
                        }else{
                            //display some message here
                            Toast.makeText(SignUpActivity.this,"Registration Error",Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();

                    }
                });
        }
    }

    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    // validating password with retype password
    private boolean isValidPassword(String pass) {
        if (pass != null && pass.length() > 6) {
            return true;
        }
        return false;
    }


    public void onClick(View view) {
        registerUser();
    }

    public void Login(View view) {
        startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
        finish();
    }

    public void Skip(View view) {
        startActivity(new Intent(SignUpActivity.this, Menu.class));
        finish();
    }
}
