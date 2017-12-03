package br.utfpr.rodrigomoretto.trabalho_final;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.sql.SQLException;
import java.util.List;

import br.utfpr.rodrigomoretto.trabalho_final.models.Jogo;
import br.utfpr.rodrigomoretto.trabalho_final.models.Transacao;
import br.utfpr.rodrigomoretto.trabalho_final.persistence.DatabaseHelper;
import br.utfpr.rodrigomoretto.trabalho_final.utils.UtilsGUI;

public class PrincipaisActivity extends AppCompatActivity {

    public static final String MODO = "MODO";
    public static final String ID = "ID";
    public static final int NOVO = 1;
    public static final int ALTERAR = 2;

    private EditText edtTransacao;
    private EditText edtValor;
    private Spinner spinnerJogos;

    private List<Jogo> listaJogos;

    private int modo;
    private Transacao transacao;

    public static void nova(Activity activity, int requestCode){
        Intent intent = new Intent(activity, PrincipalActivity.class);

        intent.putExtra(MODO, NOVO);

        activity.startActivityForResult(intent, NOVO);
    }

    public static void alterar(Activity activity, int requestCode, Transacao transacao){
        Intent intent = new Intent(activity, PrincipalActivity.class);

        intent.putExtra(MODO, ALTERAR);
        intent.putExtra(ID, transacao.getId());

        activity.startActivityForResult(intent, ALTERAR);
    }

    private int posicaoJogo(Jogo jogo){
        for (int p = 0; p < listaJogos.size(); p++){
            Jogo j = listaJogos.get(p);

            if (j.getId() == jogo.getId()){
                return p;
            }
        }
        return -1;
    }

    private void popularJogos(){
        listaJogos = null;

        try{
            DatabaseHelper conexao = DatabaseHelper.getInstance(this);

            listaJogos = conexao.getJogoDao().queryBuilder().orderBy(Jogo.JOGO_NOME, true).query();
        } catch (SQLException e){
            e.printStackTrace();
        }

        ArrayAdapter<Jogo> spinnerAdapter = new ArrayAdapter<Jogo>(this, android.R.layout.simple_list_item_1, listaJogos);

        spinnerJogos.setAdapter(spinnerAdapter);
    }

    public void salvar(){
        String nomeTransacao = UtilsGUI.validaCampo(this, edtTransacao, R.string.nome_vazio);
        if (nomeTransacao == null){
            return;
        }

        String txtValor = UtilsGUI.validaCampo(this, edtValor, R.string.valor_vazio);
        if (txtValor == null){
            return;
        }

        double valor = Double.parseDouble(txtValor);

        if (valor < 0){
            UtilsGUI.avisoErro(this, R.string.valor_invalido);
            edtValor.requestFocus();
            return;
        }

        //verificar valor?

        transacao.setNome(nomeTransacao);
        transacao.setValor(valor);

        Jogo jogo = (Jogo) spinnerJogos.getSelectedItem();
        if (jogo != null){
            transacao.setJogo(jogo);
        }

        try{
            DatabaseHelper conexao = DatabaseHelper.getInstance(this);

            if(modo == NOVO){
                conexao.getTransacaoDao().create(transacao);
            } else{
                conexao.getTransacaoDao().update(transacao);
            }

            setResult(Activity.RESULT_OK);
            finish();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void cancelar(){
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principais);

        ActionBar barraAcao = getSupportActionBar();
        if (barraAcao != null){
            barraAcao.setDisplayHomeAsUpEnabled(true);
        }

        edtTransacao = (EditText) findViewById(R.id.edtTransacao);
        edtValor = (EditText) findViewById(R.id.edtValor);
        spinnerJogos = (Spinner) findViewById(R.id.spinnerJogos);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        popularJogos();

        modo = bundle.getInt(MODO);

        if (modo == ALTERAR){
            int id = bundle.getInt(ID);

            try {
                DatabaseHelper conexao = DatabaseHelper.getInstance(this);

                transacao = conexao.getTransacaoDao().queryForId(id);

                edtTransacao.setText(transacao.getNome());
                edtValor.setText(String.valueOf(transacao.getValor()));

                conexao.getJogoDao().refresh(transacao.getJogo());
            } catch (SQLException e){
                e.printStackTrace();
            }

            int pos = posicaoJogo(transacao.getJogo());
            spinnerJogos.setSelection(pos);

            setTitle(R.string.alterar_transacao);
        } else{
            transacao = new Transacao();

            setTitle(R.string.nova_transacao);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.detalhes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.menuItemSalvar:
                salvar();
                return true;
            case R.id.menuItemCancelar:
                cancelar();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
