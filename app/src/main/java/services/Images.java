package services;

import java.util.HashMap;
import java.util.Map;

import javafx.scene.image.Image;
import utilities.Logger;

public class Images {
  private static Images singleton = new Images();

  private final Map<String, Image> images = new HashMap<>();

  private Images() {
    try {
      Image img = new Image(ClassLoader.getSystemClassLoader().getResource("images/default.png").toURI().toString());
      this.images.put("default", img);
    } catch (Exception e) {
      Logger.err("Error Loading default image", e);
    }
  }

  public static Image get(String k) {
    Image img = singleton.images.get(k);
    if (img != null) {
      return img;
    }
    try {
      img = new Image(ClassLoader.getSystemClassLoader().getResource("images/" + k + ".png").toURI().toString());
      singleton.images.put(k, img);
      return img;
    } catch (Exception e) {
      Logger.err("Error Loading image with key", k, e);
      return singleton.images.get("default");
    }
  }
}
