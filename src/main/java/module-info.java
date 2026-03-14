module lt.viko.eif.kskrebe.rsa {
    requires javafx.controls;
    requires javafx.fxml;

    opens lt.viko.eif.kskrebe.rsa to javafx.fxml;
    opens lt.viko.eif.kskrebe.rsa.controller to javafx.fxml;

    exports lt.viko.eif.kskrebe.rsa;
    exports lt.viko.eif.kskrebe.rsa.controller;
}