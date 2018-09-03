package coding.exercise.validity.controller;

import coding.exercise.validity.model.Person;
import coding.exercise.validity.service.PersonService;
import org.apache.commons.codec.language.Metaphone;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class PersonController {

    @Autowired
    private PersonService personService;

    private List<Person> fullList;

    private List<Person> nonDup;

    private List<List<Person>> dupList;

    private List<List<String>> dupStrList;

    private void retrieveAllPeople() {
        fullList = new ArrayList<>();
        personService.findAll().forEach(a -> fullList.add(a));
        fullList.sort(new Comparator<Person>() {
            @Override
            public int compare(Person o1, Person o2) {
                return !o1.getLastName().equals(o2.getLastName()) ?
                        o1.getLastName().compareToIgnoreCase(o2.getLastName()) :
                        o2.getFirstName().compareToIgnoreCase(o1.getFirstName());
            }
        });
    }

    private void getNonDup() {
        if (dupList == null) {
            potentialDup();
        }
        nonDup = new ArrayList<>();
        nonDup = fullList.stream().filter(
                p -> !dupList.stream().flatMap(List::stream).collect(Collectors.toList()).contains(p))
                .collect(Collectors.toList());
    }

    /**
     * This is the extremely simple method to detect the potential duplicate...
     */
    private void potentialDup() {
        if (fullList == null) {
            retrieveAllPeople();
        }
        LevenshteinDistance levenshteinDistance = new LevenshteinDistance();
        Metaphone metaphone = new Metaphone();
        for (int i = 0; i < fullList.size() - 1; i++) {
            Person current = fullList.get(i), next = fullList.get(i + 1);
            if (levenshteinDistance.apply(current.getFullName().toLowerCase(), next.getFullName().toLowerCase()) < 4
                    && ((current.getCompany() != "" && next.getCompany() != "" &&
                    levenshteinDistance.apply(metaphone.encode(current.getCompany()),
                            metaphone.encode(next.getCompany())) < 2)
                    ||
                    (current.getEmail() != "" && next.getEmail() != "" &&
                            levenshteinDistance.apply(current.getEmail(), next.getEmail()) < 3)
                    ||
                    (current.getAddress1() != "" && next.getAddress1() != "" &&
                            levenshteinDistance.apply(metaphone.encode(current.getAddress1()),
                                    metaphone.encode(next.getAddress1())) < 2))) {
                List<Person> subList = new ArrayList<>();
                subList.add(current);
                int j = 2;
                while (levenshteinDistance.apply(current.getFullName().toLowerCase(), next.getFullName().toLowerCase()) < 4) {
                    subList.add(next);
                    next = fullList.get(i + j++);
                }
                if (dupList == null) {
                    dupList = new ArrayList<>();
                }
                if (!subList.isEmpty()) {
                    dupList.add(subList);
                    i += subList.size() - 1;
                }
            }
        }
        dupStrList = dupList.stream().map(list -> list.stream().map(p -> p.toString()).collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    /**
     * This was a method that I think may filter/double check the potential duplicate list, but it got more and more
     * complicate so I commented it out.
     *
     * @param list the first potential duplicate list
     */
    private void potentialDupFurther(List<Person> list) {
//        LevenshteinDistance levenshteinDistance = new LevenshteinDistance();
//        Metaphone metaphone = new Metaphone();
//        int size = list.size();
//        for (int i = 0; i < size - 1; i++) {
//            Person current = list.get(i), next = list.get(i + 1);
//            if ((current.getCompany() != "" && next.getCompany() != "" &&
//                    levenshteinDistance.apply(metaphone.encode(current.getCompany()),
//                            metaphone.encode(next.getCompany())) < 2)
//                    ||
//                    (current.getEmail() != "" && next.getEmail() != "" &&
//                            levenshteinDistance.apply(current.getEmail(), next.getEmail()) > 3)
//                    ||
//                    (current.getAddress1() != "" && next.getAddress1() != "" &&
//                            levenshteinDistance.apply(metaphone.encode(current.getAddress1()),
//                                    metaphone.encode(next.getAddress1())) > 2)) {
//                list.remove(i--);
//            }
//        }
    }

    @GetMapping("/people")
    public String listAllPeople(Model model) {
        if (fullList == null)
            retrieveAllPeople();
        List<String> strList = fullList.stream().map(p -> p.toString()).collect(Collectors.toList());
        model.addAttribute("peopleStr", strList);
        return "people";
    }

    @GetMapping("/nonduppeople")
    public String listAllNonDupPeople(Model model) {
        if (nonDup == null)
            getNonDup();
        List<String> strList = nonDup.stream().map(p -> p.toString()).collect(Collectors.toList());
        model.addAttribute("peopleStr", strList);
        return "nonDupPeople";
    }

    @GetMapping("/duppeople")
    public String listAllDupPeople(Model model) {
        if (dupList == null)
            potentialDup();
        model.addAttribute("peopleList", dupStrList);
        return "dupPeople";
    }
}
