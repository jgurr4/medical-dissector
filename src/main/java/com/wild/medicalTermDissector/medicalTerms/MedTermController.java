package com.wild.medicalTermDissector.medicalTerms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/term")
public class MedTermController {
  private final MedTermService medTermService;

  @Autowired
  public MedTermController(MedTermService medTermService) {
    this.medTermService = medTermService;
  }

  @GetMapping
  public List<MedTerm> getMedTerms() { return medTermService.getMedTerm(); }

  @GetMapping(path = "{letters}")
  public List<MedTerm> getMedTerms(@PathVariable("letters") String letters) {
    return medTermService.getMedTerms(letters);
  }

  @PostMapping
  public void addNewMedTerm(@RequestBody MedTerm medTerm) {
    medTermService.addMedTerm(medTerm);
  }

  @PutMapping(path = "{termId}")
  public void updateMedTerm(@PathVariable("termId") Long termId,
                            @RequestParam(required = false) String name,
                            @RequestParam(required = false) String definition) {
    medTermService.updateMedTerm(termId, name, definition);}

  @DeleteMapping(path = "{termId}")
  public void deleteMedTerm(@PathVariable("termId") Long termId) { medTermService.deleteMedTerm(termId);}

}
