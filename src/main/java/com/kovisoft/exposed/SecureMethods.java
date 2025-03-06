package com.kovisoft.exposed;


import java.io.IOException;
import java.security.GeneralSecurityException;


public interface SecureMethods {

    String encryptFile(String filePath, String password, boolean deleteUnencrypted)
            throws IOException, GeneralSecurityException;

    String decryptFile(String filePath, String password, boolean deleteEncrypted)
        throws IOException, GeneralSecurityException;
    String[] decryptJsonToArgs(String filePath, String password, boolean deleteEncrypted)
            throws IOException, GeneralSecurityException;
    String[] unencryptedJsonToArgs(String filePath, boolean deleteAfter) throws IOException;
}
