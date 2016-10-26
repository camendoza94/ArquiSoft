package controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import play.Play;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.*;

public class Application extends Controller {
  /** serve the index page app/views/index.scala.html */
  public Result index( String any) {
    return ok(index.render());
  }

  /** resolve "any" into the corresponding HTML page URI */
  public String getURI( String any){
      String resp = "";
      switch (any) {
          case "region/main":
              resp="/public/region/main.html";
              break;

          case "region/detail":
              resp ="/public/region/detail.html";
                break;
          case "campo/main" :
              resp = "/public/campo/main.html";
              break;
          case "campo/detail" :
              resp = "/public/campo/detail.html";
              break;

          case "pozo/main" :
              resp="/public/pozo/main.html";
              break;

          case "pozo/detail" :
              resp="/public/pozo/detail.html";
              break;

          case "sensor/main" :
              resp = "/public/sensor/main.html";
              break;
          case "sensor/detail" :
              resp="/public/sensor/detail.html";
              break;
          case "inicio":
              resp="/public/inicio.html";
              break;
          default:
              resp="error";
              break;
      }
      return resp;
  }

  /** load an HTML page from public/html */
  public Result loadPublicHTML(String any) {
      File projectRoot = Play.application().path();
      File file = new File(projectRoot + getURI(any));
      if (file.exists()) {
          try {
              return ok(new String(Files.readAllBytes(Paths.get(file.getCanonicalPath())))).as("text/html");
          } catch (IOException e) {
              e.printStackTrace();
          }
      } else {
          return notFound();
      }
      return null;
  }
}
