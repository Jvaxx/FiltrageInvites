package com.jvaax.filtrageinvites;

import android.content.Context;
import android.content.SharedPreferences;

public class Person {
    // La classe Person représente une personne avec un nom, un prénom, une catégorie et une présence
    // Elle permet de sauvegarder et de charger la présence de la personne dans les données enregistrées
    // Permet aussi de sauvegarder l'image de la personne sous forme de tableau de bytes
    private final String name;
    private final String surname;
    private final String category;
    private boolean isPresent;
    private byte[] imageBlob;

    public Person(String name, String surname, String category) {
        this.name = name;
        this.surname = surname;
        this.category = category;
        this.isPresent = false;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getCategory() {
        return category;
    }

    public boolean isPresent() {
        return isPresent;
    }

    public void toggleIsPresent() {
        this.isPresent = !this.isPresent;
    }

    public byte[] getImageBlob() {
        return imageBlob;
    }

    public void setImageBlob(byte[] imageBlob) {
        this.imageBlob = imageBlob;
    }

    public void saveIsPresent(Context context) {
        // Sauvegarde la présence de la personne dans les données partagées entre les activités
        SharedPreferences sharedPreferences = context.getSharedPreferences("PersonPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // La sauvegarde se fait dans un dictionnaire ayant pour clé le nom et prénom de la personne. Ainsi, chaque personne a sa propre clé
        editor.putBoolean(name + "_" + surname + "_isPresent", isPresent);
        editor.apply();
    }

    public void loadIsPresent(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("PersonPrefs", Context.MODE_PRIVATE);
        this.isPresent = sharedPreferences.getBoolean(name + "_" + surname + "_isPresent", false);
    }
}