package br.utfpr.rodrigomoretto.trabalho_final.utils;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import br.utfpr.rodrigomoretto.trabalho_final.R;
import br.utfpr.rodrigomoretto.trabalho_final.models.Transacao;

public class PrincipalActivity extends AppCompatActivity {
    private ListView lvTransactions;
    private ArrayAdapter<Transacao> listaAdapter;

    private static final int REQUISICAO_NOVA_TRANSACAO = 1;
    private static final int REQUISICAO_ALTERAR_PESSOA = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        lvTransactions = (ListView) findViewById(R.id.lvTransactions);

    }
}
