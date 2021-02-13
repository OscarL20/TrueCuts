package e.l2040.truecuts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashScreen extends AppCompatActivity {

    boolean isBarber;
    Intent myIntent;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        myIntent = new Intent(SplashScreen.this, Home.class);

        if(mAuth.getCurrentUser() != null){

            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = database.getReference().child("barbers").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        isBarber = true;
                        myIntent.putExtra("isBarber", isBarber);
                        SplashScreen.this.startActivity(myIntent);
                        finish();
                    }else{
                        DatabaseReference someDatabaseReference = database.getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        someDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                isBarber = false;
                                myIntent.putExtra("isBarber", isBarber);
                                SplashScreen.this.startActivity(myIntent);
                                finish();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }
        else{
            Intent intent = new Intent(SplashScreen.this, Login.class);
            SplashScreen.this.startActivity(intent);
            finish();
        }


    }
}
