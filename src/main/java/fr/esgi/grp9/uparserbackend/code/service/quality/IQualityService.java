package fr.esgi.grp9.uparserbackend.code.service.quality;

import fr.esgi.grp9.uparserbackend.code.domain.Code;

import java.security.NoSuchAlgorithmException;

public interface IQualityService {
    Code isCodePlagiarism(Code code) throws NoSuchAlgorithmException;
}
