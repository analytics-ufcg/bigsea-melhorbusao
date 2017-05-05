package br.edu.ufcg.analytics.meliorbusao.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static br.edu.ufcg.analytics.meliorbusao.db.Table.Column;
import static br.edu.ufcg.analytics.meliorbusao.db.Table.ForeignKey;

public class MeliorDBOpenHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "melior_db";
    private static final int DB_VERSION = 3;

    private static Table respostaTable = null;
    private static Table avaliacaoTable = null;
    private static String avaliacaoTableRotaIndex = null;
    private static Table locationsTable = null;
    private static Table routeTable = null;
    private static Table stopTable = null;
    private static Table routeStopTable = null;
    private static Table shapesTable = null;
    private static Table horariosProvaveisTable = null;
    private static Table nonPublishedRatingsTable = null;


    //
    //private static Table horariosProvaveisTable = null;


    public MeliorDBOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    /**
     * Cria as tabelas no BD
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(getRouteTable().toSQL());
        db.execSQL(getStopTable().toSQL());
        db.execSQL(getAvaliacaoTable().toSQL());
        //TODO: o índice é adicionado 'no braço'. Verificar se há uma forma mais elegante.
        db.execSQL(getAvaliacaoTableRotaIndex());
        db.execSQL(getRespostaTable().toSQL());
        db.execSQL(getLocationsTable().toSQL());
        db.execSQL(getRouteStopTable().toSQL());
        db.execSQL(getShapesTable().toSQL());

        db.execSQL(getHorariosProvaveisTable().toSQL());
        db.execSQL(getNonPublishedRatingsTable().toSQL());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        int upgradeTo = oldVersion + 1;
        while (upgradeTo <= newVersion) {
            switch (upgradeTo) {
                case 2:
                    db.execSQL(getRouteTable().toSQL());
                    db.execSQL(getStopTable().toSQL());
                    db.execSQL(getRouteStopTable().toSQL());

                    break;
                case 3:
                    db.execSQL(getShapesTable().toSQL());

                    break;
            }
            upgradeTo++;
        }
    }


    /**
     * retorna o objeto Tabela de Horarios Prováveis (se não existir cria)
     * - coluna id (Primary Key)
     *  - coluna da rota
     *  - coluna do tipo do dia
     *  - coluna do id da parada
     *  - coluna do horário médio
     *  - coluna do horário minimo que passa na parada
     *  - coluna do horário maximo que passa na parada
     *
     * @return
     */
    public static Table getHorariosProvaveisTable() {
        if (horariosProvaveisTable == null) {
            horariosProvaveisTable = new Table("horariosProvaveisTable")
                    .addColumn(new Column("id", "INTEGER", true))
                    .addColumn(new Column("rota", "TEXT"))
                    .addColumn(new Column("tipo_dia", "TEXT"))
                    .addColumn(new Column("id_parada", "INTEGER"))
                    .addColumn(new Column("horario_medio", "TEXT"))
                    .addColumn(new Column("horario_anterior", "TEXT"))
                    .addColumn(new Column("horario_posterior", "TEXT"));
        }

        return horariosProvaveisTable;
    }



    /**
     * retorna o objeto Tabela de Resposta (se não existir cria)
     *  - coluna de categoria (primary key) : Motorista/Lotação/Conservação
     *  - coluna de valor da resposta
     *  - coluna de timestamp (primary key e foreign key - tabela Avaliação)
     *
     * @return
     */
    public static Table getRespostaTable() {
        if (respostaTable == null) {
            respostaTable = new Table("resposta")
                    .addColumn(new Column("categoria", "INTEGER", true))
                    .addColumn(new Column("valor", "INTEGER"))
                    .addColumn(new Column("timestamp", "INTEGER", true))
                    .addForeignKey(new ForeignKey(getAvaliacaoTable().getName())
                                    .addReference("timestamp", "timestamp")
                    );
        }

        return respostaTable;
    }


    /**
     * retorna o objeto Tabela de Avaliação (se não existir cria)
     *  - coluna de timestamp (primary key)
     *  - coluna de rota da avaliação
     *  - coluna de preenchida (default não preenchida)
     *
     * @return
     */
    public static Table getAvaliacaoTable() {
        if (avaliacaoTable == null) {
            avaliacaoTable = new Table("avaliacao")
                    .addColumn(new Column("timestamp", "INTEGER", true))
                    .addColumn(new Column("rota", "TEXT"))
                    .addColumn(new Column("preenchida", "INTEGER DEFAULT 0"));
        }

        return avaliacaoTable;
    }

    private static String getAvaliacaoTableRotaIndex() {
        if (avaliacaoTableRotaIndex == null) {
            avaliacaoTableRotaIndex = "CREATE INDEX avaliacao_rota_idx ON avaliacao(rota)";
        }

        return avaliacaoTableRotaIndex;
    }


    /**
     * retorna o objeto Tabela de Locations (se não existir cria)
     *  - colunas de atributos de Location
     *  - coluna de timestamp (foreign key - AvaliacaoTable)
     *
     * @return
     */
    public static Table getLocationsTable() {
        if (locationsTable == null) {
            locationsTable = new Table("location")
                    .addColumn(new Column("lat", "TEXT"))
                    .addColumn(new Column("lon", "TEXT"))
                    .addColumn(new Column("acc", "TEXT"))
                    .addColumn(new Column("speed", "TEXT"))
                    .addColumn(new Column("bear", "TEXT"))
                    .addColumn(new Column("et", "INTEGER"))
                    .addColumn(new Column("timestamp", "INTEGER"))
                    .addForeignKey(new ForeignKey(getAvaliacaoTable().getName())
                                    .addReference("timestamp", "timestamp")
                    );
        }

        return locationsTable;

    }

    /**
     * retorna o objeto Tabela de Rotas (se não existir cria)
     *  - coluna de id (primary key)
     *  - colunas de atributos de Route
     *
     * @return
     */
    public static Table getRouteTable() {
        if (routeTable == null) {
            routeTable = new Table("route")
                    .addColumn(new Column("id", "TEXT", true))
                    .addColumn(new Column("short_name", "TEXT"))
                    .addColumn(new Column("long_name", "TEXT"))
                    .addColumn(new Column("color", "TEXT"))
                    .addColumn(new Column("line_name", "TEXT"))
                    .addColumn(new Column("main_stops", "TEXT"));
        }

        return routeTable;
    }

    /**
     * retorna o objeto Tabela de Stops (se não existir cria)
     *  - coluna de id (primary key)
     *  - colunas de atributos de Stop
     *
     * @return
     */
    public static Table getStopTable() {
        if (stopTable == null) {
            stopTable = new Table("stop")
                    .addColumn(new Column("id", "INTEGER", true))
                    .addColumn(new Column("name", "TEXT"))
                    .addColumn(new Column("desc", "TEXT"))
                    .addColumn(new Column("lat", "REAL"))
                    .addColumn(new Column("lon", "REAL"));
        }

        return stopTable;
    }

    /**
     * retorna o objeto Tabela de Rota-Stop (se não existir cria)
     *  - coluna de id_route (primary key e foreign key - RouteTable)
     *  - coluna de id_stop (primary key e foreign key - StopTable)
     *  - coluna de stop_order (primary key)
     *  - coluna de next_stop (primary key e foreign key - StopTable)
     *
     * @return
     */
    public static Table getRouteStopTable() {
        if (routeStopTable == null) {
            routeStopTable = new Table("route_stop")
                    .addColumn(new Column("id_route", "TEXT", true))
                    .addColumn(new Column("id_stop", "INTEGER", true))
                    .addColumn(new Column("stop_order", "INTEGER", true))
                    .addColumn(new Column("next_stop", "INTEGER", true))
                    .addForeignKey(new ForeignKey(getRouteTable().getName())
                            .addReference("id_route", "id"))
                    .addForeignKey(new ForeignKey(getStopTable().getName())
                            .addReference("id_stop", "id"))
                    .addForeignKey(new ForeignKey(getStopTable().getName())
                            .addReference("next_stop", "id"));
        }

        return routeStopTable;
    }

    /**
     * retorna o objeto Tabela de Shapes (se não existir cria)
     *  - coluna de id_route , order_num e sub (primary keys)
     * @return
     */
    public static Table getShapesTable() {
        if (shapesTable == null) {
            shapesTable = new Table("shapes") // route_id,order,lat,lng,sub
                    .addColumn(new Column("id_route", "TEXT", true))
                    .addColumn(new Column("order_num", "INTEGER", true))
                    .addColumn(new Column("lat", "REAL"))
                    .addColumn(new Column("lon", "REAL"))
                    .addColumn(new Column("sub", "TEXT", true));
            // TODO: adicionar chave estrangeira para rotas
        }

        return shapesTable;
    }


    /**
     * Returns the NonPublishedRatings table object (creates if it doesn't exist)
     *  - timestamp column (primary key and foreign key - avaliacao)
     *
     * @return
     */
    public static Table getNonPublishedRatingsTable() {
        if (nonPublishedRatingsTable == null) {
            nonPublishedRatingsTable = new Table("non_published_ratings")
                    .addColumn(new Column("id_rating", "INTEGER", true))
                    .addForeignKey(new ForeignKey(getAvaliacaoTable().getName())
                        .addReference("id_rating", "timestamp"));
        }

        return nonPublishedRatingsTable;
    }
}
