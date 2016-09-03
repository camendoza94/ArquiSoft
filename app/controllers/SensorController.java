/**
 * Created by jd.fandino10 on 21/08/2016.
 */
package controllers;

import akka.dispatch.MessageDispatcher;
import com.fasterxml.jackson.databind.JsonNode;
import dispatchers.AkkaDispatcher;
import models.MedicionEntity;
import models.PozoEntity;
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
public class SensorController extends Controller  {

    public CompletionStage<Result> getSensores() {
        MessageDispatcher jdbcDispatcher = AkkaDispatcher.jdbcDispatcher;

        return CompletableFuture.
                supplyAsync(
                        () -> {
                            return SensorEntity.FINDER.all();
                        }
                        ,jdbcDispatcher)
                .thenApply(
                        SensorEntities -> {
                            return ok(toJson(SensorEntities));
                        }
                );
    }
    public CompletionStage<Result> createSensor(Long id){
        MessageDispatcher jdbcDispatcher = AkkaDispatcher.jdbcDispatcher;
        JsonNode nSensor = request().body().asJson();
        SensorEntity list = Json.fromJson( nSensor , SensorEntity.class ) ;
        return CompletableFuture.supplyAsync(
                ()->{
                    PozoEntity pozo=PozoEntity.FINDER.byId(id);
                    pozo.addSensor(list);
                    list.save();
                    pozo.update();
                    return list;
                }
        ).thenApply(
                sensorEntities -> {
                    return ok(Json.toJson(sensorEntities));
                }
        );
    }

    public CompletionStage<Result> getSensor(Long id) {
        MessageDispatcher jdbcDispatcher = AkkaDispatcher.jdbcDispatcher;

        return CompletableFuture.
                supplyAsync(
                        () -> {
                            return SensorEntity.FINDER.byId(id);
                        }
                        ,jdbcDispatcher)
                .thenApply(
                        sensorEntities -> {
                            return ok(toJson(sensorEntities));
                        }
                );
    }

    public CompletionStage<Result> deleteSensor(Long id) {
        MessageDispatcher jdbcDispatcher = AkkaDispatcher.jdbcDispatcher;
        return CompletableFuture.
                supplyAsync(
                        () -> {
                            SensorEntity.FINDER.deleteById(id);
                            return ok();
                        }
                        ,jdbcDispatcher)
                .thenApply(
                        sensorEntities -> sensorEntities
                );
    }

    public CompletionStage<Result> updateSensor(Long id){
        MessageDispatcher jdbcDispatcher = AkkaDispatcher.jdbcDispatcher;
        JsonNode nSensor = request().body().asJson();
        SensorEntity list = Json.fromJson( nSensor , SensorEntity.class ) ;
        return CompletableFuture.supplyAsync(
                ()->{
                    list.setId(id);
                    list.update();
                    return ok();
                }
        ).thenApply(
                statusHeader -> statusHeader
        );
    }

    public CompletionStage<Result> addMedida(Long id){
        MessageDispatcher jdbcDispatcher = AkkaDispatcher.jdbcDispatcher;
        JsonNode nSensor = request().body().asJson();
        MedicionEntity list = Json.fromJson( nSensor , MedicionEntity.class ) ;
        return CompletableFuture.supplyAsync(
                ()->{
                    SensorEntity s=SensorEntity.FINDER.byId(id);

                    s.addMedicion(list);
                    list.save();
                    //s.update();
                    return list;
                }
        ).thenApply(
                medicionEntities -> {
                    return ok(Json.toJson(medicionEntities));
                }
        );
    }

    public CompletionStage<Result> getMedida(Long id) {
        MessageDispatcher jdbcDispatcher = AkkaDispatcher.jdbcDispatcher;

        return CompletableFuture.
                supplyAsync(
                        () -> {
                            return MedicionEntity.FINDER.byId(id);
                        }
                        ,jdbcDispatcher)
                .thenApply(
                        medicionEntities -> {
                            return ok(toJson(medicionEntities));
                        }
                );
    }

    public CompletionStage<Result> getMedidas() {
        MessageDispatcher jdbcDispatcher = AkkaDispatcher.jdbcDispatcher;

        return CompletableFuture.
                supplyAsync(
                        () -> {
                            return MedicionEntity.FINDER.all();
                        }
                        ,jdbcDispatcher)
                .thenApply(
                        medicionEntities -> {
                            return ok(toJson(medicionEntities));
                        }
                );
    }

    public CompletionStage<Result> getMedidasSensor(Long id) {
        MessageDispatcher jdbcDispatcher = AkkaDispatcher.jdbcDispatcher;
        return CompletableFuture.
                supplyAsync(
                        () -> {
                            return SensorEntity.FINDER.byId(id).getMediciones();
                        }
                        ,jdbcDispatcher)
                .thenApply(
                        medicionEntities -> {
                            return ok(toJson(medicionEntities));
                        }
                );
    }

    public CompletionStage<Result> deleteMedida(Long id) {
        MessageDispatcher jdbcDispatcher = AkkaDispatcher.jdbcDispatcher;
        return CompletableFuture.
                supplyAsync(
                        () -> {
                            MedicionEntity.FINDER.deleteById(id);
                            return ok();
                        }
                        ,jdbcDispatcher)
                .thenApply(
                        sensorEntities -> sensorEntities
                );
    }

    public CompletionStage<Result> updateMedicion(Long id){
        MessageDispatcher jdbcDispatcher = AkkaDispatcher.jdbcDispatcher;
        JsonNode nMedicion = request().body().asJson();
        MedicionEntity list = Json.fromJson( nMedicion , MedicionEntity.class ) ;
        return CompletableFuture.supplyAsync(
                ()->{
                    list.setId(id);
                    System.out.println("va a update");
                    list.update();
                    System.out.println("termina update");
                    return ok();
                }
        ).thenApply(
                statusHeader -> statusHeader
        );
    }
}
