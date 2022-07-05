package banking;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TransferCommandTest {

    @Test
    void mispelled_transfer_command() {
        Bank bank = new Bank();

        TransferCommandValidator transfer = new TransferCommandValidator("Trasnfer 26736283 27362836 200", bank);
        assertFalse(transfer.validate());
    }

    @Test
    void missing_field_transfer_command() {
        Bank bank = new Bank();

        TransferCommandValidator transfer = new TransferCommandValidator("Transfer 27362836 200", bank);
        assertFalse(transfer.validate());
    }

    @Test
    void id_wrong_length_transfer() {
        Bank bank = new Bank();
        TransferCommandValidator transfer1 = new TransferCommandValidator("Transfer 62836 89384 200", bank); // Both Accounts too short
        TransferCommandValidator transfer2 = new TransferCommandValidator("Transfer 628368374 89384837492 200", bank); // Both Accounts too long
        TransferCommandValidator transfer3 = new TransferCommandValidator("Transfer 62836 89384888 200", bank); // one account too short
        TransferCommandValidator transfer4 = new TransferCommandValidator("Transfer 62836839 893848888 200", bank); // one account too long

        assertFalse(transfer1.validate());
        assertFalse(transfer2.validate());
        assertFalse(transfer3.validate());
        assertFalse(transfer4.validate());
    }

    @Test
    void id_not_int_transfer() {
        Bank bank = new Bank();
        TransferCommandValidator transfer1 = new TransferCommandValidator("Transfer hello hi 200", bank); // Both
        TransferCommandValidator transfer2 = new TransferCommandValidator("Transfer hello 89384837 200", bank); //First
        TransferCommandValidator transfer3 = new TransferCommandValidator("Transfer 73649283 hello 200", bank); //second

        assertFalse(transfer1.validate());
        assertFalse(transfer2.validate());
        assertFalse(transfer3.validate());
    }

    @Test
    void same_id_transfer() {
        Bank bank = new Bank();
        TransferCommandValidator transfer = new TransferCommandValidator("Transfer 12345678 12345678 200", bank);
        assertFalse(transfer.validate());

    }

    @Test
    void id_does_not_exist_transfer() {
        Bank bank = new Bank();
        TransferCommandValidator transfer1 = new TransferCommandValidator("Transfer 12345678 87654321 200", bank); //Both

        bank.createAccount("savings", 87654321, 1.0);
        TransferCommandValidator transfer2 = new TransferCommandValidator("Transfer 12345678 87654321 200", bank); //First
        TransferCommandValidator transfer3 = new TransferCommandValidator("Transfer 87654321 12345678 200", bank); //Second

        assertFalse(transfer1.validate());
        assertFalse(transfer2.validate());
        assertFalse(transfer3.validate());
    }

    @Test
    void id_associated_with_cd_account_transfer() {
        Bank bank = new Bank();
        bank.createAccount("savings", 87654321, 1.0);
        bank.createAccount("cd", 12345678, 1.0);
        bank.createAccount("cd", 91919191, 1.0);

        TransferCommandValidator transfer1 = new TransferCommandValidator("Transfer 12345678 91919191 200", bank); //both
        TransferCommandValidator transfer2 = new TransferCommandValidator("Transfer 12345678 87654321 200", bank); //first
        TransferCommandValidator transfer3 = new TransferCommandValidator("Transfer 87654321 12345678 200", bank); //second

        assertFalse(transfer1.validate());
        assertFalse(transfer2.validate());
        assertFalse(transfer3.validate());
    }

    @Test
    void amount_not_int_or_float_transfer() {
        Bank bank = new Bank();
        bank.createAccount("checking", 12345678, 1.0);
        bank.createAccount("checking", 87654321, 1.0);

        TransferCommandValidator transfer1 = new TransferCommandValidator("Transfer 12345678 87654321 hi", bank);
        assertFalse(transfer1.validate());
    }

    @Test
    void valid_amount_is_float() {
        Bank bank = new Bank();
        bank.createAccount("checking", 12345678, 1.0);
        bank.createAccount("checking", 87654321, 1.0);

        TransferCommandValidator transfer = new TransferCommandValidator("Transfer 12345678 87654321 200.0", bank);
        assertTrue(transfer.validate());
    }

    @Test
    void checking_account_invalid_deposit_transfer() {
        Bank bank = new Bank();
        bank.createAccount("checking", 12345678, 1.0);
        bank.createAccount("checking", 87654321, 1.0);

        TransferCommandValidator transfer1 = new TransferCommandValidator("Transfer 12345678 87654321 1500", bank); //Number over 1000
        TransferCommandValidator transfer2 = new TransferCommandValidator("Transfer 12345678 87654321 -5", bank); //Number under 0

        assertFalse(transfer1.validate());
        assertFalse(transfer2.validate());
    }

    @Test
    void savings_account_invalid_deposit_transfer() {
        Bank bank = new Bank();

        bank.createAccount("savings", 12345678, 1.0);
        bank.createAccount("savings", 87654321, 1.0);

        TransferCommandValidator transfer1 = new TransferCommandValidator("Transfer 12345678 87654321 3000", bank); //Number over 2500
        TransferCommandValidator transfer2 = new TransferCommandValidator("Transfer 12345678 87654321 -5", bank); //Number under 0

        assertFalse(transfer1.validate());
        assertFalse(transfer2.validate());

    }

    @Test
    void checking_account_invalid_withdraw_transfer() {
        Bank bank = new Bank();

        bank.createAccount("checking", 12345678, 1.0);
        bank.createAccount("savings", 87654321, 1.0);

        TransferCommandValidator transfer1 = new TransferCommandValidator("Transfer 12345678 87654321 600", bank); //Number over 400
        TransferCommandValidator transfer2 = new TransferCommandValidator("Transfer 12345678 87654321 -5", bank); //Number under 0


        assertFalse(transfer1.validate());
        assertFalse(transfer2.validate());

    }

    @Test
    void savings_account_invalid_withdraw_transfer() {
        Bank bank = new Bank();

        bank.createAccount("savings", 12345678, 1.0);
        bank.createAccount("savings", 87654321, 1.0);

        TransferCommandValidator transfer1 = new TransferCommandValidator("Transfer 12345678 87654321 1500", bank); //Number over 1000
        TransferCommandValidator transfer2 = new TransferCommandValidator("Transfer 12345678 87654321 -5", bank); //Number under 0

        assertFalse(transfer1.validate());
        assertFalse(transfer2.validate());

    }

    @Test
    void both_deposit_withdraw_invalid_transfer() {
        Bank bank = new Bank();

        bank.createAccount("savings", 12345678, 1.0);
        bank.createAccount("savings", 24682468, 1.0);
        bank.createAccount("checking", 87654321, 1.0);
        bank.createAccount("checking", 96969696, 1.0);

        TransferCommandValidator transfer1 = new TransferCommandValidator("Transfer 12345678 24682468 3000", bank); //both savings
        TransferCommandValidator transfer2 = new TransferCommandValidator("Transfer 96969696 87654321 1500", bank); //both checking
        TransferCommandValidator transfer3 = new TransferCommandValidator("Transfer 87654321 12345678 3000", bank); //first checking
        TransferCommandValidator transfer4 = new TransferCommandValidator("Transfer 12345678 87654321 1500", bank); //first savings

        assertFalse(transfer1.validate());
        assertFalse(transfer2.validate());
        assertFalse(transfer3.validate());
        assertFalse(transfer4.validate());
    }

    @Test
    void savings_withdraw_more_than_one_withdraw_this_month_transfer() {
        Bank bank = new Bank();
        bank.createAccount("savings", 12345678, 1.0);
        bank.createAccount("checking", 87654321, 1.0);
        bank.getAccount(12345678).incrementWithdrawsThisMonth();

        TransferCommandValidator transfer = new TransferCommandValidator("Transfer 12345678 87654321 300", bank);

        assertFalse(transfer.validate());
    }

    @Test
    void valid_transfer_command() {
        Bank bank = new Bank();

        bank.createAccount("savings", 12345678, 1.0);
        bank.createAccount("savings", 24682468, 1.0);
        bank.createAccount("checking", 87654321, 1.0);
        bank.createAccount("checking", 96969696, 1.0);

        TransferCommandValidator transfer1 = new TransferCommandValidator("Transfer 12345678 24682468 500", bank); //both savings
        TransferCommandValidator transfer2 = new TransferCommandValidator("Transfer 96969696 87654321 300", bank); //both checking
        TransferCommandValidator transfer3 = new TransferCommandValidator("Transfer 87654321 12345678 300", bank); //first checking
        TransferCommandValidator transfer4 = new TransferCommandValidator("Transfer 12345678 87654321 500", bank); //first savings

        assertTrue(transfer1.validate());
        assertTrue(transfer2.validate());
        assertTrue(transfer3.validate());
        assertTrue(transfer4.validate());
    }

    @Test
    void exact_amounts_transfer() {
        Bank bank = new Bank();

        bank.createAccount("savings", 12345678, 1.0);
        bank.createAccount("checking", 87654321, 1.0);

        TransferCommandValidator transfer1 = new TransferCommandValidator("Transfer 12345678 87654321 0", bank); //amount 0 checking and savings
        TransferCommandValidator transfer2 = new TransferCommandValidator("Transfer 12345678 87654321 300", bank); //savings withdraw/checking deposit exactly 1000
        TransferCommandValidator transfer3 = new TransferCommandValidator("Transfer 87654321 12345678 400", bank); //checking withdraw 400

        assertTrue(transfer1.validate());
        assertTrue(transfer2.validate());
        assertTrue(transfer3.validate());
    }

    @Test
    void withdraw_amount_over_account_balance() {
        Bank bank = new Bank();

        bank.createAccount("savings", 12345678, 1.0);
        bank.createAccount("checking", 87654321, 1.0);
        bank.getAccount(12345678).setBalance(100);

        TransferCommandValidator transfer = new TransferCommandValidator("Transfer 12345678 87654321 200", bank);
        assertTrue(transfer.validate());
    }

    @Test
    void case_insensitive_transfer_command() {
        Bank bank = new Bank();
        bank.createAccount("savings", 12345678, 1.0);
        bank.createAccount("checking", 87654321, 1.0);

        TransferCommandValidator transfer = new TransferCommandValidator("tRaNsfer 12345678 87654321 200", bank);
        assertTrue(transfer.validate());
    }
}
