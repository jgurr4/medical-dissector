package com.wild.medicalTermDissector.medicalTerms;

import com.webfirmframework.wffweb.tag.html.*;
import com.webfirmframework.wffweb.tag.html.attribute.*;
import com.webfirmframework.wffweb.tag.html.attribute.global.Id;
import com.webfirmframework.wffweb.tag.html.attribute.global.Style;
import com.webfirmframework.wffweb.tag.html.formsandinputs.Button;
import com.webfirmframework.wffweb.tag.html.formsandinputs.Form;
import com.webfirmframework.wffweb.tag.html.formsandinputs.Input;
import com.webfirmframework.wffweb.tag.html.formsandinputs.Label;
import com.webfirmframework.wffweb.tag.html.html5.Source;
import com.webfirmframework.wffweb.tag.html.html5.attribute.Placeholder;
import com.webfirmframework.wffweb.tag.html.metainfo.Head;
import com.webfirmframework.wffweb.tag.html.programming.Script;
import com.webfirmframework.wffweb.tag.htmlwff.NoTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.servlet.tags.form.AbstractHtmlElementBodyTag;
import org.springframework.web.servlet.tags.form.ButtonTag;

import javax.swing.text.html.HTML;
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

  public String generateIndexPage() { // TODO: This needs to generate javascript/ajax which takes the json and changes the webpage accordingly. Use a java web framework of some kind.
    Html rootTag = new Html(null).give(html -> {
      Head head = new Head(html);
      //FIXME: Find a way to add crossorigin attribute here. See reactjs cdn.
      new Script(head, new Type("text/javascript"), new Src("https://unpkg.com/react@17/umd/react.production.min.js"), new Value("//put javascript code here to handle response data."));
      new Script(head, new Type("text/javascript"), new Src("https://unpkg.com/react-dom@17/umd/react-dom.production.min.js"), new Value("//put javascript code here to handle response data."));
      new Script(head, new Type("text/javascript"), new Src("result.js"));
      new Body(html).give(body -> {
        new NoTag(body, "Hello World");
        new Br(body);
        new Input(body, new Placeholder("type word here"), new Id("term"), new Name("term"));
        Button button = new Button(body, new Id("driver"), new Type("submit"));
        new Br(body);
        new NoTag(button, "click me");
        P p = new P(body);
        Id id = new Id("result");
        Style style = new Style("color:green");
        p.addAttributes(id, style);
        p.addInnerHtml(new NoTag(p, "hello")); // Cannot figure this out yet.
      });
    });
// prepends the doc type <!DOCTYPE html>
    rootTag.setPrependDocType(true);
    System.out.println(rootTag.toHtmlString(true));
    return rootTag.toHtmlString(true);
  }

}
