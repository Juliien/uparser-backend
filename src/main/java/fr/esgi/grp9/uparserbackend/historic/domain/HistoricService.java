package fr.esgi.grp9.uparserbackend.historic.domain;


import org.springframework.stereotype.Service;

@Service
public class HistoricService {

    private final HistoricRepository historicRepository;

    public HistoricService(HistoricRepository historicRepository) {
        this.historicRepository = historicRepository;
    }

    public Historic insertCode(final Historic historic) {
        return this.historicRepository.save(Historic.builder()
                .language(historic.getLanguage())
                .code(historic.getCode())
                .build());
    }
}
