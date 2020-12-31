import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class pole {
    private static String urlFile;
    private static String polenta;
    private static int valor;

    public static String getPolenta() {
        return polenta;
    }

    public static void setPolenta(String polenta) {
        pole.polenta = polenta;
    }

    public static int getValor() {
        return valor;
    }

    public static void setValor(int valor) {
        pole.valor = valor;
    }

    /*Crea el directorio para las bases de datos si es necesario y actualiza la variable "urlFile" la cual utilizaremos
         mas adelante para crear y usar las bases de datos*/
    public static void setUrlFile(String urlFile) {
        try {
            Path path = Paths.get("DB/");
            Files.createDirectories(path);
        } catch (IOException e) {
            System.err.println("Failed to create directory!" + e.getMessage());
        }

        pole.urlFile = "DB/" + urlFile + ".db";
    }

    /*Crea la conexión con la base de datos*/
    public static Connection connect() {
        // SQLite connection string
        String url = "jdbc:sqlite:" + urlFile;
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    /*Crea una nueva base de datos en función del id del chat, en esta crearemos dos tablas:
        users:
            -idUser: Identificador del usuario (es clave foranea en pole).
            -username: Nick del usuario.
        poles:
            -idMessage: Identificador del mensaje con la
            -idChat: Identificador del chat donde se hizo la
            -idUser: Clave foranea de idUser.
            -position: Pueden ser 3 valores {3,2,1} dependiendo de cada número, sabremos si fue una pole, subpole o fail
                       (3: pole, 2:subpole y 1:fail).
            -Date: Dia, mes y año en el cual se realizó la
    */
    public static void createNewDatabase(Update update) {
        String chatID = String.valueOf(update.getMessage().getChatId());
        setUrlFile(chatID);

        File file = new File(urlFile);



            String sqlusers = """
                    CREATE TABLE IF NOT EXISTS users (
                      idUser integer PRIMARY KEY,
                      username text
                    );
                    """;
            String sqlpoles = """
                    CREATE TABLE IF NOT EXISTS poles (
                     idMessage integer PRIMARY KEY,
                     idChat text,
                     idUser integer,
                     position integer,
                     Date text,
                     FOREIGN KEY(idUser) REFERENCES users(idUser)
                    );
                    """;

            try {
                Connection conn = connect();

                if (conn != null) {
                    Statement stmt = conn.createStatement();
                    stmt.execute(sqlusers);
                    stmt.execute(sqlpoles);
                }

            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }


    }

    /*En función de la fecha actual, sabremos cual fue el valor de la última pole realizada.*/
    public static int getPostionsCurrentDate(String CurrentDate){
        String sql = "SELECT * FROM poles WHERE Date == \""+CurrentDate+"\""+" ORDER BY position ASC";

        try {
            Connection conn = connect();
            Statement stmt  = conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sql);
            if(!rs.isClosed()){
                int Position = rs.getInt("position");
                conn.close();
                return Position;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return 4;
    }

    /*Podremos saber si el usuario ya ha realizado la pole con anterioridad*/
    public static boolean getIDLastPole(String CurrentDate, int getIDuser){
        String sql = "SELECT * FROM poles WHERE Date == \""+CurrentDate+"\""+" AND IdUser == \""+getIDuser+"\"";

        try {
            if(pole.urlFile == null){
                return true;
            }
            Connection conn = connect();
            Statement stmt  = conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sql);
            boolean haspoles = false;
            /*En el caso de la consulta tenga algún valor, se considerará que el usuario ya ha realizado una pole en el
            dia actual.*/
            while(rs.next()){
                haspoles = true;
            }
            if(haspoles){
                conn.close();
                return false;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return true;
    }



    public static int checkuser(int idUser){
        String sql = "SELECT idUser FROM users WHERE idUser == " + "\"" + idUser + "\"";

        try {
            Connection conn = connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            if(!rs.isClosed()) {
                int returnidUser = rs.getInt("idUser");
                conn.close();
                return returnidUser;
            }
        }catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }

    public void insert(String fileName, int idMessage, int idUser, int position, String Date, String username) {

        String sqlpole = "INSERT INTO poles(idMessage,idChat,idUser,position,Date) VALUES(?,?,?,?,?)";
        String sqluser = "INSERT INTO users(idUser,username) VALUES(?,?)";

        try{

            Connection conn = connect();

            if(checkuser(idUser) == 0){
                PreparedStatement userstmt = conn.prepareStatement(sqluser);
                userstmt.setInt(1,idUser);
                userstmt.setString(2,username);
                userstmt.executeUpdate();
            }

            PreparedStatement pstmt = conn.prepareStatement(sqlpole);

            pstmt.setInt(1, idMessage);
            pstmt.setString(2, fileName);
            pstmt.setInt(3, idUser);
            pstmt.setInt(4, position);
            pstmt.setString(5, Date);

            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static SendMessage sendMessage(Update update){

        String chatID = String.valueOf(update.getMessage().getChatId());
        DateTimeFormatter dtfdate = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDateTime now = LocalDateTime.now();
        String actualDate = dtfdate.format(now);
        pole pole = new pole();

        pole.insert(chatID, update.getUpdateId(), update.getMessage().getFrom().getId(), valor, actualDate, update.getMessage().getFrom().getFirstName());
        SendMessage message = new SendMessage();
        message.setText(update.getMessage().getFrom().getFirstName() + " : " + polenta);
        message.setChatId(chatID);
        return message;
    }
}