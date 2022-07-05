package banking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/*
Specific command validation can be found in the test files for each command validator
this file tests to insure masterControl calls those validators correctly,
commands work together properly, and they are outputted correctly
 */

public class MasterControlTest {
    MasterControl masterControl;
    List<String> input;
    Output output;

    @BeforeEach
    void setup() {
        input = new ArrayList<>();
        Bank bank = new Bank();
        output = new Output(bank);
        masterControl = new MasterControl(bank, new CommandValidator(bank), new Processor(bank), output);
    }

    @Test
    void sample_make_sure_this_passes_unchanged_or_you_will_fail() {
        input.add("Create savings 12345678 0.6");
        input.add("Deposit 12345678 700");
        input.add("Deposit 12345678 5000");
        input.add("creAte cHecKing 98765432 0.01");
        input.add("Deposit 98765432 300");
        input.add("Transfer 98765432 12345678 300");
        input.add("Pass 1");
        input.add("Create cd 23456789 1.2 2000");
        List<String> actual = masterControl.start(input);

        assertEquals(5, actual.size());
        assertEquals("Savings 12345678 1000.50 0.60", actual.get(0));
        assertEquals("Deposit 12345678 700", actual.get(1));
        assertEquals("Transfer 98765432 12345678 300", actual.get(2));
        assertEquals("Cd 23456789 2000.00 1.20", actual.get(3));
        assertEquals("Deposit 12345678 5000", actual.get(4));
    }

    @Test
    void empty_input() {
        List<String> actual = masterControl.start(input);
        assertEquals(0, actual.size());
    }

    @Test
    void valid_simple_create_commands() {
        input.add("Create checking 12345678 1.0");
        input.add("Create savings 87654321 1.0");
        input.add("Create cd 67837462 1.0 1500");

        List<String> actual = masterControl.start(input);

        assertEquals(3, actual.size());
        assertEquals(actual.get(0), "Checking 12345678 0.00 1.00");
        assertEquals(actual.get(1), "Savings 87654321 0.00 1.00");
        assertEquals(actual.get(2), "Cd 67837462 1500.00 1.00");
    }

    @Test
    void typo_in_create_command() {
        input.add("Craete savings 983746284 0.5");

        List<String> results = masterControl.start(input);
        assertEquals(results.size(), 1);
        assertEquals(results.get(0), "Craete savings 983746284 0.5");
    }

    @Test
    void case_insensitive_creates() {
        input.add("CrEatE cheCkiNg 12345678 1.0");
        input.add("crEatE saViNgs 87654321 1.0");
        input.add("CrEaTe cD 67837462 1.0 1500");

        List<String> actual = masterControl.start(input);

        assertEquals(3, actual.size());
        assertEquals(actual.get(0), "Checking 12345678 0.00 1.00");
        assertEquals(actual.get(1), "Savings 87654321 0.00 1.00");
        assertEquals(actual.get(2), "Cd 67837462 1500.00 1.00");
    }

    @Test
    void invalid_create_accounts_same_id() {
        input.add("Create savings 12345678 0.5");
        input.add("Create checking 12345678 0.5");

        List<String> results = masterControl.start(input);
        assertEquals(2, results.size());
        assertEquals(results.get(0), "Savings 12345678 0.00 0.50");
        assertEquals(results.get(1), "Create checking 12345678 0.5");
    }

    @Test
    void new_account_can_be_created_after_old_deleted() {
        input.add("Create savings 12345678 0.5");
        input.add("Pass 1");
        input.add("Create checking 12345678 0.5");

        List<String> results = masterControl.start(input);
        assertEquals(1, results.size());
        assertEquals(results.get(0), "Checking 12345678 0.00 0.50");
    }


    @Test
    void typo_in_deposit_command() {
        input.add("Depiosit 74638462 500");

        List<String> results = masterControl.start(input);
        assertEquals(results.size(), 1);
        assertEquals(results.get(0), "Depiosit 74638462 500");
    }

