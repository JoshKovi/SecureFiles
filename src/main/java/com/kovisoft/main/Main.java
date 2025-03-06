package com.kovisoft.main;

import com.kovisoft.exposed.SecureMethods;
import com.kovisoft.internal.SecureMethodsImpl;

import java.io.Console;
import java.util.Arrays;
import java.util.Scanner;

public class Main {

    private static final String PASSWORD = "Password: ";
    private static final String FILE_PATH = "Enter full file path: ";
    private static final String MODE = "Enter mode (encrypt, decrypt, jsonDecrypt, simpleRead): ";
    
    public static void main(String[] args){
        StartFlags flags = new StartFlags(args);
        if(flags.filePath == null || flags.password == null || flags.mode == null){
            getInputIfNecessary(flags);
        }

        SecureMethods sm = new SecureMethodsImpl();
        try{
            if(flags.mode == StartFlags.Mode.encrypt){
                System.out.println(sm.encryptFile(flags.filePath, flags.password, flags.deleteAfter));
            } else if (flags.mode == StartFlags.Mode.decrypt) {
                System.out.println(sm.decryptFile(flags.filePath, flags.password, flags.deleteAfter));
            } else if (flags.mode == StartFlags.Mode.jsonDecrypt) {
                String[] fileArgs = sm.decryptJsonToArgs(flags.filePath, flags.password, flags.deleteAfter);
                System.out.println(Arrays.toString(fileArgs));
            } else if (flags.mode == StartFlags.Mode.simpleRead) {
                String[] fileArgs = sm.unencryptedJsonToArgs(flags.filePath, flags.deleteAfter);
                System.out.println(Arrays.toString(fileArgs));
            }
        } catch (Exception e){
            System.out.println("Exception occured during SecureMethods run! " +  e.getMessage());
            e.printStackTrace();
        }

    }

    private static void getInputIfNecessary(StartFlags flags){
        Console console = System.console();
        if(console == null) useScanner(flags);
        else useConsole(flags, console);
    }

    private static void useConsole(StartFlags flags, Console console) {
        if(flags.mode == null){
            System.out.print(MODE);
            flags.mode = StartFlags.Mode.getFromString(console.readLine());
        }
        if(flags.filePath == null){
            System.out.print(FILE_PATH);
            flags.filePath = console.readLine();
        }
        if(flags.password == null && flags.mode != StartFlags.Mode.simpleRead){
            System.out.print(PASSWORD);
            flags.password = console.readLine();
        }
    }

    private static void useScanner(StartFlags flags) {
        try(Scanner scanner = new Scanner(System.in)){
            if(flags.mode == null){
                System.out.print(MODE);
                flags.mode = StartFlags.Mode.getFromString(scanner.nextLine());
            }
            if(flags.filePath == null){
                System.out.print(FILE_PATH);
                flags.filePath = scanner.nextLine();
            }
            if(flags.password == null && flags.mode != StartFlags.Mode.simpleRead){
                System.out.print(PASSWORD);
                flags.password = scanner.nextLine();
            }
        } catch(Exception e){
            System.out.println("Exception has occurred! " + e.getMessage());
            e.printStackTrace();
        }
    }
}
