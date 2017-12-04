package br.utfpr.rodrigomoretto.trabalho_final;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
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

import br.utfpr.rodrigomoretto.trabalho_final.models.Jogo;
import br.utfpr.rodrigomoretto.trabalho_final.models.Transacao;
import br.utfpr.rodrigomoretto.trabalho_final.persistence.DatabaseHelper;
import br.utfpr.rodrigomoretto.trabalho_final.utils.UtilsGUI;

public class JogosActivity extends AppCompatActivity {

    private ListView lvJogos;
    private ArrayAdapter<Jogo> listaAdapter;

    private static final int REQUISICAO_NOVO_JOGO = 1;
    private static final int REQUISICAO_ALTERAR_JOGO = 2;

    public static void abrir(Activity activity){

        Intent intent = new Intent(activity, JogosActivity.class);

        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listas);

        ActionBar barraAcao = getSupportActionBar();
        if (barraAcao != null){
            barraAcao.setDisplayHomeAsUpEnabled(true);
        }

        lvJogos = (ListView) findViewById(R.id.lvItens);

        lvJogos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                Jogo jogo = (Jogo) adapterView.getItemAtPosition(pos);
                JogoActivity.alterar(JogosActivity.this, REQUISICAO_ALTERAR_JOGO, jogo);
            }
        });
        //inicio do menu de acao contextual
        lvJogos.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        lvJogos.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                boolean selecionado = lvJogos.isItemChecked(position);

                View view = lvJogos.getChildAt(position);

                if (selecionado){
                    view.setBackgroundColor(Color.LTGRAY);
                } else{
                    view.setBackgroundColor(Color.TRANSPARENT);
                }

                int total = lvJogos.getCheckedItemCount();

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
                if (lvJogos.getCheckedItemCount() > 1){
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
                        for (int posicao = lvJogos.getChildCount(); posicao >= 0; posicao--){
                            if (lvJogos.isItemChecked(posicao)){
                                Jogo jogo = (Jogo) lvJogos.getItemAtPosition(posicao);
                                JogoActivity.alterar(JogosActivity.this, REQUISICAO_ALTERAR_JOGO, jogo);
                                break;
                            }
                        }
                        mode.finish();
                        return true;

                    case R.id.menuAcaoDeletar:

                        try {
                            DatabaseHelper conexao = DatabaseHelper.getInstance(JogosActivity.this);

                            for (int posicao = lvJogos.getChildCount(); posicao >= 0; posicao--){
                                if (lvJogos.isItemChecked(posicao)){
                                    Jogo jogo = (Jogo) lvJogos.getItemAtPosition(posicao);
                                    List<Transacao> lista = conexao.getTransacaoDao().queryBuilder().where()
                                            .eq(Transacao.ID_JOGO, jogo.getId()).query();

                                    if (lista != null && lista.size() > 0){
                                        UtilsGUI.avisoErro(JogosActivity.this, getString(R.string.jogo_usado) + "\n" + jogo.getNome());
                                        return true;
                                    }
                                    //break;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        String mensagem = getString(R.string.deseja_apagar);
                        DialogInterface.OnClickListener listener =
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch(which){
                                            case DialogInterface.BUTTON_POSITIVE:

                                                try {
                                                    DatabaseHelper conexao = DatabaseHelper.getInstance(JogosActivity.this);
                                                    for (int posicao = lvJogos.getChildCount(); posicao >= 0; posicao--){
                                                        //if ()
                                                        if (lvJogos.isItemChecked(posicao)){
                                                            Jogo jogo = (Jogo) lvJogos.getItemAtPosition(posicao);
                                                            conexao.getJogoDao().delete(jogo);
                                                            listaAdapter.remove(jogo);
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

                        UtilsGUI.confirma(JogosActivity.this, mensagem, listener);
                        return true;

                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                for (int posicao = 0; posicao < lvJogos.getChildCount(); posicao++){
                    View view = lvJogos.getChildAt(posicao);
                    view.setBackgroundColor(Color.TRANSPARENT);
                }
            }
        });

        popularLista();
        registerForContextMenu(lvJogos);

        setTitle(R.string.txt_jogos);


    }
    private void popularLista(){

        List<Jogo> lista = null;

        try {
            DatabaseHelper conexao = DatabaseHelper.getInstance(this);

            lista = conexao.getJogoDao().queryBuilder().orderBy(Jogo.JOGO_NOME, true).query();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        listaAdapter = new ArrayAdapter<Jogo>(this,
                android.R.layout.simple_list_item_1,
                lista);

        lvJogos.setAdapter(listaAdapter);
    }

    /*private void excluirJogo(final Jogo jogo){

        try {
            DatabaseHelper conexao = DatabaseHelper.getInstance(this);
            List<Transacao> lista = conexao.getTransacaoDao().queryBuilder().where()
                    .eq(Transacao.ID_JOGO, jogo.getId()).query();

            if (lista != null && lista.size() > 0){
                UtilsGUI.avisoErro(this, R.string.jogo_usado);
                return;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        String mensagem = getString(R.string.deseja_apagar)
                + "\n" + jogo.getNome();

        DialogInterface.OnClickListener listener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch(which){
                            case DialogInterface.BUTTON_POSITIVE:

                                try {
                                    DatabaseHelper conexao = DatabaseHelper.getInstance(JogosActivity.this);

                                    conexao.getJogoDao().delete(jogo);

                                    listaAdapter.remove(jogo);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                break;
                            case DialogInterface.BUTTON_NEGATIVE:

                                break;
                        }
                    }
                };

        UtilsGUI.confirma(this, mensagem, listener);
    }*/

    @Override
    protected void onActivityResult(int requisicaoCode, int resultCode, Intent data) {

        if ((requisicaoCode == REQUISICAO_NOVO_JOGO || requisicaoCode == REQUISICAO_ALTERAR_JOGO)
                && resultCode == Activity.RESULT_OK){

            popularLista();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.lista_jogos, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){

            case R.id.menuItemNovo:
                JogoActivity.novo(this, REQUISICAO_NOVO_JOGO);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*@Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        getMenuInflater().inflate(R.menu.item_selecionado, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info;

        info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        Jogo jogo = (Jogo) lvJogos.getItemAtPosition(info.position);

        switch(item.getItemId()){

            case R.id.menuItemExibir:
                JogoActivity.alterar(this, REQUISICAO_ALTERAR_JOGO, jogo);
                return true;

            case R.id.menuItemDeletar:
                excluirJogo(jogo);
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }*/
}

