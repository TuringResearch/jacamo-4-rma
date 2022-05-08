package jason.architecture;

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

    /**
     * Search by an UUID if exists.
     *
     * @return UUID or empty text.
     */
    public static String getUUIDFromFile() throws IOException {
        File file = new File(".device");
        if (!file.exists()) {
            return "";
        }

        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            final String uuid;
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
}
