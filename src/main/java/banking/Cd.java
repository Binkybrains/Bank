package banking;

public class Cd extends Account {
    Cd(int accountId, double apr, double amount) {
        super("cd", accountId, apr);
        setBalance(amount);
    }

    @Override
    public void withdraw(double amount) {
        setBalance(0);
    }
}