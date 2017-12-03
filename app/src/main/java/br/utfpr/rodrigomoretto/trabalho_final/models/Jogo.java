package br.utfpr.rodrigomoretto.trabalho_final.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by STHGM on 02-Dec-17.
 */

@DatabaseTable(tableName = "Jogos")
public class Jogo {
    public static final String ID = "id";
    public static final String JOGO_NOME = "nome";

    @DatabaseField(generatedId = true, columnName = ID)
    private int id;

    @DatabaseField(canBeNull = false, unique = true, columnName = JOGO_NOME)
    private String nome;

    public Jogo(){

    }

    public Jogo(String name){
        setNome(name);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public String toString(){
        return getNome();
    }
}
