package com.silvertree.tombstone;

import com.silvertree.tombstone.tiemulation.*;
import com.silvertree.tombstone.tiemulation.impl.TIKeyboard;
import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.*;

/**
 * Implements the Game Tombstone City 21st century as originally
 * implemented on the TI994a.  Logic based on original assembler
 * source provided by TI.  Re-implemented JavaFX.  Java implementation
 * created from a C/C++ port done many years ago.
 */
public class TombstoneCity {

    int   	m_nLevFlg  ;
    int     timeFlg = 0 ;
    int   	m_nSpeed  ;
    int   	Score10  ;  	/* Score (10,000)          */
    int   	Score ;       	/* Score - digits 0-9999      */
    int   	Schooners = 9 ;
    int   	m_nGencur =GameBoard.PLAYAREABG  ;	// current Generator location
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
    private static final long NS_PER_MS = 1000L * 1000L;

    private final static Logger LOGGER = GameLogging.setup();
    private List<AnimationTimer> activeAnimators = new ArrayList<>() ;

    /**
     * create the game and attach to a display/input controller.
     * @param pTI99 TI99 emulator for screen and keyboard management.
     */
    public TombstoneCity(IVirtualTI pTI99) {
        virtualTI = pTI99;
        Score10 = 0;      // Score (10,000)
        Score = 0;       // Score - digits 0-9999
        Schooners = 0;
        Ship = Characters.ShipRight;            // current ship character
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

    /**
     * start the game flow by displaying the initial screen (the level menu).
     */
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
            case REDO:
                gameEnd() ;
                break;
            case BEGIN:
                startNextDay();  // backdoor for testing
                break;


        }

    }

    /**
     * start play for a day.
     * @param day
     * @return true game continues
     *         false end of game.
     */
    public boolean playDay(int day)
    {
        GameLogging.debug("playDay "+day);
        if (!activeAnimators.isEmpty()){
            activeAnimators.stream().forEach(animator->animator.stop());
            activeAnimators.clear();
        }
        boolean bEndGame = false ;

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
        timeFlg = 0 ;

       return( !bEndGame );

    }

    /**
     * begin the actual game play.  Creates the gameloop which drives background logic and
     * attaches to a keyboard event handler to process game play key events.
     */
    void gameBegin(){

        LOGGER.entering(this.getClass().getName(), "gameBegin");
        GameLogging.debug("gameBegin");

        // attach to the keyboard event handler
        virtualTI.getKeyboard().onKeyPressed(new TIKeyboardEventListener<ITIKeyboard.TIKeyboardEvent>() {
            @Override
            public void handle(TIEmulatorEvent event) {
                handleGamePlayKeys((ITIKeyboard.TIKeyboardEvent) event);
            }
        });

        // -------- Start a new day --------------
        day = 0;
        Score = 0 ;

        if (playDay(++day))
            createGameloop() ;
        LOGGER.exiting(this.getClass().getName(), "gameBegin");

    }

    /**
     * basic game loop called on each pulse.
     */
    private void doGameLoop(){
        GameLogging.debug("gameLoop begin tickcount=");

        if (checkForNewDay()){
            startNextDay();
            return ;
        }
        if (timeToReleaseMoreMonsters()){
            if (generateLargeMonsters() && SmallMonster.isEmpty()) {
                GameLogging.debug("generating more small monsters in gameLoop");
                genSmallMonsters();
            }
        }
        // move the monsters
        moveSmallMonsters();
        moveLargeMonsters();
        verifyMonsterTables() ;
        GameLogging.debug("gameLoop end");
    }

    private void startNextDay(){
        gameLoop.pause();
        playDay(++day);
    }

    private void verifyMonsterTables() {
        Consumer<Monster> x = (m -> {int current = m.getCurLocation();
            if (gameBoard.getCharacter(current) != m.getCharacter(1) && gameBoard.getCharacter(current) != m.getCharacter(2)) {
                GameLogging.error("Monster missing:" + m.toString());
                m.removeFromMontab();
            }
        });

        List<Monster> smallMonsters = SmallMonster.getMonsters();
        smallMonsters.stream().forEach(x);

        List<Monster> largeMonsters = LargeMonster.getMonsters();
        largeMonsters.stream().forEach(x);

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

    private final static int GAMELOOP_PULSE = 500 ;         // gameloop pulse in ms
    private final static int MONSTER_RELEASE_CYCLE = (4*1000)/GAMELOOP_PULSE ;

    private void createGameloop() {
        gameLoop = new Timeline(new KeyFrame(Duration.millis(GAMELOOP_PULSE),  new EventHandler<ActionEvent>() {
            int cycleCount = 0 ;  // manage game loop cycles for releasing monsters
            @Override
            public void handle(javafx.event.ActionEvent event) {
                doGameLoop() ;
                if (++cycleCount == MONSTER_RELEASE_CYCLE){
                    cycleCount = 0 ;
                    if (++timeFlg == m_nLevFlg) {
                        timeFlg = 0 ;
                        generateLargeMonsters();
                    }

                }
            }
        }));
        gameLoop.setCycleCount(Animation.INDEFINITE);
        gameLoop.play() ;

    }
    AnimationTimer startAnimationTimer(AnimationTimer animationTimer){
        activeAnimators.add(animationTimer);
        animationTimer.start() ;
        return animationTimer ;
    }
    void stopAnimationTimer(AnimationTimer animationTimer){
        animationTimer.stop() ;
        activeAnimators.remove(animationTimer);
    }
    /**
     * process game end, either game initiated or use initiated.
     * Stops the game loop and displays a restart or exit message.
     */
    void gameEnd()
    {
        gameBoard.displayAt(11, 4,"PRESS REDO OR BACK");
        gameLoop.stop();
        deleteSprite();
        virtualTI.getKeyboard().onKeyPressed(new TIKeyboardEventListener<ITIKeyboard.TIKeyboardEvent>() {
            @Override
            public void handle(TIEmulatorEvent event) {
                handleGameEndKeys((ITIKeyboard.TIKeyboardEvent) event);
            }
        });
    }

    private void handleGameEndKeys(ITIKeyboard.TIKeyboardEvent event) {
        if (event.getKeyCode() == ITIKeyboard.TIKeycode.REDO){
            gameBegin();
        } else if (event.getKeyCode() == ITIKeyboard.TIKeycode.BACK){
            start() ;
        }
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
        Generator generator = findGenerator() ;
        if (generator != null){
            releaseMonster(generator);
        }
        LOGGER.info("generateLargeMonsters "+LargeMonster.getMonsters().size() +" monsters");
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


            startAnimationTimer( new AnimationTimer(){
                long start = 0 ;
                @Override
                public void handle(long now) {
                    if (start == 0) start = now ;

                    if (Duration.millis((now - start)/(NS_PER_MS)).greaterThanOrEqualTo(Duration.millis(1500.0))){
                        gameBoard.writeChar(generator.getReleasePoint(), Characters.Large2) ;
                        ++m_nGencur ;
                        Monster monster = new LargeMonster(generator.getReleasePoint());
                        monster.addToMontab();
                        LOGGER.info("releaseMonster at "+ monster.getCurLocation()+ " from generator at "+Sprloc+" "+monster.toString());
                        gameBoard.safeAreaBlueOnBlue() ;
                        stopAnimationTimer(this);
                    }
                }
            } );
        }
    }

    /**
     * move ship
     * @param shipCharacter
     * @param offset
     * @return
     */
    boolean movshp(Characters shipCharacter, int offset)
    {
        LOGGER.info("movshp("+shipCharacter+","+offset+")");
        if (gameLoop.getStatus() == Animation.Status.PAUSED)  // if we are tranistioning days, restart the game loop.
            gameLoop.playFromStart();

        if (shipCharacter != Ship)	/* check to see if orientation changes	    */
        {
            gameBoard.writeChar(gameBoard.getCurrentShipLocation(), shipCharacter) ;
            Ship = shipCharacter ;
            offset = 0 ;
        }

        if (LargeMonster.isEmpty())
        {
            /* kill time of same depressed	    */
        }
        int newshiploc = offset + gameBoard.getCurrentShipLocation() ;

        Characters currentChr = gameBoard.getCharacter(newshiploc);
        if ((currentChr !=Characters.Blank && currentChr !=  Characters.SafeAreaBlank)|| currentChr ==  Ship)
            return( false ) ;
        gameBoard.moveShip(newshiploc, Ship);
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

            int shiprow = GameBoard.row(gameBoard.getCurrentShipLocation()) ;
            int shipcol = GameBoard.column(gameBoard.getCurrentShipLocation() );
            for(Monster monster : SmallMonster.getMonsters())
            {
                int monsterPosition = monster.getCurLocation();
                int monrow = GameBoard.row(monsterPosition) ;
                int moncol = GameBoard.column(monsterPosition);
                if (shiprow == monrow)
                { /* on same row */
                    newmonloc =monster.getCurLocation();
                    if (shipcol > moncol)
                        --newmonloc ;
                    else
                        ++newmonloc ;

                    Characters c = gameBoard.getCharacter(newmonloc);
                    if (c == Characters.Blank)
                    {
                        monblk(monster, newmonloc) ;
                        monster.setCurLocation(newmonloc);
                        LOGGER.info("moveSmallMonster from "+monster.getCurLocation()+" to "+newmonloc);
                        continue ;
                    }
                }
                if (shipcol == moncol)
                { /* on same column MONCOL  */
                    newmonloc = monster.getCurLocation() ;
                    if (shiprow > monrow )
                        newmonloc-=GameBoard.NUMBERCOLUMNS ;
                    else
                        newmonloc+=GameBoard.NUMBERCOLUMNS ;

                    Characters c = gameBoard.getCharacter(newmonloc);
                    if (c == Characters.Blank)
                    {
                        monblk(monster,newmonloc) ;
                        monster.setCurLocation(newmonloc);
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
        GameLogging.debug("moveLargeMonsters");
        int     newmonloc ;

        if (!LargeMonster.isEmpty() )
        {

            int shiprow = GameBoard.row(gameBoard.getCurrentShipLocation()) ;
            int shipcol = GameBoard.column(gameBoard.getCurrentShipLocation() );

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
        GameLogging.debug("-- moveLargeMonsters end --");
    }

    /**
     *
     * @param monster
     * @param newmonloc
     * @return
     */
    boolean tryMove ( Monster monster, int newmonloc)
    {
        GameLogging.debug("tryMove("+monster.toString()+", "+newmonloc+")");
        boolean moveFound = false ;
        Characters c = gameBoard.getCharacter(newmonloc);
        if (c == Characters.Blank)
        {
            GameLogging.debug("move "+monster.toString()+" to "+newmonloc);
            monblk(monster, newmonloc) ;
            monster.setCurLocation(newmonloc);
            moveFound =  true  ;

        }
        else if (c == Ship)
        {
            moveFound = captureShip(monster, newmonloc) ;
        }
        return( moveFound ) ;
    }

    void monblk(Monster monster, int newloc)
    {
        Characters monchar = gameBoard.getCharacter(monster.getCurLocation()) ;

        Characters newChar = monster.nextFrameCharacter(monchar);

        if (newChar == null){
            GameLogging.error("monblk " + monchar.name() + " invalid character for "+monster.toString()+ "->"+monchar);
            return ;
        }
        gameBoard.writeChar(newloc, newChar) ;
        gameBoard.putBlank(monster.getCurLocation()) ;
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
        if (!gameBoard.isShipInSafeArea())
        {
            monblk(monster, newmonloc) ;
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
        gameBoard.putBlank(gameBoard.getCurrentShipLocation()) ;
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
            int newloc = gameBoard.getCurrentShipLocation() ;
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
                    stopAnimationTimer(this) ;
                    /* Kil0 */
                }
                else if (c == Characters.Small1 || c == Characters.Small2) {
                    killMonster(new SmallMonster(newloc));
                    stopAnimationTimer(this) ;
                }
                else if (c ==  Characters.Blank || c == Characters.SafeAreaBlank)
                {  /* PUTBUL    */
                    gameBoard.writeChar(newloc, bullet) ;
                } else
                {
                    LOGGER.info("stopping bullet animator");
                    stopAnimationTimer(this);
                }
                lastTime = currentNanoTime ;

            }
        }

        startAnimationTimer(new BulletAnimator(bullet, bulletmoveicr)) ;

        LOGGER.info("-- fire() end --");
    }



    void killMonster( Monster monster)
    {
        GameLogging.getLogger().entering(this.getClass().getName(), "killMonster");
        GameLogging.debug("killMonster "+monster.toString());

        /*   generate explosion - graphic and sound       */
//        bigsnd() ;
//        killtime(24000) ;

        if (monster.removeFromMontab())
        {
            GameLogging.debug("monster "+monster.toString()+" removed from table");
            Score += monster.getPointValue() ;
        }
        gameBoard.displayScore(Score) ;
        GameLogging.debug("creating Animation timer");
        startAnimationTimer(new AnimationTimer(){

            long last = 0 ;

            @Override
            public void handle(long now) {

                if (last == 0) {
                    GameLogging.debug("animation timer first run");
                    last = now;
                    gameBoard.writeChar(monster.getCurLocation(), Characters.Explode) ;

                }
                else if (now - last > NS_PER_MS * 500){
                    GameLogging.debug("animation timer last run");
                    gameBoard.writeChar(monster.getCurLocation(), monster.replaceCharacter());
                    if (monster instanceof LargeMonster)
                    {
                        checkForAdjacentGraves(monster.getCurLocation()) ;
                    }
                    /* ---------------------------------------------------------------- */
                    /*     See  if ship is inside safe area  and surrounded    */
                    /* ---------------------------------------------------------------- */

                    if (gameBoard.isShipInSafeArea() && gameBoard.isSafeAreaSurrounded())
                    {
                        if (Schooners == 0) {
                            gameEnd();
                        }
                        gameBoard.putBlank(gameBoard.getCurrentShipLocation()) ;   /* blank out ship character */
                        --Schooners;
                        gameBoard.displaySchooners(Schooners) ;
                        arbshp() ;
                    }

                    stopAnimationTimer(this);

                }
            }
        });


        GameLogging.debug("after starting animation timer");
        GameLogging.getLogger().exiting(this.getClass().getName(), "killMonster");
    }

    /**
     *
     * check for three adjacent graves.  If so they become monsters.
     * @param grave check for graves around this location
     */
    void checkForAdjacentGraves(int grave){
        GameLogging.debug("checkForAdjacentGraves "+grave);
        List<Integer> adjacentGraves = new ArrayList<>();
        adjacentGraves.add(grave);
        checkForAdjacentGraves(adjacentGraves, grave);
        if (adjacentGraves.size() == 2 )
            checkForAdjacentGraves(adjacentGraves, adjacentGraves.get(1));

        if (adjacentGraves.size()  == 3)
        {
            /* ---------------------------------------------------------------- */
            /*  generate 1,2, or 3 monsters from the 3 adjacent graves for     */
            /*  levels 1,2, and 3 respectively               */
            /* ---------------------------------------------------------------- */
            GameLogging.debug("found 3 adjacent graves for "+grave);
//            for (int n=0, i=0; i < m_nLevFlg; i++)
//            {
//                gameBoard.writeChar(adjgraves[n], Characters.Large1) ;
//                new LargeMonster(adjgraves[n++]).addToMontab();
//                GameLogging.debug("released extra monster at "+)
//            }

            if ( m_nLevFlg < 3) /* blank out graves for levels 1 or 2  */
            {
                adjacentGraves.stream().forEach(pos -> {if (pos != grave) {gameBoard.putBlank(pos);
                                                                            GameLogging.debug(" writing blank to "+pos);
                }});

            }
            // ----------------------------------------------------------------
            //   See if sprite and grave loc still coincide
            // ----------------------------------------------------------------
            if (gameBoard.getCharacter(Sprloc)  != Characters.Grave)
            { /* if location of sprite no longer contains a saguaro */
                virtualTI.getVideo().locateSprite(0,(GameBoard.MAXROW+1)*8, 0) ;     /* turn off sprite      */
            }
        }

    }

    void checkForAdjacentGraves(List<Integer> adjacentGraves, int grave) {
        for (int i = 0; i < 8; i++) {
            int nextLocation = gameBoard.neighbor(i) + grave;
            if (gameBoard.getCharacter(nextLocation) == Characters.Grave) {
                if (!adjacentGraves.contains(nextLocation)) {
                    adjacentGraves.add(nextLocation);
                }
                if (adjacentGraves.size() == 3)
                    break;
            }
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
                gameBoard.setCurrentShipLocation(GameBoard.INITSHIPLOC);
                gameBoard.writeChar(gameBoard.getCurrentShipLocation(), Ship) ;
                GameLogging.debug("ship captured and returned to "+gameBoard.getCurrentShipLocation());
            }
        }
    }



    /**
     * put ship in 1st available location outside of the safe area
     */
    void arbshp()
    {
        GameLogging.debug("arbshp()");
        int     screen_loc = GameBoard.INITSHIPLOC;
        boolean     found = false;

        while (!found)
        {
            screen_loc = gameBoard.getRandom(GameBoard.PLAYAREABG, GameBoard.PLAYAREAEND);

            for (int i=0; i<8; i++)
            {
                if (gameBoard.getCharacter(screen_loc+gameBoard.neighbor(i)) == Characters.Blank)
                {
                    found = true  ;
                    break ;
                }
            }
        }
        gameBoard.moveShip( screen_loc, Ship );
        GameLogging.debug("arbshp to "+gameBoard.getCurrentShipLocation());

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

    void deleteSprite(){
        virtualTI.getVideo().deleteSprite(0);
    }


    void killtime(int waitval)
    {

       // Sleep(waitval/500) ;
    }



    /**
     * put up PreGame screen and display levels
     */
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

    public void setPlayLevel(int level)
    {
        switch(level){
            case 1:
                m_nLevFlg = 1 ;
                m_nSpeed = 1 ;
                break;
            case 2:
                m_nLevFlg = 1 ;
                m_nSpeed = 2 ;
                break;
            case 3:
                m_nLevFlg = 2 ;
                m_nSpeed = 3 ;
                break;
     }
    }
    private void handleDisplayLevelMenuSelection(TIEmulatorEvent event) {
        LOGGER.info("handleDisplayLevelMenuSelection event="+((ITIKeyboard.TIKeyboardEvent)event).getKeyCode().toString());
        switch(((ITIKeyboard.TIKeyboardEvent)event).getKeyCode()){
            case AID:
                displayHelpMenu();
                return ;
            case DIGIT1:
                setPlayLevel(1);
                break;
            case DIGIT2:
                setPlayLevel(2);
                break;
            case DIGIT3:
                setPlayLevel(3);
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
