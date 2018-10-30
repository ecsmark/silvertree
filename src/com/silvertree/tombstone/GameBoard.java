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
        initChars();
        initColors();
        blankScreen();
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
    void writeChar(int row, int col, int  chr){
        tiVideo.wrChar(row, col,  chr);
    }

    public void preGameScreen()
    {
        final int outl1[] = new int[]{138, 138, 136, 93, 32, 32,
                32,129,121,126,132,123,129,121,127,131,32,120,
                130,129,122,32,32,32,137,138,138,138,0} ;

        final int outl2[] = new int[] {138,138,138,138,138,139,
                140,130,122,124,122,128,130,122,124,125,133,125,
                130,130,130,141,142,138,138,138,138,138,0} ;

        blankScreen() ;
        writeChar(0,2, 136) ;       /* write Mountain char    */
        writeChar(0,29,137) ;
        writeChar(0,27, Characters.Grave.ordinal()) ;
        tiVideo.displayAt(1,2, outl1);
        tiVideo.displayAt(2,2, outl2) ;


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

        tiVideo.DisplayAt(row++,12, outl6) ;     // safe area

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

    public void displayLevelMenu(){
        tiVideo.displayAt(6,8,"LEVEL 1 = NOVICE") ;
            tiVideo.displayAt(7,8,"LEVEL 2 = MASTER") ;
            tiVideo.displayAt(8,8,"LEVEL 3 = INSANE") ;
            tiVideo.displayAt(10,10, "YOUR CHOICE?") ;
            tiVideo.displayAt(14,5, "PRESS AID FOR RULES") ;
    }

    private void initColors(){
        byte[] colors ={0x1b,0x1b,0x1b,0x1b,0x1b,0x1b,0x1b,0x1b,0x6b,0x4b,(byte)0xcb,0x1b,0x1b,0x4b,(byte) 0xdb,0x47};
        for (int i=0; i < colors.length; i++ )  {
            tiVideo.setColor(i+3, colors[i]) ;
        }
    }

    private void initChars(){

        tiVideo.setChar(92, new byte[]{0x3c, 0x42, (byte)0x99, (byte)0x0a1, (byte)0xa1,(byte) 0x099,0x042,0x03c}) ;    // copyright
        tiVideo.setChar(93, new byte[]{0x08, 0x2a, 0x3a, 0x0e, 0x08, 0x08, 0x7e, (byte)0x0ff});       	// Tombstone
        tiVideo.setChar(94, new byte[]{ 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte)0x0ff}) ;       	// Top Edge
        tiVideo.setChar(95, new byte[]{ (byte)0x0ff, (byte)0x0ff, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}) ;      	// Bottom Edge
        tiVideo.setChar(96, new byte[]{ (byte)0x0ff, (byte)0x0ff, (byte)0x0ff, (byte)0x0ff,(byte) 0x0ff, (byte)0x0ff, (byte)0x0ff, (byte)0x0ff}) ; // border char
        tiVideo.setChar(97, new byte[]{ (byte) 0x96, 0x56, 0x38, 0x7c, (byte)0x0bf, 0x3c, 0x52, (byte)0x89});     	// explosion

           /*   --- Ship ----  */
        tiVideo.setChar(104,new byte[]{ 0x00, (byte)0x0e0, 0x70, 0x7e, 0x7e, 0x70, (byte)0xe0, 0x00}) ;      	// ship right
        tiVideo.setChar(105,new byte[]{ 0x00, 0x18, 0x18, 0x18, 0x3c, 0x7e, 0x7e, 0x42});     	// ship up
        tiVideo.setChar(106,new byte[]{ 0x42, 0x7e, 0x7e, 0x3c, 0x18, 0x18, 0x18, 0x00}) ;       	// ship down
        tiVideo.setChar(107,new byte[]{ 0x00, 0x07, 0x0e, 0x7e, 0x7e, 0x0e, 0x07, 0x00}) ;       	// ship left
        /* --- bullet   --- */
        tiVideo.setChar(108, new byte[]{ 0x00, 0x00, 0x00, 0x3c, 0x3c, 0x00, 0x00, 0x00}) ;       	// bullet horz
        tiVideo.setChar(109, new byte[]{ 0x00, 0x00, 0x18, 0x18, 0x18, 0x18, 0x00, 0x00}) ;        // bullet vert

        /*   --- Large Monster --- */
        tiVideo.setChar(0, new byte[]{ (byte)0x0bd, 0x07f, (byte)0x0ff, (byte)0x0ff, 0x07e, 0x03c, 0x042, (byte)0x081}) ; /* large monst1 */
        tiVideo.setChar(1, new byte[]{ 0x03c, 0x07f, (byte)0x0ff, (byte)0x0ff, 0x07e, 0x03c, 0x042, 0x042}) ; /* large monst2 */
        tiVideo.setChar(2, new byte[]{ (byte)0x0fc, (byte)0x0fc, (byte)0x0c0, (byte)0x0c0, (byte)0x0c0, (byte)0x0c0, (byte)0x0c0, (byte)0x0c0}) ; /* Tombstone    */
        tiVideo.setChar(3, new byte[]{ (byte)0x0fc, (byte)0x0fc, (byte)0x0cc, (byte)0x0cc, (byte)0x0cc, (byte)0x0cc, (byte)0x0cc, (byte)0x0cc}) ; /* Title     */
        tiVideo.setChar(4, new byte[]{ 0x08, 0x2a, 0x3a, 0x0e, 0x08, 0x08, 0x7e, (byte)0x0ff}) ;       	// Tombstone
        tiVideo.setChar(5, new byte[]{ 0x00, 0x5a, 0x3c, 0x7e, 0x7e, 0x3c, 0x5a, 0x00}) ;     	// small monst1
        tiVideo.setChar(6,new byte[]{ 0x00, 0x18, (byte)0x0ff, 0x7e, 0x7e, (byte)0x0ff, 0x18, 0x00}) ;      // small monst2\n" +
        tiVideo.setChar(112, new byte[]{ (byte)0x0bd, 0x07f, (byte)0x0ff, (byte)0x0ff, 0x07e, 0x03c, 0x042, (byte)0x081}) ; /* large monst1 */
        tiVideo.setChar(113, new byte[]{ 0x03c, 0x07f, (byte)0x0ff, (byte)0x0ff, 0x07e, 0x03c, 0x042, 0x042}) ; /* large monst2 */

        tiVideo.setChar(120, new byte[]{ (byte)0x0fc, (byte)0x0fc, (byte)0x0c0, (byte)0x0c0, (byte)0x0c0, (byte)0x0c0, (byte)0x0c0, (byte)0x0c0}) ; /* Tombstone    */
        tiVideo.setChar(121, new byte[]{ (byte)0x0fc, (byte)0x0fc, (byte)(byte)0x0cc, (byte)0x0cc, (byte)0x0cc, (byte)0x0cc, (byte)0x0cc, (byte)0x0cc}) ; /* Title     */
        tiVideo.setChar(122, new byte[]{ (byte)0x0cc, (byte)0x0cc, (byte)0x0cc, (byte)0x0cc, (byte)0x0cc, (byte)0x0cc, (byte)0x0fc, (byte)0x0fc}) ; /* Title     */
        tiVideo.setChar(123, new byte[]{ (byte)0x0fc, (byte)0x0fc, (byte)0x0c0, (byte)0x0c0, (byte)0x0c0, (byte)0x0c0, (byte)0x0fc, (byte)0x0fc}) ; /* Title     */
        tiVideo.setChar(124, new byte[]{ (byte)0x0cc, (byte)0x0cc, (byte)0x0cc, (byte)0x0cc, (byte)0x0cc, (byte)0x0cc, (byte)0x0cc, (byte)0x0cc}) ; /* Title     */
        tiVideo.setChar(125, new byte[]{ (byte)0x0c0, (byte)0x0c0, (byte)0x0c0, (byte)0x0c0, (byte)0x0c0, (byte)0x0c0, (byte)0x0fc, (byte)0x0fc}) ; /* Title     */
        tiVideo.setChar(126, new byte[]{ (byte)0x084, (byte)0x0cc, (byte)0x0fc, (byte)0x0fc, (byte)0x0cc, (byte)0x0cc, (byte)0x0cc, (byte)0x0cc}) ; /* Title     */
        tiVideo.setChar(127, new byte[]{ (byte)0x0cc, (byte)0x0cc, (byte)0x0ec, (byte)0x0fc, (byte)0x0dc, (byte)0x0cc, (byte)0x0cc, (byte)0x0cc}) ; /* Title     */
        tiVideo.setChar(128, new byte[]{ 0x00c, 0x00c, 0x00c, 0x00c, 0x00c, 0x00c, (byte)0x0fc, (byte)0x0fc}) ; /* Title     */
        tiVideo.setChar(129, new byte[]{ (byte)0x0fc, (byte)0x0fc, 0x030, 0x030, 0x030, 0x030, 0x030, 0x030}) ; /* Title     */
        tiVideo.setChar(130, new byte[]{ 0x030, 0x030, 0x030, 0x030, 0x030, 0x030, 0x030, 0x030}) ; /* Title     */
        tiVideo.setChar(131, new byte[]{ (byte)0x0fc, (byte)0x0fc, (byte)0x0c0, (byte)0x0c0, (byte)0x0c0, (byte)0x0c0, (byte)0x0f0, (byte)0x0f0}) ; /* Title     */
        tiVideo.setChar(132, new byte[]{ (byte)0x0fc, (byte)0x0fc, (byte)0x0cc, (byte)0x0cc, (byte)0x0cc, (byte)0x0cc, (byte)0x0fc, (byte)0x0fc}) ; /* Title     */
        tiVideo.setChar(133, new byte[]{ 0x000, 0x000, 0x000, 0x000, 0x000, 0x000, 0x000, 0x000}) ; /* Title blank  */
        tiVideo.setChar(134, new byte[]{ 0x000, 0x000, 0x000, 0x000, 0x000, 0x000, 0x000, 0x000}) ; /* safe area blank */
        tiVideo.setChar(135, new byte[]{ 0x000, 0x000, 0x000, 0x000, 0x000, 0x000, 0x000, 0x000}) ; /* unsed     */
        tiVideo.setChar(136, new byte[]{ (byte)0x080, (byte)0x0c0, (byte)0x0e0, (byte)0x0f0, (byte)0x0f8, (byte)0x0fc, (byte)0x0fe, (byte)0x0ff}) ; /* Left below diag */
        tiVideo.setChar(137, new byte[]{ 0x001, 0x003, 0x007, 0x00f, 0x01f, 0x03f, 0x07f, (byte)0x0ff}) ; /* right below diag*/
        tiVideo.setChar(138, new byte[]{ (byte)0x0ff, (byte)0x0ff, (byte)0x0ff, (byte)0x0ff, (byte)0x0ff, (byte)0x0ff, (byte)0x0ff, (byte)0x0ff}) ; /* solid     */
        tiVideo.setChar(139, new byte[]{ (byte)0x080, (byte)0x0e0, (byte)0x0f8, (byte)0x0fe, (byte)0x0ff, (byte)0x0ff, (byte)0x0ff, (byte)0x0ff}) ;
        tiVideo.setChar(140, new byte[]{ 0x000, 0x000, 0x000, 0x000, (byte)0x080, (byte)0x0e0, (byte)0x0f8, (byte)0x0ff}) ;
        tiVideo.setChar(141, new byte[]{ 0x000, 0x000, 0x000, 0x000, 0x001, 0x007, 0x01f, (byte)0x0ff}) ;
        tiVideo.setChar(142, new byte[]{ 0x001, 0x007, 0x01f, 0x07f, (byte)0x0ff, (byte)0x0ff, (byte)0x0ff, (byte)0x0ff}) ;
        tiVideo.setChar(152, new byte[]{ (byte)0x0ff, (byte)0x0ff, (byte)0x0c3, (byte)0x0c3, (byte)0x0c3, (byte)0x0c3, (byte)0x0ff, (byte)0x0ff}) ; /* safe area col. */
//        /* --- Small Monster    --- */
        tiVideo.setChar(144, new byte[]{ 0x00, 0x5a, 0x3c, 0x7e, 0x7e, 0x3c, 0x5a, 0x00}) ;     	// small monst1
        tiVideo.setChar(145,new byte[]{ 0x00, 0x18, (byte)0x0ff, 0x7e, 0x7e, (byte)0x0ff, 0x18, 0x00}) ;      	// small monst2

    }
}
