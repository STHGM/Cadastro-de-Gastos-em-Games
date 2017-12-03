package br.utfpr.rodrigomoretto.trabalho_final;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SobreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sobre);

        ActionBar barraAcao = getSupportActionBar();
        if (barraAcao != null){
            barraAcao.setDisplayHomeAsUpEnabled(true);
        }

        setTitle(R.string.txt_sobre);
    }
}
