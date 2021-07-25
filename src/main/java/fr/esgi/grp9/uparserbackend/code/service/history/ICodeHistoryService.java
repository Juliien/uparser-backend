package fr.esgi.grp9.uparserbackend.code.service.history;


import fr.esgi.grp9.uparserbackend.code.domain.history.CodeHistory;

import java.util.List;

public interface ICodeHistoryService {
    List<CodeHistory> getUserCodeHistory(String userId);
    CodeHistory addCodeHistory(CodeHistory code);
    void deleteCodeHistory(String id);
    void deleteUserCodeHistory(String userId);
}
