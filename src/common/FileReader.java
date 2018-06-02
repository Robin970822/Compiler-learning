package common;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileReader {

    /**
     * 读取文件内容返回
     *
     * @param filename 文件读经
     * @return 返回读取的字符串
     */
    public static List<String> readFile(String filename) {
        List<String> stringList = new ArrayList<>();
        try {
            File file = new File(filename);
            if (file.isFile() && file.exists()) {
                InputStreamReader is = new InputStreamReader(new FileInputStream(file));
                BufferedReader br = new BufferedReader(is);
                String line;
                while ((line = br.readLine()) != null) {
                    stringList.add(line + '\n');
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringList;
    }
}
