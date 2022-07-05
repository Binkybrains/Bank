package banking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WithdrawalCommandTest {
    Bank bank;

    @BeforeEach
    void setup() {
        bank = new Bank();
        bank.createAccount("checking", 12345678, 1.0);
        bank.createAccount("savings", 87654321, 1.0);
        bank.createAccount("cd", 12121212, 1.0, 1000);
    }

    @Test
    void command_name_misspelled() {
        WithdrawalCommandValidator withdrawal = new WithdrawalCommandValidator("Whthdrawal 12345678 200", bank);
        assertFalse(withdrawal.validate());
    }

    @Test
    void case_insensitive_withdraw() {
        WithdrawalCommandValidator withdrawal = new WithdrawalCommandValidator("WithdRawaL 12345678 200", bank);
        assertTrue(withdrawal.validate());
    }

    @Test
    void incorrect_num_of_args() {
        WithdrawalCommandValidator withdrawal1 = new WithdrawalCommandValidator("Withdrawal 12345678", bank); //too little
        WithdrawalCommandValidator withdrawal2 = new WithdrawalCommandValidator("Withdrawal 12345678 200 withdrawal hi", bank); //too many
        assertFalse(withdrawal1.validate());
        assertFalse(withdrawal2.validate());
    }

    @Test
    void args_in_wrong_order() {
        WithdrawalCommandValidator withdrawal = new WithdrawalCommandValidator("12345678 Withdrawal 200", bank);
        assertFalse(withdrawal.validate());
    }

    @Test
    void id_not_int() {
        WithdrawalCommandValidator withdrawal = new WithdrawalCommandValidator("Withdrawal hi 200", bank);
        assertFalse(withdrawal.validate());
    }

    @Test
    void id_not_correct_length() {
        WithdrawalCommandValidator withdrawal1 = new WithdrawalCommandValidator("Withdrawal 1234 200", bank); //too short
        WithdrawalCommandValidator withdrawal2 = new WithdrawalCommandValidator("Withdrawal 123456789 200", bank); //too long

        assertFalse(withdrawal1.validate());
        assertFalse(withdrawal2.validate());
    }

    @Test
    void id_does_not_exist_in_bank() {
        WithdrawalCommandValidator withdrawal = new WithdrawalCommandValidator("Withdrawal 63746251 200", bank);
        assertFalse(withdrawal.validate());
    }

    @Test
    void amount_is_not_int_or_float_cd() {
        WithdrawalCommandValidator withdrawal = new WithdrawalCommandValidator("Withdrawal 12121212 hi", bank);
        assertFalse(withdrawal.validate());
    }


    @Test
    void twelve_months_not_passed_cd() {
        WithdrawalCommandValidator withdrawal = new WithdrawalCommandValidator("Withdrawal 12121212 2000", bank);
        assertFalse(withdrawal.validate());
    }

    @Test
    void exactly_twelve_months_passed_cd() {
        bank.getAccount(12121212).addTime(12);
        WithdrawalCommandValidator withdrawal = new WithdrawalCommandValidator("Withdrawal 12121212 2000", bank);
        assertTrue(withdrawal.validate());
    }

    @Test
    void amount_less_than_amount_in_cd() {
        bank.getAccount(12121212).addTime(15);
        WithdrawalCommandValidator withdrawal = new WithdrawalCommandValidator("Withdrawal 12121212 200", bank);
        assertFalse(withdrawal.validate());
    }

    @Test
    void amount_equal_amount_in_cd() {
        bank.getAccount(12121212).addTime(15);
        WithdrawalCommandValidator withdrawal = new WithdrawalCommandValidator("Withdrawal 12121212 1000", bank);
        assertTrue(withdrawal.validate());
    }

    @Test
    void amount_more_than_amount_in_cd() {
        bank.getAccount(12121212).addTime(15);
        WithdrawalCommandValidator withdrawal = new WithdrawalCommandValidator("Withdrawal 12121212 5000", bank);
        assertTrue(withdrawal.validate());
    }

    @Test
    void valid_amount_is_float_cd() {
        bank.getAccount(12121212).addTime(15);
        WithdrawalCommandValidator withdrawal = new WithdrawalCommandValidator("Withdrawal 12121212 1000.0", bank);
        assertTrue(withdrawal.validate());
    }

    @Test
    void amount_is_not_or_float_int_checking_savings() {
        WithdrawalCommandValidator withdrawal1 = new WithdrawalCommandValidator("Withdrawal 12345678 hi", bank); //checking
        WithdrawalCommandValidator withdrawal2 = new WithdrawalCommandValidator("Withdrawal 87654321 hi", bank); //savings
        assertFalse(withdrawal1.validate());
        assertFalse(withdrawal2.validate());
    }

    @Test
    void valid_amount_is_float_checking_savings() {
        bank.getAccount(12345678).setBalance(1500);
        bank.getAccount(87654321).setBalance(5000);

        WithdrawalCommandValidator withdrawal1 = new WithdrawalCommandValidator("Withdrawal 12345678 100.0", bank); //checking
        WithdrawalCommandValidator withdrawal2 = new WithdrawalCommandValidator("Withdrawal 87654321 100.0", bank); //savings

        assertTrue(withdrawal1.validate());
        assertTrue(withdrawal2.validate());
    }

    @Test
    void checking_amount_not_valid() {
        WithdrawalCommandValidator withdrawal1 = new WithdrawalCommandValidator("Withdrawal 12345678 500", bank);
        WithdrawalCommandValidator withdrawal2 = new WithdrawalCommandValidator("Withdrawal 12345678 -5", bank);
        assertFalse(withdrawal1.validate());
        assertFalse(withdrawal2.validate());
    }

    @Test
    void checking_amount_exact_limits() {
        WithdrawalCommandValidator withdrawal1 = new WithdrawalCommandValidator("Withdrawal 12345678 400", bank);
        WithdrawalCommandValidator withdrawal2 = new WithdrawalCommandValidator("Withdrawal 12345678 0", bank);
        assertTrue(withdrawal1.validate());
        assertTrue(withdrawal2.validate());
    }

    @Test
    void amount_more_than_amount_in_checking() {
        bank.getAccount(12345678).setBalance(100);
        WithdrawalCommandValidator withdrawal = new WithdrawalCommandValidator("Withdrawal 12345678 300", bank);
        assertTrue(withdrawal.validate());
    }

    @Test
    void savings_already_withdrew_this_month() {
        bank.getAccount(87654321).incrementWithdrawsThisMonth();
        WithdrawalCommandValidator withdrawal = new WithdrawalCommandValidator("Withdrawal 87654321 300", bank);
        assertFalse(withdrawal.validate());
    }

    @Test
    void savings_amount_not_valid() {
        WithdrawalCommandValidator withdrawal1 = new WithdrawalCommandValidator("Withdrawal 87654321 1500", bank); //too large
        WithdrawalCommandValidator withdrawal2 = new WithdrawalCommandValidator("Withdrawal 87654321 -5", bank); //below zero
        assertFalse(withdrawal1.validate());
        assertFalse(withdrawal2.validate());
    }

    @Test
    void savings_amount_exact_limits() {
        WithdrawalCommandValidator withdrawal1 = new WithdrawalCommandValidator("Withdrawal 87654321 1000", bank);
        WithdrawalCommandValidator withdrawal2 = new WithdrawalCommandValidator("Withdrawal 87654321 0", bank);
        assertTrue(withdrawal1.validate());
        assertTrue(withdrawal2.validate());
    }

    @Test
    void amount_more_than_amount_in_savings() {
        bank.getAccount(87654321).setBalance(500);
        WithdrawalCommandValidator withdrawal = new WithdrawalCommandValidator("Withdrawal 87654321 700", bank);
        assertTrue(withdrawal.validate());
    }

    @Test
    void valid_commands_with_money_in_account() {
        bank.getAccount(12121212).addTime(15);
        bank.getAccount(12345678).setBalance(1500);
        bank.getAccount(87654321).setBalance(5000);
        WithdrawalCommandValidator withdrawal1 = new WithdrawalCommandValidator("Withdrawal 12345678 200", bank);
        WithdrawalCommandValidator withdrawal2 = new WithdrawalCommandValidator("Withdrawal 87654321 700", bank);
        WithdrawalCommandValidator withdrawal3 = new WithdrawalCommandValidator("Withdrawal 12121212 1000", bank);
        assertTrue(withdrawal1.validate());
        assertTrue(withdrawal2.validate());
        assertTrue(withdrawal3.validate());
    }
}