    @Test
    void invalid_deposit_non_existent_id() {
        input.add("Deposit 12345678 800");

        List<String> results = masterControl.start(input);
        assertEquals(results.size(), 1);
        assertEquals(results.get(0), "Deposit 12345678 800");
    }

    @Test
    void valid_deposit_into_checking_and_savings() {
        input.add("Create checking 12345678 1.0");
        input.add("Create savings 87654321 1.0");
        input.add("Deposit 12345678 500");
        input.add("Deposit 87654321 500");

        List<String> results = masterControl.start(input);
        assertEquals(results.size(), 4);
        assertEquals(results.get(0), "Checking 12345678 500.00 1.00");
        assertEquals(results.get(1), "Deposit 12345678 500");
        assertEquals(results.get(2), "Savings 87654321 500.00 1.00");
        assertEquals(results.get(3), "Deposit 87654321 500");

    }

    @Test
    void cd_accounts_cannot_be_deposited_into() {
        input.add("Create cd 12345678 1.0 1500");
        input.add("Deposit 12345678 500");

        List<String> results = masterControl.start(input);
        assertEquals(results.size(), 2);
        assertEquals(results.get(0), "Cd 12345678 1500.00 1.00");
        assertEquals(results.get(1), "Deposit 12345678 500");
    }

    @Test
    void multiple_deposits_into_one_account() {
        input.add("Create checking 12345678 1.0");
        input.add("Deposit 12345678 500");
        input.add("Deposit 12345678 500");

        List<String> results = masterControl.start(input);
        assertEquals(results.size(), 3);

        assertEquals(results.get(0), "Checking 12345678 1000.00 1.00");
        assertEquals(results.get(1), "Deposit 12345678 500");
        assertEquals(results.get(2), "Deposit 12345678 500");
    }

    @Test
    void max_deposit_account_unchanged() {
        input.add("Create checking 12345678 1.0");
        input.add("Deposit 12345678 1500");

        List<String> results = masterControl.start(input);
        assertEquals(results.size(), 2);
        assertEquals(results.get(0), "Checking 12345678 0.00 1.00");
    }

    @Test
    void valid_withdraws() {
        input.add("Create checking 12345678 1.0");
        input.add("Create savings 87654321 1.0");
        input.add("Deposit 12345678 500");
        input.add("Deposit 87654321 500");
        input.add("Withdrawal 12345678 100");
        input.add("Withdrawal 87654321 100");

        List<String> results = masterControl.start(input);
        assertEquals(results.size(), 6);
        assertEquals(results.get(0), "Checking 12345678 400.00 1.00");
        assertEquals(results.get(3), "Savings 87654321 400.00 1.00");
    }

    @Test
    void valid_withdraws_balance_zero() {
        input.add("Create checking 12345678 1.0");
        input.add("Withdrawal 12345678 100");
        List<String> results = masterControl.start(input);
        assertEquals(results.size(), 2);
        assertEquals(results.get(0), "Checking 12345678 0.00 1.00");
    }

    @Test
    void multiple_withdraws() {
        input.add("Create checking 12345678 1.0");
        input.add("Deposit 12345678 500");
        input.add("Withdrawal 12345678 100");
        input.add("Withdrawal 12345678 100");
        List<String> results = masterControl.start(input);
        assertEquals(results.size(), 4);
        assertEquals(results.get(0), "Checking 12345678 300.00 1.00");

    }

    @Test
    void invalid_two_savings_withdraws_same_month() {
        input.add("Create savings 12345678 1.0");
        input.add("Deposit 12345678 500");
        input.add("Withdrawal 12345678 100");
        input.add("Withdrawal 12345678 100");
        List<String> results = masterControl.start(input);
        assertEquals(results.size(), 4);
        assertEquals(results.get(0), "Savings 12345678 400.00 1.00");
        assertEquals(output.getInvalidCommands().get(0), "Withdrawal 12345678 100");
    }

