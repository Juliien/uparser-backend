package fr.esgi.grp9.uparserbackend.code.service.quality;

import fr.esgi.grp9.uparserbackend.code.domain.quality.HashCode;
import fr.esgi.grp9.uparserbackend.code.domain.quality.HashCodeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HashCodeService {
    private final HashCodeRepository hashCodeRepository;

    public HashCodeService(HashCodeRepository hashCodeRepository) {
        this.hashCodeRepository = hashCodeRepository;
    }

    public HashCode addHashCode(HashCode hashCode){
        return this.hashCodeRepository.save(hashCode);
    }

    public List<HashCode> getHashCodes(){
        return this.hashCodeRepository.findAll();
    }
}
