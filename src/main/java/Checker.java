import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class Checker {

    private Update update;

    Checker() {
    }

    public Checker setUpdate(Update update) {
        this.update = update;
        return this;
    }

    boolean pole() {
        if(update.getMessage() == null || update.getMessage().getText() == null){
            return false;
        }
        String mensaje = update.getMessage().getText();
        String mensajeLower = mensaje.toLowerCase();
        String[] commandList = {"pole","subpole","fail","plata","bronce"};
        DateTimeFormatter dtfdate = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDateTime now = LocalDateTime.now();
        String actualDate = dtfdate.format(now);
        DateTimeFormatter dtfhour = DateTimeFormatter.ofPattern("HH");
        int actualHour = Integer.parseInt(dtfhour.format(now));
        pole.createNewDatabase(update);
        boolean inList = false;
        String polenta;
        int valor;


        for (int i = 0; i < commandList.length; i++){
            if(commandList[i].equals(mensajeLower) && pole.getIDLastPole(actualDate,update.getMessage().getFrom().getId())){
                inList = true;
            }
        }

        String chatID = String.valueOf(update.getMessage().getChatId());
        pole.setUrlFile(chatID);
        pole.createNewDatabase(update);

        pole.getPostionsCurrentDate(actualDate);

        if (inList) {
            if (actualHour > -1 && actualHour < 11) {
                int Currentposition = pole.getPostionsCurrentDate(actualDate);

                if (Currentposition == 4) {
                    pole.setPolenta("Oro");
                    pole.setValor(3);
                    return true;
                }
                if (Currentposition == 3 && pole.getIDLastPole(actualDate, update.getMessage().getFrom().getId())) {
                    pole.setPolenta("Plata");
                    pole.setValor(2);
                    return true;
                }
                if (Currentposition == 2 && pole.getIDLastPole(actualDate, update.getMessage().getFrom().getId())) {
                    pole.setPolenta("Bronce");
                    pole.setValor(1);
                    return true;
                }
            }
        }
        return false;
    }

    boolean polerank() {
        if(update.getMessage().getText() == null){
            return false;
        }
        String mensaje = update.getMessage().getText();
        String mensajeLower = mensaje.toLowerCase();
        String commandList = "!polerank";
        return commandList.equals(mensajeLower);
    }

    boolean cat() {
        String command = "/cat";
        return command.equals(update.getMessage().getText());
    }

    boolean ChatSaver() {
        return update.getMessage().getText() != null;
    }
}


