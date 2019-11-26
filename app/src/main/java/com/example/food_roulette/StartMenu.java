package com.example.food_roulette;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartMenu extends AppCompatActivity {

    private Button indecisivebtn;
    private Button pickybtn;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_menu);

        indecisivebtn =findViewById(R.id.IndecisiveBtn);
        pickybtn = findViewById(R.id.PickyBtn);

        indecisivebtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String filter = "&radius=16000&maxprice=2";
                Intent intent = new Intent(StartMenu.this,MapActivity.class);
                intent.putExtra("filter",filter);
                startActivity(intent);
            }
        }
        );

        pickybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartMenu.this, AdvanceMenu.class));
            }
        });
    }


}
