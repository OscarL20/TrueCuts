package e.l2040.truecuts;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class BarberOrNot extends AppCompatActivity {


    Button yes;
    Button no;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barber_or_not);

        yes = findViewById(R.id.yes);
        no = findViewById(R.id.no);
    }


    public void onYes(View view){
        Intent myIntent = new Intent(BarberOrNot.this, CreateAccount.class);
        myIntent.putExtra("barberOrNot", true);
        BarberOrNot.this.startActivity(myIntent);
    }


    public void onNo(View view){
        Intent myIntent = new Intent(BarberOrNot.this, CreateAccount.class);
        myIntent.putExtra("barberOrNot", false);
        BarberOrNot.this.startActivity(myIntent);
    }


}
