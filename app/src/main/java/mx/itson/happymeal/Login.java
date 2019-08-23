package mx.itson.happymeal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.appcompat.widget.AppCompatButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Class Login
 *
 * Shows the form for the user to enter his credentials and log in.
 * Once the user is logged in, the main activity (Restaurants) shows up.
 *
 * Author: Gabriela Alvarez Maciel
 */
public class Login extends AppCompatActivity implements View.OnClickListener {

    // Defining view objects
    private EditText email;
    private EditText password;
    private AppCompatButton btnLogin, btnRegistrar;
    private ProgressBar progressBar;

    // Declare firebase objects
    private FirebaseDatabase database;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Referencing the views
        email = (EditText) findViewById(R.id.emailLogin);
        password = (EditText) findViewById(R.id.passwordLogin);
        btnLogin = (AppCompatButton) findViewById(R.id.btn_login);
        btnRegistrar = (AppCompatButton) findViewById(R.id.btn_registrar);
        progressBar = findViewById(R.id.progressbarlogin);

        // Attaching listener to buttons
        btnRegistrar.setOnClickListener(this);
        btnLogin.setOnClickListener(this);

        // Referencing Firebase Objects
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        // Looks for the logged in user
        FirebaseUser currentUser = auth.getCurrentUser();

        // Starts the main activity (Restaurants) if there is a user logged in
        if (currentUser != null) {
            Intent i = new Intent(Login.this, Restaurants.class);
            startActivity(i);
            finish();
        }
    }

    @Override
    public void onClick(final View v) {
        // Signs in the user if the credentials are valid
        if (v.getId() == R.id.btn_login && !isEmpty(email) && !isEmpty(password)) {
            progressBar.setVisibility(v.VISIBLE);

            final String em = email.getText().toString().trim();
            final String pw = password.getText().toString().trim();

            auth.signInWithEmailAndPassword(em, pw)
                    .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(getString(R.string.success), getString(R.string.signinSuccess));
                                //FirebaseUser user = auth.getCurrentUser();

                                // Start new Intent
                                Intent i = new Intent(Login.this, Restaurants.class);
                                startActivity(i);
                                finish();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(getString(R.string.fail), getString(R.string.signinFail), task.getException());
                                Toast.makeText(Login.this, R.string.failAuth,
                                        Toast.LENGTH_SHORT).show();
                            }

                            progressBar.setVisibility(v.INVISIBLE);
                        }
                    });

        } else if (v.getId() == R.id.btn_registrar) {
            // Starts sign up activity if the user clicks the sign up button

            Intent i = new Intent(getApplication(), SignUp.class);
            startActivity(i);
            finish();
        }
    }

    private boolean isEmpty(EditText et) {
        if (et.getText().toString().trim().length() > 0)
            return false;
        return true;
    }
}
