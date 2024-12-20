package com.jvaax.filtrageinvites;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Creation de l'activité
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Récupération des boutons
        Button button_synthese = findViewById(R.id.button_synthese);
        Button button_recherche = findViewById(R.id.button_recherche);

        // Ajout des listeners (événements) sur les boutons
        button_synthese.setOnClickListener(v -> {
            // Création d'une nouvelle activité en passant l'intent
            // (permet de passer des données entre les activités)
            Intent intent = new Intent(MainActivity.this, SyntheseActivity.class);
            startActivity(intent);
        });

        button_recherche.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RechercheActivity.class);
            startActivity(intent);
        });
    }
}