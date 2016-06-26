package com.typingeek.com.myappportfolio;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button movies = (Button) findViewById(R.id.movies);
        Button hawk = (Button) findViewById(R.id.hawk);
        Button bigger = (Button) findViewById(R.id.bigger);
        Button capstone = (Button) findViewById(R.id.capstone);
        Button material = (Button) findViewById(R.id.material);
        Button ubiquitous = (Button) findViewById(R.id.ubiquitous);

        movies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), R.string.pm_toast, Toast.LENGTH_SHORT).show();
            }
        });

        hawk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), R.string.sh_toast, Toast.LENGTH_SHORT).show();

            }
        });

        bigger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), R.string.bib_toast, Toast.LENGTH_SHORT).show();

            }
        });

        capstone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), R.string.c_toast, Toast.LENGTH_SHORT).show();

            }
        });

        material.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), R.string.myam_toast, Toast.LENGTH_SHORT).show();

            }
        });

        ubiquitous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), R.string.gu_toast, Toast.LENGTH_SHORT).show();

            }
        });
        }
}
