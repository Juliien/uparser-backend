package fr.esgi.grp9.uparserbackend.code.service.history;

import fr.esgi.grp9.uparserbackend.code.domain.history.CodeHistory;
import fr.esgi.grp9.uparserbackend.code.domain.history.CodeHistoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CodeHistoryService implements ICodeHistoryService {
    private final CodeHistoryRepository codeHistoryRepository;

    public CodeHistoryService(CodeHistoryRepository codeHistoryRepository) {
        this.codeHistoryRepository = codeHistoryRepository;
    }

    @Override
    public List<CodeHistory> getUserCodeHistory(String userId) {
        return this.codeHistoryRepository.findAllByUserId(userId);
    }

    @Override
    public CodeHistory addCodeHistory(CodeHistory code) {
        return this.codeHistoryRepository.save(code);
    }

    @Override
    public void deleteCodeHistory(String id) {
        this.codeHistoryRepository.deleteById(id);
    }

    @Override
    public void deleteUserCodeHistory(String userId) {
        this.codeHistoryRepository.deleteAllByUserId(userId);
    }
}
