package com.silvertree.tombstone;

import com.silvertree.tombstone.tiemulation.ITIKeyboard;
import com.silvertree.tombstone.tiemulation.IVirtualTI;
import com.silvertree.tombstone.tiemulation.TIEmulatorEvent;
import com.silvertree.tombstone.tiemulation.TIKeyboardEventListener;
import com.silvertree.tombstone.tiemulation.impl.TIKeyboard;
import javafx.animation.AnimationTimer;

import java.util.ArrayList;
import java.util.List;

public class TombstoneCity {

    int   	m_nLevFlg  ;
    int   	m_nSpeed  ;
    int   	SMonct  ;		/* Small Monster count     */
    int   	LMonct  ;		/* Large Monster count     */
    int   	Score10  ;  	/* Score (10,000)          */
    int   	Score ;       	/* Score - digits 0-9999      */
    int   	Schooners  ;
    int   	m_nShiploc ;	/* current ship location      */
    int   	m_nGencur  ;	/* current Generator location   */
    int   	Sprloc  ;
    int   	Sprflg  ;
    Characters		Ship ;
    int     day = 0 ;
    List<Integer> SMontab ;
    int		LMontab[] = new int [24] ;      /* Large monster table     */
    int		LMontb[] ;       /* ptr to large monster table   */
    int		MouseFlag ;     /* mouse installed         */

    GameBoard gameBoard ;
    IVirtualTI virtualTI ;

    static final int BONUS = 1000 ;

    static final int SMALLMONTYPE = 0 ;
    static final int LARGEMONTYPE =1;

    static final ITIKeyboard.TIKeycode PANICKEY = TIKeyboard.TIKeycode.SPACE ;
    static final TIKeyboard.TIKeycode FIREKEY = TIKeyboard.TIKeycode.Q ;

    public TombstoneCity(IVirtualTI pTI99) {
        virtualTI = pTI99;

        SMonct = 0;      // Small Monster count
        LMonct = 0;      // Large Monster count
        Score10 = 0;      // Score (10,000)
        Score = 0;       // Score - digits 0-9999
        Schooners = 0;
        Ship = Characters.ShipRight;
        ;   // current ship character
        m_nShiploc = GameBoard.INITSHIPLOC;     // current ship location
        m_nGencur = 0;      // current Generator location
        Sprloc = 0;
        Sprflg = 0;

        MouseFlag = 0;     /* mouse installed         */

        gameBoard = new GameBoard(pTI99.getVideo());

        //if (MouseFlag = MOreset( &num_buttons) )
        //	MOsetpos(MOUSEX, MOUSEY) ;
        themeSong();
        Score = -1000;
        Schooners = 9;
    }
    public void start(){
        dispLevelMenu();
        virtualTI.getVideo().refresh();


    }

    private void handleGamePlayKeys(ITIKeyboard.TIKeyboardEvent event) {
        System.out.println("handleGamePlayKeys "+event.getKeyCode());
        switch(event.getKeyCode()){
//            case FIREKEY1:
//            case FIREKEY2:
//            case FIREKEY3:
//                c = FIREKEY ;
//                fire();
//                break;
//
//            case PANICKEY:
//                panic_key() ;
//                break;
//
            case RIGHT:
                movshp(Characters.ShipRight, 1);
                break;
            case LEFT:
                movshp(Characters.ShipLeft, -1 ) ;
                break;
            case DOWN:
                movshp(Characters.ShipDown, 32) ;
                break;
            case UP:
                movshp(Characters.ShipUp, -32) ;
                break;
            case Q:
                fire() ;
                break;
            case SPACE:
                panic_key();
                break;

//            case 0:
//            {
//                int x ;
//                int y ;
//                bool fireButton ;
//                m_pTI99->checkJoy(x, y, fireButton) ;
//                if (fireButton)
//                {
//                    fire() ;
//                }
//                else if (x > 0 )
//                {
//                    movshp(SHIPRT, 1);
//                    Msg("got an x > 0") ;
//                }
//                else if (x < 0 )
//                {
//                    movshp(Characters.ShipLeft, -1);
//                    Msg("got an x < 0") ;
//                }
//                else if (y < 0)
//                {
//                    movshp(Characters.ShipUp, -32);
//                }
//                else if (y > 0 )
//                {
//                    movshp(Characters.ShipDown, 32);
//                }
//            }
        }
        virtualTI.getVideo().refresh();

    }

