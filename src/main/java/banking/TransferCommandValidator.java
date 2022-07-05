package banking;

import java.util.Locale;

public class TransferCommandValidator extends CommandValidator {

    public String command;
    public String[] commandList;

    TransferCommandValidator(String command, Bank bank) {
        super(bank);
        this.command = command;
        this.commandList = command.split(" ");
    }

    public String getAccountType(String idString) {
        int ID = Integer.parseInt(idString);
        Account account = bank.getAccount(ID);
        if (account == null) {
            return null;
        }
        return account.accountType;
    }

    protected boolean validateAmounts(String accountTypeWithdraw, String accountTypeDeposit) {
        float amount;
        try {
            amount = Float.parseFloat(commandList[3]);
        } catch (NumberFormatException e) {
            return false;
        }

        boolean validDeposit;
        if (accountTypeDeposit.equals("checking")) {
            validDeposit = (amount >= 0 && amount <= 1000);
        } else {
            validDeposit = (amount >= 0 && amount <= 2500);
        }

        if (!validDeposit) {
            return false;
        }

        if (accountTypeWithdraw.equals("checking")) {
            return (amount >= 0 && amount <= 400);
        } else {
            int withdrawID = Integer.parseInt(commandList[1]);
            return ((amount >= 0 && amount <= 1000) && (bank.getAccount(withdrawID).getWithdrawsThisMonth() == 0)); //update once pass time is in place
        }
    }


    public boolean validate() {
        if (commandList.length != 4) {
            return false;
        }

        if (!(commandList[0].toLowerCase(Locale.ROOT).equals("transfer"))) {
            return false;
        }

        if (!(validateID(commandList[1]) && validateID(commandList[2]))) {
            return false;
        }

        if (commandList[1].equals(commandList[2])) {
            return false;
        }

        String account1Type = getAccountType(commandList[1]);
        String account2Type = getAccountType(commandList[2]);

        if (account1Type == null || account2Type == null) {
            return false;
        }

        account1Type = account1Type.toLowerCase(Locale.ROOT);
        account2Type = account2Type.toLowerCase(Locale.ROOT);

        if (account1Type.equals("cd") || account2Type.equals("cd")) {
            return false;
        }

        return validateAmounts(account1Type, account2Type);

    }
}
