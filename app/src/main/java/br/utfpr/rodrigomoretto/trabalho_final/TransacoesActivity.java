package br.utfpr.rodrigomoretto.trabalho_final;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.sql.SQLException;
import java.util.List;

import br.utfpr.rodrigomoretto.trabalho_final.models.Transacao;
import br.utfpr.rodrigomoretto.trabalho_final.persistence.DatabaseHelper;
import br.utfpr.rodrigomoretto.trabalho_final.utils.UtilsGUI;


public class TransacoesActivity extends AppCompatActivity {

    private ListView lvTransactions;
    private ArrayAdapter<Transacao> listaAdapter;

    private static final int REQUISICAO_NOVA_TRANSACAO = 1;
    private static final int REQUISICAO_ALTERAR_TRANSACAO = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listas);
        lvTransactions = (ListView) findViewById(R.id.lvItens);

        lvTransactions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int posicao, long l) {
                Transacao transacao = (Transacao) adapterView.getItemAtPosition(posicao);
                TransacaoActivity.alterar(TransacoesActivity.this, REQUISICAO_ALTERAR_TRANSACAO, transacao);
            }
        });
        lvTransactions.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        lvTransactions.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                boolean selecionado = lvTransactions.isItemChecked(position);

                View view = lvTransactions.getChildAt(position);

                if (selecionado){
                    view.setBackgroundColor(Color.LTGRAY);
                } else{
                    view.setBackgroundColor(Color.TRANSPARENT);
                }

                int total = lvTransactions.getCheckedItemCount();

                if (total > 0){
                    mode.setTitle(getResources().getQuantityString(R.plurals.selecionado, total, total));
                }
                mode.invalidate();
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.menu_acao, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                if (lvTransactions.getCheckedItemCount() > 1){
                    menu.getItem(0).setVisible(false);
                } else{
                    menu.getItem(0).setVisible(true);
                }
                return true;
            }

            @Override
            public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menuAcaoAlterar:
                        for (int posicao = lvTransactions.getChildCount(); posicao >= 0; posicao--){
                            if (lvTransactions.isItemChecked(posicao)){
                                Transacao transacao = (Transacao) lvTransactions.getItemAtPosition(posicao);
                                TransacaoActivity.alterar(TransacoesActivity.this, REQUISICAO_ALTERAR_TRANSACAO, transacao);
                                break;
                            }
                        }
                        mode.finish();
                        return true;

                    case R.id.menuAcaoDeletar:
                        String mensagem = getString(R.string.deseja_apagar);
                        DialogInterface.OnClickListener listener =
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch(which){
                                            case DialogInterface.BUTTON_POSITIVE:

                                                try {
                                                    DatabaseHelper conexao = DatabaseHelper.getInstance(TransacoesActivity.this);
                                                    for (int posicao = lvTransactions.getChildCount(); posicao >= 0; posicao--){
                                                        if (lvTransactions.isItemChecked(posicao)){
                                                            Transacao transacao = (Transacao) lvTransactions.getItemAtPosition(posicao);
                                                            conexao.getTransacaoDao().delete(transacao);
                                                            listaAdapter.remove(transacao);
                                                            //break;
                                                        }
                                                    }
                                                    mode.finish();

                                                } catch (SQLException e) {
                                                    e.printStackTrace();
                                                }
                                                break;
                                            case DialogInterface.BUTTON_NEGATIVE:
                                                break;
                                        }
                                    }
                                };

                        UtilsGUI.confirma(TransacoesActivity.this, mensagem, listener);
                        return true;

                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                for (int posicao = 0; posicao < lvTransactions.getChildCount(); posicao++){
                    View view = lvTransactions.getChildAt(posicao);
                    view.setBackgroundColor(Color.TRANSPARENT);
                }
            }
        });

        popularLista();

        registerForContextMenu(lvTransactions);
    }

    private void popularLista(){

        List<Transacao> listaJogos = null;

        try {
            DatabaseHelper conexao = DatabaseHelper.getInstance(this);

            listaJogos = conexao.getTransacaoDao().queryBuilder().orderBy(Transacao.ID_JOGO, true).query();

            for (Transacao t : listaJogos){
                conexao.getJogoDao().refresh(t.getJogo());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        listaAdapter = new ArrayAdapter<Transacao>(this, android.R.layout.simple_list_item_1, listaJogos);

        lvTransactions.setAdapter(listaAdapter);
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
                TransacaoActivity.nova(this, REQUISICAO_NOVA_TRANSACAO);
                return true;

            case R.id.menuItemJogos:
                JogosActivity.abrir(this);
                return true;

            case R.id.menuItemSobre:
                Intent intent = new Intent(this, SobreActivity.class);
                this.startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