    public boolean playDay(int day)
    {

        final byte     sprite[] ={(byte) 0xf7, (byte)0xd5, (byte) 0xc5, (byte) 0xf1, (byte) 0xf7, (byte) 0xf7, (byte) 0x81,0	};
        boolean bEndGame = false ;
        boolean bDoneWithDay = false ;

        LMontb = LMontab ;
        SMontab = new ArrayList<>(40);
        LMonct = 0 ;
        gameBoard.preGameScreen() ;
        gameBoard.displayDay(day);
        Score += BONUS ;
        gameBoard.displayScore(Score);
        gameBoard.displaySchooners(Schooners)  ;
        gameBoard.safeAreaBlueOnBlue();
        gameBoard.draw(day) ;
        if (SMonct == 0)
            genSmallMonsters();

       return( !bEndGame );

    }
    void gameBegin(){
        System.out.println("gameBegin");
        virtualTI.getKeyboard().onKeyPressed(new TIKeyboardEventListener<ITIKeyboard.TIKeyboardEvent>() {
            @Override
            public void handle(TIEmulatorEvent event) {
                handleGamePlayKeys((ITIKeyboard.TIKeyboardEvent) event);
            }
        });

        // -------- Start a new day --------------
        day = 0;
        playDay(++day);
    }

    void gameEnd()
    {

//        createSprite() ;
//        gameBoard.Video()->DisplayAt(11,4, "PRESS REDO OR BACK") ;
//        //gameBoard.Video()->refresh() ;
//        while(m_pTI99->getKey() != TIKEYREDO) ;
    }

    void themeSong()
    {
    }




// --------------------------------------------------------------------------------
//
// RandomBlank
//
//  Description:	find a random blank location within playing area
//
//  Parameters:		None.
//
//  Returns:		location of blank 
//
// --------------------------------------------------------------------------------

    int randomBlank()
    {
        int     blank_loc ;

//        do
//        {
            blank_loc = randno() ;
            blank_loc = (blank_loc >> 7)+128 ;
//        } while (gameBoard.GetChar(blank_loc) != ' ') ;

        return(blank_loc) ;
    }


// --------------------------------------------------------------------------------
//
// GenSmallMonsters
//
//  Description: 	generate 20 small monsters
//
//  Parameters:		None.
//
//  Returns:		None.
//
//  Original Name:	gensml
//
// --------------------------------------------------------------------------------

    void genSmallMonsters()
    {
        System.out.println("genSmallMonsters");
        for (int i=0; i< 20; i++)
        {
            int  screen_loc ;
            Characters  c ;

            do
            {
                screen_loc = gameBoard.randomPlayAreaLocation() ;
            } while (gameBoard.getChar(screen_loc) != ' ') ;

            int r  = randno() ;
            if (r > (r &0xff))
                c = Characters.Small1 ;
            else
                c = Characters.Small2;

            gameBoard.writeChar(screen_loc, c) ;
		    SMontab.add(screen_loc );
            ++SMonct ;
        }
   }


//  ----------------------------------------------------------------------- 
//   GenerateLargeMonsters() - subroutine to check for generators and add monster to   
//         monster table. 
//
//   Returns:   TRUE : generator found
//     			FALSE: no generator found
// 
//      Genlas - Last location checked on last entry.
//      m_nGencur - current screen location being checked.  
//      Genrel -  screen location to relase new monster. 
//
// 	Original Name:  genrat
//  -----------------------------------------------------------------------  


