package br.utfpr.rodrigomoretto.trabalho_final.utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.sql.SQLException;
import java.util.List;

import br.utfpr.rodrigomoretto.trabalho_final.R;
import br.utfpr.rodrigomoretto.trabalho_final.models.Transacao;
import br.utfpr.rodrigomoretto.trabalho_final.persistence.DatabaseHelper;

public class PrincipalActivity extends AppCompatActivity {
    private ListView lvTransactions;
    private ArrayAdapter<Transacao> listaAdapter;

    private static final int REQUISICAO_NOVA_TRANSACAO = 1;
    private static final int REQUISICAO_ALTERAR_TRANSACAO = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        lvTransactions = (ListView) findViewById(R.id.lvTransactions);

        lvTransactions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int posicao, long l) {

                Transacao transacao = (Transacao) adapterView.getItemAtPosition(posicao);

                PrincipaisActivity.alterar(PrincipalActivity.this, REQUISICAO_ALTERAR_TRANSACAO, transacao);

            }
        });
        popularLista();

        registerForContextMenu(lvTransactions);
    }

    private void popularLista(){

        List<Transacao> listaJogos = null;

        try {
            DatabaseHelper conexao = DatabaseHelper.getInstance(this);

            listaJogos = conexao.getTransacaoDao().queryBuilder().orderBy(Transacao.TRANSACAO_NOME, true).query();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        listaAdapter = new ArrayAdapter<Transacao>(this, android.R.layout.simple_list_item_1, listaJogos);

        lvTransactions.setAdapter(listaAdapter);
    }

    private void excluirTransacao(final Transacao transacao){

        String mensagem = getString(R.string.deseja_apagar)
                + "\n" + transacao.getNome();

        DialogInterface.OnClickListener listener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch(which){
                            case DialogInterface.BUTTON_POSITIVE:

                                try {
                                    DatabaseHelper conexao =
                                            DatabaseHelper.getInstance(PrincipalActivity.this);

                                    conexao.getTransacaoDao().delete(transacao);

                                    listaAdapter.remove(transacao);

                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }

                                break;
                            case DialogInterface.BUTTON_NEGATIVE:

                                break;
                        }
                    }
                };

        UtilsGUI.confirma(this, mensagem, listener);
    }

    @Override
    protected void onActivityResult(int requisicaoCode, int resultCode, Intent data) {

        if ((requisicaoCode == REQUISICAO_NOVA_TRANSACAO || requisicaoCode == REQUISICAO_ALTERAR_TRANSACAO)
                && resultCode == Activity.RESULT_OK){

            popularLista();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.lista_transacoes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){

            case R.id.menuItemNovo:
                PrincipaisActivity.nova(this, REQUISICAO_NOVA_TRANSACAO);
                return true;

            case R.id.menuItemJogos:
                JogosActivity.abrir(this);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        getMenuInflater().inflate(R.menu.item_selecionado, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info;

        info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        Transacao transacao = (Transacao) lvTransactions.getItemAtPosition(info.position);

        switch(item.getItemId()){

            case R.id.menuItemExibir:
                PrincipaisActivity.alterar(this,
                        REQUISICAO_NOVA_TRANSACAO,
                        transacao);
                return true;

            case R.id.menuItemDeletar:
                excluirTransacao(transacao);
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }
}
