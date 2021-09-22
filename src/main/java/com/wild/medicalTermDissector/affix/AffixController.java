package com.wild.medicalTermDissector.affix;

import com.wild.medicalTermDissector.medicalTerms.MedTermService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

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

  //TODO: Make note that this method should never be used anymore, it makes it too easy for people to access your app and cause issues.
  // Much better to use POST with a body, people never use GET like this anymore. They instead only use GET for getting
  // static pages, but never for sending data from client to server like a post request.
  @GetMapping(path = "/dissect/{term}")  // This takes variables from the url. That is how GET work.
  public AffixResult getDissectedTerm(@PathVariable("term") String term) {
    return affixService.dissect(term);
  }

  @PostMapping(path = "/dissect")  // This takes variables from the Body of the request. This is how POST work. //FIXME: Media type not supported
  public AffixResult getDissectedTermFromBody(@RequestBody Map term) {
    return affixService.dissect(term.get("term").toString());
  }

  private class DissectForm {
    public String term;
  }
}
