package com.eagle.api.dto;

import java.util.List;
import java.util.Map;

public class BadRequestErrorResponse {
    private String message;
    private List<Map<String, String>> details;

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public List<Map<String, String>> getDetails() { return details; }
    public void setDetails(List<Map<String, String>> details) { this.details = details; }
}