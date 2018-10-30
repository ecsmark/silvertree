package com.silvertree.tombstone;

import com.silvertree.tombstone.tiemulation.IVirtualTI;
import com.silvertree.tombstone.tiemulation.impl.TIKeyboard;
import com.silvertree.tombstone.tiemulation.impl.VirtualTI;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {
    IVirtualTI virtualTI ;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Pane root = new Pane();

        primaryStage.setTitle("Tombstone City");
        Scene emulatorScene = new Scene(root, 300, 275);
        primaryStage.setScene(emulatorScene);
        createEmulator(emulatorScene, root);
        TombstoneCity game = new TombstoneCity(virtualTI);
//        GameBoard gameBoard = new GameBoard(virtualTI.getVideo());
//        gameBoard.blankScreen();
//        gameBoard.preGameScreen();
//        gameBoard.displayLevelMenu();
        showEmulator(primaryStage) ;
        game.start() ;

    }

    private void showEmulator(Stage stage) {
        virtualTI.getVideo().refresh();
        stage.show() ;
    }


    public static void main(String[] args) {
        launch(args);
    }

    void createEmulator(Scene scene, Pane pane){
        virtualTI = new VirtualTI(scene, pane);
    }
}
