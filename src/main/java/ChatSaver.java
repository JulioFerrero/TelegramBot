import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class ChatSaver {
    private static Update update;

    static void getUpdate(Update update){
        ChatSaver.update=update;
    }

    static void send(){
        Path fileName = Path.of("Chats/"+ update.getMessage().getChat().getId());
        String content = update.getMessage().getChat().getTitle() + " : " + update.getMessage().getFrom().getUserName() + " : " + update.getMessage().getText();
        try {
            Path path = Paths.get("Chats/");
            Files.createDirectories(path);
            Files.writeString(fileName, content + System.lineSeparator(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(update.getMessage().getChat().getTitle() + " : " + update.getMessage().getFrom().getUserName() + " : " + update.getMessage().getText());
    }
}