    @Test
    void valid_two_savings_withdraws_2_months_withdraw() {
        input.add("Create savings 12345678 0.0");
        input.add("Deposit 12345678 500");
        input.add("Withdrawal 12345678 100");
        input.add("Pass 1");
        input.add("Withdrawal 12345678 100");
        List<String> results = masterControl.start(input);
        assertEquals(results.size(), 4);
        assertEquals(results.get(0), "Savings 12345678 300.00 0.00");
        assertEquals(output.getInvalidCommands().size(), 0);
    }

    @Test
    void amount_more_than_balance_withdraw() {
        input.add("Create savings 12345678 1.0");
        input.add("Deposit 12345678 200");
        input.add("Withdrawal 12345678 400");
        List<String> results = masterControl.start(input);
        assertEquals(results.size(), 3);
        assertEquals(results.get(0), "Savings 12345678 0.00 1.00");

    }

    @Test
    void valid_cd_withdraw() {
        input.add("Create cd 12345678 0.0 1500");
        input.add("Create cd 87654321 0.0 1500");
        input.add("Pass 15");
        input.add("Withdrawal 12345678 1500");
        input.add("Withdrawal 87654321 5000");

        List<String> results = masterControl.start(input);
        assertEquals(results.size(), 4);
        assertEquals(results.get(0), "Cd 12345678 0.00 0.00");
        assertEquals(results.get(2), "Cd 87654321 0.00 0.00");
    }

    @Test
    void amount_less_than_cd_withdraw() {
        input.add("Create cd 12345678 0.0 1500");
        input.add("Pass 12");
        input.add("Withdraw 12345678 500");

        List<String> results = masterControl.start(input);
        assertEquals(results.size(), 2);
        assertEquals(results.get(0), "Cd 12345678 1500.00 0.00");
    }

    @Test
    void has_not_been_12_months_cd() {
        input.add("Create cd 12345678 0.0 1500");
        input.add("Pass 1");
        input.add("Withdraw 12345678 1500");

        List<String> results = masterControl.start(input);
        assertEquals(results.size(), 2);
        assertEquals(results.get(0), "Cd 12345678 1500.00 0.00");

    }

    @Test
    void valid_transfer_commands() {
        input.add("Create checking 12345678 1.0");
        input.add("Create checking 87654321 1.0");
        input.add("Create savings 12121212 1.0");
        input.add("Create savings 76767676 1.0");
        input.add("Deposit 12345678 1000");
        input.add("Deposit 87654321 1000");
        input.add("Deposit 12121212 1000");
        input.add("Deposit 76767676 1000");
        input.add("Transfer 12345678 87654321 300"); //checking to checking
        input.add("Transfer 87654321 12121212 300"); //checking to savings
        input.add("Transfer 12121212 76767676 300"); //savings to savings
        input.add("Transfer 76767676 12345678 300"); //savings to checking

        List<String> results = masterControl.start(input);
        assertEquals(results.size(), 16);
        assertEquals(results.get(0), "Checking 12345678 1000.00 1.00");
        assertEquals(results.get(4), "Checking 87654321 1000.00 1.00");
        assertEquals(results.get(8), "Savings 12121212 1000.00 1.00");
        assertEquals(results.get(12), "Savings 76767676 1000.00 1.00");
    }

    @Test
    void transfer_shows_up_for_both_accounts() {
        input.add("Create checking 12345678 1.0");
        input.add("Create checking 87654321 1.0");
        input.add("Deposit 12345678 1000");
        input.add("Deposit 87654321 1000");
        input.add("Transfer 12345678 87654321 300");

        List<String> results = masterControl.start(input);
        assertEquals(results.size(), 6);
        assertEquals(results.get(0), "Checking 12345678 700.00 1.00");
        assertEquals(results.get(2), "Transfer 12345678 87654321 300");
        assertEquals(results.get(3), "Checking 87654321 1300.00 1.00");
        assertEquals(results.get(5), "Transfer 12345678 87654321 300");
    }

