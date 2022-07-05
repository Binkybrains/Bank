package banking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/*
Testing of the functions being called in processor are not in ProcessorTest those can be found in
the AccountTest and BankTest files, this test files ensures that the processor is calling upon those functions
properly
 */

public class ProcessorTest {
    Bank bank;
    Processor processor;

    @BeforeEach
    void setup() {
        bank = new Bank();
        processor = new Processor(bank);
    }

    @Test
    void process_create_checking() {
        processor.processCommand("Create checking 12345678 1.0");
        Account checking = bank.getAccount(12345678);
        assertNotEquals(checking, null);
        assertEquals(checking.getAccountType(), "checking");
        assertEquals(checking.getApr(), 1.0);
        assertEquals(checking.getBalance(), 0);
    }

    @Test
    void process_create_savings() {
        processor.processCommand("Create savings 12345678 1.0");
        Account savings = bank.getAccount(12345678);
        assertNotEquals(savings, null);
        assertEquals(savings.getAccountType(), "savings");
        assertEquals(savings.getApr(), 1.0);
        assertEquals(savings.getBalance(), 0);
    }

    @Test
    void process_create_cd() {
        processor.processCommand("Create cd 12345678 1.0 1500");
        Account cd = bank.getAccount(12345678);
        assertNotEquals(cd, null);
        assertEquals(cd.getAccountType(), "cd");
        assertEquals(cd.getApr(), 1.0);
        assertEquals(cd.getBalance(), 1500);
    }

    @Test
    void process_create_ignore_case() {
        processor.processCommand("crEatE cD 12345678 1.0 1500");
        processor.processCommand("cReAte SaVinGs 87654321 1.0");
        processor.processCommand("cReAte ChEcKiNg 12121212 1.0");

        Account cd = bank.getAccount(12345678);
        Account savings = bank.getAccount(87654321);
        Account checking = bank.getAccount(12121212);

        assertNotEquals(cd, null);
        assertNotEquals(savings, null);
        assertNotEquals(checking, null);
    }

    @Test
    void process_deposit_checking() {
        processor.processCommand("Create checking 12345678 1.0");
        processor.processCommand("Deposit 12345678 500");
        Account checking = bank.getAccount(12345678);

        assertEquals(checking.getBalance(), 500);
    }

    @Test
    void process_deposit_savings() {
        processor.processCommand("Create savings 12345678 1.0");
        processor.processCommand("Deposit 12345678 500");
        Account savings = bank.getAccount(12345678);

        assertEquals(savings.getBalance(), 500);
    }

    @Test
    void process_deposit_ignore_case() {
        processor.processCommand("Create savings 12345678 1.0");
        processor.processCommand("DePosiT 12345678 500");
        Account savings = bank.getAccount(12345678);

        assertEquals(savings.getBalance(), 500);
    }

    @Test
    void process_checking_withdrawal() {
        processor.processCommand("Create checking 12345678 1.0");
        processor.processCommand("Deposit 12345678 500");
        processor.processCommand("Withdrawal 12345678 200");

        Account checking = bank.getAccount(12345678);

        assertEquals(checking.getBalance(), 300);

        processor.processCommand("Withdrawal 12345678 400"); //overdraft is 0
        assertEquals(checking.getBalance(), 0);
    }

    @Test
    void process_savings_withdrawal() {
        processor.processCommand("Create savings 12345678 1.0");
        processor.processCommand("Deposit 12345678 500");
        processor.processCommand("Withdrawal 12345678 600");

        Account savings = bank.getAccount(12345678);
        assertEquals(savings.getBalance(), 0);
        assertEquals(savings.getWithdrawsThisMonth(), 1);
    }

    @Test
    void process_cd_withdrawal() {
        processor.processCommand("Create cd 12345678 1.0 1500");
        processor.processCommand("Withdrawal 12345678 1500");
        Account cd = bank.getAccount(12345678);

        assertEquals(cd.getBalance(), 0);
    }

    @Test
    void process_normal_transfer_outgoing_checking() {
        processor.processCommand("Create checking 12345678 1.0");
        processor.processCommand("Create savings 87654321 1.0");
        processor.processCommand("Deposit 12345678 500");
        processor.processCommand("Transfer 12345678 87654321 300");

        Account checking = bank.getAccount(12345678);
        Account savings = bank.getAccount(87654321);

        assertEquals(checking.getBalance(), 200);
        assertEquals(savings.getBalance(), 300);
    }

    @Test
    void process_transfer_overdraft_outgoing_checking() {
        processor.processCommand("Create checking 12345678 1.0");
        processor.processCommand("Create savings 87654321 1.0");
        processor.processCommand("Deposit 12345678 200");
        processor.processCommand("Transfer 12345678 87654321 300");

        Account checking = bank.getAccount(12345678);
        Account savings = bank.getAccount(87654321);

        assertEquals(checking.getBalance(), 0);
        assertEquals(savings.getBalance(), 200);
    }

