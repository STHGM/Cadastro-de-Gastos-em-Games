package br.utfpr.rodrigomoretto.trabalho_final;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.List;

import br.utfpr.rodrigomoretto.trabalho_final.models.Jogo;
import br.utfpr.rodrigomoretto.trabalho_final.persistence.DatabaseHelper;
import br.utfpr.rodrigomoretto.trabalho_final.utils.UtilsGUI;

public class JogoActivity extends AppCompatActivity {

    public static final String MODO    = "MODO";
    public static final String ID      = "ID";
    public static final int    NOVO    = 1;
    public static final int    ALTERAR = 2;

    private EditText edtJogo;

    private int modo;
    private Jogo jogo;

    public static void novo(Activity activity, int requestCode){
        Intent intent = new Intent(activity, JogoActivity.class);

        intent.putExtra(MODO, NOVO);

        activity.startActivityForResult(intent, requestCode);
    }

    public static void alterar(Activity activity, int requestCode, Jogo jogo){
        Intent intent = new Intent(activity, JogoActivity.class);

        intent.putExtra(MODO, ALTERAR);
        intent.putExtra(ID, jogo.getId());

        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cad_jogos);

        ActionBar barraAcao = getSupportActionBar();
        if (barraAcao != null){
            barraAcao.setDisplayHomeAsUpEnabled(true);
        }

        edtJogo = (EditText) findViewById(R.id.edtJogo);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        modo = bundle.getInt(MODO);

        if (modo == ALTERAR){
            int id = bundle.getInt(ID);

            try {
                DatabaseHelper conexao = DatabaseHelper.getInstance(this);
                jogo = conexao.getJogoDao().queryForId(id);

                edtJogo.setText(jogo.getNome());
            } catch (SQLException e){
                e.printStackTrace();
            }

            setTitle(R.string.alterar_jogo);
        } else{
            jogo = new Jogo();
            setTitle(R.string.novo_jogo);
        }
    }

    private void salvar(){
        String nome = UtilsGUI.validaCampo(this, edtJogo, R.string.jogo_vazio);
        if (nome == null){
            return;
        }

        try {
            DatabaseHelper conexao = DatabaseHelper.getInstance(this);

            List<Jogo> lista = conexao.getJogoDao().queryBuilder().where().eq(Jogo.JOGO_NOME, nome).query();

            if (modo == NOVO){
                if (lista.size() > 0){
                    UtilsGUI.avisoErro(this, getString(R.string.jogo_usado));
                    return;
                }

                jogo.setNome(nome);
                conexao.getJogoDao().create(jogo);
                //Toast.makeText(this, R.string.salvo_sucesso, Toast.LENGTH_SHORT).show();
                //();

            } else {
                if (!nome.equals(jogo.getNome())){
                    if (lista.size() >= 1){
                        UtilsGUI.avisoErro(this, getString(R.string.jogo_usado));
                        return;
                    }

                    jogo.setNome(nome);

                    conexao.getJogoDao().update(jogo);
                    //Toast.makeText(this, R.string.salvo_sucesso, Toast.LENGTH_SHORT).show();
                    //finish();
                }
            }
            Toast.makeText(this, R.string.salvo_sucesso, Toast.LENGTH_SHORT).show();
            setResult(Activity.RESULT_OK);
            finish();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    private void cancelar(){
        setResult(Activity.RESULT_CANCELED);
        finish();
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
