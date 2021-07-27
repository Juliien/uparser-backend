package fr.esgi.grp9.uparserbackend.code.service.quality;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.esgi.grp9.uparserbackend.code.domain.Code;
import fr.esgi.grp9.uparserbackend.code.domain.parser.ParserResponse;
import fr.esgi.grp9.uparserbackend.kafka.domain.KafkaTransaction;
import org.json.JSONException;

import java.security.NoSuchAlgorithmException;

public interface IQualityService {
    Code isCodePlagiarism(Code code) throws NoSuchAlgorithmException;
    Code testCodeQuality(Code code);
    ParserResponse parseFile(KafkaTransaction kafkaTransaction) throws JsonProcessingException, JSONException;
}
