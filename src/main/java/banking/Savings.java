package banking;

public class Savings extends Account {

    Savings(int accountId, double apr) {
        super("savings", accountId, apr);
        setBalance(0.0);
    }

    @Override
    public void withdraw(double amount) {
        super.withdraw(amount);
        incrementWithdrawsThisMonth();
    }
}
