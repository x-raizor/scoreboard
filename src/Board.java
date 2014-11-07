import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;

import java.util.Hashtable;

/**
 * Class Board animates train schedule board
 * Written by Andrew Shapiro on 13.08.14.
 */

public class Board {

    private static final String FONTNAME = "AlicoreStencils-Regular.otf";  // Folio Bold Condensed.ttf
    private static final int    PHWIDTH = 54, PHHEIGHT = 64,
                                XMARGIN = 10, YMARGIN = 10,
                                PHUPPERHALF = 32, PHBOTTOMHALF = 29, GAP = 3,
                                BASELINE = 56, FONTSIZE = 65,
                                DECAY = 15;  // time for empty line disappear
    private Line[] lines;
    private int xPos, yPos;
    private PApplet ctx;
    private int time;
    private int boardLength;
    private Hashtable<Character, PGraphics> symbols;


    public static class Alphabet {
        private final static boolean SKIPWILDCARDS = true;
        private final static char[] alphabet = new char[] {
                32, 33, 43, 44, 45, 46, 47,  // Space !    + , - . /
                48, 49, 50, 51, 52, 53, 54, 55, 56, 57, // 0-9
                65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90 // A-Z
        };
        private final static int WILDEND = 7;

        public static boolean isHere(char symbol){ // is symbol inside the alphabet
            if (findSymbol(symbol) < 0) return false;
            return true;
        }

        public static boolean isWild(int position) { // is it wildcard
            return position < WILDEND;
        }

        public static char getSymbolByShift(char symbol, int shift) {
            /**
             * Return symbol which shifted from on shift from given
             */
            int code = findSymbol(symbol);
            if (code < 0) return symbol;
            int position = (code + shift) % alphabet.length;
            if (SKIPWILDCARDS && position < WILDEND) { // inside wildcards zone
                position = (code + shift + WILDEND) % alphabet.length;
            }
            return alphabet[position];
        }

        public static int findSymbol(char key) {
            /**
             *  Return index of symbol in alphabet table. Bisection method, O(logN) performance
             */
            int lo = 0;
            int hi = alphabet.length - 1;
            while (lo <= hi) {
                // Key is in a[lo..hi] or not present.
                int mid = lo + (hi - lo) / 2;
                if      (key < alphabet[mid]) hi = mid - 1;
                else if (key > alphabet[mid]) lo = mid + 1;
                else return mid;
            }
            return -1;
        }

        public static int getDistance(char A, char B) { // return distance between two given chars
            int a  = findSymbol(A);
            int b = findSymbol(B);
            if (a == -1 || b == -1) return Integer.MAX_VALUE;

            if (SKIPWILDCARDS) {
            // without wildcards
                if (isWild(a) && isWild(b)) {
                    return alphabet.length - WILDEND;  // Inside the zone, out and into the zone
                }
                if (a <= b) {
                    if (isWild(a)) {
                        return b - WILDEND;           // Going out of the zone
                    } else {
                        return b - a;                 //Outside wildcard zone, b − a
                    }
                }
               else {
                    if (isWild(b)) {
                        return alphabet.length - b;  // Enter into the zone
                    } else {
                        return alphabet.length - WILDEND + a - b; // Pass through zone
                    }
                }
            } else {
            // using whole alphabet
                if (a <= b) {
                    return b - a;
                } else {
                    return alphabet.length - a + b;
                }
            }
        }

        public static char nextSymbol(char symbol) { // return next symbol in alphabet
            int len = alphabet.length;
            int newIndex = findSymbol(symbol);
            newIndex = (newIndex + 1) % len;
            if (SKIPWILDCARDS && isWild(newIndex)) {
                newIndex = WILDEND;
            }
            return alphabet[newIndex];
        }
    }


    private class Line {
        private String startText, endText;
        private int lineNumber;
        private Placeholder[] letters;
        private EventQueue events;
        private Event currentEvent;
        public boolean isReady;


        private class Placeholder {
        /**
         * Placeholder inner class. It does all work with a symbol position
         * */
            private int __x, __y;
            private int stop;
            private char startLetter, endLetter;
            private float shift;
            private float increment;

            public Placeholder(char A, char B, int x, int y) {
                this.startLetter = A;
                this.endLetter = B;
                this.__x = x;
                this.__y = y;
            }

            public void draw() {
                char letter = getLetter();
                PGraphics buffer = symbols.get(letter);
                if (buffer == null) return;
                ctx.image(buffer, get_x(__x), get_y(__y)); // out to the screen
            }

            private int get_x(int x) { return (xPos + x * (PHWIDTH + XMARGIN)); }
            private int get_y(int y) { return (yPos + 32 + y * (PHHEIGHT + YMARGIN)); }  // + 32?

            private char getLetter() {
                if (isReady) {
                    return endLetter; // to avoid incremental tinkling in the end
                }
                int scale = 1;
                if (endLetter == ' ') { // spaces must finish with speed related to its position
                    if (time > stop) return ' ';
                    scale = 1 + __x;
                }
                shift += increment * scale;
                return Alphabet.getSymbolByShift(startLetter, (int) shift);
            }
        }


