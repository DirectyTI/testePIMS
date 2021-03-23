/*
    Classe responsável por criar uma conexão com banco de dados.
 */


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class BDconnection {

    //interface Java para execução de comandos SQL.
    private Connection connection;

    public  BDconnection(){

        try{
            //string de coexão
            String ConnectionDB = "jdbc:sqlserver://TR1SQLPRD1:1433;databaseName=PBI_PIMS_CMT;user=PBIPIMS;password=Pbi@Pims2021";
            //Driver de conexão
            connection = DriverManager.getConnection(ConnectionDB);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    //Método que abre conexão com o banco de dados.
    public Connection getConnection(){
        return this.connection;
    }

    //Método que fecha a conexão com o banco de dados.
    public void closeDataBaseConnection(){
        try{
            connection.close();
        }catch(SQLException e) {
            e.getErrorCode();
        }
    }
}
