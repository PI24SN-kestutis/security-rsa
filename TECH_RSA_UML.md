# TECH.md

## Projekto tikslas

Šio projekto tikslas -- parodyti RSA algoritmo veikimą ir matematinį
saugumo pagrindą.

Sistema realizuoja RSA **be kriptografijos bibliotekų**, naudojant tik
Java `BigInteger`.

------------------------------------------------------------------------

# RSA algoritmo pseudokodas

## RSA raktų generavimas

    function generateKeys(p, q):

        n = p * q

        phi = (p-1) * (q-1)

        choose e such that:
            gcd(e, phi) = 1

        d = modularInverse(e, phi)

        return (n, e, d)

------------------------------------------------------------------------

## Šifravimas

    function encrypt(message, e, n):

        for each character m in message:

            c = m^e mod n

            add c to cipher list

        return cipher

------------------------------------------------------------------------

## Dešifravimas

    function decrypt(cipher, d, n):

        for each c in cipher:

            m = c^d mod n

            add character(m) to text

        return text

------------------------------------------------------------------------

# Išplėstinis Euklido algoritmas

Naudojamas rasti:

d = e⁻¹ mod Φ

Pseudokodas:

    function extendedGCD(a, b):

        if b == 0:
            return (gcd=a, x=1, y=0)

        (gcd, x1, y1) = extendedGCD(b, a mod b)

        x = y1
        y = x1 - (a // b) * y1

        return (gcd, x, y)

------------------------------------------------------------------------

# RSA matematinės atakos algoritmas

    function breakRSA(n, e):

        find p and q such that:

            p * q = n

        phi = (p-1)(q-1)

        d = modularInverse(e, phi)

        return private key (d)

------------------------------------------------------------------------

# UML schema (supaprastinta)

    +------------------+
    |   RsaController  |
    +------------------+
    | handleEncrypt()  |
    | handleDecrypt()  |
    | handleAttack()   |
    +--------+---------+
             |
             v
    +------------------+
    |    RsaService    |
    +------------------+
    | encrypt()        |
    | decrypt()        |
    | generateKeys()   |
    +--------+---------+
             |
             v
    +------------------+
    |    MathService   |
    +------------------+
    | gcd()            |
    | extendedGcd()    |
    | factorizeN()     |
    | modInverse()     |
    +--------+---------+
             |
             v
    +------------------+
    |    FileService   |
    +------------------+
    | saveEncrypted()  |
    | readEncrypted()  |
    +------------------+

------------------------------------------------------------------------

# Projekto architektūra

Naudojamas **MVC principas**:

Model\
- duomenų struktūros

Service\
- RSA algoritmai ir matematika

Controller\
- UI logika

View\
- JavaFX FXML sąsaja

------------------------------------------------------------------------

# Pastaba

Ši sistema yra **mokomoji RSA demonstracija**.

Realiuose sprendimuose naudojami:

-   2048--4096 bitų raktai
-   saugios kriptografinės bibliotekos
-   papildomos apsaugos (padding, OAEP).
