public class MyLZW {
    private static final int ENTRY_MAX = 65536;
    private static final double MONITOR_RESET_THRESHOLD = 1.1;

    private static int R = 256;
    private static int L = 512;
    private static int W = 9;

    private static final char RESET_SYMBOL = 'R';
    private static final char MONITOR_SYMBOL = 'M';
    private static final char REGULAR_SYMBOL = 'N';

    public static void main(String[] args) {
        if (args.length < 1) throw new IllegalArgumentException("\nPlease format arguments like so:\n\'java MyLZW - m < input.file > output.lzw\'");

        if (args[0].equals("-")){
            if(args[1].equals("n")){
                compress();
            } else if (args[1].equals("r")) {
                compressWithReset();
            } else if (args[1].equals("m")) {
                compressWithMonitor();
            }
        } else if (args[0].equals("+")){
            if(args[1].equals("n")){
                expand();
            } else if (args[1].equals("r")) {
                expandWithReset();
            } else if (args[1].equals("m")) {
                expandWithMonitor();
            }
        }
        else throw new IllegalArgumentException("Illegal command line argument");
    }

    public static void compressWithMonitor() {
        System.err.println("Compressing...");
        BinaryStdOut.write(MONITOR_SYMBOL);

        double currNumber = 0, cDenominator = 0, cRatio, sRatio = 0;
        boolean noRatio = true;

        String input = BinaryStdIn.readString();

        TST<Integer> tree = new TST<>();
        for (int i = 0; i < R; i++) tree.put("" + (char)i, i);

        int currCode = R + 1;
        while (input.length() > 0) {
            String longestPrefixString = tree.longestPrefixOf(input);
            BinaryStdOut.write(tree.get(longestPrefixString), W);

            int prefixLength = longestPrefixString.length();

            if ((currCode < L) && (prefixLength < input.length())) tree.put(input.substring(0, prefixLength + 1), currCode++);

            if (currCode == ENTRY_MAX) {
                currNumber += prefixLength * 8;
                cDenominator += W;

                cRatio = currNumber/cDenominator;
                if (noRatio) {
                    noRatio = false;
                    sRatio = cRatio;
                }

                if ((sRatio/cRatio) > MONITOR_RESET_THRESHOLD) {
                    tree = new TST<>();
                    for (int i = 0; i < R; i++) tree.put("" + (char)i, i);
                    currCode = R + 1;
                    W = 9;
                    L = 512;
                    sRatio = 0;
                    noRatio = true;
                }
            }

            if (((int)Math.pow(2, W) == currCode) && (W < 16)) {
                L = (int)Math.pow(2, ++W);
                tree.put(input.substring(0, prefixLength + 1), currCode++);
            }

            input = input.substring(prefixLength);
        }
        BinaryStdOut.write(R, W);
        BinaryStdOut.close();

        System.err.println("Compression complete");
    }

    public static void expandWithMonitor() {
        if (BinaryStdIn.readInt(8) != MONITOR_SYMBOL) throw new IllegalArgumentException("This LZW file is incompatible with Expand Monitor");
        System.err.println("Expanding...");

        double sRatio = 0, cRatio, currNumber = 0, cDenominator = 0;
        boolean noRatio = true;
        int cIndex;

        String[] tree = new String[(int) Math.pow(2, 16)];

        for (cIndex = 0; cIndex < R; cIndex++) tree[cIndex] = "" + (char)cIndex;
        tree[cIndex++] = "";

        int currCode = BinaryStdIn.readInt(W);
        if (currCode == R) return;

        String writeVal = tree[currCode];
        while (true) {
            BinaryStdOut.write(writeVal);
            currCode = BinaryStdIn.readInt(W);
            if (currCode == R) break;

            String currString = tree[currCode];
            if (currCode == cIndex) currString = writeVal + writeVal.charAt(0);
            if (cIndex < L-1) tree[cIndex++] = writeVal + currString.charAt(0);

            if (W < 16 && cIndex == L - 1) {
                tree[cIndex++] = writeVal + currString.charAt(0);
                L = (int)Math.pow(2, ++W);
            }

            writeVal = currString;
            if (cIndex == ENTRY_MAX - 1) {
                currNumber += writeVal.length() * 8;
                cDenominator += W;
                cRatio = (currNumber/cDenominator);

                if (noRatio) {
                    noRatio = false;
                    sRatio = cRatio;
                }

                if ((sRatio/cRatio) > MONITOR_RESET_THRESHOLD) {
                    L = 512;
                    W = 9;

                    tree = new String[(int) Math.pow(2, 16)];
                    for (cIndex = 0; cIndex < R; cIndex++) tree[cIndex] = "" + (char)cIndex;
                    tree[cIndex++] = "";

                    BinaryStdOut.write(writeVal);

                    currCode = BinaryStdIn.readInt(W);
                    if (currCode == R) return;

                    writeVal = tree[currCode];
                    sRatio = 0;
                    noRatio = true;
                }
            }

        }

        BinaryStdOut.close();
        System.err.println("Expansion complete...");
    }

