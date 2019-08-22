package mx.itson.happymeal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import mx.itson.happymeal.Model.Users;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class SignUp extends AppCompatActivity implements View.OnClickListener {
    // Defining view objects
    private EditText password, name, email;
    private AppCompatButton btnRegistrar, btnMenu;
    private ProgressBar progressBar;

    // Declare firebase objects
    private FirebaseDatabase database;
    private DatabaseReference usuariosDB;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //Referencing the views
        email = (EditText) findViewById(R.id.emailSignUp);
        password = (EditText) findViewById(R.id.passwordSignup);
        name = (EditText) findViewById(R.id.nameSignup);

        btnRegistrar = (AppCompatButton) findViewById(R.id.btn_signup);
        btnMenu = (AppCompatButton) findViewById(R.id.btn_backLogin);

        progressBar = findViewById(R.id.progressbarSignup);

        //attaching listener to button
        btnRegistrar.setOnClickListener(this);
        btnMenu.setOnClickListener(this);

        database = FirebaseDatabase.getInstance();
        usuariosDB = database.getReference("users");
        auth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = auth.getCurrentUser();
        // Siguiente pantalla (LOGGED IN)
        if (currentUser != null) {
            Intent i = new Intent(SignUp.this, Restaurants.class);
            startActivity(i);
            finish();
        }
    }

    @Override
    public void onClick(final View v) {
        final String txtEmail = email.getText().toString().trim();
        String txtPwd = password.getText().toString().trim();

        if(v.getId()==R.id.btn_signup && !isEmpty(email) && !isEmpty(name) && !isEmpty(password)){
            progressBar.setVisibility(v.VISIBLE);

            auth.createUserWithEmailAndPassword(txtEmail, txtPwd).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Log.d("SUCCESS", "createUserWithEmail:success");

                        if(auth.getCurrentUser() != null) {
                            Users us = new Users(name.getText().toString(), email.getText().toString());
                            usuariosDB.child(auth.getCurrentUser().getUid()).setValue(us);
                            Log.i("USER REGISTER: ", "El usuario se ha registrado");

                            Toast.makeText(SignUp.this, "Usuario registrado con éxito", Toast.LENGTH_SHORT).show();

                            // Start new Intent
                            Intent i = new Intent(SignUp.this, Restaurants.class);
                            startActivity(i);
                            finish();
                        }

                       /* auth.signInWithEmailAndPassword(txtEmail, txtPwd).addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Log.d("SUCCESS", "createUserWithEmail:success");

                                    // Start new Intent
                                    Intent i = new Intent(SignUp.this, Restaurants.class);
                                    startActivity(i);
                                    finish();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w("FAILED", "signInWithEmail:failure", task.getException());
                                    Toast.makeText(SignUp.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        }); */


                    } else {
                        Log.w("FAILED", "createUserWithEmail:failure", task.getException());
                        Toast.makeText(SignUp.this, "Ocurrió un problema al registrar usuario.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            if(auth.getCurrentUser() != null) {
                Users us = new Users(name.getText().toString(), email.getText().toString());
                usuariosDB.child(auth.getCurrentUser().getUid()).setValue(us);
                Log.i("USER REGISTER: ", "El usuario se ha registrado");

                Toast.makeText(SignUp.this, "Usuario registrado con éxito", Toast.LENGTH_SHORT).show();

                // Start new Intent
                Intent i = new Intent(SignUp.this, Restaurants.class);
                startActivity(i);
                finish();
                /*
                usuariosDB.addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Users us = new Users(name.getText().toString(), email.getText().toString());
                        usuariosDB.child(auth.getCurrentUser().getUid()).setValue(us);
                        Log.i("USER REGISTER: ", "El usuario se ha registrado");

                        Toast.makeText(SignUp.this, "Usuario registrado con éxito", Toast.LENGTH_SHORT).show();

                        // Start new Intent
                        Intent i = new Intent(SignUp.this, Restaurants.class);
                        startActivity(i);
                        finish();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });*/
            }

        } else if(v.getId()== R.id.btn_backLogin){
            Intent i=new Intent(getApplication(), Login.class);
            this.finish();
            startActivity(i);
        }
    }

    private boolean isEmpty(EditText et) {
        if (et.getText().toString().trim().length() > 0)
            return false;
        return true;
    }
}
