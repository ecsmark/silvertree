package com.silvertree.tombstone;

import com.silvertree.tombstone.tiemulation.ITIVideo;
import javafx.scene.shape.Rectangle;

public class GameBoard {
    final static  Rectangle s_rectSafeArea = new Rectangle(12, 8, 19 , 13  );

    final static  int MAXROW = 23 ;
    final static int MAXCOL = 31 ;

    final static int BORDER[] =
            {96, 96, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32,
                    32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32,
                    32, 32, 32, 32, 32, 32, 96, 96
            };

    ITIVideo tiVideo ;
    public GameBoard(ITIVideo tiVideo){
        this.tiVideo = tiVideo ;
    }

    public void blankScreen(){
        for (int row=0; row < MAXROW+1; row++)
        {

            for (int col=0; col < MAXCOL+1; col++)
            {
                writeChar(row, col, BORDER[col] ) ;

            }
        }
    }
    void writeChar(int row, int col, int color){

    }

    public void preGameScreen()
    {
//        final byte outl1[] = new byte[]{138, 138, 136, 93, 32, 32,
//                32,129,121,126,132,123,129,121,127,131,32,120,
//                130,129,122,32,32,32,137,138,138,138,0} ;
//
//        final byte outl2[] = new byte {138,138,138,138,138,139,
//                140,130,122,124,122,128,130,122,124,125,133,125,
//                130,130,130,141,142,138,138,138,138,138,0} ;
//
//        blankScreen() ;
//        writeChar(0,2, 136) ;       /* write Mountain char    */
//        writeChar(0,29,137) ;
//        writeChar(0,27, GRAVE) ;
//        Video()->DisplayAt(1,2, (char *)outl1);
//        Video()->DisplayAt(2,2, (char *)outl2) ;
//

    }

    void draw(int day)
    {
        final  char    outl6[] =new char [] {152, 134, 152,134, 152, 134, 152, 0	};
        final  char    outl7[] = new char [] {134, 134, 134,134, 134, 134, 134, 0	};

/*
        for (int i=0; i<28; i++)
        {
            writeChar(21, 2+i, BOTTOMEDGE) ;

        }

        tiVideo.displayAt(22,3, "DAY  POPULATION  SCHOONERS") ;
        int row = 8 ;
        for (i=0; i<3; i++)
        {
            tiVideo.DisplayAt(row++,12, outl6) ;    // safe area
            tiVideo.DisplayAt(row++,12, outl7) ;
        }

        m_ptiVideo->DisplayAt(row++,12, outl6) ;     // safe area

        // ----------------------------------------------------
        // Throw up pairs of graves randomly
        // ----------------------------------------------------
        int graves = min((day*4+1), MAXGRAVEPAIRS)   ;

        for (int n=0; n < graves; )
        {
            // find a blank random location
            short grave1=  RandomBlank() ;
            short grave2 = Neighbor((randno () >> 13) )+grave1 ;
            if (AreAllNeighborsBlank(grave1) && AreAllNeighborsBlank(grave2))
            {
                writeChar(grave1, Characters.Grave.ordinal()) ;
                writeChar(grave2, Characters.Grave.ordinal()) ;
                n++ ;
            }
        }
        */

    }

    public void displayScore(int score) {

    }
    void displaySchooners(int numberSchooners){

    }

    void displayDay(int day){

    }

}
