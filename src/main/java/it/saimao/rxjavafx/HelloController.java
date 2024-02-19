package it.saimao.rxjavafx;

import io.reactivex.Observable;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import io.reactivex.schedulers.Schedulers;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.IntStream;

public class HelloController implements Initializable {
    private static final int NUMBER_OF_TASKS = 10;
    @FXML
    ListView<String> listFx;
    @FXML
    Button btBlocking;
    @FXML
    Button btNonBlocking;
    @FXML
    Button btReactiveX;
    @FXML
    Button btClear;

    private ObservableList<String> observableList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        observableList = FXCollections.observableArrayList();
        btBlocking.setOnAction(event -> actionBlockingList());
        btNonBlocking.setOnAction(event -> actionNonBlockingList());
        btReactiveX.setOnAction(event -> actionReactiveX());
        btClear.setOnAction(event -> clearList());
        listFx.setItems(observableList);
    }

    private void actionReactiveX() {
        listFx.getItems().clear();
        Observable.range(1, NUMBER_OF_TASKS) // 1
                .subscribeOn(Schedulers.computation()) // 2
                .map(this::runTask) // 3
                .map(result -> result.time() > 500 ? new Result(result.name() + " (slow)", result.time()) : result) // 3
                .observeOn(JavaFxScheduler.platform()) // 4
                .forEach(result -> observableList.add(result.toString()));
    }

    private void clearList() {
        if (observableList != null && !observableList.isEmpty()) {
            observableList.clear();
            listFx.setItems(observableList);
        }
    }

    private void actionNonBlockingList() {
        new Thread(() -> {
            IntStream.rangeClosed(1, NUMBER_OF_TASKS) // Stream API way of iterating
                    .mapToObj(this::runTask) // Execute and map the results of our long-running task
                    .map(result -> result.time() > 500 ? new Result(result.name() + " (slow)", result.time()) : result) // "Annotate" those that took too long
                    .forEach(result -> Platform.runLater(() -> observableList.add(result.toString()))); // And push them to result list so that they are displayed in UI
        }).start();
    }

    private Result runTask(Integer i) {
        long currentTime = System.currentTimeMillis();

        String name = "Task" + i;
        long sleepDuration = (long) (Math.random() * 1000);

        try {
            Thread.sleep(sleepDuration);
            return new Result(name, sleepDuration);
        } catch (Exception e) {
            return new Result("-", 0);
        } finally {
            System.out.println(name + " took " + (System.currentTimeMillis() - currentTime) + " ms");
        }
    }

    private void actionBlockingList() {
        observableList.clear();
        IntStream.rangeClosed(1, NUMBER_OF_TASKS) // Stream API way of iterating
                .mapToObj(this::runTask) // Execute and map the results of our long-running task
                .map(result -> result.time() > 500 ? new Result(result.name() + " (slow)", result.time()) : result) // "Annotate" those that took too long
                .forEach(result -> observableList.add(result.toString())); // And push them to result list so that they are displayed in UI
    }
}