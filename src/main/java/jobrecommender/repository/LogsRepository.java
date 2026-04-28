package jobrecommender.repository;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.List;

@Repository
public class LogsRepository {
    private final Path filePath;

    public LogsRepository(@Value("${app.logs.path}") String filePathString) {
        this.filePath = Paths.get(filePathString);
    }

    public void logCommand(String command) throws IOException {
        if (filePath.getParent() != null) {
            Files.createDirectories(filePath.getParent());
        }

        Files.writeString(filePath, command + "\n", StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    public List<String> getCommands() throws IOException {
        if (!Files.exists(filePath)) {
            return Collections.emptyList();
        }

        return Files.readAllLines(filePath);
    }
}
