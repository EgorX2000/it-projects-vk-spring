package jobrecommender.service;

import jobrecommender.repository.LogsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class LogsService {
    private final LogsRepository repo;

    @Autowired
    public LogsService(LogsRepository repo) {
        this.repo = repo;
    }

    public void logCommand(String command) throws IOException {
        repo.logCommand(command);
    }

    public List<String> getCommands() throws IOException {
        return repo.getCommands();
    }
}
