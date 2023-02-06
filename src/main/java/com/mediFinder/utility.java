package com.mediFinder;

import jakarta.servlet.http.HttpServletRequest;

public class utility {
    public static String getSiteURL(HttpServletRequest request){
        String siteURL = request.getRequestURI().toString();
        return siteURL.replace(request.getServletPath(), "");
    }
}
