package banking;

public class Account {
    public int withdrawsThisMonth;
    protected String accountType;
    protected int accountId;
    protected double apr;
    protected double balance;
    protected int time;


    Account(String accountType, int accountId, double apr) {
        this.accountType = accountType; /*Either 'checking', 'savings' or 'cd' (all lowercase) */
        this.accountId = accountId;
        this.apr = apr;
        this.time = 0;
        this.withdrawsThisMonth = 0;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public int getWithdrawsThisMonth() {
        return withdrawsThisMonth;
    }

    public int getTime() {
        return time;
    }

    public String getAccountType() {
        return accountType;
    }

    public void resetWithdrawsThisMonth() {
        this.withdrawsThisMonth = 0;
    }

    public double getApr() {
        return apr;
    }

    public void addTime(int months) {
        time += months;
    }

    public void incrementWithdrawsThisMonth() {
        withdrawsThisMonth++;
    }


    public int accountId() {
        return accountId;
    }

    public void deposit(double amount) {
        setBalance(getBalance() + amount);
    }

    public void withdraw(double amount) {
        if (getBalance() >= amount) {
            setBalance(getBalance() - amount);
        } else {
            setBalance(0);
        }
    }

    public void transfer(Account account, double amount) {
        if (amount > balance) {
            amount = balance;
        }
        withdraw(amount);
        account.deposit(amount);
    }
}
