package com.silvertree.tombstone;

import com.silvertree.tombstone.tiemulation.ITIKeyboard;
import com.silvertree.tombstone.tiemulation.ITIVideo;
import com.silvertree.tombstone.tiemulation.IVirtualTI;
import com.silvertree.tombstone.tiemulation.impl.TIVideo;
import com.silvertree.tombstone.tiemulation.impl.VirtualTI;
import javafx.scene.layout.Pane;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;



public class TombstoneCityTest {

    TombstoneCity tombstoneCity ;


    @BeforeEach
    public void setupTest(){
        tombstoneCity = new TombstoneCity(createMockTIEmulator()) ;
        tombstoneCity.m_nLevFlg = 1 ;
        tombstoneCity.gameBoard.draw(1);
        LargeMonster.createMontab();
        SmallMonster.createMontab();
    }
    @Test
    public void tryMoveBasicMoves() {
        int startPosition = GameBoard.PLAYAREABG + 2 ;
        LargeMonster monster = new LargeMonster(startPosition);
        tombstoneCity.gameBoard.writeChar(monster.getCurLocation(), Characters.Large1);
        int newPosition = monster.getCurLocation() + 32 ;
        assertTrue(tombstoneCity.tryMove(monster, newPosition));
        Characters c = tombstoneCity.gameBoard.getCharacter(newPosition);
        assertEquals(Characters.Large2, c);
        assertEquals(Characters.Blank, tombstoneCity.gameBoard.getCharacter(startPosition));

        startPosition = monster.getCurLocation() ;
        newPosition = monster.getCurLocation() -32 ;
        assertTrue(tombstoneCity.tryMove(monster, newPosition));
        c = tombstoneCity.gameBoard.getCharacter(newPosition);
        assertEquals(Characters.Large1, c);
        assertEquals(Characters.Blank, tombstoneCity.gameBoard.getCharacter(startPosition));

        newPosition = monster.getCurLocation() -1 ;
        assertTrue(tombstoneCity.tryMove(monster, newPosition));
        c = tombstoneCity.gameBoard.getCharacter(newPosition);
        assertEquals(Characters.Large2, c);

        newPosition = monster.getCurLocation() +1 ;
        assertTrue(tombstoneCity.tryMove(monster, newPosition));
        c = tombstoneCity.gameBoard.getCharacter(newPosition);
        assertEquals(Characters.Large1, c);
    }

    @Test
    public void tryMoveBlockedByGraves(){
        LargeMonster monster = new LargeMonster(GameBoard.PLAYAREABG + 2);
        tombstoneCity.gameBoard.writeChar(monster.getCurLocation(), Characters.Large1);
        int newPosition = monster.getCurLocation() + 32 ;
        tombstoneCity.gameBoard.writeChar(newPosition, Characters.Grave);
        assertFalse(tombstoneCity.tryMove(monster, newPosition));
        Characters c = tombstoneCity.gameBoard.getCharacter(newPosition);
        assertEquals(Characters.Grave, c);

        assertEquals(Characters.Large1, tombstoneCity.gameBoard.getCharacter(monster.getCurLocation()));

    }

    @Test
    public void killLargeMonster(){
        int startPosition = GameBoard.PLAYAREABG + 2 ;
        LargeMonster monster = new LargeMonster(startPosition);
        monster.addToMontab();

        tombstoneCity.gameBoard.writeChar(monster.getCurLocation(), Characters.Large1);

        tombstoneCity.killMonster(monster);

        Characters c = tombstoneCity.gameBoard.getCharacter(startPosition);
        assertTrue(LargeMonster.isEmpty());

    }

    @Test
    public void killSmallMonster(){
        int startPosition = GameBoard.PLAYAREABG + 2 ;
        SmallMonster monster = new SmallMonster(startPosition);
        monster.addToMontab();

        tombstoneCity.gameBoard.writeChar(monster.getCurLocation(), Characters.Small1);

        tombstoneCity.killMonster(monster);

        Characters c = tombstoneCity.gameBoard.getCharacter(startPosition);
        assertTrue(SmallMonster.isEmpty());


    }

    @Test
    void testGenerateSmallMonsters() {

        tombstoneCity.genSmallMonsters();
        assertEquals(SmallMonster.MAXSMALLMONSTERCOUNT, SmallMonster.getMonsters().size());
        assertTrue(verifySmallMonsterLocations());
    }

