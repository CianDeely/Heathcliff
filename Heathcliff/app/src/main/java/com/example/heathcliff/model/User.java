package com.example.heathcliff.model;

public class User {
    private String FirstName;
    private String LastName;
    private String Email;
    private Boolean Active;

    public User(String FirstName, String LastName, String Email, Boolean Active){
        this.FirstName = FirstName;
        this.LastName = LastName;
        this.Email = Email;
        this.Active = Active;
    }

    public User(){

    }







    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }


    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public Boolean getActive() {
        return Active;
    }

    public void setActive(Boolean active) {
        Active = active;
    }

    @Override
    public String toString() {
        return "User{" +
                "FirstName='" + FirstName + '\'' +
                ", LastName='" + LastName + '\'' +
                ", Email='" + Email + '\'' +
                ", Active=" + Active +
                '}';
    }
}
