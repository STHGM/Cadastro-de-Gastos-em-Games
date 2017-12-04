package br.utfpr.rodrigomoretto.trabalho_final.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by STHGM on 02-Dec-17.
 */

@DatabaseTable(tableName = "transacoes")
public class Transacao {
    public static final String ID = "id";
    public static final String TRANSACAO_NOME = "nome";
    public static final String VALOR = "valor";
    public static final String ID_JOGO = "jogo_id";

    @DatabaseField(generatedId = true, columnName = ID)
    private int id;

    @DatabaseField(canBeNull = false, columnName = TRANSACAO_NOME)
    private String nome;

    @DatabaseField(canBeNull = false, columnName = VALOR)
    private double valor;

    @DatabaseField(foreign = true)
    private Jogo jogo;


    public Transacao(){

    }

    public Transacao(String nome){
        setNome(nome);
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

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public Jogo getJogo() {
        return jogo;
    }

    public void setJogo(Jogo jogo) {
        this.jogo = jogo;
    }


    @Override
    public String toString(){
        return getNome() + " - " + getJogo().toString() + " - R$ " + getValor();
    }
}
