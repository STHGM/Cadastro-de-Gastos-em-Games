package br.utfpr.rodrigomoretto.trabalho_final;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class SobreActivity extends AppCompatActivity {

    private int cor = Color.BLUE;
    private ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sobre);
        constraintLayout = (ConstraintLayout) findViewById(R.id.layoutPrincipal);

        ActionBar barraAcao = getSupportActionBar();
        if (barraAcao != null){
            barraAcao.setDisplayHomeAsUpEnabled(true);
        }

        lerCor();

        setTitle(R.string.txt_sobre);
    }

    private void mudaCorFundo(){
        constraintLayout.setBackgroundColor(cor);
    }

    private void lerCor(){
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preferencia_cor), Context.MODE_PRIVATE);
        cor = sharedPref.getInt(getString(R.string.cor_fundo), cor);

        mudaCorFundo();
    }

    private void salvarCor(int novaCor){
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preferencia_cor), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(getString(R.string.cor_fundo), novaCor);
        editor.commit();
        cor = novaCor;
        mudaCorFundo();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        switch (cor){
            case Color.BLUE:
                menu.getItem(0).setChecked(true);
                return true;
            case Color.GREEN:
                menu.getItem(1).setChecked(true);
                return true;
            case Color.RED:
                menu.getItem(2).setChecked(true);
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_sobre, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        item.setChecked(true);
        switch (item.getItemId()){
            case R.id.corAzul:
                salvarCor(Color.BLUE);
                return true;
            case R.id.corVerde:
                salvarCor(Color.GREEN);
                return true;
            case R.id.corVermelho:
                salvarCor(Color.RED);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
