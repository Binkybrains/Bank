package banking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BankTest {
    Bank bank;

    @BeforeEach
    void setup() {
        bank = new Bank();
    }

    @Test
    void bank_has_no_accounts_initially() {
        assertEquals(bank.numberOfAccounts(), 0);
    }

    @Test
    void create_savings_account() {
        assertTrue((bank.createAccount("savings", 76382987, 0.5)) != null); /* valid creation */
        assertTrue((bank.createAccount("savings", 76382987, 0.5, 80.0)) == null); /* too many arguments */
        assertEquals(bank.getAccount(76382987).getBalance(), 0.0); /* savings account has 0 balance initially */
    }

    @Test
    void create_checking_account() {
        assertTrue((bank.createAccount("checking", 76382987, 0.5)) != null); /* valid creation */
        assertTrue((bank.createAccount("checking", 76382987, 0.5, 80.0)) == null); /* too many arguments */
        assertEquals(bank.getAccount(76382987).getBalance(), 0.0); /* checking account has 0 balance initially */
    }

    @Test
    void create_cd_account() {
        assertTrue((bank.createAccount("cd", 76382987, 0.5, 80.0)) != null); /* valid creation */
        assertTrue((bank.createAccount("cd", 76382987, 0.5)) == null); /* too little arguments */
        assertEquals(bank.getAccount(76382987).getBalance(), 80.0); /* cd account has the supplied balance initially */
    }

    @Test
    void locate_checking_account_in_bank() {
        Account checkingAccount = bank.createAccount("checking", 76382987, 0.5);
        assertEquals(bank.getAccount(checkingAccount.accountId()), checkingAccount);
    }

    @Test
    void locate_savings_account_in_bank() {
        Account savingsAccount = bank.createAccount("savings", 83729374, 0.5);
        assertEquals(bank.getAccount(savingsAccount.accountId()), savingsAccount);
    }

    @Test
    void locate_cd_account_in_bank() {
        Account cdAccount = bank.createAccount("cd", 63728364, 0.5, 5000.0);
        assertEquals(bank.getAccount(cdAccount.accountId()), cdAccount);
    }

    @Test
    void deposit_into_account_in_bank() {
        bank.createAccount("checking", 63728364, 0.5);
        bank.createAccount("savings", 12345678, 0.5);
        bank.getAccount(63728364).deposit(500);
        bank.getAccount(12345678).deposit(500);

        assertEquals(bank.getAccount(63728364).getBalance(), 500);
        assertEquals(bank.getAccount(12345678).getBalance(), 500);
    }

    @Test
    void withdraw_from_account_in_bank() {
        Account checking = bank.createAccount("checking", 63728364, 0.5);
        Account savings = bank.createAccount("savings", 12345678, 0.5);
        bank.createAccount("cd", 87654321, 0.5, 3000);
        checking.deposit(500);
        savings.deposit(500);
        bank.getAccount(63728364).withdraw(300);
        bank.getAccount(12345678).withdraw(300);
        bank.getAccount(87654321).withdraw(3000);

        assertEquals(bank.getAccount(63728364).getBalance(), 200);
        assertEquals(bank.getAccount(12345678).getBalance(), 200);
        assertEquals(bank.getAccount(87654321).getBalance(), 0);
    }

    @Test
    void transfer_from_accounts_in_bank() {
        Account checking = bank.createAccount("checking", 63728364, 0.5);
        Account savings = bank.createAccount("savings", 12345678, 0.5);
        checking.deposit(500);
        savings.deposit(500);

        bank.getAccount(12345678).transfer(bank.getAccount(63728364), 200);

        assertEquals(bank.getAccount(63728364).getBalance(), 700);
        assertEquals(bank.getAccount(12345678).getBalance(), 300);
    }

    @Test
    void pass_time_account_is_removed_balance_0() {
        bank.createAccount("savings", 24567854, 1.0);
        bank.createAccount("checking", 64372618, 1.0);
        bank.getAccount(64372618).setBalance(200);
        bank.passTime(1);

        assertFalse(bank.getAccount(64372618) == null);
        assertEquals(bank.getAccount(24567854), null);
    }

    @Test
    void pass_time_accounts_still_exist() {
        bank.createAccount("checking", 64372618, 1.0);
        bank.getAccount(64372618).setBalance(200);

        bank.passTime(1);
        assertFalse(bank.getAccount(64372618) == null);

        bank.passTime(30);
        assertFalse(bank.getAccount(64372618) == null);
    }

    @Test
    void minimum_fee_deducted() {
        bank.createAccount("checking", 64372618, 0);
        bank.createAccount("savings", 12345678, 0);
        bank.createAccount("cd", 87654321, 0, 1500);

        bank.getAccount(64372618).setBalance(100);
        bank.getAccount(12345678).setBalance(20);

        bank.passTime(1);
        assertEquals(bank.getAccount(64372618).getBalance(), 75);
        assertEquals(bank.getAccount(12345678).getBalance(), 0);
        assertEquals(bank.getAccount(87654321).getBalance(), 1500);

        bank.passTime(2);
        assertEquals(bank.getAccount(64372618).getBalance(), 25);
    }

    @Test
    void apr_correct() {
        bank.createAccount("checking", 64372618, 1.0);
        bank.createAccount("savings", 12345678, 5.0);
        bank.createAccount("cd", 87654321, 10, 1500);

        bank.getAccount(64372618).setBalance(500);
        bank.getAccount(12345678).setBalance(500);

        bank.passTime(2);

        assertEquals(bank.getAccount(64372618).getBalance(), 500.8336805555556);
        assertEquals(bank.getAccount(12345678).getBalance(), 504.17534722222223);
        assertEquals(bank.getAccount(87654321).getBalance(), 1602.9657875333896);

    }

    @Test
    void time_of_account_is_accurate() {
        bank.createAccount("checking", 64372618, 0);
        bank.getAccount(64372618).setBalance(500);

        bank.passTime(1);

        assertEquals(bank.getAccount(64372618).getTime(), 1);

        bank.passTime(5);
        assertEquals(bank.getAccount(64372618).getTime(), 6);
    }

    @Test
    void withdraws_this_month_reset() {
        bank.createAccount("savings", 64372618, 0);
        bank.getAccount(64372618).setBalance(500);

        assertEquals(bank.getAccount(64372618).getWithdrawsThisMonth(), 0);

        bank.getAccount(64372618).withdraw(25);

        assertEquals(bank.getAccount(64372618).getWithdrawsThisMonth(), 1);

        bank.passTime(1);

        assertEquals(bank.getAccount(64372618).getWithdrawsThisMonth(), 0);
    }
}
