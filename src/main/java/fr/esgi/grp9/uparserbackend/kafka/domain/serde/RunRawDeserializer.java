package fr.esgi.grp9.uparserbackend.kafka.domain.serde;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.esgi.grp9.uparserbackend.run.domain.RunRaw;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

public class RunRawDeserializer implements Deserializer {
    ObjectMapper object = new ObjectMapper();

    @Override
    public void configure(Map configs, boolean isKey) {
        Deserializer.super.configure(configs, isKey);
    }

    @Override
    public Object deserialize(String arg0, byte[] arg1) {
        ObjectMapper mapper = new ObjectMapper();
        RunRaw runRaw = null;
        try {
            runRaw = mapper.readValue(arg1, RunRaw.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return runRaw;
    }

    @Override
    public Object deserialize(String topic, Headers headers, byte[] data) {
        return Deserializer.super.deserialize(topic, headers, data);
    }

    @Override
    public void close() {
        Deserializer.super.close();
    }
}
