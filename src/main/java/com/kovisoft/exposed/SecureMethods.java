package com.kovisoft.exposed;


import com.kovisoft.internal.SecureMethodsImpl;

import java.io.IOException;
import java.security.GeneralSecurityException;


public interface SecureMethods {

    /**
     * Returns an implementation of the SecureMethods interface
     * @return SecureMethods interface
     */
    static SecureMethods getSecureMethod(){return new SecureMethodsImpl();}

    /**
     * Generates an encrypted file and saves it in the same directory;
     * @param filePath The full filepath where the file to encrypt lives.
     * @param password The password used to encrypt the file.
     * @param deleteUnencrypted Deletes the unencrypted file upon completion if true.
     * @return Returns the File path of the now encrypted file.
     * @throws IOException Primarily results from read and write privileges being incorrect.
     * @throws GeneralSecurityException This includes all the various encryption/decryption related issues.
     */
    String encryptFile(String filePath, String password, boolean deleteUnencrypted)
            throws IOException, GeneralSecurityException;

    /**
     * Generates a decrypted file and saves it in the same directory;
     * @param filePath The full filepath where the file to decrypt lives.
     * @param password The password used to decrypt the file.
     * @param deleteEncrypted Deletes the encrypted file upon completion if true.
     * @return Returns the File path of the now decrypted file.
     * @throws IOException Primarily results from read and write privileges being incorrect.
     * @throws GeneralSecurityException This includes all the various encryption/decryption related issues.
     */
    String decryptFile(String filePath, String password, boolean deleteEncrypted)
        throws IOException, GeneralSecurityException;

    /**
     * Similar to decryptFile but returns an String[] in the String[] args traditional format from
     * am encrypted json file.
     * @param filePath The full filepath where the file to decrypt lives.
     * @param password The password used to decrypt the file.
     * @param deleteEncrypted Deletes the encrypted file upon completion if true.
     * @return Returns the String[] args of the json file, "-key", "value", "-key2", "value2" etc...
     * @throws IOException Primarily results from read and write privileges being incorrect.
     * @throws GeneralSecurityException This includes all the various encryption/decryption related issues.
     */
    String[] decryptJsonToArgs(String filePath, String password, boolean deleteEncrypted)
            throws IOException, GeneralSecurityException;

    /**
     * Similar to decryptJsonToArgs but this is reading a previously decrypted (or unencrypted) file.
     * @param filePath The full filepath where the file to decrypt lives.
     * @param deleteAfter Deletes the file upon completion if true.
     * @return Returns the String[] args of the json file, "-key", "value", "-key2", "value2" etc...
     * @throws IOException Primarily results from read and write privileges being incorrect.
     */
    String[] unencryptedJsonToArgs(String filePath, boolean deleteAfter) throws IOException;
}