    boolean generateLargeMonsters()
    {
//        static  int     Genlas ;
//        static  int     Genrel ;
//
//
        boolean	generatorFound = false ;  	/*  flag set if generator encountered.  */
//        boolean	releasePointFound = false ; /*  flag set if able to release monster */
//
//        gameBoard.SafeAreaBlueOnLightBlue() ;     /* safe area blue on light blue */
//        Genlas = m_nGencur ;
//        int save = m_nGencur ;
//
//
//        while (++m_nGencur != Genlas && !(generatorFound && releasePointFound))
//        {
//            if (m_nGencur >= PLAYAREAEND)
//            {
//                m_nGencur = PLAYAREABEG ;
//                ++Genlas ;
//                if (Genlas > PLAYAREAEND)
//                    Genlas = PLAYAREAEND ;
//            }
//
//            int  c  = gameBoard.GetChar(m_nGencur) ;
//            if (c == GRAVE)
//            {
//                /* ------------------------------------------------------------ */
//                /* found a Saguaro -> if another one adjacent then we have      */
//                /* a generator               									*/
//                /* ------------------------------------------------------------ */
//                for (int i=0 ; i< 8; i++)
//                {
//                    c = gameBoard.GetChar(m_nGencur+gameBoard.Neighbor(i)) ;
//                    if (c == GRAVE)
//                        generatorFound = true ;    /* found a generator         */
//                    else if (c == ' ')
//                    {       /* found Monster release position   */
//                        releasePointFound = true  ;
//                        Genrel = m_nGencur+gameBoard.Neighbor(i) ;
//                    }
//
//                }
//
//                /*   Did we find a generator yet ?              */
//                if (generatorFound && releasePointFound)
//                {
//                    /* put up sprite at generator     */
//                    Sprloc = m_nGencur ;
//                    gameBoard.Video()->Locate(0, CGameBoard::Row(m_nGencur)*8,CGameBoard::Column(m_nGencur)*8) ;
//
//                    if (Sprflg != 1)
//                    { /* release Large monster    */
//                        ReleaseMonster(Genrel) ;
//                    }
//                    else
//                    {
//                        Sprflg = 0 ;  /* reset sprite flag */
//                        m_nGencur = save ;
//                    }
//                    break ;
//                }
//            }
//        }
        return(generatorFound) ;
    }

    void releaseMonster(int loc)
    {

        // put up sprite at generator 
//        Sprloc = m_nGencur ;
//        gameBoard.Video()->Locate(0, CGameBoard::Row(m_nGencur)*8,CGameBoard::Column(m_nGencur)*8) ;
//
//        // release Large monster
//        if (LMonct < MAXMNL)
//        {
//            gameBoard.WriteChar(loc, LARGE2) ;
//            ++m_nGencur ;
//            ++LMonct ;
//		*LMontb++ = loc ;
//        }
//        gameBoard.SafeAreaBlueOnBlue() ;
    }




    void keydep()
    {
//        while(m_pTI99->checkKeyboard() == 0)
//            randno() ;
    }


    // -----------------------------------------------------------------------
    //  movshp() - move ship
    // -----------------------------------------------------------------------
    boolean movshp(Characters shipCharacter, int offset)
    {
        System.out.println("movshp("+shipCharacter+","+offset+")");
        int     newshiploc ;

        if (shipCharacter != Ship)	/* check to see if orientation changes	    */
        {
            gameBoard.writeChar(m_nShiploc, shipCharacter) ;
            Ship = shipCharacter ;
            offset = 0 ;
        }

        if (LMonct == 0)
        {
            /* kill time of same depressed	    */
        }
        newshiploc = offset + m_nShiploc ;
        int currentChr = gameBoard.getChar(newshiploc);
        if ((currentChr != ' ' && currentChr !=  Characters.SafeAreaBlank.getChrIndex())|| currentChr ==  Ship.getChrIndex())
            return( false ) ;
        gameBoard.writeChar(newshiploc, Ship) ;
        gameBoard.putBlank(m_nShiploc) ;
        m_nShiploc = newshiploc ;
        return( false ) ;
    }


