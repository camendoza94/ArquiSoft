/**
 * Created by jd.fandino10 on 21/08/2016.
 */
package controllers;

import akka.dispatch.MessageDispatcher;
import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import com.fasterxml.jackson.databind.JsonNode;
import dispatchers.AkkaDispatcher;
import models.MedicionEntity;
import models.SensorEntity;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static play.libs.Json.toJson;

/**
 * Clase controladora de la lista de deseos
 */
@Restrict({@Group(Application.USER_ROLE), @Group(Application.ADMIN_ROLE)})
public class EmergenciaController extends SensorController  {

    public CompletionStage<Result> addEmergencia(Long id){
        MessageDispatcher jdbcDispatcher = AkkaDispatcher.jdbcDispatcher;
        JsonNode nSensor = request().body().asJson();
        MedicionEntity list = Json.fromJson( nSensor , MedicionEntity.class ) ;
        System.out.println("EMERGENCIA:informando a autoridades....");
        return CompletableFuture.supplyAsync(
                ()->{
                    SensorEntity s=SensorEntity.FINDER.byId(id);
                    s.addMedicion(list);
                    list.save();
                    s.update();
                    return list;
                }
        ).thenApply(
                medicionEntities -> {
                    return ok(Json.toJson(medicionEntities));
                }
        );
    }

}
