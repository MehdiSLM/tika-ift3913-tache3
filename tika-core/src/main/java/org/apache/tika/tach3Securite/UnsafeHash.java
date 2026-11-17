package org.apache.tika.tach3Securite;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UnsafeHash {
    public byte[] hashDocument(byte[] document)
            throws NoSuchAlgorithmException {


        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        return digest.digest(document);
    }
}