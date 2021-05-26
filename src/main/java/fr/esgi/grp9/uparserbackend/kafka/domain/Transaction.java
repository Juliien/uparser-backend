package fr.esgi.grp9.uparserbackend.kafka.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Transaction {
    private String id;
    private String userId;
    private String fileContent;
    private String code;
}
