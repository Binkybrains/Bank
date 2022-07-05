package banking;/*
Nelly Duke
ND659
Section- 001
 */

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AccountTest {

    Bank bank;
    Account checkingAccount;
    Account cdAccount;
    Account savingsAccount;

    @BeforeEach
    void setup() {
        bank = new Bank();
        checkingAccount = bank.createAccount("checking", 76382987, 0.5);
        savingsAccount = bank.createAccount("savings", 83749274, 0.5);
        cdAccount = bank.createAccount("cd", 63728364, 0.5, 1500);
    }

    @Test
    void basic_checking_deposit() {
        checkingAccount.deposit(300.0);
        assertEquals(checkingAccount.getBalance(), 300.0);
    }

    @Test
    void deposit_exactly_0_1000() {
        checkingAccount.deposit(0);
        assertEquals(checkingAccount.getBalance(), 0);
        checkingAccount.deposit(1000);
        assertEquals(checkingAccount.getBalance(), 1000);
    }

    @Test
    void checking_withdraw() {
        checkingAccount.deposit(300.0);
        checkingAccount.withdraw(200.0);
        assertEquals(checkingAccount.getBalance(), 100.0);
        checkingAccount.withdraw(200.0); /* overdrawing results in 0 balance */
        assertEquals(checkingAccount.getBalance(), 0);
    }

    @Test
    void checking_withdraw_exactly_0_400() {
        checkingAccount.deposit(1000.0);
        checkingAccount.withdraw(0);
        assertEquals(checkingAccount.getBalance(), 1000);
        checkingAccount.withdraw(400);
        assertEquals(checkingAccount.getBalance(), 600);
    }

    @Test
    void checking_withdraw_full_amount() {
        checkingAccount.deposit(300.0);
        checkingAccount.withdraw(300);
        assertEquals(checkingAccount.getBalance(), 0);
    }

    @Test
    void basic_savings_deposit() {
        savingsAccount.deposit(300.0);
        assertEquals(savingsAccount.getBalance(), 300.0);
    }

    @Test
    void savings_deposit_exactly_0_2500() {
        savingsAccount.deposit(0);
        assertEquals(savingsAccount.getBalance(), 0);
        savingsAccount.deposit(2500.0);
        assertEquals(savingsAccount.getBalance(), 2500.0);
    }

    @Test
    void savings_withdraw() {
        savingsAccount.deposit(300.0);
        savingsAccount.withdraw(200.0);
        assertEquals(savingsAccount.getBalance(), 100.0);
        savingsAccount.withdraw(500.0); /* overdrawing results in 0 balance */
        assertEquals(savingsAccount.getBalance(), 0);
    }

    @Test
    void savings_withdraw_exactly_0_1000() {
        savingsAccount.deposit(2000.0);
        savingsAccount.withdraw(0);
        assertEquals(savingsAccount.getBalance(), 2000);
        savingsAccount.withdraw(1000);
        assertEquals(savingsAccount.getBalance(), 1000);
    }

    @Test
    void savingsWithdraw_full_amount() {
        savingsAccount.deposit(400.0);
        savingsAccount.withdraw(400);
        savingsAccount.withdraw(0);
    }

    @Test
    void savings_account_withdraws_this_month() {
        assertEquals(savingsAccount.getWithdrawsThisMonth(), 0);
        savingsAccount.deposit(400.0);
        savingsAccount.withdraw(100);
        assertEquals(savingsAccount.getWithdrawsThisMonth(), 1);
    }

    @Test
    void valid_cd_withdraw() {
        cdAccount.withdraw(1500);
        assertEquals(cdAccount.getBalance(), 0);
        cdAccount.withdraw(2000); /* overdrawing results in 0 */
        assertEquals(cdAccount.getBalance(), 0);
    }

    @Test
    void basic_transfer() {
        savingsAccount.deposit(1000);
        checkingAccount.deposit(1000);

        savingsAccount.transfer(checkingAccount, 500);

        assertEquals(savingsAccount.getBalance(), 500);
        assertEquals(checkingAccount.getBalance(), 1500);
    }

    @Test
    void transfer_between_two_checking_accounts() {
        Account checkingAccount2 = new Account("checking", 65463729, 1.0);
        checkingAccount.deposit(1000);
        checkingAccount2.deposit(1000);

        checkingAccount.transfer(checkingAccount2, 300);
        assertEquals(checkingAccount.getBalance(), 700);
        assertEquals(checkingAccount2.getBalance(), 1300);
    }

    @Test
    void transfer_between_two_savings_accounts() {
        Account savingsAccount2 = new Account("savings", 65463729, 1.0);
        savingsAccount.deposit(1000);
        savingsAccount2.deposit(1000);

        savingsAccount.transfer(savingsAccount2, 500);
        assertEquals(savingsAccount.getBalance(), 500);
        assertEquals(savingsAccount2.getBalance(), 1500);
    }

    @Test
    void transfer_only_transfers_amount_withdrawn() {
        savingsAccount.deposit(500);
        checkingAccount.deposit(500);

        savingsAccount.transfer(checkingAccount, 1000);
        assertEquals(savingsAccount.getBalance(), 0);
        assertEquals(checkingAccount.getBalance(), 1000);
    }

    @Test
    void transfer_to_savings_still_adds_withdraws_this_month() {
        savingsAccount.deposit(500);
        checkingAccount.deposit(500);
        assertEquals(savingsAccount.getWithdrawsThisMonth(), 0);

        savingsAccount.transfer(checkingAccount, 100);
        assertEquals(savingsAccount.getWithdrawsThisMonth(), 1);
    }

    @Test
    void transfer_0_dollars() {
        savingsAccount.deposit(500);
        checkingAccount.deposit(500);

        savingsAccount.transfer(checkingAccount, 0);
        assertEquals(savingsAccount.getBalance(), 500);
        assertEquals(checkingAccount.getBalance(), 500);
    }

    @Test
    void transfer_into_empty_amount() {
        savingsAccount.deposit(500);
        savingsAccount.transfer(checkingAccount, 200);
        assertEquals(checkingAccount.getBalance(), 200);
    }

    @Test
    void transfer_from_empty_account() {
        checkingAccount.setBalance(500);
        savingsAccount.transfer(checkingAccount, 200);
        assertEquals(savingsAccount.getBalance(), 0);
        assertEquals(checkingAccount.getBalance(), 500);
    }

    @Test
    void transfer_checking_limits() {
        savingsAccount.deposit(5000);
        checkingAccount.deposit(5000);

        savingsAccount.transfer(checkingAccount, 1000);
        assertEquals(checkingAccount.getBalance(), 6000);

        checkingAccount.transfer(savingsAccount, 400);
        assertEquals(checkingAccount.getBalance(), 5600);
    }

    @Test
    void transfer_exact_limits() {
        savingsAccount.deposit(5000);
        checkingAccount.deposit(5000);

        savingsAccount.transfer(checkingAccount, 1000);

        /*maximum deposit is not considered as it would not pass the validator since 2500 is more than the
        max checking or savings withdraw
         */

        assertEquals(savingsAccount.getBalance(), 4000);
    }

}
