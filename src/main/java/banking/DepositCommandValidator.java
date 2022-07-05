package banking;

import java.util.Locale;

public class DepositCommandValidator extends CommandValidator {

    public String command;
    public String[] commandList;

    DepositCommandValidator(String command, Bank bank) {
        super(bank);
        this.command = command;
        this.commandList = command.split(" ");
    }

    protected String getAccountType() {
        int ID = Integer.parseInt(commandList[1]);
        Account account = bank.getAccount(ID);
        if (account == null) {
            return null;
        }
        return account.accountType.toLowerCase(Locale.ROOT);
    }

    protected boolean validateDeposit(String accountType) {
        float deposit;
        try {
            deposit = Float.parseFloat(commandList[2]);
        } catch (NumberFormatException e) {
            return false;
        }
        if (accountType.equals("checking")) {
            return (deposit >= 0 && deposit <= 1000);
        } else {
            return (deposit >= 0 && deposit <= 2500);
        }
    }


    public boolean validate() {
        if (!(checkNumArguments(3, commandList))) {
            return false;
        }
        if (!(commandList[0].toLowerCase(Locale.ROOT).equals("deposit"))) {
            return false;
        }

        if (!(validateID(commandList[1]))) {
            return false;
        }
        String accountType = getAccountType();

        if (accountType == null) {
            return false;
        }

        accountType = accountType.toLowerCase(Locale.ROOT);

        if (!(accountType.equals("checking") | accountType.equals("savings"))) {
            return false;
        }
        return validateDeposit(accountType);
    }
}
