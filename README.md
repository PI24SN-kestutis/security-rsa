# RSA Demonstravimo Sistema

Mokomoji **JavaFX** aplikacija, skirta parodyti kaip veikia **RSA
šifravimo algoritmas** ir kaip teoriškai galima atlikti **matematinę RSA
ataką**, jei naudojami per maži pirminiai skaičiai.

Projektas skirtas **kriptografijos mokymuisi**.

------------------------------------------------------------------------

# Funkcionalumas

Programa leidžia:

-   generuoti RSA raktus iš pirminių skaičių `p` ir `q`
-   užšifruoti tekstą
-   išsaugoti užšifruotus duomenis faile
-   nuskaityti užšifruotą failą
-   atkurti tekstą
-   parodyti RSA matematinę ataką (p ir q atkūrimą)

------------------------------------------------------------------------

# Programos veikimo principas

## 1. RSA raktų generavimas

Vartotojas įveda:

p\
q

Programa apskaičiuoja:

n = p × q

Φ(n) = (p − 1)(q − 1)

Parenkamas viešasis eksponentas:

e

Apskaičiuojamas privatus raktas:

d = e⁻¹ mod Φ

------------------------------------------------------------------------

## 2. Teksto šifravimas

Tekstas konvertuojamas į simbolių kodus.

Kiekvienas simbolis šifruojamas:

c = m\^e mod n

Rezultatas yra skaičių seka.

------------------------------------------------------------------------

## 3. Failo formatas

Užšifruoti duomenys saugomi vienoje eilutėje:

n;e;cipher1;cipher2;cipher3;...

Pvz:

3233;7;1087;3020;1877

------------------------------------------------------------------------

## 4. Dešifravimas

Tekstas atkuriamas:

m = c\^d mod n

------------------------------------------------------------------------

# Matematinės RSA atakos demonstracija

Programa parodo, kad jei `n` yra mažas, galima:

1.  išskaidyti `n` į `p` ir `q`
2.  apskaičiuoti `Φ`
3.  apskaičiuoti `d`
4.  dešifruoti tekstą

Todėl realiame RSA naudojami **labai dideli pirminiai skaičiai (2048+
bitų)**.

------------------------------------------------------------------------

# Projekto struktūra

    src/main/java
    │
    ├─ controller
    │   └─ RsaController
    │
    ├─ service
    │   ├─ RsaService
    │   ├─ MathService
    │   └─ FileService
    │
    ├─ model
    │   ├─ RsaKeyPair
    │   ├─ PrimeFactors
    │   ├─ ExtendedGcdResult
    │   └─ EncryptedFileData
    │
    └─ util
        └─ AlertUtil

    resources
    │
    ├─ rsa-view.fxml
    └─ styles.css

------------------------------------------------------------------------

# Paleidimas

Reikalavimai:

-   Java 21
-   Maven
-   JavaFX

Paleidimas:

    mvn javafx:run

arba per IntelliJ:

    MainApp

------------------------------------------------------------------------

# Licencija

Mokomoji licencija.

Šis projektas skirtas:

-   studijoms
-   kriptografijos demonstracijai
-   algoritmų mokymuisi

Neskirtas realiam saugiam šifravimui.