    /* ----------------------------------------------------------------------- */
    /*  movmon.c - Move monster routines                  */
    /* ----- Move large monsters toward spaceship or small monster away ------ */
    /* ----------------------------------------------------------------------- */
    void moveSmallMonsters()
    {
//        int     *montab ;
//        int     newmonloc ;
//
//        if (SMonct != 0)
//        {
//
//            int shiprow = m_nShiploc /32 ;
//            int shipcol = CGameBoard::Column(m_nShiploc);
//            for (montab=SMontab; montab != SMontb; montab++)
//            {
//                int monrow = (*montab)/32 ;
//                int moncol = CGameBoard::Column((*montab)) ;
//                if (shiprow == monrow)
//                { /* on same row */
//                    newmonloc = *montab ;
//                    if (shipcol > moncol)
//                        --newmonloc ;
//                    else
//                        ++newmonloc ;
//
//                    int c = gameBoard.GetChar(newmonloc);
//                    if (c == ' ')
//                    {
//                        monblk(*montab, newmonloc) ;
//					*montab = newmonloc ;
//                        continue ;
//                    }
//                }
//                if (shipcol == moncol)
//                { /* on same column MONCOL  */
//                    newmonloc = *montab ;
//                    if (shiprow > monrow )
//                        newmonloc-=32 ;
//                    else
//                        newmonloc+=32 ;
//
//                    int c = gameBoard.GetChar(newmonloc);
//                    if (c == ' ')
//                    {
//                        monblk(*montab, newmonloc);
//					*montab = newmonloc ;
//                        continue ;
//                    }
//
//                }
//            }
//        }
    }

// --------------------------------------------------------------------------------
//
// MoveLargeMonsters
//
//  Description:
//
//  Parameters:
//
//  Returns:
//
// --------------------------------------------------------------------------------

    void moveLargeMonsters()
    {
        int     newmonloc ;
        int     moveflg = 0;

//        if (LMonct != 0)
//        {
//
//            int shiprow = CGameBoard::Row(m_nShiploc) ;
//            int shipcol = CGameBoard::Column(m_nShiploc );
//            for (int* montab=LMontab; montab != LMontb; montab++)
//            {
//                int monrow = CGameBoard::Row(*montab);
//                int moncol = CGameBoard::Column(*montab) ;
//                if (shiprow == monrow)
//                { /* on same row */
//                    newmonloc = *montab ;
//
//                    if (moncol> shipcol)
//                        --newmonloc ;
//                    else
//                        ++newmonloc ;
//
//                    if (tryMove(montab, newmonloc))
//                        continue ;
//                }
//
//                if ( shipcol == moncol)
//                { /* on same column MONCOL  */
//                    newmonloc = *montab ;
//                    if (shiprow > monrow)
//                        newmonloc+=32 ;
//                    else
//                        newmonloc-=32 ;
//
//                    if (TryMove(montab, newmonloc))
//                        continue ;
//
//                }
//
//                if (randno() & 0x0001)
//                { /* try column  */
//                    newmonloc = *montab ;
//
//                    if (moncol> shipcol)
//                        --newmonloc ;
//                    else
//                        ++newmonloc ;
//
//                    if(TryMove(montab, newmonloc))
//                        continue ;
//                }
//                newmonloc = *montab ;
//                if (shiprow > monrow)
//                    newmonloc+=32 ;
//                else
//                    newmonloc-=32 ;
//
//                tryMove(montab, newmonloc) ;
//
//            }
//        }
    }

    boolean tryMove ( int[] montab, int newmonloc)
    {
        boolean moveFound = false ;
//        int c = gameBoard.GetChar(newmonloc);
//        if (c == ' ')
//        {
//            monblk(*montab, newmonloc) ;
//		*montab = newmonloc ;
//            moveFound =  true  ;
//
//        }
//        else if (c == Ship)
//        {
//            moveFound = CaptureShip(montab, newmonloc) ;
//        }
        return( moveFound ) ;
    }

    void monblk(int curloc, int newloc)
    {
//        BYTE monchar = gameBoard.GetChar(curloc) ;
//        if (monchar == LARGE1)
//            monchar = LARGE2 ;
//        else if (monchar == LARGE2)
//            monchar = LARGE1 ;
//        else if (monchar == SMALL1)
//            monchar = SMALL2 ;
//        else
//            monchar = SMALL1 ;
//        gameBoard.WriteChar(newloc, monchar) ;
//        gameBoard.PutBlank(curloc) ;
    }

    boolean captureShip(int[] montab, int newmonloc)
    {
        boolean captured = false ;
//        if (!isShipInSafeArea())
//        {
//            monblk(*montab, newmonloc) ;
//		*montab = newmonloc ;
//            gulp() ;
//            capture() ;
//            captured = true  ;
//        }
        return( captured ) ;
    }

