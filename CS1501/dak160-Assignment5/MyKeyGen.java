import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.io.*;
import java.util.Random;

class MyKeyGen {

    static final int KEY_SIZE = 512;
    static final String PUB_KEY_FILE_NAME = "PublicKey.rsa";
    static final String PRIV_KEY_FILE_NAME = "PrivateKey.rsa";


    public static void main(String args[]) throws IOException {
        BigInteger bi_one = new BigInteger(String.valueOf(1));
        BigInteger p = new BigInteger(KEY_SIZE, 1, new Random());
        BigInteger q = new BigInteger(KEY_SIZE, 1, new Random());
        BigInteger n = p.multiply(q);
        BigInteger pLessOne = p.subtract(bi_one);
        BigInteger qLessOne = q.subtract(bi_one);
        BigInteger phi = pLessOne.multiply(qLessOne);
        BigInteger e =  new BigInteger(KEY_SIZE, 1, new Random());

        // Must make sure that phi != e
        while((phi.compareTo(e) != 1) || !(phi.gcd(e).equals(bi_one))) {
            e =  new BigInteger(KEY_SIZE, 1, new Random());
        }

        BigInteger d = e.modInverse(phi);

        FileWriter fw_pub = new FileWriter(PUB_KEY_FILE_NAME);
        fw_pub.write(e.toString() + "\n");
        fw_pub.write(n.toString());
        fw_pub.close();

        FileWriter fw_priv = new FileWriter(PRIV_KEY_FILE_NAME);
        fw_priv.write(d.toString() + "\n");
        fw_priv.write(n.toString());
        fw_priv.close();

        System.out.println("Public Key: " + PUB_KEY_FILE_NAME);
        System.out.println("Private Key: " + PRIV_KEY_FILE_NAME);
    }
}
