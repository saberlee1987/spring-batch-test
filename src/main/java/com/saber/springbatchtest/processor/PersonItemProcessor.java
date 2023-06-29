package com.saber.springbatchtest.processor;

import com.saber.springbatchtest.dto.Person;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class PersonItemProcessor implements ItemProcessor<Person, Person> {
    @Override
    public Person process(Person person) throws Exception {

        Long id = person.getId();
        String firstName = person.getFirstName().toUpperCase();
        String lastName = person.getLastName().toUpperCase();
        Person transformPerson = new Person(id, firstName, lastName);

        log.info("converting ( {} ) into ( {} ) ", person, transformPerson);

        return transformPerson;
    }
}
