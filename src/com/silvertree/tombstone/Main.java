package com.silvertree.tombstone;

import com.silvertree.tombstone.tiemulation.IVirtualTI;
import com.silvertree.tombstone.tiemulation.impl.VirtualTI;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    IVirtualTI virtualTI ;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Group root = new Group();

        primaryStage.setTitle("Tombstone City");
        Scene emulatorScene = new Scene(root, 300, 275);
        primaryStage.setScene(emulatorScene);
        createEmulator(emulatorScene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

    void createEmulator(Scene scene){
        virtualTI = new VirtualTI(scene);
    }
}