    public static void compressWithReset() {
        System.err.println("Compressing...");
        BinaryStdOut.write(RESET_SYMBOL);

        String input = BinaryStdIn.readString();

        TST<Integer> tree = new TST<>();
        for (int i = 0; i < R; i++) tree.put("" + (char)i, i);
        int currCode = R + 1;

        while (input.length() > 0) {
            String longestPrefixString = tree.longestPrefixOf(input);
            BinaryStdOut.write(tree.get(longestPrefixString), W);

            int prefixLength = longestPrefixString.length();
            if ((L > currCode) && (prefixLength < input.length())) tree.put(input.substring(0, prefixLength + 1), currCode++);

            if (currCode == ENTRY_MAX) {
                tree = new TST<>();
                for (int i = 0; i < R; i++) tree.put("" + (char) i, i);

                currCode = R + 1;
                W = 9;
                L = 512;
            }

            if ((W < 16) && ((int)Math.pow(2, W) == currCode)) {
                L = (int)Math.pow(2, ++W);
                tree.put(input.substring(0, prefixLength + 1), currCode++);
            }

            input = input.substring(prefixLength);
        }

        BinaryStdOut.write(R, W);
        BinaryStdOut.close();

        System.err.println("Compression complete...");
    }

    public static void expandWithReset() {
        if (BinaryStdIn.readInt(8) != RESET_SYMBOL) throw new IllegalArgumentException("This LZW file is incompatible with Expand Reset");
        System.err.println("Expanding...");

        String[] stringArr = new String[(int)Math.pow(2, 16)];
        int nextCode;

        for (nextCode = 0; nextCode < R; nextCode++) stringArr[nextCode] = "" + (char)nextCode;
        stringArr[nextCode++] = "";

        int currCode = BinaryStdIn.readInt(W);
        if (currCode == R) return;
        String exportString = stringArr[currCode];

        while (true) {
            BinaryStdOut.write(exportString);

            currCode = BinaryStdIn.readInt(W);
            if (currCode == R) break;

            String currCodeString = stringArr[currCode];
            if (nextCode == currCode) currCodeString = exportString + exportString.charAt(0);
            if (nextCode < L - 1) stringArr[nextCode++] = exportString + currCodeString.charAt(0);

            if ((nextCode == L - 1) && (W < 16)) {
                stringArr[nextCode++] = exportString + currCodeString.charAt(0);
                W++;
                L = (int)Math.pow(2, W);
            }

            exportString = currCodeString;

            if (nextCode == (ENTRY_MAX - 1)) {
                L = 512;
                W = 9;
                stringArr = new String[(int)Math.pow(2, 16)];
                for (nextCode = 0; nextCode < R; nextCode++) stringArr[nextCode] = "" + (char)nextCode;

                stringArr[nextCode++] = "";
                BinaryStdOut.write(exportString);

                currCode = BinaryStdIn.readInt(W);
                if (currCode == R) return;
                exportString = stringArr[currCode];
            }
        }

        BinaryStdOut.close();
        System.err.println("Expansion complete");
    }

    public static void compress() {
        System.err.println("Compressing...");
        BinaryStdOut.write(REGULAR_SYMBOL);

        String input = BinaryStdIn.readString();
        TST<Integer> st = new TST<>();

        for (int i = 0; i < R; i++) st.put("" + (char) i, i);

        int currCode = R + 1;

        while (input.length() > 0) {
            if (currCode == L && W != 16) {
                W++;
                L = (int)Math.pow(2,W);
            }

            String s = st.longestPrefixOf(input);
            BinaryStdOut.write(st.get(s), W);
            int t = s.length();

            if (t < input.length() && currCode < L) st.put(input.substring(0, t + 1), currCode++);
            input = input.substring(t);
        }

        BinaryStdOut.write(R, W);
        BinaryStdOut.close();
        System.err.println("Compression complete");
    }

    public static void expand() {
        if (BinaryStdIn.readInt(8) != REGULAR_SYMBOL) throw new IllegalArgumentException("This LZW file is incompatible with Reset");
        System.err.println("Expanding...");

        String[] input = new String[L];
        int currentIndex;

        for (currentIndex = 0; currentIndex < R; currentIndex++) input[currentIndex] = "" + (char)currentIndex;
        input[currentIndex++] = "";

        int currCode = BinaryStdIn.readInt(W);
        if (currCode == R) return;
        String value = input[currCode];

        while (true) {
            if ((currentIndex == L - 1) && (W != 16)){
                W++;
                String[] inputClone;
                L = (int) Math.pow(2,W);

                inputClone = input.clone();
                input = new String[L];

                for (int i = 0; i < inputClone.length; i++) input[i] = inputClone[i];
            }
            BinaryStdOut.write(value);
            currCode = BinaryStdIn.readInt(W);

            if (currCode == R) break;
            String currString = input[currCode];

            if (currentIndex == currCode) currString = value + value.charAt(0);
            if (currentIndex < L) input[currentIndex++] = value + currString.charAt(0);

            value = currString;
        }

        BinaryStdOut.close();
        System.err.println("Expansion complete");
    }
}