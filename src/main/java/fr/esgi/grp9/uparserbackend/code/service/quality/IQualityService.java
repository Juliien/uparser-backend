package fr.esgi.grp9.uparserbackend.code.service.quality;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.esgi.grp9.uparserbackend.code.domain.Code;
import fr.esgi.grp9.uparserbackend.kafka.domain.KafkaTransaction;

import java.security.NoSuchAlgorithmException;

public interface IQualityService {
    Code isCodePlagiarism(Code code) throws NoSuchAlgorithmException;
    Code testCodeQuality(Code code);
    String parseFile(KafkaTransaction kafkaTransaction) throws JsonProcessingException;
}
