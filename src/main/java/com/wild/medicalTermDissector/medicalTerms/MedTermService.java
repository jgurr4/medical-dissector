package com.wild.medicalTermDissector.medicalTerms;

import com.webfirmframework.wffweb.tag.html.*;
import com.webfirmframework.wffweb.tag.html.attribute.Action;
import com.webfirmframework.wffweb.tag.html.attribute.Method;
import com.webfirmframework.wffweb.tag.html.attribute.Type;
import com.webfirmframework.wffweb.tag.html.attribute.Value;
import com.webfirmframework.wffweb.tag.html.formsandinputs.Button;
import com.webfirmframework.wffweb.tag.html.formsandinputs.Form;
import com.webfirmframework.wffweb.tag.html.html5.Text;
import com.webfirmframework.wffweb.tag.html.metainfo.Head;
import com.webfirmframework.wffweb.tag.htmlwff.NoTag;
import com.wild.medicalTermDissector.affix.Affix;
import org.dom4j.tree.AbstractAttribute;
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
    Html rootTag = new Html(null).give(html -> {
      new Head(html);
      new Body(html).give(body -> {
        new NoTag(body, "Hello World");
        Form form = new Form(body, new Method(Method.GET), new Action("/api/term/dissect/hypoglycemia"));
        Button button = new Button(form, new Type("submit"));
        new Br(form);
        new NoTag(button, "click me");
      });

    });
// prepends the doc type <!DOCTYPE html>
    rootTag.setPrependDocType(true);
    System.out.println(rootTag.toHtmlString(true));
    return rootTag.toHtmlString(true);
  }

}
