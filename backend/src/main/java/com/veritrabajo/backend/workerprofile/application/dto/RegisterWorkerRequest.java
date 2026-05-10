package com.veritrabajo.backend.workerprofile.application.dto;

import java.util.List;

public class RegisterWorkerRequest {

    private String fullName;
    private String phoneNumber;
    private String experienceDescription;
    private List<String> toolPhotoUrls;

    public RegisterWorkerRequest() {
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getExperienceDescription() {
        return experienceDescription;
    }

    public void setExperienceDescription(String experienceDescription) {
        this.experienceDescription = experienceDescription;
    }

    public List<String> getToolPhotoUrls() {
        return toolPhotoUrls;
    }

    public void setToolPhotoUrls(List<String> toolPhotoUrls) {
        this.toolPhotoUrls = toolPhotoUrls;
    }
}
