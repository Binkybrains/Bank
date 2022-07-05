package banking;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class Output {
    Bank bank;
    List<String> invalidCommands;
    List<String> validCommands;
    List<String> outputStringList;
    List<Integer> accountIdList;
    OutputNode outputList;

    Output(Bank bank) {
        this.bank = bank;
        this.invalidCommands = new ArrayList<>();
        this.outputStringList = new ArrayList<>();
        this.validCommands = new ArrayList<>();
        this.accountIdList = new ArrayList<>();
        this.outputList = null;
    }

    protected List<String> getValidCommands() {
        return validCommands;
    }

    protected List<String> getInvalidCommands() {
        return invalidCommands;
    }

    protected void addInvalidCommand(String command) {
        invalidCommands.add(command);
    }

    protected void addValidCommand(String command) {
        String[] commandList = command.split(" ");
        String commandName = commandList[0].toLowerCase(Locale.ROOT);
        if (!(commandName.equals("pass") || commandName.equals("create"))) {
            this.validCommands.add(command);
        } else if (commandName.equals("create")) {
            int id = Integer.parseInt(commandList[2]);
            if (!(accountIdList.contains(id))) {
                accountIdList.add(Integer.parseInt(commandList[2]));
            }
        }
    }

    protected void generateOutputList(Set<Integer> openAccountIds) {
        OutputNode head = null;
        OutputNode current = null;

        for (int id : accountIdList) {
            if (openAccountIds.contains(id)) {
                if (head == null) {
                    head = new OutputNode(id);
                    current = head;
                } else {
                    OutputNode node = new OutputNode(id);
                    current.setNext(node);
                    current = node;
                }
                Account account = bank.getAccount(id);
                String accountType = account.getAccountType();
                accountType = accountType.substring(0, 1).toUpperCase() + accountType.substring(1);

                DecimalFormat decimalFormat = new DecimalFormat("0.00");
                decimalFormat.setRoundingMode(RoundingMode.FLOOR);
                String accountDescription = String.format("%s %d %s %s", accountType, id, decimalFormat.format(account.getBalance()), decimalFormat.format(account.getApr()));
                current.addValidCommand(accountDescription);
            }
        }
        outputList = head;
    }

    protected void addValidCommandsToOutputList() {
        for (String command : validCommands) {
            String[] commandList = command.split(" ");
            int id = Integer.parseInt(commandList[1]);
            int potentialID = Integer.parseInt(commandList[2]);

            if (potentialID < 50000) {
                potentialID = 0;
            }

            OutputNode temp = outputList;

            while (temp != null) {
                if (temp.id == id) {
                    temp.addValidCommand(command);
                    if (potentialID == 0) {
                        break;
                    }
                }
                if (temp.id == potentialID) {
                    temp.addValidCommand(command);
                }
                temp = temp.getNext();
            }
        }
    }

    public void generateOutputStringList() {
        while (outputList != null) {
            outputStringList.addAll(outputList.getValidCommands());
            outputList = outputList.getNext();
        }
        outputStringList.addAll(invalidCommands);
    }

    public List<String> getOutput() {
        Set<Integer> openAccountIds = bank.getOpenAccountIds();
        generateOutputList(openAccountIds);
        addValidCommandsToOutputList();
        generateOutputStringList();
        return outputStringList;
    }
}
