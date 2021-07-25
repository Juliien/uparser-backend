package fr.esgi.grp9.uparserbackend.kafka.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.esgi.grp9.uparserbackend.kafka.domain.KafkaTransaction;
import fr.esgi.grp9.uparserbackend.run.domain.RunRaw;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public interface IKafkaService {
    void sendProducerRecord(KafkaTransaction kafkaTransaction) throws ExecutionException, InterruptedException, TimeoutException;
    RunRaw seekForRunnerResults(String runId) throws JsonProcessingException;
    Properties propertiesProvider();
}
