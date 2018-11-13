package com.silvertree.tombstone;

import com.silvertree.tombstone.tiemulation.*;
import com.silvertree.tombstone.tiemulation.impl.TIKeyboard;
import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;
import java.util.logging.*;

public class TombstoneCity {

    int   	m_nLevFlg  ;
    int   	m_nSpeed  ;
    int   	Score10  ;  	/* Score (10,000)          */
    int   	Score ;       	/* Score - digits 0-9999      */
    int   	Schooners = 9 ;
    int   	m_nShiploc ;	/* current ship location      */
    int   	m_nGencur =GameBoard.PLAYAREABG  ;	/* current Generator location   */
    int   	Sprloc  ;
    int   	Sprflg  ;
    Characters		Ship ;
    int     day = 0 ;

    int		MouseFlag ;     /* mouse installed         */

    GameBoard gameBoard ;
    IVirtualTI virtualTI ;

    static final int BONUS = 1000 ;

    static final ITIKeyboard.TIKeycode PANICKEY = TIKeyboard.TIKeycode.SPACE ;
    static final TIKeyboard.TIKeycode FIREKEY = TIKeyboard.TIKeycode.Q ;
    Timeline gameLoop ;

    private final static Logger LOGGER = Logger.getLogger("InfoLogger");

    public TombstoneCity(IVirtualTI pTI99) {
        virtualTI = pTI99;
        setupLogging(); ;
        Score10 = 0;      // Score (10,000)
        Score = 0;       // Score - digits 0-9999
        Schooners = 0;
        Ship = Characters.ShipRight;            // current ship character
        m_nShiploc = GameBoard.INITSHIPLOC;     // current ship location
        m_nGencur = GameBoard.PLAYAREABG;      // current Generator location
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

    private void setupLogging(){
        try {
            FileHandler fileHandler = new FileHandler("tombstonecity.log");
            Formatter formatter = new Formatter() {
                @Override
                public String format(LogRecord record) {
                    StringBuffer buffer = new StringBuffer();
                    buffer.append(record.getMillis());
                    buffer.append(",");
                    buffer.append(record.getLoggerName());
                    buffer.append(",");
                    buffer.append(record.getLevel().getName());
                    buffer.append(",[");
                    buffer.append(Thread.currentThread().getName());
                    buffer.append("],");
                    buffer.append(record.getMessage());
                    buffer.append("\r\n") ;
                    return buffer.toString();

                }
            };

            fileHandler.setFormatter(formatter);
            LOGGER.addHandler(fileHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void start(){
        dispLevelMenu();


    }

    private void handleGamePlayKeys(ITIKeyboard.TIKeyboardEvent event) {
        LOGGER.info("handleGamePlayKeys "+event.getKeyCode());
        switch(event.getKeyCode()){
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

        }

    }

    /**
     *
     * @param day
     * @return
     */
    public boolean playDay(int day)
    {
        LOGGER.info("playDay "+day);

        boolean bEndGame = false ;
        boolean bDoneWithDay = false ;

        LargeMonster.createMontab();
        SmallMonster.createMontab() ;

        gameBoard.preGameScreen() ;
        gameBoard.displayDay(day);
        Score += BONUS ;
        gameBoard.displayScore(Score);
        gameBoard.displaySchooners(Schooners)  ;
        gameBoard.safeAreaBlueOnBlue();
        gameBoard.draw(day) ;
        createSprite();
        if (SmallMonster.isEmpty())
            genSmallMonsters();

       return( !bEndGame );

    }
    void gameBegin(){

        LOGGER.entering(this.getClass().getName(), "gameBegin");
        LOGGER.info("gameBegin");
        virtualTI.getKeyboard().onKeyPressed(new TIKeyboardEventListener<ITIKeyboard.TIKeyboardEvent>() {
            @Override
            public void handle(TIEmulatorEvent event) {
                handleGamePlayKeys((ITIKeyboard.TIKeyboardEvent) event);
            }
        });

        // -------- Start a new day --------------
        day = 0;
        playDay(++day);
        createGameloop() ;
        LOGGER.exiting(this.getClass().getName(), "gameBegin");

    }

    int loopCount = 0 ;
    private void doGameLoop(){
        LOGGER.info("gameLoop begin");
        if (checkForNewDay()){
            playDay(++day);
            return ;
        }
        if (timeToReleaseMoreMonsters()){
            if (generateLargeMonsters() && SmallMonster.isEmpty()) {
                LOGGER.info("generating more small monsters in gameLoop");
                genSmallMonsters();
            }
        }
        moveSmallMonsters();
        moveLargeMonsters();
        verifyMonsterTables() ;
        LOGGER.info("gameLoop end");
    }

    private void verifyMonsterTables() {
        List<Monster> smallMonsters = SmallMonster.getMonsters();
        smallMonsters.stream().forEach(m -> {int current = m.getCurLocation();
        if (gameBoard.getCharacter(current) != m.getCharacter(1) && gameBoard.getCharacter(current) != m.getCharacter(2))
            System.err.println("Monster missing:"+m.toString());
        });
    }

    private boolean timeToReleaseMoreMonsters() {
        return false ;
    }

    private boolean checkForNewDay() {
        if  (LargeMonster.isEmpty()   && SmallMonster.getMonsters().size() < 10){
            if (generateLargeMonsters()){
                if (SmallMonster.isEmpty())
                    genSmallMonsters();
                return false ;
            } else {
                return true ;
            }

        }
        return false ;
    }

    private void createGameloop() {
        gameLoop = new Timeline(new KeyFrame(Duration.millis(500),  new EventHandler<ActionEvent>() {

            @Override
            public void handle(javafx.event.ActionEvent event) {

                doGameLoop() ;
            }
        }));
        KeyFrame genLargeMonstersFrame = new KeyFrame(Duration.seconds(4), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                generateLargeMonsters();
            }
        });
        gameLoop.getKeyFrames().add(genLargeMonstersFrame);
        gameLoop.setCycleCount(Animation.INDEFINITE);
        gameLoop.play() ;

    }

    void gameEnd()
    {

//        createSprite() ;
        virtualTI.getVideo().displayAt(11,4, "PRESS REDO OR BACK") ;
        gameLoop.stop();
//        while(m_pTI99->getKey() != TIKEYREDO) ;
    }

    void themeSong()
    {
    }







    /**
     * generate max small monsters
     */
    void genSmallMonsters()
    {
        LOGGER.info("genSmallMonsters");
        for (int i=0; i< SmallMonster.MAXSMALLMONSTERCOUNT; i++)
        {
            int  screen_loc ;
            Characters  c ;

            do
            {
                screen_loc = gameBoard.randomPlayAreaLocation() ;
            } while (gameBoard.getCharacter(screen_loc) !=  Characters.Blank) ;

            int r  = gameBoard.getRandom(0, Short.MAX_VALUE) ;
            if (r > (r &0xff))
                c = Characters.Small1 ;
            else
                c = Characters.Small2;

            gameBoard.writeChar(screen_loc, c) ;
            new SmallMonster(screen_loc).addToMontab();
        }
   }


    /**
     *  last location checked
     */
    private Generator lastGenerator ;
    /**
     * screen location to release new monster
     */
    private int     Genrel ;

    /**
     *  check for generators and add monster to monster table.
     * @return  true - generator found
     */
    boolean generateLargeMonsters()
    {
        LOGGER.info("genarateLargeMonsters");

        Generator generator = findGenerator() ;
        if (generator != null){
            releaseMonster(generator);
        }
        return(generator != null) ;
    }

    /**
     *
     * @return next generator found, null if none.
     */
    Generator findGenerator() {
        boolean generatorFound = false;    //  flag set if generator encountered.
        boolean releasePointFound = false; //  flag set if able to release monster

        gameBoard.safeAreaBlueOnLightBlue();     // safe area blue on light blue
        int Genlas = m_nGencur;

        while (++m_nGencur != Genlas && !(generatorFound && releasePointFound)) {
            if (m_nGencur >= GameBoard.PLAYAREAEND) {
                m_nGencur = GameBoard.PLAYAREABG;
                ++Genlas;
                if (Genlas > GameBoard.PLAYAREAEND)
                    Genlas = GameBoard.PLAYAREAEND;
            }

            Characters c = gameBoard.getCharacter(m_nGencur);
            if (c == Characters.Grave) {
                /* ------------------------------------------------------------ */
                /* found a Saguaro -> if another one adjacent then we have      */
                /* a generator               									*/
                /* ------------------------------------------------------------ */
                for (int i = 0; i < 8; i++) {
                    c = gameBoard.getCharacter(m_nGencur + gameBoard.neighbor(i));
                    if (c == Characters.Grave)
                        generatorFound = true;    /* found a generator         */
                    else if (c == Characters.Blank) {       /* found Monster release position   */
                        releasePointFound = true;
                        Genrel = m_nGencur + gameBoard.neighbor(i);
                    }

                }

                /*   Did we find a generator yet ?              */
                if (generatorFound && releasePointFound) {
                    return new Generator(m_nGencur, Genrel);
                }
            }

        }
        return null;
    }

    void releaseMonster(Generator generator)
    {

        // put up sprite at generator 
        Sprloc = generator.getGeneratorLocation() ;
        int y = GameBoard.row(generator.getGeneratorLocation())*8;
        int x =  GameBoard.column(generator.getGeneratorLocation() )*8;


        // release Large monster
        if (!LargeMonster.isFull())
        {
            virtualTI.getVideo().locateSprite(0, y, x) ;


            new AnimationTimer(){
                long start = 0 ;
                @Override
                public void handle(long now) {
                    if (start == 0) start = now ;

                    if (Duration.millis((now - start)/(1000*1000)).greaterThanOrEqualTo(Duration.millis(1500.0))){
                        gameBoard.writeChar(generator.getReleasePoint(), Characters.Large2) ;
                        ++m_nGencur ;
                        Monster monster = new LargeMonster(generator.getReleasePoint());
                        monster.addToMontab();
                        LOGGER.info("releaseMonster at "+ monster.getCurLocation()+ " from generator at "+Sprloc);
                        gameBoard.safeAreaBlueOnBlue() ;
                        this.stop() ;

                    }
                }
            }.start() ;
        }
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
        LOGGER.info("movshp("+shipCharacter+","+offset+")");
        int     newshiploc ;

        if (shipCharacter != Ship)	/* check to see if orientation changes	    */
        {
            gameBoard.writeChar(m_nShiploc, shipCharacter) ;
            Ship = shipCharacter ;
            offset = 0 ;
        }

        if (LargeMonster.isEmpty())
        {
            /* kill time of same depressed	    */
        }
        newshiploc = offset + m_nShiploc ;

        Characters currentChr = gameBoard.getCharacter(newshiploc);
        if ((currentChr !=Characters.Blank && currentChr !=  Characters.SafeAreaBlank)|| currentChr ==  Ship)
            return( false ) ;
        gameBoard.writeChar(newshiploc, Ship) ;
        gameBoard.putBlank(m_nShiploc) ;
        m_nShiploc = newshiploc ;
        return( false ) ;
    }


    /**
     *  move Small monsters away from spaceship
     */
    void moveSmallMonsters()
    {
        LOGGER.info("move small monsters");
        int     newmonloc =0;

        if (!SmallMonster.isEmpty())
        {

            int shiprow = GameBoard.row(m_nShiploc) ;
            int shipcol = GameBoard.column(m_nShiploc );
            for(Monster monster : SmallMonster.getMonsters())
            {
                int monsterPosition = monster.getCurLocation();
                int monrow = GameBoard.row(monsterPosition) ;
                int moncol = GameBoard.column(monsterPosition);
                if (shiprow == monrow)
                { /* on same row */
                    newmonloc =monsterPosition;
                    if (shipcol > moncol)
                        --newmonloc ;
                    else
                        ++newmonloc ;

                    Characters c = gameBoard.getCharacter(newmonloc);
                    if (c == Characters.Blank)
                    {
                        monster.setCurLocation(newmonloc);
                        monblk(monsterPosition, newmonloc) ;
                        LOGGER.info("moveSmallMonster from "+monsterPosition+" to "+newmonloc);
                        continue ;
                    }
                }
                if (shipcol == moncol)
                { /* on same column MONCOL  */
                    newmonloc = monsterPosition ;
                    if (shiprow > monrow )
                        newmonloc-=GameBoard.NUMBERCOLUMNS ;
                    else
                        newmonloc+=GameBoard.NUMBERCOLUMNS ;

                    Characters c = gameBoard.getCharacter(newmonloc);
                    if (c == Characters.Blank)
                    {
                        monster.setCurLocation(newmonloc);
                        monblk(monsterPosition, newmonloc);
                        LOGGER.info("moveSmallMonster from "+monsterPosition+" to "+newmonloc);
                        continue ;
                    }

                }
            }
        }
        LOGGER.info("-- moveSmallMonsters end --");
    }


    /**
     *
     */
    void moveLargeMonsters()
    {
        LOGGER.info("moveLargeMonsters");
        int     newmonloc ;
        int     moveflg = 0;

        if (!LargeMonster.isEmpty() )
        {

            int shiprow = GameBoard.row(m_nShiploc) ;
            int shipcol = GameBoard.column(m_nShiploc );

            for (Monster monster : LargeMonster.getMonsters())
            {
                int monrow = GameBoard.row(monster.getCurLocation());
                int moncol = GameBoard.column(monster.getCurLocation()) ;
                if (shiprow == monrow)
                { /* on same row */
                    newmonloc = monster.getCurLocation();

                    if (moncol> shipcol)
                        --newmonloc ;
                    else
                        ++newmonloc ;

                    if (tryMove(monster, newmonloc))
                        continue ;
                }

                if ( shipcol == moncol)
                { /* on same column MONCOL  */
                    newmonloc = monster.getCurLocation() ;
                    if (shiprow > monrow)
                        newmonloc+=32 ;
                    else
                        newmonloc-=32 ;

                    if (tryMove(monster, newmonloc))
                        continue ;

                }

                if ( gameBoard.getRandom(0, 1) != 0)
                { /* try column  */
                    newmonloc = monster.getCurLocation();

                    if (moncol> shipcol)
                        --newmonloc ;
                    else
                        ++newmonloc ;

                    if(tryMove(monster, newmonloc))
                        continue ;
                }
                newmonloc = monster.getCurLocation() ;
                if (shiprow > monrow)
                    newmonloc+=32 ;
                else
                    newmonloc-=32 ;

                tryMove(monster, newmonloc);

            }
        }
        LOGGER.info("-- moveLargeMonsters end --");
    }

    /**
     *
     * @param monster
     * @param newmonloc
     * @return
     */
    boolean tryMove ( Monster monster, int newmonloc)
    {
        LOGGER.info("tryMove("+monster.toString()+", "+newmonloc+")");
        boolean moveFound = false ;
        Characters c = gameBoard.getCharacter(newmonloc);
        if (c == Characters.Blank)
        {
            LOGGER.info("move "+monster.toString()+" to "+newmonloc);
            monblk(monster.getCurLocation(), newmonloc) ;
            monster.setCurLocation(newmonloc);
            moveFound =  true  ;

        }
        else if (c == Ship)
        {
            moveFound = captureShip(monster, newmonloc) ;
        }
        return( moveFound ) ;
    }

    void monblk(int curloc, int newloc)
    {
        Characters monchar = gameBoard.getCharacter(curloc) ;

        Characters newChar = Characters.Small1;

        if (monchar == Characters.Large1)
            newChar = Characters.Large2 ;
        else if (monchar == Characters.Large2)
            newChar = Characters.Large1 ;
        else if (monchar == Characters.Small1)
            newChar = Characters.Small2 ;
        else if (monchar == Characters.Small2)
            newChar = Characters.Small1 ;
        else {
            LOGGER.info("monblk " + monchar.name() + " set to " + newChar.name());
            return ;
        }
        gameBoard.writeChar(newloc, newChar) ;
        gameBoard.putBlank(curloc) ;
    }

    /**
     *
     * @param monster monster capturing ship
     * @param newmonloc
     * @return true if captured
     *         false ship is in safe area
     */
    boolean captureShip(Monster monster, int newmonloc)
    {
        LOGGER.info("captureShip("+monster.toString()+", "+newmonloc+")");
        boolean captured = false ;
        if (!isShipInSafeArea())
        {
            monblk(monster.getCurLocation(), newmonloc) ;
            monster.setCurLocation(newmonloc);
            gulp() ;
            capture() ;
            captured = true  ;
        }
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


    /**
     * fire bullet
     */
    void fire()
    {
        LOGGER.info("fire") ;

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
            final static long WaitInterval = 1000L*1000L*10L;

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
                Characters c = gameBoard.getCharacter(newloc) ;
                if (c == Characters.Large1 || c == Characters.Large2)
                { /* KILLAR   */
                    killMonster(new LargeMonster(newloc)) ;
                    stop() ;
                    /* Kil0 */
                }
                else if (c == Characters.Small1 || c == Characters.Small2) {
                    killMonster(new SmallMonster(newloc));
                    stop() ;
                }
                else if (c ==  Characters.Blank || c == Characters.SafeAreaBlank)
                {  /* PUTBUL    */
                    gameBoard.writeChar(newloc, bullet) ;
                } else
                {
                    LOGGER.info("stopping bullet animator");
                    stop() ;
                }
                lastTime = currentNanoTime ;

            }
        }

        new BulletAnimator(bullet, bulletmoveicr).start() ;

        LOGGER.info("-- fire() end --");
    }


    void killMonster( Monster monster)
    {
        LOGGER.entering(this.getClass().getName(), "killMonster");
        LOGGER.info("killMonster "+monster.toString());

        /*   generate explosion - graphic and sound       */
//        bigsnd() ;
//        killtime(24000) ;

        Characters replacementChar = monster.replaceCharacter();
        gameBoard.writeChar(monster.getCurLocation(), replacementChar);

        new AnimationTimer(){

            long last = 0 ;

            @Override
            public void handle(long now) {
                if (last == 0) {
                    last = now;
                    gameBoard.writeChar(monster.getCurLocation(), Characters.Explode) ;

                }
                else if (now - last > 1000L*1000L*240){
                    gameBoard.writeChar(monster.getCurLocation(), monster.replaceCharacter());
                    stop();
                }
            }
        }.start() ;

        if (monster.removeFromMontab())
        {
            Score += monster.getPointValue() ;
        }
        gameBoard.displayScore(Score) ;

        if (monster instanceof LargeMonster)
        {
            checkForAdjacentGraves(monster.getCurLocation(), false) ;
        }
        /* ---------------------------------------------------------------- */
        /*     See  if ship is inside safe area  and surrounded    */
        /* ---------------------------------------------------------------- */

        if (isShipInSafeArea() && gameBoard.isSafeAreaSurrounded())
        {
            if (Schooners == 0)
                gameEnd() ;
            gameBoard.putBlank(m_nShiploc) ;   /* blank out ship character */
            --Schooners;
            gameBoard.displaySchooners(Schooners) ;
            arbshp() ;
        }
        LOGGER.exiting(this.getClass().getName(), "killMonster");
    }

    /**
     *
     * check for three adjacent graves.  If so they become monsters.
     * @param grave check for graves around this location
     * @param retry
     */
    void checkForAdjacentGraves( int grave, boolean retry)
    {

        int    adjgraves[] = new int[3];
        int     adjcount = 0 ;


        adjgraves[adjcount++] = grave ;
        for (int i=0; i<8; i++)
        {
            if (gameBoard.getCharacter(gameBoard.neighbor(i)+grave) == Characters.Grave)
            {
                adjgraves[adjcount++] = gameBoard.neighbor(i)+grave ;
                if (adjcount == 3)
                    break ;
            }
        }

        if (adjcount  == 3)
        {
            /* ---------------------------------------------------------------- */
            /*  generate 1,2, or 3 monsters from the 3 adjacent graves for     */
            /*  levels 1,2, and 3 respectively               */
            /* ---------------------------------------------------------------- */

            for (int n=0, i=0; i < m_nLevFlg; i++)
            {
                gameBoard.writeChar(adjgraves[n], Characters.Large1) ;
                new LargeMonster(adjgraves[n++]).addToMontab();
            }

            if ( m_nLevFlg < 3) /* blank out graves for levels 1 or 2  */
            {
                for (int i=0 ; i < m_nLevFlg; i++)
                    gameBoard.putBlank(adjgraves[i]) ;
            }
            // ----------------------------------------------------------------
            //   See if sprite and grave loc still coincide
            // ----------------------------------------------------------------
            if (gameBoard.getCharacter(Sprloc)  != Characters.Grave)
            { /* if location of sprite no longer contains a saguaro */
                virtualTI.getVideo().locateSprite(0,(GameBoard.MAXROW+1)*8, 0) ;     /* turn off sprite      */
            }
        }
        else if (retry && adjcount == 2)
        {
            checkForAdjacentGraves( adjgraves[1], false) ;
        }

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

    /**
     * capture ship routine - decrement #schooners and return to safe area
     */
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
            if (gameBoard.isSafeAreaSurrounded())
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


    boolean isShipInSafeArea(){
        return gameBoard.isInSafeArea(m_nShiploc);
    }

    /**
     * put ship in 1st available location outside of the safe area
     */
    void arbshp()
    {
        LOGGER.info("arbshp()");
        int     screen_loc ;
        boolean     found = false;

        while (!found)
        {
            screen_loc = gameBoard.getRandom(GameBoard.PLAYAREABG, GameBoard.PLAYAREAEND);
            m_nShiploc = screen_loc ;

            for (int i=0; i<8; i++)
            {
                if (gameBoard.getCharacter(screen_loc+gameBoard.neighbor(i)) == Characters.Blank)
                {
                    found = true  ;
                    break ;
                }
            }
        }

        gameBoard.writeChar(m_nShiploc, Ship) ;
    }


    /**
     * create sprite used to highlight cactus
     */
    void createSprite()
    {
        final byte     sprite[] ={(byte) 0xf7, (byte)0xd5, (byte) 0xc5, (byte) 0xf1, (byte) 0xf7, (byte) 0xf7, (byte) 0x81,0	};
        virtualTI.getVideo().setChar(160, sprite);
        virtualTI.getVideo().sprite(0, 160, 15, 192, 0, 0, 0) ;
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
        LOGGER.info("handleDisplayLevelMenuSelection event="+((ITIKeyboard.TIKeyboardEvent)event).getKeyCode().toString());
        switch(((ITIKeyboard.TIKeyboardEvent)event).getKeyCode()){
            case AID:
                displayHelpMenu();
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

    private void handleHelpMenuReturn(TIEmulatorEvent event) {
        LOGGER.info("handleHelpMenuReturn");
        dispLevelMenu();
    }
}
