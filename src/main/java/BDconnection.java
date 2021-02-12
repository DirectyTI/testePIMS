import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class BDconnection {

    private Connection connection;

    public  BDconnection(){

        try{
            String ConnectionDB = "jdbc:sqlserver://TR1SQLPRD1:1433;databaseName=PBI_PIMS_CMT;user=PBIPIMS;password=Pbi@Pims2021";
            connection = DriverManager.getConnection(ConnectionDB);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public Connection getConnection(){
        return this.connection;
    }

    public void closeDataBaseConnection(){
        try{
            connection.close();
        }catch(SQLException e) {
            e.getErrorCode();
        }
    }
}



/*
2. Servidor do SQL
TR1SQLPRD1
10.96.33.15
3. String de conexão
Created the bank PBI_PIMS_CMT
and User PBIPIMS
with pass: c
as dbowner from PBI_PIMS_CMT
 */
/*
String connectionUrl = "jdbc:sqlserver://<server>:<port>;databaseName=AdventureWorks;user=<user>;password=<password>";

        try (Connection con = DriverManager.getConnection(connectionUrl); Statement stmt = con.createStatement();) {
            String SQL = "SELECT TOP 10 * FROM Person.Contact";
            ResultSet rs = stmt.executeQuery(SQL);
 */
