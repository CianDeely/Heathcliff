package com.example.heathcliff.model;

public class Pet {
    private String name;
    private int age;
    private String breed;
    private String coat;
    private String type;
    private String shelter;
    private String userID;
    private String description;


    public Pet(String name, int age, String breed, String coat, String type, String shelter, String userID, String description){
        this.name = name;
        this.age = age;
        this.breed = breed;
        this.coat = coat;
        this.type = type;
        this.shelter = shelter;
        this.userID = userID;
        this.description = description;
    }

    public Pet(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getCoat() {
        return coat;
    }

    public void setCoat(String coat) {
        this.coat = coat;
    }



    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getShelter() {
        return shelter;
    }

    public void setShelter(String shelter) {
        this.shelter = shelter;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Pet{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", breed='" + breed + '\'' +
                ", coat='" + coat + '\'' +
                ", type='" + type + '\'' +
                ", shelter='" + shelter + '\'' +
                ", userID='" + userID + '\'' +
                '}';
    }
}
