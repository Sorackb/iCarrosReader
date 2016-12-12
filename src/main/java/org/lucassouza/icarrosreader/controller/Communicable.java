package org.lucassouza.icarrosreader.controller;

import org.lucassouza.icarrosreader.type.ResourceType;

/**
 *
 * @author Lucas Souza [sorackb@gmail.com]
 */
public interface Communicable {

  void informAmount(ResourceType resourceType, Integer amount);

  void informIncrement(ResourceType resourceType);
  
  void showError(String message);
}
