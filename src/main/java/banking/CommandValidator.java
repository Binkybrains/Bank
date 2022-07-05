package banking;

public class CommandValidator {

    public Bank bank;
    public String command;

    CommandValidator(Bank bank) {
        this.bank = bank;
    }

    protected boolean validateID(String id) {
        String stringID = id;

        int ID;
        try {
            ID = Integer.parseInt(stringID);
        } catch (NumberFormatException e) {
            return false;
        }
        int lengthOfDigitsID = stringID.length();

        return (lengthOfDigitsID == 8);
    }

    protected boolean checkNumArguments(int correctArgumentNumber, String[] commandList) {
        int numArguments = commandList.length;
        return (numArguments == correctArgumentNumber);
    }

    protected boolean validateCommand(String command) {
        CreateCommandValidator createValidator = new CreateCommandValidator(command, bank);
        DepositCommandValidator depositValidator = new DepositCommandValidator(command, bank);
        TransferCommandValidator transferCommandValidator = new TransferCommandValidator(command, bank);
        WithdrawalCommandValidator withdrawalCommandValidator = new WithdrawalCommandValidator(command, bank);
        PassTimeCommandValidator passTimeCommandValidator = new PassTimeCommandValidator();


        return (createValidator.validate() || depositValidator.validate() || transferCommandValidator.validate() ||
                withdrawalCommandValidator.validate() || passTimeCommandValidator.validate(command));
    }

}
