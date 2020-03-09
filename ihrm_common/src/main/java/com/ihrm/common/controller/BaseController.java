package com.ihrm.common.controller;


import io.jsonwebtoken.Claims;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BaseController {

    public HttpServletRequest request;
    public HttpServletResponse response;
    protected String companyId;
    protected String companyName;
    protected Claims claims;

    @ModelAttribute
    public void setResAndRep(HttpServletRequest request, HttpServletResponse response){
        this.request= request;
        this.response=response;

        Object user_claims = request.getAttribute("user_claims");
        if(user_claims!=null){
            this.claims = (Claims) user_claims;
            this.companyId = (String) claims.get("companyId");
            this.companyName = (String) claims.get("companyName");
        }
    }
}
