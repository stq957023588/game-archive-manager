package com.fool.gamearchivemanager.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

public class SecurityContextUtils {

    public static UserDetails getUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        return (UserDetails) authentication.getPrincipal();
    }


    public static String getUsername() {
        UserDetails userDetails = getUserDetails();
        if (userDetails == null) {
            return null;
        }
        return userDetails.getUsername();
    }

}

