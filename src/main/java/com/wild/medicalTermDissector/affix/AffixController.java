package com.wild.medicalTermDissector.affix;

import com.wild.medicalTermDissector.medicalTerms.MedTermService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/term")
public class AffixController {

  private final AffixService affixService;

  @Autowired
  public AffixController(AffixService affixService) {
    this.affixService = affixService;
  }

  @GetMapping(path = "/dissect/{term}")
  public Map<String, List<Affix>> getDissectedTerm(@PathVariable("term") String term) {
//    return affixService.dissect(term);
    return null;
  }

}
