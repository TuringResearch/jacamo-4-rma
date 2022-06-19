package jason.architecture;

import com.github.f4b6a3.uuid.UuidCreator;
import jason.infra.centralised.RunCentralisedMAS;

import java.io.*;
import java.nio.file.Files;

public class CommunicatorUtils {

    public static final String COMMUNICATOR_PREAMBLE = "fffe";

    public static String int2hex(int v) {
        String stringOne = Integer.toHexString(v);
        if (v < 16) {
            stringOne = "0" + stringOne;
        }
        return stringOne;
    }

    public static String getUUIDFromAlias(String alias) {
        return UuidCreator.getNameBasedMd5(alias).toString();
    }

    /**
     * Search by an UUID if exists.
     *
     * @return UUID or empty text.
     */
    public static String getUUIDFromFile(Communicator communicator) throws IOException {
        File file = new File(".device");
        String uuid;
        if (!file.exists()) {
            String alias = RunCentralisedMAS.getRunner().getProject().getSocName() + "_" + communicator.getAgName();
            uuid = getUUIDFromAlias(alias);
            setUUIDToFile(uuid);
            return uuid;
        }

        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            uuid = bufferedReader.readLine();
            return uuid;
        } finally {
            inputStream.close();
        }
    }

    /**
     * Create a file if not exists and write a new UUID value.
     *
     * @param uuid UUID.
     * @throws IOException
     */
    public static void setUUIDToFile(String uuid) throws IOException {
        File file = new File(".device");
        if (!file.exists()) {
            Files.createFile(file.toPath());
        }
        FileWriter writer = new FileWriter(".device");
        writer.write(uuid);
        writer.close();
    }

    public static Communicator getCommunicatorArch(AgArch currentArch) {
        while (!(currentArch instanceof Communicator)) {
            currentArch = currentArch.getNextAgArch();
        }
        return (Communicator) currentArch;
    }

    public static int hex2int(int x, int y) {
        int converted = x + (y * 16);
        return converted;
    }

    public static int char2int(char charValue) {
        int intValue = 0;
        switch (charValue) {
            case '1':
                intValue = 1;
                break;
            case '2':
                intValue = 2;
                break;
            case '3':
                intValue = 3;
                break;
            case '4':
                intValue = 4;
                break;
            case '5':
                intValue = 5;
                break;
            case '6':
                intValue = 6;
                break;
            case '7':
                intValue = 7;
                break;
            case '8':
                intValue = 8;
                break;
            case '9':
                intValue = 9;
                break;
            case 'a':
                intValue = 10;
                break;
            case 'b':
                intValue = 11;
                break;
            case 'c':
                intValue = 12;
                break;
            case 'd':
                intValue = 13;
                break;
            case 'e':
                intValue = 14;
                break;
            case 'f':
                intValue = 15;
                break;
        }
        return intValue;
    }
}
