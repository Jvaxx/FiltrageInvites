package com.jvaax.filtrageinvites;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.PersonViewHolder> {
    // Classe permettant de gérer l'affichage des données dans la liste des personnes
    // C'est l'interace de communication entre les donnéees de type Person (de la classe Person) et
    // le recyclerView (la liste des personnes)
    private final List<Person> personList;
    private final Context context;

    public PersonAdapter(Context context, List<Person> personList) {
        // Le contexte est nécessaire pour accéder aux ressources de l'application
        // (les infos relatives à la présence des personnes)
        this.context = context;
        this.personList = personList;
    }

    @NonNull
    @Override
    public PersonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_person, parent, false);
        return new PersonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PersonViewHolder holder, int position) {
        // Permet de mettre à jour les données de la vue (item_person) en fonction de la position
        Person person = personList.get(position);
        person.loadIsPresent(context);
        holder.name.setText(person.getName());
        holder.surname.setText(person.getSurname());
        holder.category.setText(person.getCategory());
        holder.isPresent.setText(person.isPresent() ? "Oui" : "Non");

        // Coloration des éléments de la liste en fonction de la catégorie de la personne
        int color;
        switch (person.getCategory()) {
            case "Bleue":
                color = ContextCompat.getColor(context, R.color.category_bleue);
                break;
            case "Rouge":
                color = ContextCompat.getColor(context, R.color.category_rouge);
                break;
            case "Argent":
                color = ContextCompat.getColor(context, R.color.category_argent);
                break;
            case "Or":
                color = ContextCompat.getColor(context, R.color.category_or);
                break;
            default:
                color = ContextCompat.getColor(context, android.R.color.transparent);
                break;
        }
        holder.name.setTextColor(color);
        holder.surname.setTextColor(color);
        holder.category.setTextColor(color);
        holder.isPresent.setTextColor(color);

        // Ajout d'un listener pour ouvrir la page de détail de la personne
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PersonDetailActivity.class);
            intent.putExtra("name", person.getName());
            intent.putExtra("surname", person.getSurname());
            intent.putExtra("category", person.getCategory());
            intent.putExtra("isPresent", person.isPresent());
            intent.putExtra("imageBlob", person.getImageBlob());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return personList.size();
    }

    public static class PersonViewHolder extends RecyclerView.ViewHolder {
        TextView name, surname, category, isPresent;

        public PersonViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            surname = itemView.findViewById(R.id.surname);
            category = itemView.findViewById(R.id.category);
            isPresent = itemView.findViewById(R.id.is_present);
        }
    }
}
