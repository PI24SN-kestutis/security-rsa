package lt.viko.eif.kskrebe.rsa.service;

import lt.viko.eif.kskrebe.rsa.model.ExtendedGcdResult;
import lt.viko.eif.kskrebe.rsa.model.PrimeFactors;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MathServiceTest {

    private final MathService mathService = new MathService();

    // Patikrina kelis tipinius pirminių skaičių pavyzdžius.
    @Test
    void shouldReturnTrueForPrimeNumbers() {
        assertTrue(mathService.isPrime(BigInteger.valueOf(2)));
        assertTrue(mathService.isPrime(BigInteger.valueOf(3)));
        assertTrue(mathService.isPrime(BigInteger.valueOf(5)));
        assertTrue(mathService.isPrime(BigInteger.valueOf(17)));
        assertTrue(mathService.isPrime(BigInteger.valueOf(97)));
    }

    // Sudėtiniai skaičiai neturi būti atpažįstami kaip pirminiai.
    @Test
    void shouldReturnFalseForNonPrimeNumbers() {
        assertFalse(mathService.isPrime(BigInteger.ONE));
        assertFalse(mathService.isPrime(BigInteger.valueOf(4)));
        assertFalse(mathService.isPrime(BigInteger.valueOf(9)));
        assertFalse(mathService.isPrime(BigInteger.valueOf(21)));
        assertFalse(mathService.isPrime(BigInteger.valueOf(100)));
    }

    // GCD testai apima ir įprastą, ir ribinį atvejį su nuliu.
    @Test
    void shouldReturnGreatestCommonDivisor() {
        assertEquals(BigInteger.valueOf(6), mathService.gcd(BigInteger.valueOf(48), BigInteger.valueOf(18)));
        assertEquals(BigInteger.ONE, mathService.gcd(BigInteger.valueOf(17), BigInteger.valueOf(3120)));
        assertEquals(BigInteger.valueOf(10), mathService.gcd(BigInteger.valueOf(100), BigInteger.valueOf(90)));
        assertEquals(BigInteger.valueOf(7), mathService.gcd(BigInteger.ZERO, BigInteger.valueOf(7)));
    }

    // Tikrinama Bėzu tapatybė: a*x + b*y = gcd(a, b).
    @Test
    void shouldReturnExtendedGcdResult() {
        ExtendedGcdResult result = mathService.extendedGcd(BigInteger.valueOf(17), BigInteger.valueOf(3120));

        assertEquals(BigInteger.ONE, result.getGcd());

        BigInteger leftSide = BigInteger.valueOf(17).multiply(result.getX())
                .add(BigInteger.valueOf(3120).multiply(result.getY()));

        assertEquals(BigInteger.ONE, leftSide);
    }

    // RSA viešasis eksponentas turi būti mažesnis už phi ir tarpusavyje pirminis su phi.
    @Test
    void shouldFindValidPublicExponent() {
        BigInteger phi = BigInteger.valueOf(3120);

        BigInteger e = mathService.findPublicExponent(phi);

        assertTrue(e.compareTo(BigInteger.ONE) > 0);
        assertTrue(e.compareTo(phi) < 0);
        assertEquals(BigInteger.ONE, mathService.gcd(e, phi));
    }

    // Per maža phi reikšmė netinka viešojo eksponento paieškai.
    @Test
    void shouldThrowExceptionWhenPhiIsTooSmall() {
        assertThrows(IllegalArgumentException.class,
                () -> mathService.findPublicExponent(BigInteger.TWO));
    }

    // Privatus eksponentas turi tenkinti sąlygą e * d mod phi = 1.
    @Test
    void shouldFindPrivateExponent() {
        BigInteger phi = BigInteger.valueOf(3120);
        BigInteger e = BigInteger.valueOf(17);

        BigInteger d = mathService.findPrivateExponent(e, phi);

        assertEquals(BigInteger.ONE, e.multiply(d).mod(phi));
    }

    // Jei e ir phi nėra tarpusavyje pirminiai, inversas neegzistuoja.
    @Test
    void shouldThrowExceptionWhenNumbersAreNotCoprime() {
        BigInteger e = BigInteger.valueOf(6);
        BigInteger phi = BigInteger.valueOf(3120);

        assertThrows(IllegalArgumentException.class,
                () -> mathService.findPrivateExponent(e, phi));
    }

    // Matematinės atakos metu n turi būti suskaidomas į du pirminius daugiklius.
    @Test
    void shouldFactorizeNIntoPrimeFactors() {
        PrimeFactors factors = mathService.factorizeN(BigInteger.valueOf(3233));

        assertEquals(BigInteger.valueOf(3233), factors.getP().multiply(factors.getQ()));
        assertTrue(mathService.isPrime(factors.getP()));
        assertTrue(mathService.isPrime(factors.getQ()));
    }

    // Per mažas modulis n laikomas netinkama įvestimi.
    @Test
    void shouldThrowExceptionWhenNIsTooSmall() {
        assertThrows(IllegalArgumentException.class,
                () -> mathService.factorizeN(BigInteger.TWO));
    }

    // Pirminio skaičiaus neįmanoma išskaidyti į du pirminius daugiklius šiame scenarijuje.
    @Test
    void shouldThrowExceptionWhenNCannotBeFactorizedIntoTwoPrimes() {
        assertThrows(IllegalStateException.class,
                () -> mathService.factorizeN(BigInteger.valueOf(17)));
    }

    // Phi reikšmė turi būti korektiškai atkurta iš p ir q.
    @Test
    void shouldRecoverPhiFromPrimeFactors() {
        BigInteger p = BigInteger.valueOf(61);
        BigInteger q = BigInteger.valueOf(53);

        BigInteger phi = mathService.recoverPhi(p, q);

        assertEquals(BigInteger.valueOf(3120), phi);
    }

    // Nepirminiai p ir q netinka phi atkūrimui.
    @Test
    void shouldThrowExceptionWhenFactorsAreNotPrime() {
        assertThrows(IllegalArgumentException.class,
                () -> mathService.recoverPhi(BigInteger.valueOf(10), BigInteger.valueOf(20)));
    }
}
