import java.sql.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DBConnection
{
    public static Connection connection;
    public static PreparedStatement preparedStatement;
    private static final String dbName = "bank";
    private static final String dbUser = "root";
    private static final String dbPass = "mysql";
    private static final StringBuilder insertQuery = new StringBuilder ();
    private static final int processor = Runtime.getRuntime ().availableProcessors ();
    private static final ExecutorService service = Executors.newFixedThreadPool(processor);
    public static Connection getConnection()
    {
        if(connection == null)
        {
            try {
                connection = DriverManager.getConnection(
                    "jdbc:mysql://127.0.0.1:3306/" + dbName +
                    "?user=" + dbUser + "&password=" + dbPass);
                connection.createStatement().execute("DROP TABLE IF EXISTS voter_count");
            /*connection.createStatement().execute("CREATE TABLE voter_count(" +
                    "id INT NOT NULL AUTO_INCREMENT, " +
                    "name TINYTEXT NOT NULL, " +
                    "birthDate DATE NOT NULL, " +
                    "count INT NOT NULL, " +
                    "PRIMARY KEY(id), " +
                    "UNIQUE KEY name_date(name(50), birthDate))");*/

                connection.createStatement().execute("CREATE TABLE voter_count(" +
                        "id INT NOT NULL AUTO_INCREMENT, " +
                        "name TINYTEXT NOT NULL, " +
                        "birthDate DATE NOT NULL, " +
                        "PRIMARY KEY(id))");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }


    public static void countVoter (String name, String birthDay) throws SQLException
    {
        birthDay = birthDay.replace('.', '-');
        for(int i = 0; i < 10; i++) {
            String finalBirthDay = birthDay;
            service.submit( new Runnable() {
                public void run() {
                    insertQuery.append ( insertQuery.length () == 0 ? "" : ", " )
                            .append ( "('" )
                            .append ( name )
                            .append ( "', '" )
                            .append ( finalBirthDay )
                            .append ( "', 1) " );
                }
            });
        }
        service.shutdown ();


    }


    public static void executeMultiInsert () throws SQLException {
        String sql = "INSERT INTO voter_count(name, birthDate, count) " +
                "VALUES " + insertQuery.toString() +
                "ON DUPLICATE KEY UPDATE count = count + 1";

        DBConnection.getConnection().createStatement().execute(sql);

    }

    public static void printVoterCounts () throws SQLException
    {
        String sql = "SELECT voter.id, voter.name, voter.birthDate, count(*) c FROM bank.voter_count voter  group by concat(name, birthDate) having c > 1 order by name";
        ResultSet rs = DBConnection.connection.createStatement().executeQuery(sql);

            service.submit( new Runnable() {
                public void run() {
                    while (true) {
                        try {
                            if (!rs.next()) break;
                        } catch (SQLException throwables) {
                            throwables.printStackTrace ();
                        }
                        try {
                            System.out.println( new StringBuilder ().append ( "\t" ).append ( rs.getString ( "name" ) ).append ( " (" ).append ( rs.getString ( "birthDate" ) ).append ( ") - " ).append ( rs.getInt ( "c" ) ).toString () );
                        } catch (SQLException throwables) {
                            throwables.printStackTrace ();
                        }
                    }
                }
            });

        service.shutdown ();

        while (rs.next()) {
            System.out.println("\t" + rs.getString("name") + " (" +
                    rs.getString("birthDate") + ") - " + rs.getInt("c"));
        }

    }
    /*public static void countVoter(String name, String birthDay) throws SQLException
    {
        birthDay = birthDay.replace('.', '-');
        boolean isStart =  insertQuery.length () == 0;
        insertQuery.append ( isStart ? "" : ", " )
                .append ( "('" )
                .append ( name )
                .append ( "', '" )
                .append ( birthDay )
                .append ( "', 1)" );

                //"ON CONFLICT (name) DO UPDATE SET voter_count.count = count + 1";
                //"ON DUPLICATE KEY UPDATE count = count + 1";

        *//*String sql = "SELECT id FROM voter_count WHERE birthDate='" + birthDay + "' AND name='" + name + "'" + "INSERT INTO voter_count(name, birthDate, count) VALUES('" +
                name + "', '" + birthDay + "', 1)" + "UPDATE voter_count SET count = count +1 WHERE id=" + id;
        ResultSet rs = DBConnection.getConnection().createStatement().executeQuery(sql);*//*
       *//* if(!rs.next())
        {
            DBConnection.getConnection().createStatement()
                    .execute("INSERT INTO voter_count(name, birthDate, count) VALUES('" +
                            name + "', '" + birthDay + "', 1)");
        }
        else {
            int id = rs.getInt("id");
            DBConnection.getConnection().createStatement()
                    .execute("UPDATE voter_count SET count = count +1 WHERE id=" + id);
        }
        rs.close();*//*
    }*/



    public static void testConnecting() {
        System.out.println("Testing connection to JDBC connecting.");

        try {
            Class.forName("org.mysql.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("JDBC Driver is not found. Include it in your library path ");
            e.printStackTrace();
            return;
        }

        System.out.println("mysql JDBC Driver successfully connected");
        Connection connection;

        try {
            connection = DriverManager
                    .getConnection("jdbc:mysql://127.0.0.1:3306//" + dbName +
                            "?user=" + dbUser + "&password=" + dbPass);

        } catch (SQLException e) {
            System.out.println("Connection Failed");
            e.printStackTrace();
            return;
        }

        if (connection != null) {
            System.out.println("You successfully connected to database now");
        } else {
            System.out.println("Failed to make connection to database");
        }
    }
    public static int customSelect() throws SQLException {
        String sql = "SELECT id FROM voter_count WHERE name='Исаичев Эмилиан'";
        ResultSet rs = DBConnection.getConnection ().createStatement ().executeQuery ( sql );
        if(!rs.next ()) {
            return -1;
        }
        else {
            return rs.getInt ( "id" );
        }
    }


}
