package controllers;

import java.io.File;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import com.feth.play.module.pa.PlayAuthenticate;
import models.UsuarioEntity;
import play.Logger;
import play.Play;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import providers.MyUsernamePasswordAuthProvider;
import service.UserProvider;
import views.html.*;

import javax.inject.Inject;

public class Application extends Controller {

    public static final String FLASH_MESSAGE_KEY = "message";
    public static final String FLASH_ERROR_KEY = "error";
    public static final String USER_ROLE = "user";
    public static final String ADMIN_ROLE = "admin";
    private final PlayAuthenticate auth;

    private final MyUsernamePasswordAuthProvider provider;

    private final UserProvider userProvider;

    @Inject
    public Application(final PlayAuthenticate auth, final MyUsernamePasswordAuthProvider provider,
                       final UserProvider userProvider) {
        this.auth = auth;
        this.provider = provider;
        this.userProvider = userProvider;
    }
    /** serve the indexSign page app/views/indexSign.scala.htmlhtml */
    @Restrict({@Group(Application.USER_ROLE), @Group(Application.ADMIN_ROLE)})
    public Result indexSign( String any) {
    return ok(indexSign.render(this.userProvider));
  }

    /**
     * Redirect to the provider
     * @return
     */
  public Result index() {

          return ok(index.render(this.userProvider));

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

    public static String formatTimestamp(final long t) {
        return new SimpleDateFormat("yyyy-dd-MM HH:mm:ss").format(new Date(t));
    }


    @Restrict(@Group({Application.ADMIN_ROLE,Application.USER_ROLE}))
    public Result profile() {
        final UsuarioEntity localUser = userProvider.getUser(session());
        return ok(profile.render(this.auth, this.userProvider, localUser));
    }

    public Result login() {
        return ok(login.render(this.auth, this.userProvider,  this.provider.getLoginForm()));
    }

    public Result doLogin() {
        com.feth.play.module.pa.controllers.Authenticate.noCache(response());

        final Form<MyUsernamePasswordAuthProvider.MyLogin> filledForm = this.provider.getLoginForm()
                .bindFromRequest();
        Logger.info(filledForm.data().toString());
        if (filledForm.hasErrors() || !filledForm.data().isEmpty()) {
            // User did not fill everything properly
            return badRequest(login.render(this.auth, this.userProvider, filledForm));
        } else {
            // Everything was filled
            return this.provider.handleLogin(ctx());
        }
    }

    public Result signup() {
        return ok(signup.render(this.auth, this.userProvider, this.provider.getSignupForm()));
    }

    public Result doSignup() {
        com.feth.play.module.pa.controllers.Authenticate.noCache(response());
        final Form<MyUsernamePasswordAuthProvider.MySignup> filledForm = this.provider.getSignupForm().bindFromRequest();
        if (filledForm.hasErrors()) {
            // User did not fill everything properly
            return badRequest(signup.render(this.auth, this.userProvider, filledForm));
        } else {
            // Everything was filled
            // do something with your part of the form before handling the user
            // signup
            return this.provider.handleSignup(ctx());
        }
    }
}
