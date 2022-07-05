package banking;

import java.util.ArrayList;
import java.util.List;

public class OutputNode {
    OutputNode next;
    int id;
    List<String> validCommands;

    OutputNode(int id) {
        this.id = id;
        this.validCommands = new ArrayList<>();
        this.next = null;
    }

    public int getId() {
        return id;
    }

    public OutputNode getNext() {
        return next;
    }

    public void setNext(OutputNode node) {
        next = node;
    }

    public List<String> getValidCommands() {
        return validCommands;
    }

    public void addValidCommand(String command) {
        validCommands.add(command);
    }


}
