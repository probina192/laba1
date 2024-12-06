package com.example.data_base;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;
import java.io.IOException;

import java.io.*;


public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("view.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);
        stage.setTitle("База данных");
        stage.setScene(scene);
        stage.show();

    }

    //Класс записей
    public static class MyRecord implements Serializable {
        private Integer key;
        private String field1;
        private Float field2;
        private Double field3;

        public void setKey(Integer key) {
            this.key = key;
        }

        public void setField1(String field1) {
            this.field1 = field1;
        }

        public void setField2(Float field2) {
            this.field2 = field2;
        }

        public void setField3(Double field3) {
            this.field3 = field3;
        }

        public Integer getKey() {
            return key;
        }

        public String getField1() {
            return field1;
        }

        public Float getField2() {
            return field2;
        }

        public Double getField3() {
            return field3;
        }
    }

    public static void main(String[] args) {
        launch();
    }
}