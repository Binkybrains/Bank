package banking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PassTimeCommandTest {
    PassTimeCommandValidator passTime;

    @BeforeEach
    void setup() {
        passTime = new PassTimeCommandValidator();
    }

    @Test
    void misspelled_command() {
        assertFalse(passTime.validate("Pss 10"));
    }

    @Test
    void incorrect_num_of_arguments() {
        assertFalse(passTime.validate("Pass 10 20"));
        assertFalse(passTime.validate("Pass"));
    }

    @Test
    void case_insensitive_pass_time() {
        assertTrue(passTime.validate("pAsS 10"));
    }

    @Test
    void time_is_not_int() {
        assertFalse(passTime.validate("Pass hi"));
    }

    @Test
    void time_is_float() {
        assertFalse(passTime.validate("Pass 10.0"));
    }

    @Test
    void time_is_not_in_bounds() {
        assertFalse(passTime.validate("Pass 0"));
        assertFalse(passTime.validate("Pass 100"));
    }

    @Test
    void time_is_exactly_1_or_60() {
        assertTrue(passTime.validate("Pass 1"));
        assertTrue(passTime.validate("Pass 60"));
    }

    @Test
    void pass_time_correct() {
        assertTrue(passTime.validate("Pass 10"));
    }
}
