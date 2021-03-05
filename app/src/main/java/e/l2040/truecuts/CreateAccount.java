package e.l2040.truecuts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateAccount extends AppCompatActivity {

    private FirebaseAuth mAuth;
    EditText username;
    EditText email;
    EditText password;
    EditText confirmPassword;
    Button createAccount;
    Intent intentInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        mAuth = FirebaseAuth.getInstance();

        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);
        createAccount = findViewById(R.id.createAccount);

        intentInfo = getIntent();

    }



    public void createAccount(View view){
        if (password.getText().toString().equals(confirmPassword.getText().toString())) {

            mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                //Add user to database also add true or false in BarberOrNot section of database

                                boolean barberOrNot = intentInfo.getBooleanExtra("barberOrNot", false);
                                if (barberOrNot) {
                                    Barber barber = new Barber(email.getText().toString(), true, username.getText().toString(), task.getResult().getUser().getUid());
                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    DatabaseReference databaseReference = database.getReference().child("barbers").child(task.getResult().getUser().getUid());
                                    databaseReference.setValue(barber);

                                    Intent myIntent = new Intent(CreateAccount.this, Home.class);
                                    myIntent.putExtra("isBarber", true);
                                    CreateAccount.this.startActivity(myIntent);
                                } else {
                                    User user = new User(email.getText().toString(), false, username.getText().toString());
                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    DatabaseReference databaseReference = database.getReference().child("users").child(task.getResult().getUser().getUid());
                                    databaseReference.setValue(user);

                                    Intent myIntent = new Intent(CreateAccount.this, Home.class);
                                    myIntent.putExtra("isBarber", false);
                                    CreateAccount.this.startActivity(myIntent);
                                }


                            } else {
                                Toast.makeText(CreateAccount.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
        }else {
            Toast.makeText(this, "Passwords must be the same", Toast.LENGTH_SHORT).show();
        }
    }
}
