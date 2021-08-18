package com.wild.medicalTermDissector.medicalTerms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

  @GetMapping(path = "/dissect/{term}")
  public Map<String, String> getDissectedTerm(@PathVariable("term") String term) {
    return medTermService.dissect(term);
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
// @RequestParam is a useful way to set a default value in case a parameter is not given. Example:
//  public List<Student> getStudents(@RequestParam(value = "name", defaultValue = "World") String name) {
