import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;

public class polerank {
    private static String urlFile;

    public static void setUrlFile(String urlFile) {
        try {
            Path path = Paths.get("DB/");
            Files.createDirectories(path);
        } catch (IOException e) {
            System.err.println("Failed to create directory!" + e.getMessage());
        }

        polerank.urlFile = "DB/" + urlFile + ".db";
    }

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

    public static SendMessage sendMessage(Update update){
        String chatID = String.valueOf(update.getMessage().getChatId());
        setUrlFile(chatID);
        SendMessage message = new SendMessage();
        message.setText(polerank());
        message.setChatId(chatID);
        return message;
    }

    /*Este metodo se utiliza con el comando !polerank, realiza una cosulta a la base de datos, va pasando por cada uno
    de los resultados y devuelve una string con la lista de usuarios y sus puntuaciones*/
    public static String polerank(){
        String sql = "SELECT users.username, sum(poles.position)total FROM poles,users WHERE poles.idUser == users.idUser GROUP by users.idUser ORDER by sum(poles.position) DESC";

        try {
            Connection conn = connect();
            Statement stmt  = conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sql);
            String Stringpolerank = "Ranking \n ---";
            while (rs.next()) {
                Stringpolerank = Stringpolerank + "\n" +rs.getString("username")+ " :\t" + rs.getInt("total");
            }
            conn.close();
            return Stringpolerank;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return "ERROR";
    }
}
