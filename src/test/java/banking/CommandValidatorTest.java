package banking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/*
Most tests are covered in the seperate validators for each command type, this test file
only tests that the combing validate method used by master control correctly calls upon each seperate validator
*/


public class CommandValidatorTest {
    Bank bank;
    CommandValidator commandValidator;

    @BeforeEach
    void setup() {
        bank = new Bank();
        commandValidator = new CommandValidator(bank);
    }

    @Test
    void valid_create_commands() {
        assertTrue(commandValidator.validateCommand("Create checking 76372846 1.0"));
        assertTrue(commandValidator.validateCommand("Create savings 67382735 1.0"));
        assertTrue(commandValidator.validateCommand("Create cd 76472836 1.0 1500"));
        assertTrue(commandValidator.validateCommand("CrEatE cHecKing 92738465 1.0"));
    }

    @Test
    void valid_deposit_commands() {
        bank.createAccount("savings", 12345678, 1.0);
        bank.createAccount("checking", 87654321, 1.0);

        assertTrue(commandValidator.validateCommand("Deposit 12345678 300"));
        assertTrue(commandValidator.validateCommand("Deposit 87654321 400"));
        assertTrue(commandValidator.validateCommand("DEpOsit 12345678 0"));
        assertTrue(commandValidator.validateCommand("dEposit 87654321 1000"));
    }

    @Test
    void valid_withdrawal_commands() {
        bank.createAccount("savings", 12345678, 1.0);
        bank.createAccount("checking", 87654321, 1.0);
        bank.createAccount("cd", 12121212, 1.0, 1500);
        bank.getAccount(87654321).deposit(500);
        bank.getAccount(12121212).addTime(13);

        assertTrue(commandValidator.validateCommand("Withdrawal 12345678 300"));
        assertTrue(commandValidator.validateCommand("Withdrawal 87654321 400"));
        assertTrue(commandValidator.validateCommand("Withdrawal 12121212 1500"));
        assertTrue(commandValidator.validateCommand("WiThDrawAl 12345678 0"));
        assertTrue(commandValidator.validateCommand("wIThDraWal 87654321 200"));
    }

    @Test
    void valid_transfer_commands() {
        bank.createAccount("savings", 12345678, 1.0);
        bank.createAccount("checking", 87654321, 1.0);
        bank.getAccount(87654321).deposit(500);
        bank.getAccount(12345678).deposit(500);

        assertTrue(commandValidator.validateCommand("Transfer 12345678 87654321 300"));
        assertTrue(commandValidator.validateCommand("Transfer 87654321 12345678 400"));
        assertTrue(commandValidator.validateCommand("Transfer 12345678 87654321 1000"));
        assertTrue(commandValidator.validateCommand("tRaNSfEr 87654321 12345678 200"));
    }

    @Test
    void valid_pass_time_commands() {
        assertTrue(commandValidator.validateCommand("Pass 12"));
        assertTrue(commandValidator.validateCommand("Pass 1"));
        assertTrue(commandValidator.validateCommand("Pass 60"));
        assertTrue(commandValidator.validateCommand("pAsS 12"));
    }

    @Test
    void invalid_create_commands() {
        assertFalse(commandValidator.validateCommand("Ceate checking 16527389 1.0")); //misspelled
        assertFalse(commandValidator.validateCommand("Create cd 12354678 1.0")); //cd amount is missing
        assertFalse(commandValidator.validateCommand("Create checking 16527389 1.0 500")); //amount given for non-cd
        assertFalse(commandValidator.validateCommand("Create savings 16527389 30")); //APR too high

        bank.createAccount("checking", 12345678, 1.0);
        assertFalse(commandValidator.validateCommand("Create checking 12345678 1.0")); //account already exists in bank
    }

    @Test
    void invalid_deposit_commands() {
        assertFalse(commandValidator.validateCommand("Deposit 12345678 500")); //id does not exist in bank

        bank.createAccount("checking", 12345678, 1.0);
        bank.createAccount("cd", 87654321, 1.0, 1500);

        assertFalse(commandValidator.validateCommand("Deposit 87654321 500")); //id associated with cd account
        assertFalse(commandValidator.validateCommand("Depsit 12345678 500")); //misspelled
        assertFalse(commandValidator.validateCommand("Deposit 12345678 10000")); //deposit amount too large
        assertFalse(commandValidator.validateCommand("Deposit 12345678 -10")); //deposit amount negative
    }

    @Test
    void invalid_withdrawal_commands() {
        assertFalse(commandValidator.validateCommand("Withdrawal 12345678 300")); //id does not exist in bank

        bank.createAccount("checking", 12345678, 1.0);
        bank.createAccount("cd", 87654321, 1.0, 1500);

        assertFalse(commandValidator.validateCommand("Withdrawal 87654321 500")); //cd account amount under balance
        assertFalse(commandValidator.validateCommand("Withdraw 12345678 300")); //misspelled
        assertFalse(commandValidator.validateCommand("Withdrawal 12345678 10000")); //withdrawal amount too large
        assertFalse(commandValidator.validateCommand("Withdrawal 12345678 -10")); //withdrawal amount negative

        bank.createAccount("savings", 65473826, 1.0);
        bank.getAccount(65473826).withdraw(200);

        assertFalse(commandValidator.validateCommand("Withdrawal 65473826 200")); //already withdrew this month savings
    }

    @Test
    void invalid_transfer_commands() {
        assertFalse(commandValidator.validateCommand("Transfer 12345678 87654321 300")); //account does not exist

        bank.createAccount("checking", 12345678, 1.0);
        bank.createAccount("savings", 87654321, 1.0);
        bank.createAccount("cd", 56273547, 1.0, 1500);

        assertFalse(commandValidator.validateCommand("Transfer 12345678 56273547 300")); //id associated with cd
        assertFalse(commandValidator.validateCommand("Trasnfer 12345678 87654321 300")); //misspelled
        assertFalse(commandValidator.validateCommand("Transfer 12345678 87654321 1000")); //amount wrong
        assertFalse(commandValidator.validateCommand("Transfer 12345678 12345678 200")); // same id

        bank.getAccount(87654321).withdraw(100);
        assertFalse(commandValidator.validateCommand("Transfer 87654321 12345678 300")); //savings already withdrawn
    }

    @Test
    void invalid_pass_time_commands() {
        assertFalse(commandValidator.validateCommand("Pss 10")); //misspelled
        assertFalse(commandValidator.validateCommand("Pass 10.0")); //float given
        assertFalse(commandValidator.validateCommand("Pass 100")); //to large
        assertFalse(commandValidator.validateCommand("Pass 0")); //too small
    }
}
