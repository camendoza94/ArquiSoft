/**
 * Created by jd.fandino10 on 21/08/2016.
 */
package controllers;

import akka.dispatch.MessageDispatcher;
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
                    s.save();
                    list.save();
                    return list;
                }
        ).thenApply(
                medicionEntities -> {
                    return ok(Json.toJson(medicionEntities));
                }
        );
    }

}