package com.jvaax.filtrageinvites;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class RechercheActivity extends AppCompatActivity {

    // Chargement des propriétés depuis le fichier de configuration
    private Properties loadProperties() {
        Properties properties = new Properties();
        try {
            Resources resources = getResources();
            InputStream rawResource = resources.openRawResource(R.raw.config);
            properties.load(rawResource);
        } catch (Exception e) {
            Log.e("SyntheseActivity", "Error loading properties", e);
        }
        return properties;
    }

    // Déclaration des variables
    private RecyclerView recyclerView;
    private PersonAdapter personAdapter;
    private List<Person> personList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recherche);

        // Récupération de la vue RecyclerView
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialisation de la liste des personnes
        personList = new ArrayList<>();
        // Création de l'adapter pour la liste des personnes (cela permet de gérer l'affichage des données)
        personAdapter = new PersonAdapter(this, personList);
        recyclerView.setAdapter(personAdapter);
    }

    @Override
    protected void onResume() {
        // Appel de la méthode a chaque fois que l'activité est affichée
        super.onResume();

        // Chargement des données des personnes
        loadPersonData();
    }

    private void loadPersonData() {
        // Création d'un nouveau thread pour la connexion à la base de donée.
        // Cela permet de ne pas bloquer l'interface graphique.
        // On se connecte à la base de données distante via un tunnel SSH
        // (car alwaysdata ne permet pas de connexion directe)
        new Thread(() -> {
            Session session = null;
            Connection connection = null;
            try {
                // Chargement des propriétés depuis le fichier de configuration
                Properties properties = loadProperties();
                String sshHost = properties.getProperty("ssh_host");
                String sshUser = properties.getProperty("ssh_user");
                String sshPassword = properties.getProperty("ssh_password");
                String dbHost = properties.getProperty("db_host");
                String dbName = properties.getProperty("db_name");
                String dbUser = properties.getProperty("db_user");
                String dbPassword = properties.getProperty("db_password");

                personList.clear();

                // Creation du tunnel SSH
                // (connexion à alwaysdata via SSH et redirection du port 3306 de la base de données)
                // On utilise la librairie JSch pour cela
                JSch jsch = new JSch();
                session = jsch.getSession(sshUser, sshHost, 22);
                session.setPassword(sshPassword);

                Properties config = new Properties();
                config.put("StrictHostKeyChecking", "no");
                session.setConfig(config);
                session.connect();

                int assignedPort = session.setPortForwardingL(0, dbHost, 3306);

                // Connexion à la base de données
                Class.forName("org.mariadb.jdbc.Driver");
                connection = DriverManager.getConnection(
                        "jdbc:mariadb://localhost:" + assignedPort + "/" + dbName,
                        dbUser,
                        dbPassword
                );

                // Création d'une requête pour exécuter des requêtes SQL
                Statement statement = connection.createStatement();
                // Exécution de la requête pour récupérer les invités
                ResultSet resultSet = statement.executeQuery("SELECT nom, prenom, nomcategorie, photo FROM invites");


                // Remplissage de la liste des personnes avec les données récupérées
                while (resultSet.next()) {
                    String name = resultSet.getString("nom");
                    String surname = resultSet.getString("prenom");
                    String category = resultSet.getString("nomcategorie");
                    String imageBlobString = resultSet.getString("photo");
                    // Correction de l'image si elle n'est pas correctement encodée (il manque le /9j/ au début)
                    if (imageBlobString != null && !imageBlobString.startsWith("data:image/jpeg;base64,/9j/")) {
                        imageBlobString = "/9j/" + imageBlobString;
                    }
                    // Conversion de l'image en tableau de bytes
                    byte[] imageBlob = imageBlobString != null ? imageBlobString.getBytes() : null;
                    Person person = new Person(name, surname, category);
                    person.setImageBlob(imageBlob);
                    personList.add(person);
                }

                // Mise à jour de l'interface graphique
                runOnUiThread(() -> personAdapter.notifyDataSetChanged());


                Log.i("RechercheActivity", "connection SSH établie avec succès");

            } catch (Exception e) {
                Log.e("RechercheActivity", "connection SSH échouée", e);
            } finally {
                if (session != null) {
                    session.disconnect();
                }
            }
        }).start();
    }
}