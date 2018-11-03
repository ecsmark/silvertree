package com.silvertree.tombstone;

import com.silvertree.tombstone.tiemulation.ITIVideo;
import javafx.scene.shape.Rectangle;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class GameBoard {
    final static  Rectangle s_rectSafeArea = new Rectangle(12, 8, 19 , 13  );

    final static  int MAXROW = 23 ;
    final static int MAXCOL = 31 ;
    final static int MAXGRAVEPAIRS = 30 ;

    public final static int INITSHIPLOC = 367 ;
    public final static int PLAYAREABG = 98;
    public final static int PLAYAREAEND = 670;

    final static int BORDER[] =
            {96, 96, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32,
                    32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32,
                    32, 32, 32, 32, 32, 32, 96, 96
            };

    ITIVideo tiVideo ;

    Characters Ship = Characters.ShipRight;
    int currentShipLocation = INITSHIPLOC ;

    public GameBoard(ITIVideo tiVideo){
        this.tiVideo = tiVideo ;
        initRandom() ;
        initChars();
        initColors();
        blankScreen();
    }

    /**
     *
     */
    public void blankScreen(){
        for (int row=0; row < MAXROW+1; row++)
        {

            for (int col=0; col < MAXCOL+1; col++)
            {
                writeChar(row, col, BORDER[col] ) ;

            }
        }
    }

    /**
     * retrieve the character in the gameboard at the screen position.
     *
     * @param position - screen relative character position
     * @return character at position
     */
    public int getChar(int position){
        byte byteValue = tiVideo.getChar(position);
        return ((int)byteValue)   & 0x00ff  ;
    }

    public Characters getCharacter(int position){
        int charValue = getChar(position);
        return Characters.get(charValue);

    }
    public void putBlank(int position){
        putBlank(position / ITIVideo.NumColums, position % ITIVideo.NumColums);
    }
    public void putBlank(int row, int col)
    {

        int     c = ' ' ;

        //   check for location in safe area
        if (row >= 8 && row <= 14)
        {
            if (col >=12 && col <= 18)
                c = Characters.SafeAreaBlank.getChrIndex() ;
        }

        writeChar(row, col, c) ;
    }

    public void displayAt(int row, int col, Characters[] characters){
        for(Characters character: characters)   {
            writeChar(row, col++, character);
        }
    }

    public void writeChar(int row, int col, Characters chr){
        tiVideo.wrChar(row, col, chr.getChrIndex());
    }

    public void writeChar(int position, Characters chr){
        writeChar(position, chr.getChrIndex());
    }

    private void writeChar(int position, int chr){
        writeChar(position / ITIVideo.NumColums, position % ITIVideo.NumColums, chr);
    }

    public void writeChar(int row, int col, int  chr){
        tiVideo.wrChar(row, col,  chr);
    }

    public void refresh(){
        /*tiVideo.refresh();*/
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
        writeChar(0,27, Characters.Grave) ;
        tiVideo.displayAt(1,2, outl1);
        tiVideo.displayAt(2,2, outl2) ;


    }

    void draw(int day)
    {
        final  Characters[] outl6 =new Characters[] {Characters.SafeAreaColumn, Characters.SafeAreaBlank, Characters.SafeAreaColumn,
                Characters.SafeAreaBlank, Characters.SafeAreaColumn, Characters.SafeAreaBlank, Characters.SafeAreaColumn	};
        final  Characters[] outl7 = new Characters[] {Characters.SafeAreaBlank, Characters.SafeAreaBlank, Characters.SafeAreaBlank,
                Characters.SafeAreaBlank,Characters.SafeAreaBlank,Characters.SafeAreaBlank,Characters.SafeAreaBlank};


        for (int i=0; i<28; i++)
        {
            writeChar(21, 2+i, Characters.BottomEdge) ;

        }

        tiVideo.displayAt(22,3, "DAY  POPULATION  SCHOONERS") ;
        int row = 8 ;
        for (int i=0; i<3; i++)
        {
            displayAt(row++,12, outl6) ;    // safe area
            displayAt(row++,12, outl7) ;
        }

        displayAt(row++,12, outl6) ;     // safe area

        // ----------------------------------------------------
        // Throw up pairs of graves randomly
        // ----------------------------------------------------
        int graves = Math.min((day*4+1), MAXGRAVEPAIRS)   ;

        for (int n=0; n < graves; )
        {
            // find a blank random location
            int grave1=  RandomBlank() ;
            int grave2= neighbor((short) random.nextInt(7))+grave1;
            if (areAllNeighborsBlank(grave1) && areAllNeighborsBlank(grave2))
            {
                writeChar(grave1, Characters.Grave) ;
                writeChar(grave2, Characters.Grave) ;
                n++ ;
            }
        }
        //   put ship to screen in safe area

        currentShipLocation = INITSHIPLOC ;
        writeChar(currentShipLocation,  Ship) ;


    }



    public void displayScore(int score) {
        tiVideo.displayAt(23,8, "          ") ;
        DisplayNumeric(score,23,17) ;

    }
    public void displayDay(int day){
        DisplayNumeric(day, 23, 5);
    }

    void DisplayNumeric(int val, int row, int col)
    {

        while (val > 0)
        {
            int rem = val % 10 ;
            int c = rem + '0' ;
            tiVideo.wrChar(row, col--, c) ;
            val = val / 10 ;
        }
    }

    void displaySchooners(int numberSchooners){
        DisplayNumeric(numberSchooners, 23, 28);
    }

    public void safeAreaBlueOnBlue(){
        tiVideo.setColor(19,(byte) 0x44);
    }

    public void displayLevelMenu(){
        tiVideo.displayAt(6,8,"LEVEL 1 = NOVICE") ;
            tiVideo.displayAt(7,8,"LEVEL 2 = MASTER") ;
            tiVideo.displayAt(8,8,"LEVEL 3 = INSANE") ;
            tiVideo.displayAt(10,10, "YOUR CHOICE?") ;
            tiVideo.displayAt(14,5, "PRESS AID FOR RULES") ;
    }


    public boolean isInSafeArea(int position)
    {
        int row = position/ 32;
        int col = position % 32 ;

        return (row >= 8 &&  row <= 14 && col >= 12 && col <= 18) ? true : false ;

    }

    public boolean isSafeAreaSurrounded()
    {
        final int surm[] =
                {237, 239, 241, 307, 371, 435,
                        497, 495, 493, 427, 363, 299
                };

        for(int position : surm)
        {
            if (getChar(position) != Characters.Grave.getChrIndex())
            {
                return(false) ;
            }
        }
        return(true) ;
    }


    public void displayHelpMenu(){
        final String szRule1 ="MOVE SCHOONER\u0082 ARROW KEYS" ;
        final String szRule2 ="FIRE MISSILE \u0082Q/Y/INSERT" ;
        final String szRule3 ="] SAGUARO    \u0082  0 POINTS";
        final String szRule4 ="\u0090 TUMBLEWEED \u0082 100 POINTS";
        final String szRule5 = "p MORG       \u0082 150 POINTS";
        final String szRule6 ="RESTART GAME \u0082REDO";
        final String szRule7 ="SELECT LEVEL \u0082 BACK";
        final String szRule8 ="PANIC BUTTON \u0082SPACE BAR";
        final String szOut11 ="PRESS ANY KEY TO CONTINUE";

        preGameScreen() ;
        tiVideo.displayAt(5,4, szRule1) ;
        tiVideo.displayAt(6,4, szRule2) ;
        tiVideo.displayAt(8,4, szRule3) ;
        tiVideo.displayAt(9,4, szRule4) ;
        tiVideo.displayAt(10,4,szRule5 ) ;
        tiVideo.displayAt(12,4,szRule6 ) ;
        tiVideo.displayAt(13,4,szRule7 ) ;
        tiVideo.displayAt(14,4,szRule8 ) ;
        tiVideo.displayAt(21,4, szOut11) ;
        tiVideo.wrChar(7, 17, 0x82) ;
        tiVideo.wrChar(11,17, 0x82) ;

    }
    int s_neighborCells[] = new int[] {	-32, -31, 1, 33, 32, 31, -1, -33 };

    boolean areAllNeighborsBlank(int screenloc)
    {
        boolean areBlank = true ;
        for (int i=0; i< 8 && areBlank; i++)
        {
            areBlank = (tiVideo.getChar(screenloc+neighbor(i)) == ' ') ;
        }
        return( areBlank ) ;
    }

    int neighbor(int cell)
    {
        if (cell < 0 ) cell = 0 ;
        return(s_neighborCells[cell]);

    }

    public int randomPlayAreaLocation(){
        return random.nextInt(PLAYAREAEND-PLAYAREABG)+PLAYAREABG;
    }

    int RandomBlank()
    {
        int tryCount = 0 ;
        int blank_loc ;
        do
        {
            blank_loc =  randomPlayAreaLocation() ;
            tryCount++ ;
        } while (tiVideo.getChar(blank_loc) != ' ') ;
        System.out.println("RandomBlank tryCount ="+tryCount);
        return(blank_loc) ;
    }
    private void initColors(){
        byte[] colors ={0x1b,0x1b,0x1b,0x1b,0x1b,0x1b,0x1b,0x1b,0x6b,0x4b,(byte)0xcb,0x1b,0x1b,0x4b,(byte) 0xdb,0x47};
        for (int i=0; i < colors.length; i++ )  {
            tiVideo.setColor(i+3, colors[i]) ;
        }
    }

    private void initChars(){

        tiVideo.setChar(92, new byte[]{0x3c, 0x42, (byte)0x99, (byte)0x0a1, (byte)0xa1,(byte) 0x099,0x042,0x03c}) ;    // copyright
        tiVideo.setChar(Characters.Grave.getChrIndex(), new byte[]{0x08, 0x2a, 0x3a, 0x0e, 0x08, 0x08, 0x7e, (byte)0x0ff});       	// Tombstone
        tiVideo.setChar(94, new byte[]{ 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte)0x0ff}) ;       	// Top Edge
        tiVideo.setChar(Characters.BottomEdge.getChrIndex(), new byte[]{ (byte)0x0ff, (byte)0x0ff, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}) ;      	// Bottom Edge
        tiVideo.setChar(96, new byte[]{ (byte)0x0ff, (byte)0x0ff, (byte)0x0ff, (byte)0x0ff,(byte) 0x0ff, (byte)0x0ff, (byte)0x0ff, (byte)0x0ff}) ; // border char
        tiVideo.setChar(Characters.Explode.getChrIndex(), new byte[]{ (byte) 0x96, 0x56, 0x38, 0x7c, (byte)0x0bf, 0x3c, 0x52, (byte)0x89});     	// explosion

           /*   --- Ship ----  */
        tiVideo.setChar(Characters.ShipRight.getChrIndex(),new byte[]{ 0x00, (byte)0x0e0, 0x70, 0x7e, 0x7e, 0x70, (byte)0xe0, 0x00}) ;      	// ship right
        tiVideo.setChar(Characters.ShipUp.getChrIndex(),new byte[]{ 0x00, 0x18, 0x18, 0x18, 0x3c, 0x7e, 0x7e, 0x42});     	// ship up
        tiVideo.setChar(Characters.ShipDown.getChrIndex(),new byte[]{ 0x42, 0x7e, 0x7e, 0x3c, 0x18, 0x18, 0x18, 0x00}) ;       	// ship down
        tiVideo.setChar(Characters.ShipLeft.getChrIndex(),new byte[]{ 0x00, 0x07, 0x0e, 0x7e, 0x7e, 0x0e, 0x07, 0x00}) ;       	// ship left
        /* --- bullet   --- */
        tiVideo.setChar(Characters.BulletHorizontal.getChrIndex(), new byte[]{ 0x00, 0x00, 0x00, 0x3c, 0x3c, 0x00, 0x00, 0x00}) ;       	// bullet horz
        tiVideo.setChar(Characters.BulletVertical.getChrIndex(), new byte[]{ 0x00, 0x00, 0x18, 0x18, 0x18, 0x18, 0x00, 0x00}) ;        // bullet vert

        /*   --- Large Monster --- */
        tiVideo.setChar(0, new byte[]{ (byte)0x0bd, 0x07f, (byte)0x0ff, (byte)0x0ff, 0x07e, 0x03c, 0x042, (byte)0x081}) ; /* large monst1 */
        tiVideo.setChar(1, new byte[]{ 0x03c, 0x07f, (byte)0x0ff, (byte)0x0ff, 0x07e, 0x03c, 0x042, 0x042}) ; /* large monst2 */
        tiVideo.setChar(2, new byte[]{ (byte)0x0fc, (byte)0x0fc, (byte)0x0c0, (byte)0x0c0, (byte)0x0c0, (byte)0x0c0, (byte)0x0c0, (byte)0x0c0}) ; /* Tombstone    */
        tiVideo.setChar(3, new byte[]{ (byte)0x0fc, (byte)0x0fc, (byte)0x0cc, (byte)0x0cc, (byte)0x0cc, (byte)0x0cc, (byte)0x0cc, (byte)0x0cc}) ; /* Title     */
        tiVideo.setChar(4, new byte[]{ 0x08, 0x2a, 0x3a, 0x0e, 0x08, 0x08, 0x7e, (byte)0x0ff}) ;       	// Tombstone
        tiVideo.setChar(5, new byte[]{ 0x00, 0x5a, 0x3c, 0x7e, 0x7e, 0x3c, 0x5a, 0x00}) ;     	// small monst1
        tiVideo.setChar(6,new byte[]{ 0x00, 0x18, (byte)0x0ff, 0x7e, 0x7e, (byte)0x0ff, 0x18, 0x00}) ;      // small monst2\n" +
        tiVideo.setChar(Characters.Large1.getChrIndex(), new byte[]{ (byte)0x0bd, 0x07f, (byte)0x0ff, (byte)0x0ff, 0x07e, 0x03c, 0x042, (byte)0x081}) ; /* large monst1 */
        tiVideo.setChar(Characters.Large2.getChrIndex(), new byte[]{ 0x03c, 0x07f, (byte)0x0ff, (byte)0x0ff, 0x07e, 0x03c, 0x042, 0x042}) ; /* large monst2 */

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
        tiVideo.setChar(Characters.SafeAreaBlank.getChrIndex(), new byte[]{ 0x000, 0x000, 0x000, 0x000, 0x000, 0x000, 0x000, 0x000}) ; /* safe area blank */
        tiVideo.setChar(135, new byte[]{ 0x000, 0x000, 0x000, 0x000, 0x000, 0x000, 0x000, 0x000}) ; /* unsed     */
        tiVideo.setChar(136, new byte[]{ (byte)0x080, (byte)0x0c0, (byte)0x0e0, (byte)0x0f0, (byte)0x0f8, (byte)0x0fc, (byte)0x0fe, (byte)0x0ff}) ; /* Left below diag */
        tiVideo.setChar(137, new byte[]{ 0x001, 0x003, 0x007, 0x00f, 0x01f, 0x03f, 0x07f, (byte)0x0ff}) ; /* right below diag*/
        tiVideo.setChar(138, new byte[]{ (byte)0x0ff, (byte)0x0ff, (byte)0x0ff, (byte)0x0ff, (byte)0x0ff, (byte)0x0ff, (byte)0x0ff, (byte)0x0ff}) ; /* solid     */
        tiVideo.setChar(139, new byte[]{ (byte)0x080, (byte)0x0e0, (byte)0x0f8, (byte)0x0fe, (byte)0x0ff, (byte)0x0ff, (byte)0x0ff, (byte)0x0ff}) ;
        tiVideo.setChar(140, new byte[]{ 0x000, 0x000, 0x000, 0x000, (byte)0x080, (byte)0x0e0, (byte)0x0f8, (byte)0x0ff}) ;
        tiVideo.setChar(141, new byte[]{ 0x000, 0x000, 0x000, 0x000, 0x001, 0x007, 0x01f, (byte)0x0ff}) ;
        tiVideo.setChar(142, new byte[]{ 0x001, 0x007, 0x01f, 0x07f, (byte)0x0ff, (byte)0x0ff, (byte)0x0ff, (byte)0x0ff}) ;
        tiVideo.setChar(Characters.SafeAreaColumn.getChrIndex(), new byte[]{ (byte)0x0ff, (byte)0x0ff, (byte)0x0c3, (byte)0x0c3, (byte)0x0c3, (byte)0x0c3, (byte)0x0ff, (byte)0x0ff}) ; /* safe area col. */
        /* --- Small Monster    --- */
        tiVideo.setChar(Characters.Small1.getChrIndex(), new byte[]{ 0x00, 0x5a, 0x3c, 0x7e, 0x7e, 0x3c, 0x5a, 0x00}) ;     	// small monst1
        tiVideo.setChar(Characters.Small2.getChrIndex(),new byte[]{ 0x00, 0x18, (byte)0x0ff, 0x7e, 0x7e, (byte)0x0ff, 0x18, 0x00}) ;      	// small monst2

    }

    private Random random ;

    private void initRandom() {
        random = ThreadLocalRandom.current();
    }
    private static long rand16 = Math.abs((long) (Math.random() * 10000.0));
    short randno()
    {

        int  n = (int)( 28645*rand16 +31417) ;
        rand16 = n ;
        return((short) (Math.abs(rand16) & 0x7fff) );
    }
}