    @Test
    void testGenerateLargeMonsters(){
        int startingLargeMonsterCount = LargeMonster.getMonsters().size() ;
        assertEquals(0, startingLargeMonsterCount);
        boolean generatedLargeMonsters = tombstoneCity.generateLargeMonsters();
        assertTrue(generatedLargeMonsters);
        assertEquals(1, LargeMonster.getMonsters().size());
        Monster monster = LargeMonster.getMonsters().get(0);
        assertTrue(monster instanceof LargeMonster);
        Characters c = tombstoneCity.gameBoard.getCharacter(monster.getCurLocation());
        assertTrue(  c == Characters.Large2);
    }

    @Test
    void testFindGenerator(){
        clearGraves();
        int generatorPosition = GameBoard.PLAYAREABG+16;
        tombstoneCity.gameBoard.writeChar(generatorPosition, Characters.Grave);
        tombstoneCity.gameBoard.writeChar(generatorPosition+33, Characters.Grave);
        Generator generator = tombstoneCity.findGenerator();
        assertNotNull(generator);
        assertEquals(generatorPosition, generator.getGeneratorLocation());

        Generator nextGenerator = tombstoneCity.findGenerator() ;
        assertNotEquals(generator.getGeneratorLocation(), nextGenerator.getGeneratorLocation());


    }

    @Test
    void testFindGeneratorWithNoGraves(){
        clearGraves() ;
        Generator generator = tombstoneCity.findGenerator() ;
        assertNull(generator);

        int gravePosition = GameBoard.PLAYAREABG+16;
        tombstoneCity.gameBoard.writeChar(gravePosition, Characters.Grave);

        assertNull(tombstoneCity.findGenerator());


    }

    @Test
    void testCheckAdjacentGraves(){
        clearGraves() ;
        int generatorPosition = GameBoard.PLAYAREABG+16;
        tombstoneCity.gameBoard.writeChar(generatorPosition, Characters.Grave);
        tombstoneCity.gameBoard.writeChar(generatorPosition+33, Characters.Grave);
        tombstoneCity.gameBoard.writeChar(generatorPosition+2,Characters.Grave);
        tombstoneCity.checkForAdjacentGraves(generatorPosition);
        Characters grave1 = tombstoneCity.gameBoard.getCharacter(generatorPosition+33);
        assertEquals(Characters.Blank, grave1);
        Characters grave2 = tombstoneCity.gameBoard.getCharacter(generatorPosition+2);
        assertEquals(Characters.Blank, grave2);

    }
    @Test
    void testReleaseMonster(){
        Generator generator = new Generator(0, 1);
        tombstoneCity.releaseMonster(generator);
    }

    private void clearGraves(){
        for (int position = GameBoard.PLAYAREABG; position < GameBoard.PLAYAREAEND; position++){
            Characters c = tombstoneCity.gameBoard.getCharacter(position);
            if (c == Characters.Grave)
                tombstoneCity.gameBoard.writeChar(position, Characters.Blank);
        }
    }
    private boolean verifyGeneratorAdjacent(int location){
        int[] checkPosition = new int []{-1, 1, -32, 32, };
        return false ;
    }
    private boolean verifySmallMonsterLocations(){
        boolean verify = true ;
        for (Monster monster : SmallMonster.getMonsters()){
            Characters c = tombstoneCity.gameBoard.getCharacter(monster.getCurLocation()) ;
            assertTrue(c == Characters.Small1 || c == Characters.Small2);
            verify = verify ? (c== Characters.Small2 || c == Characters.Small1) : verify ;
        }
        return true ;
    }
    @Test
    public void monblk() {
    }

    @Test
    public void captureShip() {
    }

    IVirtualTI createMockTIEmulator(){
        return new MockVirtualTI() ;
    }

    static class MockVirtualTI implements IVirtualTI{
        ITIVideo video ;
        ITIKeyboard keyboard ;

        public MockVirtualTI(){
            video = new TIVideo(new Pane(), 2.0);

        }
        @Override
        public ITIVideo getVideo() {
            return video;
        }

        @Override
        public ITIKeyboard getKeyboard() {
            return null;
        }
    }
}