    void gulp()
    {
        final char   soundlist[] =
            {3,0x81,0x07,0x90,3,
                    2,0x8B,0x06,2,
                    2,0x8F,0x05,2,
                    1,0x9f,4,
                    3,0x89,0x38,0x01,3,
                    3,0x8b,0x38,0x02,2,
                    3,0x8d,0x38,0x04,1,
                    3,0x8f,0x38,0x04,1,
                    2,0x83,0x39,1,
                    2,0x87,0x39,1,
                    2,0x8b,0x39,1,
                    2,0x8f,0x39,1,
                    2,0x83,0x3a,1,
                    2,0x87,0x3a,1,
                    2,0x8b,0x3a,1,
                    2,0x8f,0x3a,1,
                    2,0x83,0x3b,1,
                    2,0x8b,0x3b,1,
                    2,0x8f,0x3b,1,
                    2,0x83,0x3c,1,
                    2,0x87,0x3c,1,
                    2,0x8b,0x3c,1,
                    2,0x87,0x3c,1,
                    3,0x8f,0x38,0x90,3,
                    4,0x9f,0xbf,0xdf,0xff,0
            };


        //m_pTI99.sound(soundlist) ;
    }


    // ----------------------------------------------------------------------- 
//
//  scan() - Check keyboard for game keys 
//
// -----------------------------------------------------------------------
//    KEYCODE scan()
//    {
//        KEYCODE c;
//        c = m_pTI99->checkKeyboard() ;
//
//        switch(c)
//        {
//            case FIREKEY1:
//            case FIREKEY2:
//            case FIREKEY3:
//                c = FIREKEY ;
//                fire();
//                break;
//
//            case PANICKEY:
//                panic_key() ;
//                break;
//
//            case TIKEYRIGHT:
//                movshp(SHIPRT, 1);
//                break;
//            case TIKEYLEFT:
//                movshp(Characters.ShipLeft, -1 ) ;
//                break;
//            case TIKEYDOWN:
//                movshp(Characters.ShipDown, 32) ;
//                break;
//            case TIKEYUP:
//                movshp(Characters.ShipUp, -32) ;
//                break;
//            case 0:
//            {
//                int x ;
//                int y ;
//                bool fireButton ;
//                m_pTI99->checkJoy(x, y, fireButton) ;
//                if (fireButton)
//                {
//                    fire() ;
//                }
//                else if (x > 0 )
//                {
//                    movshp(SHIPRT, 1);
//                    Msg("got an x > 0") ;
//                }
//                else if (x < 0 )
//                {
//                    movshp(Characters.ShipLeft, -1);
//                    Msg("got an x < 0") ;
//                }
//                else if (y < 0)
//                {
//                    movshp(Characters.ShipUp, -32);
//                }
//                else if (y > 0 )
//                {
//                    movshp(Characters.ShipDown, 32);
//                }
//            }
//        }
//
//        return(c) ;
//    }


    void panic_key()
    {
        if (Score >= 1000)
        {   // Penalize player 1000 pts for the relocation  
            Score -= 1000 ;
        }
        else if (Score10 == 0)
        {

            Score = 0 ;
        }
        else
        {
            Score += 9000 ;
            --Score10 ;
        }
        gameBoard.displayScore(Score) ;
        gameBoard.putBlank(m_nShiploc) ;
        ++Schooners ;
        capture() ;
    }