        public Line(String text, int lNumber) {
            startText  = text;
            endText  = text;
            lineNumber = lNumber;
            isReady = true;

            letters = new Placeholder[boardLength]; // born a set of placeholders
            fillIn(startText, startText);
            events = new EventQueue();
            currentEvent = null;
        }

        /* Fill two lines, start and end, maintaining proper space characters  */
        private void fillIn(String txt0, String txt1) {
            int len0 = txt0.length();
            int len1 = txt1.length();
            for (int i = 0; i < boardLength; i++) {
                char A = 32, B = 32;
                if (i < len0) { A = txt0.charAt(i); }
                if (i < len1) { B = txt1.charAt(i); }
                letters[i] = new Placeholder(A, B, i, lineNumber);
            }
        }

        /*
        * Recreate placeholder set for the line
        * Calculate distances between symbols
        * */
        public void loadEvent() {
            startText = endText;
            endText = currentEvent.name();
            int deltaT = currentEvent.to() - currentEvent.from();
            fillIn(startText, endText);
            Placeholder x;
            for (int i = 0; i < boardLength; i++) {
                x = letters[i];
                x.increment =  Alphabet.getDistance(x.startLetter, x.endLetter) / (float) deltaT;
                x.stop = currentEvent.from() + deltaT/(i + 1);  // stop animate, using only for spaces
                //x.stop = currentEvent.from() + (int) Math.ceil(deltaT/(Math.sin(Math.PI*i/boardLength/2) + 1));
            }
        }

        /* Calculate line state for current frame  */
        public void nextFrame() {
            if (isReady) {
                Event newEvent = events.next(); // load new event
                if (newEvent != null) {
                    currentEvent = newEvent;
                    loadEvent();
                    isReady = false;
                }
            }
            if (currentEvent == null) return; // nothing to do
            if (time < currentEvent.from()) return; // not the time
            if (time >= currentEvent.to()) isReady = true; // time for new event
            draw();
        }

        /* Draw all letters in the line */
        public void draw() {
            for (Placeholder letter : letters) {
                letter.draw();
            }
        }
    }


    /* Board object constructor  */
    public Board(PApplet context, String[] text) {

        int line_number = text.length;
        lines = new Line[line_number];
        ctx = context;

        int maxLineLength = 0;
        for (String str : text) {
            maxLineLength = Math.max(maxLineLength, str.length());
        }
        boardLength = maxLineLength;
        for (int i = 0; i < line_number; i++) {
            lines[i] = new Line(text[i], i);
        }
        xPos = (context.width - maxLineLength * (PHWIDTH + XMARGIN) + XMARGIN)/2;
        yPos = (context.height - line_number * (PHHEIGHT + YMARGIN) + YMARGIN)/2;

        PFont captions = ctx.createFont(FONTNAME, FONTSIZE);
        ctx.textFont(captions);
        ctx.smooth();

        //create alphabet
        int alphabetLength = Alphabet.alphabet.length;
        symbols = new Hashtable<Character, PGraphics>(); //PGraphics[alphabetLength];
        for (Character symbol : Alphabet.alphabet) {
            symbols.put(symbol, createSymbol(symbol));
        }
        draw();
    }

    public void nextFrame() { // calculate current frame for all lines
        time = ctx.frameCount;
        for (Line line : lines) {
            line.nextFrame();
        }
    }

    public void draw() { // draw al lines
        for (Line line : lines) {
            line.draw();
        }
    }


    /* Create the sprite for the given symbol */
    private PGraphics createSymbol(char symbol) {
        PGraphics buffer = ctx.createGraphics(PHWIDTH, PHHEIGHT);
        buffer.beginDraw();
        buffer.background(0);
        buffer.smooth();

        // print placeholder
        buffer.fill(51);
        buffer.noStroke();
        buffer.rect(0, 0, PHWIDTH, PHUPPERHALF, 6, 6, 3, 3);
        buffer.rect(0, PHUPPERHALF + GAP, PHWIDTH, PHBOTTOMHALF, 1, 1, 6, 6);

        // print startLetter
        buffer.fill(255);
        buffer.textFont(ctx.createFont(FONTNAME, FONTSIZE));
        float symbolWidth = buffer.textWidth(symbol);
        int shiftX = (int) (PHWIDTH - symbolWidth)/2;
        buffer.text(symbol, shiftX, BASELINE);

        // erase the middle
        buffer.fill(0);
        buffer.rect(0, PHUPPERHALF, PHWIDTH, GAP);
        buffer.endDraw();

        return buffer;
    }

    /* add text event in the certain line events queue */
    public void addEvent(String text, int l, int t0, int t1) {
        if (text.length() > boardLength) new IllegalArgumentException("Text mustn't be longer than the most long initial line");
        if (t0 > t1) new IllegalArgumentException("Ending time must be greater than beginning one");
        if (l >= lines.length) new IllegalArgumentException("There is no such of line");
        text = text.toUpperCase();
        for (char symbol : text.toCharArray()) {
            //TODO doesn't work for some reason
            if (!symbols.containsKey(symbol)) new IllegalArgumentException("There is a symbol which is out of table alphabet!");
        }
        lines[l].events.add(new Event(text, t0, t1));
    }

}
