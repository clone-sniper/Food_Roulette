package com.example.food_roulette;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class AdvanceMenu extends AppCompatActivity {
    private Button find;
    private Spinner pSpinner;
    private EditText eRadius;
    private int radius;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.advance_menu);

        double lat = getIntent().getDoubleExtra("lat",5);
        double lng = getIntent().getDoubleExtra("lng",6);

        pSpinner = findViewById(R.id.sPrice);
        ArrayAdapter<CharSequence> pAdapter = ArrayAdapter.createFromResource(this, R.array.price_limit, android.R.layout.simple_spinner_item);
        pAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pSpinner.setAdapter(pAdapter);

        eRadius = findViewById(R.id.eRadius);

        find = findViewById(R.id.PickyFindBtn);
        find.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String filter = "";
                if(eRadius.getText().toString().isEmpty())
                    radius = 5;
                else
                    radius = Integer.parseInt(eRadius.getText().toString());
                radius *= 1609;
                filter = filter.concat("&radius=" + radius);
                int price = 1;
                switch(pSpinner.getSelectedItem().toString())
                {
                    case "$$":
                        price = 2;
                        break;
                    case "$$$":
                        price = 3;
                        break;
                    case "$$$$":
                        price = 4;
                        break;
                    default:
                        price = 1;
                        break;
                }
                filter = filter.concat("&maxprice=" + price);
                Intent intent = new Intent(AdvanceMenu.this,MapActivity.class);
                intent.putExtra("lat", lat);
                intent.putExtra("lng", lng);
                intent.putExtra("filter",filter);
                startActivity(intent);
            }
        });
    }
}