    // -------------------------------------------------------------------------- 
//   fire() - fire bullet 
//
//	ETKEY0
// --------------------------------------------------------------------------  
    void fire()
    {
        System.out.println("fire") ;

        Characters    bullet = Characters.BulletHorizontal;
        int     bulletmoveicr =0;

        fireSound() ;      /* fire bullet sound    */
        if (Ship == Characters.ShipRight)
        {
            bullet = Characters.BulletHorizontal ;
            bulletmoveicr = 1 ;
        }
        else if (Ship == Characters.ShipUp)
        {
            bullet = Characters.BulletVertical ;
            bulletmoveicr = -32 ;
        }
        else if(Ship == Characters.ShipDown)
        {
            bullet = Characters.BulletVertical ;
            bulletmoveicr = 32 ;
        }
        else if (Ship == Characters.ShipLeft)
        {
            bullet = Characters.BulletHorizontal ;
            bulletmoveicr = -1 ;
        }

        class BulletAnimator extends AnimationTimer{

            long lastTime =0L ;
            int newloc = m_nShiploc ;
            Characters bullet ;
            final static long WaitInterval = 1000L*1000L*100L;

            int bulletmoveicr ;
            public BulletAnimator(Characters bullet, int bulletMoveIncr){
                this.bulletmoveicr = bulletMoveIncr ;
                this.bullet = bullet ;
            }

            @Override
            public void handle(long currentNanoTime) {


                if (lastTime !=0){
                    if (currentNanoTime - lastTime < WaitInterval)
                        return ;
                    gameBoard.putBlank(newloc);
                }

                newloc = newloc +bulletmoveicr ;
                int c = gameBoard.getChar(newloc) ;
                if (c == Characters.Large1.getChrIndex() || c == Characters.Large2.getChrIndex())
                { /* KILLAR   */
                    killMonster(newloc, LARGEMONTYPE) ;
                    /* Kil0 */
                }
                else if (c == Characters.Small1.getChrIndex() || c == Characters.Small2.getChrIndex())
                    killMonster(newloc, SMALLMONTYPE) ;

                else if (c == ' ' || c == Characters.SafeAreaBlank.getChrIndex())
                {  /* PUTBUL    */
                    gameBoard.writeChar(newloc, bullet) ;
                    gameBoard.refresh();
                } else
                {
                    System.out.println("stopping bullet animator");
                    gameBoard.refresh();
                    stop() ;
                }
                lastTime = currentNanoTime ;

            }
        }

        new BulletAnimator(bullet, bulletmoveicr).start() ;

        System.out.println("-- fire() end --");
    }

// --------------------------------------------------------------------------------
//
// KillMonster		
//
//  Description:	kill monster of type montype at screen location. 
//
//  Parameters:		[in ] screenloc - monster's screen location 
//					[in ] montype - monster type
//
//  Returns:
//
// --------------------------------------------------------------------------------

    void killMonster(int screenloc, int montype)
    {

//        int     *montab ;       /* monster table for monster type  */
//        int     *toptab ;       /* top of monster table      */
//        int     *newtab ;
//
//        /*   generate explosion - graphic and sound       */
//        gameBoard.WriteChar(screenloc, EXPLOD) ;
//        bigsnd() ;
//        killtime(24000) ;
//
//        if (montype == LARGEMONTYPE)
//        {   /* for large monsters display grave  (KIL1A)    */
//            gameBoard.WriteChar(screenloc, GRAVE) ;
//            montab = LMontab ;
//            toptab = LMontb ;
//        }
//        else
//        {   /* for small monsters put blank        */
//            gameBoard.PutBlank(screenloc) ;
//            montab = SMontab ;
//            toptab = SMontb ;
//        }
//
//        while (*montab++ != screenloc) ;   /* search for deceased monster  */
//        newtab = montab-1 ;           /* deceased monster table loc   */
//        while(montab !=  toptab)
//		*newtab++ = *montab++ ;
//
//        if (montype == SMALLMONTYPE)
//        {
//            --SMontb ;
//            --SMonct ;
//            Score+= SPOINT;
//        }
//        else
//        {
//            --LMontb ;
//            --LMonct ;
//            Score+= LPOINT ;
//        }
//
//        gameBoard.DisplayScore(Score) ;
//
//        if (montype == LARGEMONTYPE)
//        {
//            CheckForAdjacentGraves(screenloc) ;
//        }
//        /* ---------------------------------------------------------------- */
//        /*     See  if ship is inside safe area  and surrounded    */
//        /* ---------------------------------------------------------------- */
//
//        if (isShipInSafeArea() && isSafeAreaSurrounded())
//        {
//            if (Schooners == 0)
//                gameEnd() ;
//            gameBoard.PutBlank(m_nShiploc) ;   /* blank out ship character */
//            --Schooners;
//            gameBoard.displaySchooners(Schooners) ;
//            arbshp() ;
//        }
    }

// --------------------------------------------------------------------------------
//
// CheckForAdjacentGraves
//
//  Description:	check for three adjacent graves.  If so they become monsters. 
//
//  Parameters:		[in ] grave - check for graves around this location
//
//  Returns:		None.
//
// --------------------------------------------------------------------------------