    @Test
    void transfer_after_withdraw_savings() {
        input.add("Create savings 12345678 1.0");
        input.add("Create checking 87654321 1.0");
        input.add("Deposit 12345678 1000");
        input.add("Withdrawal 12345678 0");
        input.add("Transfer 12345678 87654321 300");

        List<String> results = masterControl.start(input);
        assertEquals(results.get(0), "Savings 12345678 1000.00 1.00");
        assertEquals(output.getInvalidCommands().get(0), "Transfer 12345678 87654321 300");
    }

    @Test
    void transfer_after_withdraw_2_months_savings() {
        input.add("Create savings 12345678 0.0");
        input.add("Create checking 87654321 0.0");
        input.add("Deposit 12345678 1000");
        input.add("Deposit 87654321 1000");
        input.add("Withdrawal 12345678 0");
        input.add("Pass 1");
        input.add("Transfer 12345678 87654321 300");

        List<String> results = masterControl.start(input);
        assertEquals(results.get(0), "Savings 12345678 700.00 0.00");
        assertEquals(output.getInvalidCommands().size(), 0);
    }

    @Test
    void transfer_twice() {
        input.add("Create checking 12345678 1.0");
        input.add("Create checking 87654321 1.0");
        input.add("Deposit 12345678 1000");
        input.add("Deposit 87654321 1000");
        input.add("Transfer 12345678 87654321 300");
        input.add("Transfer 12345678 87654321 300");

        List<String> results = masterControl.start(input);
        assertEquals(results.size(), 8);
        assertEquals(results.get(0), "Checking 12345678 400.00 1.00");
        assertEquals(results.get(4), "Checking 87654321 1600.00 1.00");

    }

    @Test
    void transfer_cd() {
        input.add("Create cd 12345678 1.0 1500");
        input.add("Create checking 87654321 1.0");
        input.add("Transfer 12345678 87654321 300");
        input.add("Transfer 87654321 12345678 300");

        List<String> results = masterControl.start(input);
        assertEquals(results.get(0), "Cd 12345678 1500.00 1.00");
        assertEquals(output.getInvalidCommands().size(), 2);
        assertEquals(results.get(2), "Transfer 12345678 87654321 300");
        assertEquals(results.get(3), "Transfer 87654321 12345678 300");
    }

    @Test
    void transfer_over_account_balance() {
        input.add("Create checking 12345678 1.0");
        input.add("Create checking 87654321 1.0");
        input.add("Deposit 12345678 200");
        input.add("Transfer 12345678 87654321 300");

        List<String> results = masterControl.start(input);
        assertEquals(results.get(0), "Checking 12345678 0.00 1.00");
        assertEquals(results.get(3), "Checking 87654321 200.00 1.00");

    }

    @Test
    void transfer_amount_valid_for_savings_invalid_for_checking() {
        input.add("Create checking 12345678 1.0");
        input.add("Create savings 87654321 1.0");
        input.add("Transfer 12345678 87654321 500");

        List<String> results = masterControl.start(input);
        assertEquals(output.getInvalidCommands().size(), 1);
        assertEquals(output.getInvalidCommands().get(0), "Transfer 12345678 87654321 500");
    }

    @Test
    void transfer_same_account() {
        input.add("Create checking 12345678 1.0");
        input.add("Deposit 12345678 300");
        input.add("Transfer 12345678 12345678 100");

        List<String> results = masterControl.start(input);
        assertEquals(results.size(), 3);
        assertEquals(results.get(0), "Checking 12345678 300.00 1.00");
        assertEquals(results.get(2), "Transfer 12345678 12345678 100");
    }

    @Test
    void account_closed_if_balance_zero() {
        input.add("Create checking 12345678 1.0");
        input.add("Pass 1");

        List<String> results = masterControl.start(input);
        assertEquals(results.size(), 0);
    }

