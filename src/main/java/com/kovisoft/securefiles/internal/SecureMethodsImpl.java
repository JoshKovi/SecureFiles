package com.kovisoft.securefiles.internal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kovisoft.securefiles.exposed.SecureMethods;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;

public class SecureMethodsImpl implements SecureMethods {

    private final static int BYTE_LENGTH = 16;
    private final static int ITER_COUNT = 65536;
    private final static int KEY_LENGTH = 256;
    private final static String KEY_FACTORY_ALGO = "PBKDF2WithHmacSha256";
    private final static String KEY_SPEC_ALGO = "AES";
    private final static String CIPHER_TRANSFORMATION = "AES/CBC/PKCS5Padding";

    @Override
    public String encryptFile(String filePath, String password, boolean deleteUnencrypted)
            throws IOException, GeneralSecurityException {
        Path path = Paths.get(filePath);
        byte[] fileContent = Files.readAllBytes(path);

        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[BYTE_LENGTH];
        byte[] iv = new byte[BYTE_LENGTH];
        random.nextBytes(salt);
        random.nextBytes(iv);

        SecretKey secretKey = getSecretKey(salt, password);

        Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));

        byte[] encryptedData = cipher.doFinal(fileContent);

        String encodedSalt = Base64.getEncoder().encodeToString(salt);
        String encodedIV = Base64.getEncoder().encodeToString(cipher.getIV());
        String encodedEncryptedData = Base64.getEncoder().encodeToString(encryptedData);

        Files.write(Paths.get(filePath + ".enc"),
                (encodedSalt + "::" + encodedIV + "::" + encodedEncryptedData).getBytes());

        if(deleteUnencrypted){
            Files.delete(path);
        }
        return filePath + ".enc";
    }

    @Override
    public String decryptFile(String filePath, String password, boolean deleteEncrypted)
            throws IOException, GeneralSecurityException {
        Path path = Paths.get(filePath);
        byte[] decryptedData = decryptContents(path, password);

        String decryptedPath = filePath.replace(".enc", "");
        Files.write(Paths.get(decryptedPath), decryptedData);
        if(deleteEncrypted){
            Files.delete(path);
        }
        return decryptedPath;
    }

    @Override
    @SuppressWarnings("unchecked")
    public String[] decryptJsonToArgs(String filePath, String password, boolean deleteEncrypted)
            throws IOException, GeneralSecurityException{
        byte[] decryptedData = decryptContents(Paths.get(filePath), password);
        String resultString = new String(decryptedData, StandardCharsets.UTF_8);
        return convertJsonStringToArgs(resultString);
    }

    @Override
    public String[] unencryptedJsonToArgs(String filePath, boolean deleteAfter) throws IOException {
        Path path = Paths.get(filePath);
        byte[] fileContent = Files.readAllBytes(path);
        String contents = new String(fileContent, StandardCharsets.UTF_8);
        String[] args = convertJsonStringToArgs(contents);
        if(deleteAfter){
            Files.delete(path);
        }
        return args;
    }

    private String[] convertJsonStringToArgs(String content) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        HashMap<String, Object> contentMap = (HashMap<String, Object>) mapper.readValue(content, HashMap.class);
        ArrayList<String> args = new ArrayList<>();
        contentMap.forEach((key, value) -> {
            args.add("-" + key);
            args.add(String.valueOf(value));
        });
        return args.toArray(new String[0]);
    }

    private byte[] decryptContents(Path path, String password)
        throws IOException, GeneralSecurityException{
        byte[] fileContent = Files.readAllBytes(path);
        String combined = new String(fileContent);
        String[] parts = combined.split("::");

        if(parts.length != 3){
            throw new IOException("The encrypted file is corrupted!");
        }

        byte[] salt = Base64.getDecoder().decode(parts[0]);
        byte[] iv = Base64.getDecoder().decode(parts[1]);
        byte[] encryptedData = Base64.getDecoder().decode(parts[2]);
        SecretKey secretKey = getSecretKey(salt, password);

        Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
        return cipher.doFinal(encryptedData);
    }

    private SecretKey getSecretKey(byte[] salt, String password) throws GeneralSecurityException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance(KEY_FACTORY_ALGO);
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITER_COUNT, KEY_LENGTH);
        SecretKey tmp = factory.generateSecret(spec);
        return new SecretKeySpec(tmp.getEncoded(), KEY_SPEC_ALGO);
    }


}
