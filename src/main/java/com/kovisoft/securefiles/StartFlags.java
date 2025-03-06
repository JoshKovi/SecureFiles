package com.kovisoft.securefiles;

public class StartFlags {
    public enum Mode {
        encrypt{},
        decrypt{},
        jsonDecrypt{},
        simpleRead{};

        public static Mode getFromString(String mode){
            if(mode.equalsIgnoreCase("encrypt")) return Mode.encrypt;
            else if(mode.equalsIgnoreCase("decrypt")) return Mode.decrypt;
            else if(mode.equalsIgnoreCase("jsonDecrypt")) return Mode.jsonDecrypt;
            else if(mode.equalsIgnoreCase("simpleRead")) return Mode.simpleRead;
            else return null;
        }
    }

    protected Mode mode;
    protected boolean deleteAfter = false;
    protected String filePath = null;
    protected String password = null;

    public StartFlags(String[] args){
        for(int i = 0; i < args.length; i++){
            String arg = args[i];
            if(arg.startsWith("-")){
                i = assignFlag(arg.substring(1).toLowerCase(), i, args);
            }
        }
    }

    private int assignFlag(String arg, int i, String[] args) {
        return switch(arg){
            case "mode", "m" -> {
                mode = Mode.getFromString(args[i + 1]);
                yield i + 1;
            }
            case "delete", "d" -> {
                deleteAfter = true;
                yield i;
            }
            case "file", "path", "filepath", "fp" -> {
                filePath = args[i + 1];
                yield i + 1;
            }
            case "pass", "password", "p" -> {
                password = args[i + 1];
                yield i + 1;
            }
            default -> i;
        };
    }
}
