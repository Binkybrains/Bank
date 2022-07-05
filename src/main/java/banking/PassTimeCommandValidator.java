package banking;

import java.util.Locale;

public class PassTimeCommandValidator {
    String[] commandList;

    PassTimeCommandValidator() {
    }

    protected boolean validateTime() {
        String stringTime = commandList[1];
        int time;
        try {
            time = Integer.parseInt(stringTime);
        } catch (NumberFormatException e) {
            return false;
        }
        return (time >= 1 && time <= 60);
    }

    public boolean validate(String command) {
        commandList = command.split(" ");

        if (commandList.length != 2) {
            return false;
        }

        if (!(commandList[0].toLowerCase(Locale.ROOT).equals("pass"))) {
            return false;
        }

        return validateTime();
    }
}
