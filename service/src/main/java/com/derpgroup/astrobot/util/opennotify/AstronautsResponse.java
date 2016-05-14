package com.derpgroup.astrobot.util.opennotify;

public class AstronautsResponse extends OpenNotifyResponse {

  private int number;
  private Astronaut[] people;

  public int getNumber() {
    return number;
  }

  public void setNumber(int number) {
    this.number = number;
  }

  public Astronaut[] getPeople() {
    return people;
  }

  public void setPeople(Astronaut[] people) {
    this.people = people;
  }
}