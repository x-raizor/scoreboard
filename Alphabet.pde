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

