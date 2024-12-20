package com.jvaax.filtrageinvites;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class PersonDetailActivity extends AppCompatActivity {
    // Activité permettant d'afficher les détails d'une personne
    private Person person;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_detail);

        // Récupération des éléments de la vue
        TextView nameTextView = findViewById(R.id.name);
        TextView surnameTextView = findViewById(R.id.surname);
        TextView categoryTextView = findViewById(R.id.category);
        TextView isPresentTextView = findViewById(R.id.is_present);
        Button toggleIsPresentButton = findViewById(R.id.toggle_is_present);
        ImageView imageView = findViewById(R.id.image);

        // Récupération des données passées par l'intent
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String surname = intent.getStringExtra("surname");
        String category = intent.getStringExtra("category");
        boolean isPresent = intent.getBooleanExtra("isPresent", false);
        byte[] imageBlob = intent.getByteArrayExtra("imageBlob");

        // Création de l'objet Person
        person = new Person(name, surname, category);
        person.loadIsPresent(this);
        person.setImageBlob(imageBlob);

        // Mise à jour des éléments de la vue
        nameTextView.setText(name);
        surnameTextView.setText(surname);
        categoryTextView.setText(category);
        isPresentTextView.setText(isPresent ? "Oui" : "Non");

        // Decode Base64 string to byte array and convert to Bitmap
        if (imageBlob != null) {
            try {
                String imageBlobString = new String(imageBlob);
                byte[] decodedString = Base64.decode(imageBlobString, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                imageView.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
                // Gérer l'erreur potentiellement
            }
        }

        // Ajout d'un listener pour le bouton de présence
        toggleIsPresentButton.setOnClickListener(v -> {
            person.toggleIsPresent();
            person.saveIsPresent(this);
            isPresentTextView.setText(person.isPresent() ? "Oui" : "Non");
        });
    }
}