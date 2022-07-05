package banking;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DepositCreateCommandTest {

    @Test
    void empty_command() {
        Bank bank = new Bank();
        CreateCommandValidator emptyCreate = new CreateCommandValidator("", bank);
        DepositCommandValidator emptyDeposit = new DepositCommandValidator("", bank);
        assertFalse(emptyCreate.validate());
        assertFalse(emptyDeposit.validate());
    }

    @Test
    void incorrect_command_name() {
        Bank bank = new Bank();
        bank.createAccount("savings", 83746578, 1.0);
        CreateCommandValidator create = new CreateCommandValidator("depsit savings 83746578 1.0", bank);
        DepositCommandValidator deposit = new DepositCommandValidator("crate 8374678 500", bank);
        assertFalse(create.validate());
        assertFalse(deposit.validate());
    }

    @Test
    void case_insensitive_create() {
        Bank bank = new Bank();
        CreateCommandValidator create = new CreateCommandValidator("cReAtE saViNgs 83746578 1.0", bank);
        assertTrue(create.validate());
    }

    @Test
    void case_insensitive_deposit() {
        Bank bank = new Bank();
        bank.createAccount("savings", 83746578, 1.0);
        DepositCommandValidator deposit = new DepositCommandValidator("DepOsiT 83746578 500", bank);
        assertTrue(deposit.validate());
    }

    @Test
    void incorrect_number_of_arguments_create() {
        Bank bank = new Bank();
        CreateCommandValidator create1 = new CreateCommandValidator("83746578 1.0", bank); /* too little */
        CreateCommandValidator create2 = new CreateCommandValidator("create savings 83746572 1.0 800 hello", bank); /* too many */
        assertFalse(create1.validate());
        assertFalse(create2.validate());
    }

    @Test
    void incorrect_number_of_arguments_deposit() {
        Bank bank = new Bank();
        bank.createAccount("savings", 8374678, 1.0);
        DepositCommandValidator deposit1 = new DepositCommandValidator("deposit 8374678", bank); /* too little */
        DepositCommandValidator deposit2 = new DepositCommandValidator("deposit 8374678 500 deposit 500", bank); /*too many*/
        assertFalse(deposit1.validate());
        assertFalse(deposit2.validate());
    }

    @Test
    void valid_create_checking_where_bank_is_empty() {
        Bank bank = new Bank();
        CreateCommandValidator create = new CreateCommandValidator("Create checking 83746578 1.0", bank);
        assertTrue(create.validate());
    }

    @Test
    void valid_create_savings_where_bank_is_empty() {
        Bank bank = new Bank();
        CreateCommandValidator create = new CreateCommandValidator("Create savings 83746578 1.0", bank);
        assertTrue(create.validate());
    }

    @Test
    void valid_create_cd_where_bank_is_empty() {
        Bank bank = new Bank();
        CreateCommandValidator create = new CreateCommandValidator("Create cd 83746578 1.0 3000", bank);
        assertTrue(create.validate());
    }

    @Test
    void valid_create_where_bank_is_not_empty() {
        Bank bank = new Bank();
        bank.createAccount("savings", 28374829, 1.0);
        CreateCommandValidator createSavings = new CreateCommandValidator("Create savings 83746578 1.0", bank);
        CreateCommandValidator createChecking = new CreateCommandValidator("Create checking 83246578 1.0", bank);
        CreateCommandValidator createCD = new CreateCommandValidator("Create cd 83746563 1.0 3000", bank);
        assertTrue(createSavings.validate());
        assertTrue(createChecking.validate());
        assertTrue(createCD.validate());
    }

    @Test
    void deposit_bank_empty() {
        Bank bank = new Bank();
        DepositCommandValidator deposit = new DepositCommandValidator("Deposit 8374678 500", bank);
        assertFalse(deposit.validate());
    }

    @Test
    void valid_deposit_bank_not_empty() {
        Bank bank = new Bank();
        bank.createAccount("savings", 83746787, 1.0);
        DepositCommandValidator deposit = new DepositCommandValidator("Deposit 83746787 500", bank);
        assertTrue(deposit.validate());
    }

    @Test
    void arguments_in_wrong_place_create() {
        Bank bank = new Bank();
        CreateCommandValidator create1 = new CreateCommandValidator("cd Create 83746578 1.0 3000", bank);
        CreateCommandValidator create2 = new CreateCommandValidator("Create cd 1.0 3000 83746578", bank);
        assertFalse(create1.validate());
        assertFalse(create2.validate());
    }

    @Test
    void arguments_in_wrong_place_deposit() {
        Bank bank = new Bank();
        bank.createAccount("savings", 83746778, 1.0);
        DepositCommandValidator deposit = new DepositCommandValidator("Deposit 500 83746778", bank);
        assertFalse(deposit.validate());
    }

    @Test
    void account_type_not_string_create() {
        Bank bank = new Bank();
        CreateCommandValidator create = new CreateCommandValidator("Create 7463 83746578 1.0", bank);
        assertFalse(create.validate());
    }

    @Test
    void account_type_invalid_create() {
        Bank bank = new Bank();
        CreateCommandValidator create = new CreateCommandValidator("Create trustfund 83746578 1.0", bank);
        assertFalse(create.validate());
    }

    @Test
    void amount_given_savings_checking_create() {
        Bank bank = new Bank();
        CreateCommandValidator createSavings = new CreateCommandValidator("Create savings 83746578 1.0 3000", bank);
        CreateCommandValidator createChecking = new CreateCommandValidator("Create checking 83746578 1.0 3000", bank);
        assertFalse(createSavings.validate());
        assertFalse(createChecking.validate());
    }

    @Test
    void no_amount_given_cd_create() {
        Bank bank = new Bank();
        CreateCommandValidator create = new CreateCommandValidator("Create cd 74628376 1.0", bank);
        assertFalse(create.validate());
    }

    @Test
    void amount_not_int_cd_create() {
        Bank bank = new Bank();
        CreateCommandValidator create = new CreateCommandValidator("Create cd 74628376 1.0 hello", bank);
        assertFalse(create.validate());
    }

    @Test
    void amount_invalid_cd_create() {
        Bank bank = new Bank();
        CreateCommandValidator create1 = new CreateCommandValidator("Create cd 74628376 1.0 100", bank); /* less than 1000 */
        CreateCommandValidator create2 = new CreateCommandValidator("Create cd 74628376 1.0 100000", bank); /* more than 10000 */
        assertFalse(create1.validate());
        assertFalse(create2.validate());
    }

    @Test
    void amount_exactly_1000_10000_cd_create() {
        Bank bank = new Bank();
        CreateCommandValidator create1 = new CreateCommandValidator("Create cd 74628376 1.0 1000", bank);
        CreateCommandValidator create2 = new CreateCommandValidator("Create cd 74628376 1.0 10000", bank);
        assertTrue(create1.validate());
        assertTrue(create2.validate());
    }

    @Test
    void id_is_not_int_create() {
        Bank bank = new Bank();
        CreateCommandValidator create = new CreateCommandValidator("Create savings hello 1.0", bank);
        assertFalse(create.validate());
    }

    @Test
    void id_is_not_int_deposit() {
        Bank bank = new Bank();
        bank.createAccount("savings", 8374678, 1.0);
        DepositCommandValidator deposit = new DepositCommandValidator("Deposit hello 500", bank);
        assertFalse(deposit.validate());
    }

    @Test
    void id_not_correct_length_create() {
        Bank bank = new Bank();
        CreateCommandValidator create1 = new CreateCommandValidator("Create savings 245 1.0", bank); /*too short*/
        CreateCommandValidator create2 = new CreateCommandValidator("Create savings 24574832738 1.0", bank); /*too long*/
        assertFalse(create1.validate());
        assertFalse(create2.validate());
    }

    @Test
    void id_not_correct_length_deposit() {
        Bank bank = new Bank();
        bank.createAccount("savings", 83746578, 1.0);
        DepositCommandValidator deposit1 = new DepositCommandValidator("Deposit 8374 500", bank); /*too short*/
        DepositCommandValidator deposit2 = new DepositCommandValidator("Deposit 8374837492 500", bank); /*too long*/
        assertFalse(deposit1.validate());
        assertFalse(deposit2.validate());
    }

    @Test
    void id_does_not_exist_deposit() {
        Bank bank = new Bank();
        bank.createAccount("savings", 24567854, 1.0);
        DepositCommandValidator deposit = new DepositCommandValidator("Deposit 83747832 500", bank);
        assertFalse(deposit.validate());
    }

    @Test
    void valid_id_associated_with_checking_deposit() {
        Bank bank = new Bank();
        bank.createAccount("checking", 24567854, 1.0);
        DepositCommandValidator deposit = new DepositCommandValidator("Deposit 24567854 500", bank);
        assertTrue(deposit.validate());
    }

    @Test
    void valid_id_associated_with_savings_deposit() {
        Bank bank = new Bank();
        bank.createAccount("savings", 24567854, 1.0);
        DepositCommandValidator deposit = new DepositCommandValidator("Deposit 24567854 500", bank);
        assertTrue(deposit.validate());
    }

    @Test
    void id_associated_with_cd_deposit() {
        Bank bank = new Bank();
        bank.createAccount("cd", 24567854, 1.0, 3000);
        DepositCommandValidator deposit = new DepositCommandValidator("Deposit 24567854 500", bank);
        assertFalse(deposit.validate());
    }

    @Test
    void id_exists_in_bank_create() {
        Bank bank = new Bank();
        bank.createAccount("checking", 24567854, 1.0);
        CreateCommandValidator create = new CreateCommandValidator("Create checking 24567854 1.0", bank);
        assertFalse(create.validate());
    }

    @Test
    void valid_apr_is_int_create() {
        Bank bank = new Bank();
        CreateCommandValidator create = new CreateCommandValidator("Create checking 24567854 1", bank);
        assertTrue(create.validate());
    }

    @Test
    void apr_not_int_or_float_create() {
        Bank bank = new Bank();
        CreateCommandValidator create = new CreateCommandValidator("Create checking 24567854 hello", bank);
        assertFalse(create.validate());
    }

    @Test
    void apr_invalid_create() {
        Bank bank = new Bank();
        CreateCommandValidator create1 = new CreateCommandValidator("Create checking 24567854 -1.0", bank);/*APR is negative*/
        CreateCommandValidator create2 = new CreateCommandValidator("Create checking 76482736 15.0", bank);/*APR is more than 10*/
        assertFalse(create1.validate());
        assertFalse(create2.validate());
    }

    @Test
    void apr_exactly_0_10_create() {
        Bank bank = new Bank();
        CreateCommandValidator create1 = new CreateCommandValidator("Create checking 24567854 0.0", bank);
        CreateCommandValidator create2 = new CreateCommandValidator("Create checking 76482736 10.0", bank);/*APR is more than 10*/
        assertTrue(create1.validate());
        assertTrue(create2.validate());
    }

    @Test
    void amount_is_not_int_deposit() {
        Bank bank = new Bank();
        bank.createAccount("savings", 24567854, 1.0);
        DepositCommandValidator deposit = new DepositCommandValidator("Deposit 24567854 hello", bank);
        assertFalse(deposit.validate());
    }

    @Test
    void amount_invalid_checking_deposit() {
        Bank bank = new Bank();
        bank.createAccount("checking", 24567854, 1.0);
        DepositCommandValidator deposit1 = new DepositCommandValidator("Deposit 24567854 -100", bank);/*too low*/
        DepositCommandValidator deposit2 = new DepositCommandValidator("Deposit 24567854 1500", bank);/*over 1000*/
        assertFalse(deposit1.validate());
        assertFalse(deposit2.validate());
    }

    @Test
    void amount_exactly_0_1000_checking_deposit() {
        Bank bank = new Bank();
        bank.createAccount("checking", 24567854, 1.0);
        DepositCommandValidator deposit1 = new DepositCommandValidator("Deposit 24567854 0", bank);
        DepositCommandValidator deposit2 = new DepositCommandValidator("Deposit 24567854 1000", bank);
        assertTrue(deposit1.validate());
        assertTrue(deposit2.validate());
    }

    @Test
    void amount_invalid_savings_deposit() {
        Bank bank = new Bank();
        bank.createAccount("savings", 24567854, 1.0);
        DepositCommandValidator deposit1 = new DepositCommandValidator("Savings 24567854 -100", bank);/*too low*/
        DepositCommandValidator deposit2 = new DepositCommandValidator("Savings 24567854 3000", bank);/*over 2500*/
        assertFalse(deposit1.validate());
        assertFalse(deposit2.validate());
    }

    @Test
    void amount_exactly_0_2500_savings_deposit() {
        Bank bank = new Bank();
        bank.createAccount("savings", 24567854, 1.0);
        DepositCommandValidator deposit1 = new DepositCommandValidator("Deposit 24567854 0", bank);
        DepositCommandValidator deposit2 = new DepositCommandValidator("Deposit 24567854 2500", bank);
        assertTrue(deposit1.validate());
        assertTrue(deposit2.validate());
    }

    @Test
    void create_cd_amount_float() {
        Bank bank = new Bank();
        CreateCommandValidator create = new CreateCommandValidator("Create cd 87626473 1.0 1500.0", bank);
        assertTrue(create.validate());
    }

    @Test
    void deposit_amount_float() {
        Bank bank = new Bank();
        bank.createAccount("savings", 24567854, 1.0);
        DepositCommandValidator deposit = new DepositCommandValidator("Deposit 24567854 500.0", bank);
        assertTrue(deposit.validate());
    }

}