    void checkForAdjacentGraves( int grave, boolean retry)
    {

//        int     adjgraves[3];
//        int     adjcount = 0 ;
//
//
//        adjgraves[adjcount++] = grave ;
//        for (int i=0; i<8; i++)
//        {
//            if (gameBoard.GetChar(gameBoard.Neighbor(i)+grave) == GRAVE)
//            {
//                adjgraves[adjcount++] = gameBoard.Neighbor(i)+grave ;
//                if (adjcount == 3)
//                    break ;
//            }
//        }
//
//        if (adjcount  == 3)
//        {
//            /* ---------------------------------------------------------------- */
//            /*  generate 1,2, or 3 monsters from the 3 adjacent graves for     */
//            /*  levels 1,2, and 3 respectively               */
//            /* ---------------------------------------------------------------- */
//
//            for (int n=0, i=0; i < m_nLevFlg; i++)
//            {
//                gameBoard.writeChar(adjgraves[n], LARGE1) ;
//			*LMontb++= adjgraves[n++] ;
//                ++LMonct ;
//            }
//
//            while ( n < 3) /* blank out graves for levels 1 or 2  */
//            {
//                gameBoard.PutBlank(adjgraves[n++]) ;
//            }
//            // ----------------------------------------------------------------
//            //   See if sprite and grave loc still coincide
//            // ----------------------------------------------------------------
//            if (gameBoard.GetChar(Sprloc)  != GRAVE)
//            { /* if location of sprite no longer contains a saguaro */
//                gameBoard.Video()->Locate(0, (CGameBoard::MaxRow()+1)*8, 0) ;     /* turn off sprite      */
//            }
//        }
//        else if (retry && adjcount == 2)
//        {
//            CheckForAdjacentGraves( adjgraves[1], false) ;
//        }
//
    }

    void bigsnd()
    {
//        final byte  soundlist[] =
//                {4, 0xf0, 0xe7, 0xc0, 0x03, 0x12,
//                        2, 0xc0, 0x05, 0x12,
//                        2, 0xc0, 0x07, 0x09,
//                        2, 0xc0, 0x09, 0x09,
//                        4, 0x9f, 0xbf,0xdf, 0xff,0
//                } ;
//
//        m_pTI99->Sound(soundlist) ;
    }


    void fireSound()
    {
//        static BYTE    soundlist[] =
//                { 4, 0x0f0, 0x0e7, 0x0c0, 0x03, 6,
//                        2, 0x0c1, 0x05, 3,
//                        2, 0x0c3, 0x07, 3,
//                        4, 0x09f, 0x0bf, 0x0df, 0xff, 00
//                } ;
//
//        m_pTI99->Sound(soundlist) ;
    }

// --------------------------------------------------------------------------------
//
// capture
//
//  Description:	capture ship routine - decrement #schooners and return to 
//					safe area
//
//  Parameters:
//
//  Returns:
//
// --------------------------------------------------------------------------------

    void capture()
    {
        if (Schooners == 0)
        {
            gameEnd() ;
        }
        else
        {
            --Schooners ;
            gameBoard.displaySchooners(Schooners) ;
            if (isSafeAreaSurrounded())
            {
                arbshp() ;
            }
            else
            {
                m_nShiploc = GameBoard.INITSHIPLOC;
                gameBoard.writeChar(m_nShiploc, Ship) ;
                keydep() ;
            }
        }
    }

// --------------------------------------------------------------------------------
//
// IsSafeAreaSurrounded
//
//  Description:	Check to see if all exits from safe area blocked.
//
//  Parameters:		None.
//
//  Returns:		true - surrounded
//
// --------------------------------------------------------------------------------

    boolean isSafeAreaSurrounded()
    {
        final int surm[] =
                {237, 239, 241, 307, 371, 435,
                        497, 495, 493, 427, 363, 299
                };

        for(int position : surm)
        {
            if (gameBoard.getChar(position) != Characters.Grave.getChrIndex())
            {
                return(false) ;
            }
        }
        return(true) ;
    }

// --------------------------------------------------------------------------------
//
// arbshp
//
//  Description:	routine to put ship in 1st available location outside of the safe area
//
//  Parameters:
//
//  Returns:
//
// --------------------------------------------------------------------------------

