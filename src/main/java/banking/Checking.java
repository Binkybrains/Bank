package banking;

public class Checking extends Account {
    Checking(int accountId, double apr) {
        super("checking", accountId, apr);
        setBalance(0.0);
    }
}
