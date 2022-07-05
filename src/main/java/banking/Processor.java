package banking;

import java.util.Locale;

public class Processor {
    Bank bank;

    Processor(Bank bank) {
        this.bank = bank;
    }

    protected void processCommand(String command) {
        String[] commandList = command.split(" ");
        String commandType = commandList[0].toLowerCase(Locale.ROOT);
        if (commandType.equals("create")) {
            String accountType = commandList[1].toLowerCase(Locale.ROOT);
            if (commandList.length == 4) {
                bank.createAccount(accountType, Integer.parseInt(commandList[2]), Float.parseFloat(commandList[3]));
            } else {
                bank.createAccount(accountType, Integer.parseInt(commandList[2]), Float.parseFloat(commandList[3]), Float.parseFloat(commandList[4]));
            }
        } else if (commandType.equals("deposit") || commandType.equals("withdrawal")) {
            int id = Integer.parseInt(commandList[1]);
            Account account = bank.getAccount(id);
            float amount = Float.parseFloat(commandList[2]);
            if (commandType.equals("deposit")) {
                account.deposit(amount);
            } else { //withdrawal
                account.withdraw(amount);
            }
        } else if (commandType.equals("transfer")) {
            int id1 = Integer.parseInt(commandList[1]);
            int id2 = Integer.parseInt(commandList[2]);
            Account account1 = bank.getAccount(id1);
            Account account2 = bank.getAccount(id2);
            float amount = Float.parseFloat(commandList[3]);

            account1.transfer(account2, amount);
        } else {
            int months = Integer.parseInt(commandList[1]);
            bank.passTime(months);
        }
    }
}
