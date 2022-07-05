package banking;

import java.util.List;

public class MasterControl {
    Bank bank;
    CommandValidator commandValidator;
    Processor processor;
    Output output;

    MasterControl(Bank bank, CommandValidator commandValidator, Processor processor, Output output) {
        this.bank = bank;
        this.commandValidator = commandValidator;
        this.processor = processor;
        this.output = output;
    }

    public List<String> start(List<String> input) {
        for (String command : input) {
            if (commandValidator.validateCommand(command)) {
                processor.processCommand(command);
                output.addValidCommand(command);
            } else {
                output.addInvalidCommand(command);
            }
        }
        return output.getOutput();
    }

}
