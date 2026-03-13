package lt.viko.eif.kskrebe.rsa.service;

import lt.viko.eif.kskrebe.rsa.model.ExtendedGcdResult;
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

    /**
     * Testuoja didžiausio bendro daliklio skaičiavimą.
     */
    @Test
    void shouldReturnGreatestCommonDivisor() {
        assertEquals(BigInteger.valueOf(6),
                mathService.gcd(BigInteger.valueOf(48), BigInteger.valueOf(18)));

        assertEquals(BigInteger.ONE,
                mathService.gcd(BigInteger.valueOf(17), BigInteger.valueOf(3120)));

        assertEquals(BigInteger.valueOf(10),
                mathService.gcd(BigInteger.valueOf(100), BigInteger.valueOf(90)));

        assertEquals(BigInteger.valueOf(7),
                mathService.gcd(BigInteger.valueOf(0), BigInteger.valueOf(7)));
    }


    /**
     * Testuojama sąlyga: a*x+b*y=gcd(a,b)
     */
    @Test
    void shouldReturnExtendedGcdResult() {
        ExtendedGcdResult result = mathService.extendedGcd(
                BigInteger.valueOf(17),
                BigInteger.valueOf(3120)
        );

        assertEquals(BigInteger.ONE, result.getGcd());

        BigInteger leftSide = BigInteger.valueOf(17).multiply(result.getX())
                .add(BigInteger.valueOf(3120).multiply(result.getY()));

        assertEquals(BigInteger.ONE, leftSide);
    }

    @Test
    void shouldFindValidPublicExponent() {
        BigInteger phi = BigInteger.valueOf(3120);

        BigInteger e = mathService.findPublicExponent(phi);

        assertTrue(e.compareTo(BigInteger.ONE) > 0);
        assertTrue(e.compareTo(phi) < 0);
        assertEquals(BigInteger.ONE, mathService.gcd(e, phi));
    }

    @Test //blogam įvedimui testas
    void shouldThrowExceptionWhenPhiIsTooSmall() {
        assertThrows(IllegalArgumentException.class,
                () -> mathService.findPublicExponent(BigInteger.TWO));
    }

}