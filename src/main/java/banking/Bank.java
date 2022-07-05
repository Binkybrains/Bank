package banking;

import java.util.*;

public class Bank {

    private Hashtable accounts = new Hashtable();

    public int numberOfAccounts() {
        return accounts.size();
    }

    public Account getAccount(int id) {
        Account account = (Account) accounts.get(id);
        return account;
    }

    public Set<Integer> getOpenAccountIds() {
        return accounts.keySet();
    }

    /* creates accounts for two parameter checking and savings commands */
    public Account createAccount(String type, int accountId, double apr) {
        Account account = null;
        switch (type.toLowerCase()) {
            case "checking":
                account = new Checking(accountId, apr);
                break;
            case "savings":
                account = new Savings(accountId, apr);
                break;
            default:
                return null;
        }
        if (account != null) {
            accounts.put(account.accountId(), account);
        }
        return account;
    }

    /* creates account for three parameter cd command */
    public Account createAccount(String type, int accountId, double apr, double amount) {
        if (type.toLowerCase().equals("cd")) {
            Cd account = new Cd(accountId, apr, amount);
            accounts.put(account.accountId(), account);
            return account;
        } else {
            return null;
        }
    }

    public void passTime(int months) {
        for (int i = 0; i < months; i++) {
            List<Integer> toRemove = new ArrayList<>();
            Set<Integer> setOfKeys = accounts.keySet();
            Iterator<Integer> itr = setOfKeys.iterator();

            while (itr.hasNext()) {
                int key = itr.next();
                Account account = (Account) accounts.get(key);
                double balance = account.getBalance();

                if (balance == 0) {
                    toRemove.add(key);
                }
                if (balance <= 100) {
                    if (balance > 25) {
                        account.setBalance(balance - 25);
                    } else {
                        account.setBalance(0);
                    }
                }
                if (account.getAccountType().equals("cd")) {
                    for (int x = 0; x < 4; x++) {
                        balance = account.getBalance();
                        double apr = ((account.getApr() / 100.0) / 12) * balance;
                        account.setBalance(balance + apr);
                    }
                } else {
                    balance = account.getBalance();
                    double apr = ((account.getApr() / 100.0) / 12) * balance;
                    account.setBalance(balance + apr);
                }
                account.addTime(1);
                account.resetWithdrawsThisMonth();
            }

            for (int r : toRemove) {
                accounts.remove(r);
            }
        }
    }
}
