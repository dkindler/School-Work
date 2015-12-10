import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.String;
import java.lang.StringBuilder;
import java.lang.System;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Scanner;
import java.security.*;
import java.math.BigInteger;


public class MySign {
    static final String PUB_KEY_FILE_NAME = "PublicKey.rsa";
    static final String PRIV_KEY_FILE_NAME = "PrivateKey.rsa";

    public static void main(String[] args) throws FileNotFoundException, IOException, NoSuchAlgorithmException {
        if (args.length != 2) {
            System.out.println("Inappropriate use of flags.\n Please do:");
            System.out.println("java MySign [s/v] [filename]");
            return;
        }

        String flag = args[0].toLowerCase();
        String fileName = args[1];

        switch (flag) {
            case "s":
                s(fileName);
                break;
            case "v":
                v(fileName);
                break;
            default:
                System.out.println("Please use flag 's' or 'v'");
                return;
        }

    }

    public static void s(String fileName) throws FileNotFoundException, IOException, NoSuchAlgorithmException {
        File readFile = new File(fileName);
        Scanner scanner = new Scanner(readFile);

        ArrayList<String> lines = new ArrayList<>();
        StringBuilder fileAsString = new StringBuilder();
        while (scanner.hasNext()) {
            String l = scanner.nextLine();
            fileAsString.append(l);
            lines.add(l);
        }

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        BigInteger hash = new BigInteger(md.digest(fileAsString.toString().getBytes())).abs();

        try {
            File privKey = new File(PRIV_KEY_FILE_NAME);
            Scanner privScanner = new Scanner(privKey);
            BigInteger d = new BigInteger(privScanner.nextLine());
            BigInteger n = new BigInteger(privScanner.nextLine());

            BigInteger decrpytK = hash.modPow(d, n);

            FileWriter signedFile = new FileWriter(fileName + ".signed");
            signedFile.write(decrpytK + "\n");

            for (String s : lines) {
                signedFile.write(s + "\n");
            }

            signedFile.close();
            System.out.println("Signed File Saved To: " + fileName + ".signed");

        } catch (FileNotFoundException e) {
            System.out.println(e);
            return;
        }
    }

    public static void v(String fileName) throws FileNotFoundException, IOException, NoSuchAlgorithmException {

        if (!fileName.toLowerCase().contains(".signed")) {
            System.out.println("This function only takes files witht he postfix '.signed'");
            return;
        }

        File readFile = new File(fileName);
        Scanner scanner = new Scanner(readFile);
        ArrayList<String> lines = new ArrayList<>();
        StringBuilder fileAsString = new StringBuilder();
        BigInteger decrypt = new BigInteger(scanner.nextLine());

        while (scanner.hasNext()) {
            String l = scanner.nextLine();
            fileAsString.append(l);
            lines.add(l);
        }

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        BigInteger hash = new BigInteger(md.digest(fileAsString.toString().getBytes())).abs();

        try {
            File pubKey = new File(PUB_KEY_FILE_NAME);
            Scanner pubScanner = new Scanner(pubKey);

            BigInteger e = new BigInteger(pubScanner.nextLine());
            BigInteger n = new BigInteger(pubScanner.nextLine());

            BigInteger encrypt = decrypt.modPow(e, n);
            if (hash.equals(encrypt)) {
                System.out.println("VALID.");
            } else {
                System.out.println("INVALID.");
            }

        } catch (FileNotFoundException e) {
            System.out.println(e);
            return;
        }
    }

}