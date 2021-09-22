package com.wild.medicalTermDissector.medicalTerms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class MedTermService {

  private final MedTermRepository medTermRepository;

  @Autowired
  public MedTermService(MedTermRepository medTermRepository) {
    this.medTermRepository = medTermRepository;
  }

  public List<MedTerm> getMedTerm() {
    return medTermRepository.findAll();
  }

  public Optional<MedTerm> getMedTerm(String name) {
    return medTermRepository.findByName(name);
  }

  public void addMedTerm(MedTerm medTerm) {
    Optional<MedTerm> medTermOptional = medTermRepository.findByName(medTerm.getName());
    if (medTermOptional.isPresent()) {
      throw new IllegalStateException("Medical Term already exists.");
    }
    medTermRepository.save(medTerm);
  }

  public void updateMedTerm(Long termId, String name, String definition) {
    MedTerm medTerm = medTermRepository.findById(termId).orElseThrow(() -> new IllegalStateException("MedTerm with termId " + termId + " does not exist."));
    MedTerm newMedTerm = new MedTerm(termId, medTerm.getName(), definition);
    if (name != null && name.length() > 0 && !Objects.equals(medTerm.getName(), name)) {
      newMedTerm.setName(name);
    }
    newMedTerm.setDefinition(definition);
    medTermRepository.save(newMedTerm);
  }

  public void deleteMedTerm(Long termId) {
    final boolean exists = medTermRepository.existsById(termId);
    if (!exists) {
      throw new IllegalStateException("MedTerm with id " + termId + " does not exist.");
    }
    medTermRepository.deleteById(termId);
  }

  public List<MedTerm> getMedTerms(String letters) {
    letters += "%";
    return medTermRepository.findByNameStartsWith(letters);
  }

  public String generateIndexPage() {
    return null;
  }

}
