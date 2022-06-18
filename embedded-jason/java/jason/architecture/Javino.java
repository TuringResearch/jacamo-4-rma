////
//// Source code recreated from a .class file by IntelliJ IDEA
//// (powered by FernFlower decompiler)
////
//
//package jason.architecture;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.PrintWriter;
//
//public class Javino {
//    private final String version = "stable 1.1";
//    private static final String staticversion = "stable 1.1";
//    private String pythonPlataform;
//    private String finalymsg = null;
//
//    public Javino() {
//        this.load();
//    }
//
//    public Javino(String pathPython) {
//        this.load();
//        if (this.whoSO() == 1) {
//            this.pythonPlataform = pathPython + "\\python.exe";
//        } else {
//            this.pythonPlataform = pathPython + "/python";
//        }
//
//    }
//
//    private void load() {
//        this.unlock();
//        System.out.println("[JAVINO] Using version " + "stable 1.1" + " CEFET/RJ, Brazil");
//        this.setpath(this.whoSO());
//        this.createPythonFile();
//    }
//
//    private void setpath(int sp_OS) {
//        if (sp_OS == 1) {
//            this.pythonPlataform = "C:\\Python27\\python.exe";
//        } else {
//            this.pythonPlataform = "/usr/bin/python";
//        }
//
//    }
//
//    private int whoSO() {
//        String os = System.getProperty("os.name");
//        return os.substring(0, 1).equals("W") ? 1 : 0;
//    }
//
//    private void unlock() {
//        File pasta = new File(".");
//        File[] arquivos = pasta.listFiles();
//        File[] var6 = arquivos;
//        int var5 = arquivos.length;
//
//        for(int var4 = 0; var4 < var5; ++var4) {
//            File arquivo = var6[var4];
//            if (arquivo.getName().endsWith("lock") || arquivo.getName().endsWith("py")) {
//                arquivo.delete();
//            }
//        }
//
//    }
//
//    private String lockFileName(String PORT) {
//        PORT = PORT + ".lock";
//        PORT = PORT.replace("/", "_");
//        PORT = PORT.replace("1", "i");
//        PORT = PORT.replace("2", "ii");
//        PORT = PORT.replace("3", "iii");
//        PORT = PORT.replace("4", "iv");
//        PORT = PORT.replace("5", "v");
//        PORT = PORT.replace("6", "vi");
//        PORT = PORT.replace("7", "vii");
//        PORT = PORT.replace("8", "viii");
//        PORT = PORT.replace("9", "ix");
//        PORT = PORT.replace("0", "x");
//        return PORT;
//    }
//
//    private boolean portLocked(String lc_PORT) {
//        File file = new File(this.lockFileName(lc_PORT));
//        boolean busy;
//        if (file.exists()) {
//            busy = true;
//            System.out.println("[JAVINO] The port " + lc_PORT + " is busy");
//        } else {
//            busy = false;
//        }
//
//        return busy;
//    }
//
//    private void lockPort(boolean lock, String PORT) {
//        try {
//            File file = new File(this.lockFileName(PORT));
//            if (lock) {
//                file.createNewFile();
//            } else {
//                file.delete();
//            }
//        } catch (IOException var4) {
//            var4.printStackTrace();
//            System.exit(1);
//        }
//
//    }
//
//    public boolean sendCommand(String PORT, String MSG) {
//        boolean result;
//        if (this.portLocked(PORT)) {
//            result = false;
//        } else {
//            this.lockPort(true, PORT);
//            String operation = "command";
//            String[] command = new String[]{this.pythonPlataform, "javython.py", operation, PORT, this.preparetosend(MSG)};
//            ProcessBuilder pBuilder = new ProcessBuilder(command);
//            pBuilder.redirectErrorStream(true);
//
//            try {
//                Process p = pBuilder.start();
//                p.waitFor();
//                BufferedReader saida = new BufferedReader(new InputStreamReader(p.getInputStream()));
//                if (p.exitValue() == 0) {
//                    result = true;
//                    this.lockPort(false, PORT);
//                } else {
//                    String line = null;
//
//                    String out;
//                    for(out = ""; (line = saida.readLine()) != null; out = out + line) {
//                    }
//
//                    System.out.println("[JAVINO] Fatal error! [" + out + "]");
//                    result = false;
//                    this.lockPort(false, PORT);
//                }
//            } catch (InterruptedException | IOException var11) {
//                System.out.println("[JAVINO] Error on commnad execution");
//                var11.printStackTrace();
//                result = false;
//                this.lockPort(false, PORT);
//            }
//        }
//
//        return result;
//    }
//
//    public boolean listenArduino(String PORT) {
//        boolean result;
//        if (this.portLocked(PORT)) {
//            result = false;
//        } else {
//            this.lockPort(true, PORT);
//            String operation = "listen";
//            String[] command = new String[]{this.pythonPlataform, "javython.py", operation, PORT, "listen"};
//            ProcessBuilder pBuilder = new ProcessBuilder(command);
//            pBuilder.redirectErrorStream(true);
//
//            try {
//                Process p = pBuilder.start();
//                p.waitFor();
//                BufferedReader read_to_array = new BufferedReader(new InputStreamReader(p.getInputStream()));
//                if (p.exitValue() == 0) {
//                    result = this.setArryMsg(read_to_array);
//                    this.lockPort(false, PORT);
//                } else {
//                    String line = null;
//
//                    String out;
//                    for(out = ""; (line = read_to_array.readLine()) != null; out = out + line) {
//                    }
//
//                    System.out.println("[JAVINO] Fatal error! [" + out + "]");
//                    result = false;
//                    this.lockPort(false, PORT);
//                }
//            } catch (InterruptedException | IOException var10) {
//                System.out.println("[JAVINO] Error on listen");
//                var10.printStackTrace();
//                result = false;
//                this.lockPort(false, PORT);
//            }
//        }
//
//        return result;
//    }
//
//    public boolean requestData(String PORT, String MSG) {
//        boolean result;
//        if (this.portLocked(PORT)) {
//            result = false;
//        } else {
//            this.lockPort(true, PORT);
//            String operation = "request";
//            String[] command = new String[]{this.pythonPlataform, "javython.py", operation, PORT, this.preparetosend(MSG)};
//            ProcessBuilder pBuilder = new ProcessBuilder(command);
//            pBuilder.redirectErrorStream(true);
//
//            try {
//                Process p = pBuilder.start();
//                p.waitFor();
//                BufferedReader read_to_array = new BufferedReader(new InputStreamReader(p.getInputStream()));
//                if (p.exitValue() == 0) {
//                    result = this.setArryMsg(read_to_array);
//                    this.lockPort(false, PORT);
//                } else {
//                    String line = null;
//
//                    String out;
//                    for(out = ""; (line = read_to_array.readLine()) != null; out = out + line) {
//                    }
//
//                    System.out.println("[JAVINO] Fatal error! [" + out + "]");
//                    result = false;
//                    this.lockPort(false, PORT);
//                }
//            } catch (InterruptedException | IOException var11) {
//                System.out.println("[JAVINO] Error on listen");
//                var11.printStackTrace();
//                result = false;
//                this.lockPort(false, PORT);
//            }
//        }
//
//        return result;
//    }
//
//    public String getData() {
//        String out = this.finalymsg;
//        this.finalymsg = null;
//        return out;
//    }
//
//    private boolean setArryMsg(BufferedReader reader) {
//        String line = null;
//        String out = new String();
//
//        try {
//            while((line = reader.readLine()) != null) {
//                out = out + line;
//            }
//        } catch (IOException var5) {
//            System.out.println("[JAVINO] Error at message processing");
//            return false;
//        }
//
//        return this.preamble(out.toCharArray());
//    }
//
//    private String char2string(char[] in, int sizein) {
//        int newsize = sizein - 6;
//        char[] out = new char[newsize];
//        int cont = 0;
//
//        for(int i = 6; i < sizein; ++i) {
//            out[cont] = in[i];
//            ++cont;
//        }
//
//        return String.valueOf(out);
//    }
//
//    private void setfinalmsg(String s_msg) {
//        this.finalymsg = s_msg;
//    }
//
//    private boolean preamble(char[] pre_arraymsg) {
//        try {
//            char p1 = pre_arraymsg[0];
//            char p2 = pre_arraymsg[1];
//            char p3 = pre_arraymsg[2];
//            char p4 = pre_arraymsg[3];
//            if (p1 == 'f' && p2 == 'f' && p3 == 'f' && p4 == 'e' && this.monitormsg(this.forInt(pre_arraymsg[5]), this.forInt(pre_arraymsg[4]), pre_arraymsg.length)) {
//                this.setfinalmsg(this.char2string(pre_arraymsg, pre_arraymsg.length));
//                return true;
//            } else {
//                char[] newArrayMsg = new char[pre_arraymsg.length - 1];
//
//                for(int cont = 0; cont < newArrayMsg.length; ++cont) {
//                    newArrayMsg[cont] = pre_arraymsg[cont + 1];
//                }
//
//                return this.preamble(newArrayMsg);
//            }
//        } catch (Exception var8) {
//            System.out.println("[JAVINO] Invalid message");
//            var8.printStackTrace();
//            return false;
//        }
//    }
//
//    private boolean monitormsg(int x, int y, int m_size) {
//        int converted = x + y * 16;
//        int size_of_msg = m_size - 6;
//        return converted == size_of_msg;
//    }
//
//    private int forInt(char v) {
//        int vI = 0;
//        switch (v) {
//            case '1':
//                vI = 1;
//                break;
//            case '2':
//                vI = 2;
//                break;
//            case '3':
//                vI = 3;
//                break;
//            case '4':
//                vI = 4;
//                break;
//            case '5':
//                vI = 5;
//                break;
//            case '6':
//                vI = 6;
//                break;
//            case '7':
//                vI = 7;
//                break;
//            case '8':
//                vI = 8;
//                break;
//            case '9':
//                vI = 9;
//                break;
//            case 'a':
//                vI = 10;
//                break;
//            case 'b':
//                vI = 11;
//                break;
//            case 'c':
//                vI = 12;
//                break;
//            case 'd':
//                vI = 13;
//                break;
//            case 'e':
//                vI = 14;
//                break;
//            case 'f':
//                vI = 15;
//        }
//
//        return vI;
//    }
//
//    private String preparetosend(String msg) {
//        msg = "fffe" + this.int2hex(msg.length()) + msg;
//        return msg;
//    }
//
//    private String int2hex(int v) {
//        String stringOne = Integer.toHexString(v);
//        if (v < 16) {
//            stringOne = "0" + stringOne;
//        }
//
//        return stringOne;
//    }
//
//    private void createPythonFile() {
//        try {
//            FileWriter arq = new FileWriter("javython.py");
//            PrintWriter gravarArq = new PrintWriter(arq);
//            gravarArq.printf("import sys\n");
//            gravarArq.printf("import serial\n");
//            gravarArq.printf("OP=sys.argv[1]\n");
//            gravarArq.printf("PORT=sys.argv[2]\n");
//            gravarArq.printf("MSG=sys.argv[3]\n");
//            gravarArq.printf("try:\n");
//            gravarArq.printf("\tcomm = serial.Serial(PORT, 9600)\n");
//            gravarArq.printf("\tcomm.open\n");
//            gravarArq.printf("\tcomm.isOpen\n");
//            gravarArq.printf("\tif(OP=='command'):\n");
//            gravarArq.printf("\t\tcomm.write(MSG)\n");
//            gravarArq.printf("\tif(OP=='request'):\n");
//            gravarArq.printf("\t\tcomm.write(MSG)\n");
//            gravarArq.printf("\t\tprint (comm.readline())\n");
//            gravarArq.printf("\tif(OP=='listen'):\n");
//            gravarArq.printf("\t\tprint (comm.readline())\n");
//            gravarArq.printf("\tcomm.close\n");
//            gravarArq.printf("except:\n");
//            gravarArq.printf("\tprint (\"Error on conect \"+PORT)\n");
//            gravarArq.printf("\tsys.exit(1)\n");
//            arq.flush();
//            arq.close();
//        } catch (IOException var3) {
//            var3.printStackTrace();
//            System.exit(1);
//        }
//
//    }
//
//    public static void main(String[] args) {
//        try {
//            String type = args[0];
//            if (type.equals("--help")) {
//                System.out.println("java -jar javino.jar [TYPE] [PORT] [MSG] [PythonPath]");
//                System.out.println("\n[TYPE] \n request -- send a request to Arduino, wait answer \n command -- send a command to Arduino, without wait answer");
//                System.out.println("\n[PORT]\n Set communication serial port\n example: \t COM3 - For Windows\n \t\t /dev/ttyACM0 - For Linux");
//                System.out.println("\n[MSG]\n Message for Arduino-side\n example: \t \"Hello Arduino!\"");
//                System.out.println("\n[PythonPath]\n Set Path of Python in your system. This is a optional value.example: \n\t \"C:\\\\Python27\" - For a System Windows\n\t \"/usr/bin\" - For a System Linux");
//            } else {
//                String port = args[1];
//                String msg = args[2];
//                String path = null;
//
//                try {
//                    path = args[3];
//                } catch (Exception var6) {
//                    System.out.println("[JAVINO] Using default path 'C:\\Python27' or '/usr/bin'");
//                }
//
//                Javino j;
//                if (path == null) {
//                    j = new Javino();
//                } else {
//                    j = new Javino(path);
//                }
//
//                if (type.equals("command")) {
//                    if (!j.sendCommand(port, msg)) {
//                        System.exit(1);
//                    }
//                } else if (type.equals("request")) {
//                    if (j.requestData(port, msg)) {
//                        System.out.println(j.getData());
//                    } else {
//                        System.exit(1);
//                    }
//                }
//            }
//        } catch (Exception var7) {
//            System.out.println("[JAVINO] Using version stable 1.1 CEFET/RJ, Brazil");
//            System.out.println("\tTo use Javino, look the User Manual at http://javino.sf.net");
//            System.out.println("For more information try: \n\t java -jar javino.jar --help");
//        }
//
//    }
//}
