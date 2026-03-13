package lt.viko.eif.kskrebe.rsa.service;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testai MathService klasei.
 */
class MathServiceTest {

    private final MathService mathService = new MathService();

    /**
     * Testuoja pirminius skaičius.
     */
    @Test
    void shouldReturnTrueForPrimeNumbers() {

        assertTrue(mathService.isPrime(BigInteger.valueOf(2)));
        assertTrue(mathService.isPrime(BigInteger.valueOf(3)));
        assertTrue(mathService.isPrime(BigInteger.valueOf(5)));
        assertTrue(mathService.isPrime(BigInteger.valueOf(17)));
        assertTrue(mathService.isPrime(BigInteger.valueOf(97)));

    }

    /**
     * Testuoja nepirminius skaičius.
     */
    @Test
    void shouldReturnFalseForNonPrimeNumbers() {

        assertFalse(mathService.isPrime(BigInteger.valueOf(1)));
        assertFalse(mathService.isPrime(BigInteger.valueOf(4)));
        assertFalse(mathService.isPrime(BigInteger.valueOf(9)));
        assertFalse(mathService.isPrime(BigInteger.valueOf(21)));
        assertFalse(mathService.isPrime(BigInteger.valueOf(100)));

    }

}