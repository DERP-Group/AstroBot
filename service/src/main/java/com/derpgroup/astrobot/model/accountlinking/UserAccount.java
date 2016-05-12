package com.derpgroup.astrobot.model.accountlinking;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.NON_NULL)
public class UserAccount {

  private String userId;
  private String firstName;
  private long dateCreated;
  private Map<String,ExternalAccountLink> externalAccountLinks;
  
  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public long getDateCreated() {
    return dateCreated;
  }

  public void setDateCreated(long dateCreated) {
    this.dateCreated = dateCreated;
  }

  public Map<String, ExternalAccountLink> getExternalAccountLinks() {
    return externalAccountLinks;
  }

  public void setExternalAccountLinks(
      Map<String, ExternalAccountLink> externalAccountLinks) {
    this.externalAccountLinks = externalAccountLinks;
  }

  @Override
  public String toString() {
    return "UserAccount [userId=" + userId + ", firstName=" + firstName
        + ", dateCreated=" + dateCreated + ", externalAccountLinks="
        + externalAccountLinks + "]";
  }
}