    @Test
    void multiple_accounts_closed() {
        input.add("Create checking 12345678 1.0");
        input.add("Create savings 87654321 1.0");
        input.add("Pass 1");

        List<String> results = masterControl.start(input);
        assertEquals(results.size(), 0);
    }

    @Test
    void withdraw_to_zero_then_pass_time() {
        input.add("Create checking 12345678 1.0");
        input.add("Deposit 12345678 200");
        input.add("Withdrawal 12345678 200");
        input.add("Pass 1");

        List<String> results = masterControl.start(input);
        assertEquals(results.size(), 0);
    }

    @Test
    void commands_invalid_if_account_closed() {
        input.add("Create checking 12345678 1.0");
        input.add("Create savings 87654321 1.0");
        input.add("Pass 1");
        input.add("Deposit 12345678 200");
        input.add("Withdrawal 87654321 300");
        input.add("Transfer 12345678 87654321 300");

        List<String> results = masterControl.start(input);
        assertEquals(results.size(), 3);
        assertEquals(output.getInvalidCommands().size(), 3);
        assertEquals(output.getInvalidCommands().get(0), "Deposit 12345678 200");
        assertEquals(output.getInvalidCommands().get(1), "Withdrawal 87654321 300");
        assertEquals(output.getInvalidCommands().get(2), "Transfer 12345678 87654321 300");
    }

    @Test
    void account_closed_if_minimun_deducted_continues() {
        input.add("Create checking 12345678 0.0");
        input.add("Deposit 12345678 100");
        input.add("Pass 5");

        List<String> results = masterControl.start(input);
        assertEquals(results.size(), 0);
    }

    @Test
    void balance_less_than_minimum_deduct_fee() {
        input.add("Create checking 12345678 0.0");
        input.add("Deposit 12345678 20");
        input.add("Pass 1");

        List<String> results = masterControl.start(input);
        assertEquals(results.size(), 2);
        assertEquals(results.get(0), "Checking 12345678 0.00 0.00");
    }

    @Test
    void account_stays_unchanged_if_apr_zero() {
        input.add("Create checking 12345678 0.0");
        input.add("Deposit 12345678 500");
        input.add("Pass 1");

        List<String> results = masterControl.start(input);
        assertEquals(results.size(), 2);
        assertEquals(results.get(0), "Checking 12345678 500.00 0.00");
    }

    @Test
    void valid_apr_calculations() {
        input.add("Create checking 12345678 5.0");
        input.add("Create savings 87654321 2.0");
        input.add("Create cd 12121212 10.0 1500");
        input.add("Deposit 12345678 500");
        input.add("Deposit 87654321 500");
        input.add("Pass 1");

        List<String> results = masterControl.start(input);
        assertEquals(results.size(), 5);
        assertEquals(results.get(0), "Checking 12345678 502.08 5.00");
        assertEquals(results.get(2), "Savings 87654321 500.83 2.00");
        assertEquals(results.get(4), "Cd 12121212 1550.62 10.00");

    }

    @Test
    void apr_calculations_more_than_one_pass_time() {
        input.add("Create checking 12345678 5.0");
        input.add("Deposit 12345678 500");
        input.add("Create cd 12121212 10.0 1500");
        input.add("Pass 1");
        input.add("Pass 5");
        input.add("Pass 10");

        List<String> results = masterControl.start(input);
        assertEquals(results.size(), 3);
        assertEquals(results.get(0), "Checking 12345678 534.39 5.00");
        assertEquals(results.get(2), "Cd 12121212 2551.26 10.00");

    }

    @Test
    void apr_balance_zero() {
        input.add("Create checking 12345678 5.0");
        input.add("Deposit 12345678 25");
        input.add("Pass 1");

        List<String> results = masterControl.start(input);
        assertEquals(results.size(), 2);
        assertEquals(results.get(0), "Checking 12345678 0.00 5.00");
    }
}