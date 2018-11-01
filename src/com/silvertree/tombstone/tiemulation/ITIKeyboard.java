package com.silvertree.tombstone.tiemulation;

import com.silvertree.tombstone.tiemulation.impl.TIKeyboard;
import javafx.event.EventHandler;

public interface ITIKeyboard {
     enum TIKeycode { DEL(3),        			/* FCTN 1  */
         INS( 4),    		/* FCTN 2  */
         ERASE (7),			/* FCTN 3  */
         CLEAR(2) ,			/* FCTN 4  */
         BEGIN (14),			/* FCTN 5  */
         PROCD(12),			/* FCTN 6  */
         AID(1),   		/* FCTN 7  */
         REDO(6),			/* FCTN 8  */
         BACK (5),			/* FCTN 9  */
         QUIT(5),			/* FCTN =  */

         SPACE(32),
         DIGIT0(48),
         DIGIT1(49),
         DIGIT2(50),
         DIGIT3(51),
         DIGIT4(52),
         DIGIT5(53),
         DIGIT6(54),
         DIGIT7(55),
         DIGIT8(56),
         DIGIT9(57),

         Q((int)'Q'),
         RIGHT(0x80),
         LEFT(0x81),
         UP(0x82),
         DOWN(0x83) ,

         UNMAPPED(0);
         TIKeycode(int val){
            this.val = val ;
        }
        public int val ;
    }
    class TIKeyboardEvent extends TIEmulatorEvent {
        private TIKeycode keycode ;

        public TIKeycode getKeyCode() {
            return keycode ;
        }
        public TIKeyboardEvent(TIKeycode keycode){
            this.keycode = keycode ;
        }
    }
    TIKeycode scan() ;
    boolean checkkey(TIKeycode keycode) ;
    void onKeyPressed(TIKeyboardEventListener<? super ITIKeyboard.TIKeyboardEvent> eventHandler) ;
}
