package fr.esgi.grp9.uparserbackend.code.domain.catalog;

import fr.esgi.grp9.uparserbackend.code.domain.Code;

import java.util.List;
import java.util.Optional;

public interface CatalogService {
    List<Code> getCatalog();
    Optional<Code> getCatalogItem(String id);
}
