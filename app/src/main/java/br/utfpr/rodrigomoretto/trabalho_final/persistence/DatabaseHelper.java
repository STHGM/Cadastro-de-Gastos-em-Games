package br.utfpr.rodrigomoretto.trabalho_final.persistence;

/**
 * Created by STHGM on 02-Dec-17.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.utfpr.rodrigomoretto.trabalho_final.R;
import br.utfpr.rodrigomoretto.trabalho_final.models.Jogo;
import br.utfpr.rodrigomoretto.trabalho_final.models.Transacao;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DB_NAME    = "transacoes.db";
    private static final int    DB_VERSION = 1;

    private static DatabaseHelper instance;

    private Context               context;
    private Dao<Jogo, Integer>    jogoDao;
    private Dao<Transacao, Integer>  transacaoDao;

    public static DatabaseHelper getInstance(Context contexto){

        if (instance == null){
            instance = new DatabaseHelper(contexto);
        }

        return instance;
    }

    private DatabaseHelper(Context contexto) {
        super(contexto, DB_NAME, null, DB_VERSION);
        context = contexto;
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {

            TableUtils.createTable(connectionSource, Jogo.class);

            String[] tiposBasicos = context.getResources().getStringArray(R.array.jogos_iniciais);

            List<Jogo> lista = new ArrayList<Jogo>();

            for(int cont = 0; cont < tiposBasicos.length; cont++){

                Jogo jogo = new Jogo(tiposBasicos[cont]);
                lista.add(jogo);
            }

            getJogoDao().create(lista);

            TableUtils.createTable(connectionSource, Transacao.class);

        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "onCreate", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {

            TableUtils.dropTable(connectionSource, Transacao.class, true);
            TableUtils.dropTable(connectionSource, Jogo.class, true);

            onCreate(database, connectionSource);

        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "onUpgrade", e);
            throw new RuntimeException(e);
        }
    }

    public Dao<Transacao, Integer> getTransacaoDao() throws SQLException {

        if (transacaoDao == null) {
            transacaoDao = getDao(Transacao.class);
        }

        return transacaoDao;
    }

    public Dao<Jogo, Integer> getJogoDao() throws SQLException {

        if (jogoDao == null) {
            jogoDao = getDao(Jogo.class);
        }

        return jogoDao;
    }
}