    void arbshp()
    {
        int     screen_loc ;
        boolean     found = false;

        while (!found)
        {
            while ((screen_loc = gameBoard.getChar(((randno() >> 7)+128))) != ' ') ;
            m_nShiploc = screen_loc ;

            for (int i=0; i<8; i++)
            {
                if (gameBoard.getChar(screen_loc+gameBoard.neighbor(i)) == ' ')
                {
                    found = true  ;
                    break ;
                }
            }
        }
//
        gameBoard.writeChar(m_nShiploc, Ship) ;
//        keydep() ;
    }

// --------------------------------------------------------------------------------
//
// IsShipInSafeArea
//
//  Description:	subroutine to see if ship is in safe area.
//
//  Parameters:		none.
//
//  Returns:		none.
//
// --------------------------------------------------------------------------------

    boolean isShipInSafeArea()
    {
//        int row = CGameBoard::Row(m_nShiploc) ;
//        int col = CGameBoard::Column(m_nShiploc) ;
//
//        return (row >= 8 &&  row <= 14 && col >= 12 && col <= 18) ? true : false ;
        throw new java.lang.UnsupportedOperationException();
    }



// --------------------------------------------------------------------------------
//
// CreateSprite
//
//  Description: 	subroutine to set up sprite attribute block
//
//  Parameters:
//
//  Returns:
//
// --------------------------------------------------------------------------------

    void createSprite()
    {

       // gameBoard.Video()->Sprite(0, 160, 15, 192, 0, 0, 0) ;
    }




    void killtime(int waitval)
    {

       // Sleep(waitval/500) ;
    }



// --------------------------------------------------------------------------------
//
// DispLevelMenu
//
//  Parameters:		None.
//
//  Description: 	routine to put up PreGame screen and display levels
//
//  Returns:		None.
//
// --------------------------------------------------------------------------------

    void dispLevelMenu()
    {
        ITIKeyboard.TIKeycode	key ;
        m_nLevFlg = 0 ;

        gameBoard.preGameScreen();
        gameBoard.displayLevelMenu();
        virtualTI.getKeyboard().onKeyPressed(new TIKeyboardEventListener<ITIKeyboard.TIKeyboardEvent>() {
            @Override
            public void handle(TIEmulatorEvent event) {
                handleDisplayLevelMenuSelection(event);
            }
        });
    }

    private void handleDisplayLevelMenuSelection(TIEmulatorEvent event) {
        System.out.println("handleDisplayLevelMenuSelection event="+((ITIKeyboard.TIKeyboardEvent)event).getKeyCode().toString());
        switch(((ITIKeyboard.TIKeyboardEvent)event).getKeyCode()){
            case AID:
                displayHelpMenu();
                virtualTI.getVideo().refresh();
                return ;
            case DIGIT1:
                m_nLevFlg = 1 ;
                m_nSpeed = 1 ;
                break;
            case DIGIT2:
                m_nLevFlg = 1 ;
                m_nSpeed = 2 ;
                break;
            case DIGIT3:
                m_nLevFlg = 2 ;
                m_nSpeed = 3 ;
                break;
            default:
                return ;
        }
        gameBegin();
        virtualTI.getVideo().refresh();

    }

    private void displayHelpMenu() {
        gameBoard.displayHelpMenu() ;
        virtualTI.getKeyboard().onKeyPressed(new TIKeyboardEventListener<ITIKeyboard.TIKeyboardEvent>() {
            @Override
            public void handle(TIEmulatorEvent event) {
                handleHelpMenuReturn(event);
            }
        });
    }

    private void handleHelpMenuReturn(TIEmulatorEvent event){
        System.out.println("handleHelpMenuReturn");
        dispLevelMenu();
        virtualTI.getVideo().refresh();
    }
    static int     rand16 ;

    short randno()
    {
        long   n ;

        n = 28645*rand16 +31417 ;
        rand16 = (int) n ;
        return((short) rand16) ;
    }


}
