package com.saber.springbatchtest.dto;

import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name = "Person")
public class Person {
    private Long id;
    private String firstName;
    private String lastName;

    public Person(String firstName, String lastName) {
        this(null, firstName, lastName);
    }
}
