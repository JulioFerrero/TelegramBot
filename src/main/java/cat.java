import org.apache.commons.io.FileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class cat {



    public static SendPhoto sendPhoto(Update update) throws FileNotFoundException {
        String chatID = String.valueOf(update.getMessage().getChatId());

        SendPhoto sendPhotoRequest = new SendPhoto();
        sendPhotoRequest.setChatId(chatID);
        sendPhotoRequest.setPhoto(new InputFile(getSaveCat()));
        return sendPhotoRequest;
    }

    public static String getSaveCat (){
        try {
            URL url = new URL("https://api.thecatapi.com/v1/images/search");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            //Getting the response code
            int responsecode = conn.getResponseCode();

            if (responsecode != 200) {
                throw new RuntimeException("HttpResponseCode: " + responsecode);
            } else {

                String inline = "";
                Scanner scanner = new Scanner(url.openStream());

                //Write all the JSON data into a string using a scanner
                while (scanner.hasNext()) {
                    inline += scanner.nextLine();
                }

                //Close the scanner
                scanner.close();

                //Using the JSON simple library parse the string into a json object
                JSONParser parse = new JSONParser();
                JSONArray data_obj = (JSONArray) parse.parse(inline);
                JSONObject hhh = (JSONObject) data_obj.get(0);

                System.out.println(hhh.get("url"));

                //FileUtils.copyURLToFile(new URL(urlString),new File("cat"),1000,1000);
                //SendPhoto juan = new SendPhoto().setPhoto();
                //JSONObject data_obj = (JSONObject) parse.parse(inline);

                //JSONObject urlAPI = (JSONObject) data_obj.get("url");
                //System.out.println(urlAPI);
                return hhh.get("url").toString();
            }
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
