package com.derpgroup.astrobot.util.opennotify;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings
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