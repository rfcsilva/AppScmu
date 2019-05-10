package com.agrosmart.Utils;

import org.apache.commons.validator.routines.EmailValidator;

public class InformationChecker {


    private static final int PASSWORD_MIN_LENGTH = 6;

    public static boolean validRegistration(String username, String password, String confirmation_password,
                                            String email, String role){

        return nonEmptyField(username) && nonEmptyField(password) && nonEmptyField(confirmation_password) &&
                nonEmptyField(email) && validEmail(email) && validPassword( password)
                && nonEmptyField(role);
    }

    public static boolean validEmail(String email) {

        return  EmailValidator.getInstance().isValid(email);

    }

    public static boolean validPassword(String password) {

        if(password == null)
            return false;

        if(password.length() < PASSWORD_MIN_LENGTH )
            return false;

        return true;

    }


    private static boolean nonEmptyField(String field) {
        return field != null && !field.isEmpty();
    }

}
