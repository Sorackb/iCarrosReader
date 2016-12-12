package org.lucassouza.icarrosreader.controller;

import java.util.ArrayList;
import java.util.List;
import org.lucassouza.icarrosreader.type.ResourceType;

/**
 *
 * @author Lucas Souza [sorack@gmail.com]
 */
public class Comunicator {

  private static Comunicator comunicator;
  private final List<Communicable> observers;

  public static Comunicator getInstance() {
    if (comunicator == null) {
      comunicator = new Comunicator();
    }

    return comunicator;
  }

  private Comunicator() {
    this.observers = new ArrayList<>();
  }

  public void observe(Communicable observer) {
    this.observers.add(observer);
  }

  public void informAmount(ResourceType resourceType, Integer amount) {
    this.observers.forEach((observer) -> {
      observer.informAmount(resourceType, amount);
    });
  }

  public void informIncrement(ResourceType resourceType) {
    this.observers.forEach((observer) -> {
      observer.informIncrement(resourceType);
    });
  }

  public void showError(String message) {
    this.observers.forEach((observer) -> {
      observer.showError(message);
    });
  }
}
