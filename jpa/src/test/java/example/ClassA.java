package example;

import lombok.Data;
import org.junit.Ignore;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author CJ
 */
@Data
@Ignore
public class ClassA implements Serializable {
    private static final long serialVersionUID = 5767831974050072791L;
    Path path = Paths.get("src", "test", "resources", "data1").toAbsolutePath();
    Pattern pattern = Pattern.compile(".*classdesc serialVersionUID = (.+), local class serialVersionUID.*");
    private String name;

    @Test
    public void storeIt() throws IOException {

        name = UUID.randomUUID().toString();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try (ObjectOutputStream stream = new ObjectOutputStream(buffer)) {
            stream.writeObject(this);
            stream.flush();
        }

        Files.write(path, buffer.toByteArray(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
    }

    @Test
    public void go() throws IOException, ClassNotFoundException {
        long x = readSerialVersionUID(new FileInputStream(path.toFile()));
        System.out.println(x);
    }

    /**
     * @param stream 数据流
     * @return 读到的 serialVersionUID;如果是0 说明刚好和当前类serialVersionUID 一致
     * @throws ClassNotFoundException 特定类没有被找到
     * @throws IOException            如果丢进的数据流是{@link java.io.ByteArrayInputStream}是绝对不会发生的
     * @throws IllegalStateException  无法解析 serialVersionUID
     */
    private long readSerialVersionUID(InputStream stream) throws ClassNotFoundException, IOException {
        try (ObjectInputStream inputStream = new ObjectInputStream(stream)) {
            try {
                inputStream.readObject();
            } catch (InvalidClassException x) {
                Matcher matcher = pattern.matcher(x.getMessage());
                if (matcher.matches()) {
                    return Long.parseLong(matcher.group(1));
                } else
                    throw new IllegalStateException("未知的serialVersionUID");
            }
        }
        return 0;
    }
}
