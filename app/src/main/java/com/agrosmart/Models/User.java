package com.agrosmart.Models;


public class User{

    static final String USERNAME = "username";
    public static final String NAME = "name";
    public static final String EMAIL = "email";
    public static final String ROLE = "role";



    public static final String TYPE = "User";

    public String username;
    public String name;
    public String email;
    public String role;

    public User(){}

    public User(String username, String name, String email, String role){
        this.username = username;
        this.name = name;
        this.email = email;
        this.role = role;
    }

}
