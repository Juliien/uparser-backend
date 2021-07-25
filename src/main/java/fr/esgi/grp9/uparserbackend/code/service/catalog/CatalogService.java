package fr.esgi.grp9.uparserbackend.code.service.catalog;

import fr.esgi.grp9.uparserbackend.code.domain.Code;
import fr.esgi.grp9.uparserbackend.code.domain.CodeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CatalogService implements ICatalogService {
    private final CodeRepository codeRepository;

    public CatalogService(CodeRepository codeRepository) {
        this.codeRepository = codeRepository;
    }

    @Override
    public List<Code> getCatalog() {
        return this.codeRepository.findAll()
                .stream()
                .filter(Code::isEnable)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Code> getCatalogItem(String id) {
        return this.codeRepository.findById(id);
    }
}
