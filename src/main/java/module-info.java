module it.saimao.rxjavafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires io.reactivex.rxjava2;
    requires rxjavafx;


    opens it.saimao.rxjavafx to javafx.fxml;
    exports it.saimao.rxjavafx;
}