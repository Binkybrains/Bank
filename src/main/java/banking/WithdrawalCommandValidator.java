package banking;

import java.util.Locale;

public class WithdrawalCommandValidator extends CommandValidator {

    public String command;
    public String[] commandList;

    WithdrawalCommandValidator(String command, Bank bank) {
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
        return account.accountType;
    }

    protected boolean validateCD() {
        int ID = Integer.parseInt(commandList[1]);

        float amount;

        try {
            amount = Float.parseFloat(commandList[2]);
        } catch (NumberFormatException e) {
            return false;
        }

        if (bank.getAccount(ID).getTime() < 12) {
            return false;
        }

        return (amount >= bank.getAccount(ID).getBalance());
    }

    public boolean validateAmount(String accountType) {
        float amount;

        try {
            amount = Float.parseFloat(commandList[2]);
        } catch (NumberFormatException e) {
            return false;
        }

        if (accountType.equals("checking")) {
            return (amount >= 0 && amount <= 400);
        } else {
            int ID = Integer.parseInt(commandList[1]);
            if (bank.getAccount(ID).getWithdrawsThisMonth() != 0) {
                return false;
            }
            return (amount >= 0 && amount <= 1000);
        }
    }

    public boolean validate() {
        if (commandList.length != 3) {
            return false;
        }

        if (!(commandList[0].toLowerCase(Locale.ROOT).equals("withdrawal"))) {
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

        if (accountType.equals("cd")) {
            return validateCD();
        }

        return validateAmount(accountType);
    }
}

