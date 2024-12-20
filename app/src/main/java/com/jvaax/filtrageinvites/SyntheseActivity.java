package com.jvaax.filtrageinvites;

import android.content.res.Resources;
import java.io.InputStream;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;



public class SyntheseActivity extends AppCompatActivity {

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
    private List<Person> personList;
    private TextView category1CountView, category2CountView, category3CountView, category4CountView, totalCountView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Création de l'activité
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_synthese);

        // Récupération des vues
        category1CountView = findViewById(R.id.category1_count);
        category2CountView = findViewById(R.id.category2_count);
        category3CountView = findViewById(R.id.category3_count);
        category4CountView = findViewById(R.id.category4_count);
        totalCountView = findViewById(R.id.total_count);

        // Initialisation de la liste des personnes
        personList = new ArrayList<>();
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
                ResultSet resultSet = statement.executeQuery("SELECT nom, prenom, nomcategorie FROM invites");

                // Remplissage de la liste des personnes avec les données récupérées
                while (resultSet.next()) {
                    String name = resultSet.getString("nom");
                    String surname = resultSet.getString("prenom");
                    String category = resultSet.getString("nomcategorie");
                    personList.add(new Person(name, surname, category));
                }

                // Mise à jour de l'interface graphique
                runOnUiThread(this::updateCounts);

                // Log pour debugger
                Log.i("SyntheseActivity", "connection SSH étbalie avec succès");

            } catch (Exception e) {
                Log.e("SyntheseActivity", "connection SSH échouée", e);
            } finally {
                if (session != null) {
                    session.disconnect();
                }
            }
        }).start();
    }

    private void updateCounts() {
        // Initialisation des compteurs
        int category1Present = 0;
        int category2Present = 0;
        int category3Present = 0;
        int category4Present = 0;

        // Parcours de la liste des personnes pour compter les présents
        for (Person person : personList) {
            person.loadIsPresent(this);
            if (person.isPresent()) {
                switch (person.getCategory()) {
                    case "Bleue":
                        category1Present++;
                        break;
                    case "Rouge":
                        category2Present++;
                        break;
                    case "Argent":
                        category3Present++;
                        break;
                    case "Or":
                        category4Present++;
                        break;
                }
            }
        }

        // Mise à jour des vues avec les compteurs
        category1CountView.setText(String.valueOf(category1Present));
        category2CountView.setText(String.valueOf(category2Present));
        category3CountView.setText(String.valueOf(category3Present));
        category4CountView.setText(String.valueOf(category4Present));
        totalCountView.setText(String.valueOf(category1Present + category2Present + category3Present + category4Present));
    }


}

