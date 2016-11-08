package br.edu.ufcg.analytics.meliorbusao.db;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class Table {

    private String name;
    private ArrayList<Column> columns;
    private ArrayList<ForeignKey> foreignKeys;


    /**
     * Construtor da Classe tabela
     * @param name
     */
    public Table(String name){
        setName(name);
        setColumns(new ArrayList<Column>());
        setForeignKeys(new ArrayList<ForeignKey>());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private ArrayList<Column> getColumns() {
        return columns;
    }

    private void setColumns(ArrayList<Column> columns) {
        this.columns = columns;
    }

    public ArrayList<ForeignKey> getForeignKeys() {
        return foreignKeys;
    }

    public void setForeignKeys(ArrayList<ForeignKey> foreignKeys) {
        this.foreignKeys = foreignKeys;
    }

    /**
     * Adiciona coluna(s) na tabela
     * @param column
     * @return
     */
    public Table addColumn(Column column) {
        getColumns().add(column);

        return this;
    }

    public Table addForeignKey(ForeignKey foreignKey) {
        getForeignKeys().add(foreignKey);

        return this;
    }

    /**
     * Cria a query SQL do objeto tabela
     * @return
     */
    public String toSQL(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("CREATE TABLE " + getName() + " (");

        for (Column c : getColumns()) {
            stringBuilder.append(c.toSQL() + ", ");
        }

        List<Column> primaryKeys = getPrimaryKeys();
        if (primaryKeys.size() > 0) {
            stringBuilder.append("PRIMARY KEY (");
            for (Column c : primaryKeys) {
                stringBuilder.append(c.getName() + ", ");
            }

            stringBuilder.setLength(stringBuilder.length() - 2);
            stringBuilder.append("), ");
        }

        for (ForeignKey f : getForeignKeys()) {
            stringBuilder.append(f.toSQL() + ", ");
        }

        if (getColumns().size() != 0 || getForeignKeys().size() != 0) {
            stringBuilder.setLength(stringBuilder.length() - 2);
        }

        stringBuilder.append(");");

        return stringBuilder.toString();
    }

    /**
     * retorna as chaves primarias da tabela
     * @return
     */
    public List<Column> getPrimaryKeys() {
        ArrayList<Column> primaryKeys = new ArrayList<>();

        for (Column column : getColumns()) {
            if (column.isPrimaryKey()) {
                primaryKeys.add(column);
            }
        }

        return primaryKeys;
    }


    public static class Column {
        private String name;
        private String type;
        private boolean isPrimaryKey;
        private boolean isAutoIncrement;

        /**
         * Construtor da Classe Coluna (da tabela) - a coluna pode ser auto incrementavel e chave primaria da tabela
         * @param name
         * @param type
         * @param isPrimaryKey
         * @param isAutoIncrement
         */
        public Column(String name, String type, boolean isPrimaryKey, boolean isAutoIncrement) {
            this(name, type, isPrimaryKey);
            setIsAutoIncrement(isAutoIncrement);
        }

        /**
         * Construtor da Classe Coluna (da tabela) - a coluna não é auto incrementavel e pode ser chave primaria da tabela
         * @param name
         * @param type
         * @param isPrimaryKey
         */
        public Column(String name, String type, boolean isPrimaryKey) {
            this(name, type);
            setIsPrimaryKey(isPrimaryKey);
        }

        /**
         * Construtor da classe Coluna (da tabela) - a coluna não é chave primaria, nem auto incrementavel
         * @param name
         * @param type
         */
        public Column(String name, String type) {
            setName(name);
            setType(type);
            setIsPrimaryKey(false);
            setIsAutoIncrement(false);
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public boolean isPrimaryKey() {
            return isPrimaryKey;
        }

        public void setIsPrimaryKey(boolean isPrimaryKey) {
            this.isPrimaryKey = isPrimaryKey;
        }

        public boolean isAutoIncrement() {
            return isAutoIncrement;
        }

        public void setIsAutoIncrement(boolean isAutoIncrement) {
            this.isAutoIncrement = isAutoIncrement;
        }

        /**
         * Cria a query SQL do objeto coluna (utilizado no toSQL de tabela)
         * @return
         */
        public String toSQL() {
            StringBuilder builder = new StringBuilder();
            builder.append(getName() + " " + getType());
            if (isAutoIncrement) {
                builder.append(" " + "AUTOINCREMENT");
            }

            return builder.toString();
        }
    }

    public static class ForeignKey {
        private TreeMap<String, String> references;
        private String referencedTable;

        /**
         * Construtor da classe de chave estrangeira (de uma tabela)
         * @param referencedTable
         */

        public ForeignKey(String referencedTable) {
            setReferencedTable(referencedTable);
            setReferences(new TreeMap<String, String>());
        }

        /**
         * Pega a coluna da tabela que a chave estrageira se referência
         * @return
         */
        private TreeMap<String, String> getReferences() {
            return references;
        }

        private void setReferences(TreeMap<String, String> references) {
            this.references = references;
        }

        public String getReferencedTable() {
            return referencedTable;
        }

        public void setReferencedTable(String referencedTable) {
            this.referencedTable = referencedTable;
        }

        public ForeignKey addReference(String from, String to) {
            getReferences().put(from, to);
            return this;
        }

        public void removeReference(String from) {
            getReferences().remove(from);
        }

        /**
         * Cria a query SQL do objeto Chave Estrangeira (utilizado no toSQL de tabela)
         * @return
         */
        public String toSQL() {
            StringBuilder builder = new StringBuilder();

            builder.append("FOREIGN KEY (");
            for (String column : getReferences().keySet()) {
                builder.append(column + ", ");
            }
            builder.setLength(builder.length() - 2);

            builder.append(") REFERENCES " + referencedTable + "(");
            for (String column : getReferences().keySet()) {
                builder.append(getReferences().get(column) + ", ");
            }
            builder.setLength(builder.length() - 2);
            builder.append(")");
            return builder.toString();
        }
    }
}