    @Test
    void process_normal_transfer_outgoing_savings() {
        processor.processCommand("Create checking 12345678 1.0");
        processor.processCommand("Create savings 87654321 1.0");
        processor.processCommand("Deposit 87654321 500");
        processor.processCommand("Transfer 87654321 12345678 300");

        Account checking = bank.getAccount(12345678);
        Account savings = bank.getAccount(87654321);

        assertEquals(checking.getBalance(), 300);
        assertEquals(savings.getBalance(), 200);

        assertEquals(savings.getWithdrawsThisMonth(), 1);
    }

    @Test
    void process_transfer_overdraft_outgoing_savings() {
        processor.processCommand("Create checking 12345678 1.0");
        processor.processCommand("Create savings 87654321 1.0");
        processor.processCommand("Deposit 87654321 500");
        processor.processCommand("Transfer 87654321 12345678 600");

        Account checking = bank.getAccount(12345678);
        Account savings = bank.getAccount(87654321);

        assertEquals(checking.getBalance(), 500);
        assertEquals(savings.getBalance(), 0);

        assertEquals(savings.getWithdrawsThisMonth(), 1);
    }

    @Test
    void transfer_0_amount() {
        processor.processCommand("Create checking 12345678 1.0");
        processor.processCommand("Create savings 87654321 1.0");
        processor.processCommand("Deposit 12345678 200");
        processor.processCommand("Deposit 87654321 200");
        processor.processCommand("Transfer 12345678 87654321 0");

        Account checking = bank.getAccount(12345678);
        Account savings = bank.getAccount(87654321);

        assertEquals(checking.getBalance(), 200);
        assertEquals(savings.getBalance(), 200);
    }

    @Test
    void transfer_0_in_account() {
        processor.processCommand("Create checking 12345678 1.0");
        processor.processCommand("Create savings 87654321 1.0");
        processor.processCommand("Deposit 87654321 200");
        processor.processCommand("Transfer 12345678 87654321 200");

        Account checking = bank.getAccount(12345678);
        Account savings = bank.getAccount(87654321);

        assertEquals(checking.getBalance(), 0);
        assertEquals(savings.getBalance(), 200);
    }

    @Test
    void pass_time_account_time() {
        processor.processCommand("Create checking 12345678 0.0");
        processor.processCommand("Create savings 87654321 0.0");
        processor.processCommand("Create cd 12121212 0.0 1500");

        Account checking = bank.getAccount(12345678);
        Account savings = bank.getAccount(87654321);
        Account cd = bank.getAccount(12345678);

        checking.deposit(1000);
        savings.deposit(1000);

        processor.processCommand("Pass 10");

        assertEquals(checking.getTime(), 10);
        assertEquals(savings.getTime(), 10);
        assertEquals(cd.getTime(), 10);
    }

    @Test
    void pass_time_remove_0_accounts() {
        processor.processCommand("Create checking 12345678 0.0");
        processor.processCommand("Create savings 87654321 0.0");
        processor.processCommand("Create cd 12121212 0.0 0");

        processor.processCommand("Pass 1");

        Account checking = bank.getAccount(12345678);
        Account savings = bank.getAccount(87654321);
        Account cd = bank.getAccount(12345678);

        assertNull(checking);
        assertNull(savings);
        assertNull(cd);
    }

    @Test
    void pass_time_low_balance_fee() {
        processor.processCommand("Create checking 12345678 0.0");
        processor.processCommand("Create savings 87654321 0.0");
        processor.processCommand("Deposit 12345678 50");
        processor.processCommand("Deposit 87654321 75");

        Account checking = bank.getAccount(12345678);
        Account savings = bank.getAccount(87654321);

        processor.processCommand("Pass 1");

        assertEquals(checking.getBalance(), 25);
        assertEquals(savings.getBalance(), 50);
    }

    @Test
    void pass_time_reset_withdrawals_savings() {
        processor.processCommand("Create savings 87654321 0.0");
        processor.processCommand("Deposit 87654321 500");
        processor.processCommand("Withdrawal 87654321 200");
        Account savings = bank.getAccount(87654321);

        assertEquals(savings.getWithdrawsThisMonth(), 1);

        processor.processCommand("Pass 1");

        assertEquals(savings.getWithdrawsThisMonth(), 0);
    }

    @Test
    void pass_time_APR() {
        processor.processCommand("Create savings 87654321 1.0");
        processor.processCommand("Deposit 87654321 500");
        Account savings = bank.getAccount(87654321);

        processor.processCommand("Pass 1");

        assertEquals(savings.getBalance(), 500.4166666666667);
    }
}
