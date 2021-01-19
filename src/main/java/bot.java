import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.FileNotFoundException;

public class bot extends TelegramLongPollingBot {
    @Override
    public void onUpdateReceived(Update update) {

        try {
            if (new Checker().setUpdate(update).pole()) {
                execute(pole.sendMessage(update));
            }
            if (new Checker().setUpdate(update).polerank()){
                execute(polerank.sendMessage(update));
            }
            if (new Checker().setUpdate(update).ChatSaver()){
                ChatSaver.getUpdate(update);
                ChatSaver.send();
            }
            if (new Checker().setUpdate(update).cat()){
                execute(cat.sendPhoto(update));
            }

        }catch (TelegramApiException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        // TODO
        //Añadir String con el @ del bot
        return secret.getName();
    }

    @Override
    public String getBotToken() {
        // TODO
        //Añadir String con la clave del bot
        return secret.getKey();
    }



}