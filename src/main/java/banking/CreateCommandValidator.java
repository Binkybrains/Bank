package banking;

import java.util.Locale;

public class CreateCommandValidator extends CommandValidator {

    public String command;
    public String[] commandList;

    CreateCommandValidator(String command, Bank bank) {
        super(bank);
        this.command = command;
        this.commandList = command.split(" ");
    }

    protected boolean checkAmount() {
        String stringAmount = commandList[4];
        float amount;
        try {
            amount = Float.parseFloat(stringAmount);
        } catch (NumberFormatException e) {
            return false;
        }
        return (amount >= 1000 && amount <= 10000);
    }

    protected boolean validateID() {
        String stringID = commandList[2];
        int ID;
        try {
            ID = Integer.parseInt(stringID);
        } catch (NumberFormatException e) {
            return false;
        }
        int lengthOfDigitsID = stringID.length();

        if (lengthOfDigitsID != 8) {
            return false;
        }
        return (bank.getAccount(ID) == null);
    }

    protected boolean validateAPR() {
        String stringAPR = commandList[3];
        double APR;
        try {
            APR = Double.parseDouble(stringAPR);
        } catch (NumberFormatException e) {
            return false;
        }

        return (APR >= 0 && APR <= 10);
    }

    public boolean validate() {
        String type;
        if (commandList.length > 1) {
            type = commandList[1].toLowerCase(Locale.ROOT);
        } else {
            return false;
        }
        if (!(commandList[0].toLowerCase(Locale.ROOT).equals("create"))) {
            return false;
        }
        if (type.equals("checking") | type.equals("savings")) {
            if (!(checkNumArguments(4, commandList))) {
                return false;
            }
        } else if (type.equals("cd")) {
            if (!(checkNumArguments(5, commandList))) {
                return false;
            }
            if (!(checkAmount())) {
                return false;
            }
        } else {
            return false;
        }
        if (!(validateID())) {
            return false;
        }

        return validateAPR();
    }
}
