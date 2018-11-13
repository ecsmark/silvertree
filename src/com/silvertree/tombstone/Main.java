package com.silvertree.tombstone;

import com.silvertree.tombstone.tiemulation.IVirtualTI;
import com.silvertree.tombstone.tiemulation.impl.VirtualTI;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {
    IVirtualTI virtualTI ;
    final static double SCALE_FACTOR = 2.0 ;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Pane root = new Pane();

        primaryStage.setTitle("Tombstone City");
        Scene emulatorScene = new Scene(root, 512, 384);
        primaryStage.setScene(emulatorScene);
        createEmulator(emulatorScene, root);
        TombstoneCity game = new TombstoneCity(virtualTI);
        showEmulator(primaryStage) ;
        game.start() ;

    }

    private void showEmulator(Stage stage) {
        stage.show() ;
    }


    public static void main(String[] args) {
        launch(args);
    }

    void createEmulator(Scene scene, Pane pane){
        virtualTI = new VirtualTI(scene, pane, SCALE_FACTOR);
    }
}
