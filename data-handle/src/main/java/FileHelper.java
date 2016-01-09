import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileHelper {
    public static final String DEFAULT_ENCODING = "utf-8";

    public static List<String> read(String filePath) {
        return read(filePath, DEFAULT_ENCODING);
    }

    public static List<String> read(File file, String encoding) {
        if (encoding == null) {
            encoding = DEFAULT_ENCODING;
        }

        ArrayList<String> results = new ArrayList<String>();
        try {
            if (file.isFile() && file.exists()) {
                InputStreamReader reader = new InputStreamReader(new FileInputStream(file), encoding);
                BufferedReader bfr = new BufferedReader(reader);
                String line;
                StringBuffer content = new StringBuffer();
                while ((line = bfr.readLine()) != null) {
                    if (!(line.length() == 0)){
                        content.append(line);
                        continue;
                    }
                    results.add(content.toString());
                    content.delete(0, content.length());
                }

                if (content.length() != 0) {
                    results.add(content.toString());
                }

                reader.close();
            } else {
                System.err.println("Error: " + FileHelper.class.getName() +
                        "||" + file.getAbsolutePath() + " No such file!");
                System.exit(-1);
            }
        } catch (IOException e) {
            System.err.println("Error: reading file.." + e );
            System.exit(-2);
        }
        return results;
    }

    public static List<String> read(String filePath, String encoding) {
        return read(new File(filePath), encoding);
    }

    public static void write(List<String> content, String filePath) {
        File file = new File(filePath);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter clean_writer = new FileWriter(file, false);
            clean_writer.write("");
            clean_writer.close();

            FileWriter writer = new FileWriter(file, true);
            for (int i = 0; i < content.size(); i++) {
                writer.write(content.get(i));
                writer.write(System.getProperty("line.separator"));
                writer.write(System.getProperty("line.separator"));
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            System.err.println("Error: write file.." + e );
            System.exit(-3);
        }
    }
}
