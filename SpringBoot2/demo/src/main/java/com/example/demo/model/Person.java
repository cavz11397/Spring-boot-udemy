package com.example.demo.model;

public class Person {

    private String identification;
    private String identificationType;
    private String name;
    private String lastName;
    private String phone;

    public Person() {
        super();
    }

    public Person(String identification, String identificationType, String name, String lastName, String phone) {
        this.identification = identification;
        this.identificationType = identificationType;
        this.name = name;
        this.lastName = lastName;
        this.phone = phone;
    }

    public String getIdentification() {
        return identification;
    }

    public void setIdentification(String identification) {
        this.identification = identification;
    }

    public String getIdentificationType() {
        return identificationType;
    }

    public void setIdentificationType(String identificationType) {
        this.identificationType = identificationType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "Person{" +
                "identification='" + identification + '\'' +
                ", identificationType='" + identificationType + '\'' +
                ", name='" + name + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
