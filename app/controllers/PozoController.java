package controllers;

import akka.dispatch.MessageDispatcher;
import com.fasterxml.jackson.databind.JsonNode;
import dispatchers.AkkaDispatcher;
import models.CampoEntity;
import models.PozoEntity;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static play.libs.Json.toJson;

public class PozoController extends Controller {

    /**
     * Obtención de todos los campos por generación de petición GET /pozos
     * @return Los pozos
     */
    public CompletionStage<Result> getPozos(String periodo) {
        MessageDispatcher jdbcDispatcher = AkkaDispatcher.jdbcDispatcher;

        return CompletableFuture.
                supplyAsync(
                        () -> {
                            if(periodo != null){

                                //Keys: periodo
                                //Values: (diario, semanal, mensual, trimestral, semestral y anual)
                                List<PozoEntity> respuesta = null;

                                if (periodo.equals("diario"))
                                {
                                    LocalDateTime ultimaFecha= LocalDateTime.now();
                                    LocalDateTime primerFecha = LocalDateTime.now().minusDays(1);
                                    respuesta = PozoEntity.FINDER.where()
                                            .between("sensores.mediciones.fecha", Timestamp.valueOf(primerFecha), Timestamp.valueOf(ultimaFecha)).findList();
                                }else if (periodo.equals("semanal")){

                                    LocalDateTime ultimaFecha= LocalDateTime.now();
                                    LocalDateTime primerFecha = LocalDateTime.now().minusWeeks(1);
                                    respuesta = PozoEntity.FINDER.where()
                                            .between("sensores.mediciones.fecha", Timestamp.valueOf(primerFecha), Timestamp.valueOf(ultimaFecha)).findList();
                                }
                                else if (periodo.equals("mensual"))
                                {
                                    LocalDateTime ultimaFecha= LocalDateTime.now();
                                    LocalDateTime primerFecha = LocalDateTime.now().minusMonths(1);
                                    respuesta = PozoEntity.FINDER.where()
                                            .between("sensores.mediciones.fecha", Timestamp.valueOf(primerFecha), Timestamp.valueOf(ultimaFecha)).findList();
                                }
                                else if (periodo.equals("trimestral"))
                                {

                                    LocalDateTime ultimaFecha= LocalDateTime.now();
                                    LocalDateTime primerFecha = LocalDateTime.now().minusMonths(3);
                                    respuesta = PozoEntity.FINDER.where()
                                            .between("sensores.mediciones.fecha", Timestamp.valueOf(primerFecha), Timestamp.valueOf(ultimaFecha)).findList();
                                }
                                else if (periodo.equals("semestral"))
                                {

                                    LocalDateTime ultimaFecha= LocalDateTime.now();
                                    LocalDateTime primerFecha = LocalDateTime.now().minusMonths(6);
                                    respuesta = PozoEntity.FINDER.where()
                                            .between("sensores.mediciones.fecha", Timestamp.valueOf(primerFecha), Timestamp.valueOf(ultimaFecha)).findList();
                                }
                                else if (periodo.equals("anual"))
                                {

                                    LocalDateTime ultimaFecha= LocalDateTime.now();
                                    LocalDateTime primerFecha = LocalDateTime.now().minusYears(1);
                                    respuesta = PozoEntity.FINDER.where()
                                            .between("sensores.mediciones.fecha", Timestamp.valueOf(primerFecha), Timestamp.valueOf(ultimaFecha)).findList();
                                }else{

                                    respuesta = PozoEntity.FINDER.all();
                                }
                                return respuesta;

                            }
                            else{

                                return PozoEntity
                                        .FINDER.all();
                            }
                        }
                        , jdbcDispatcher)
                .thenApply(
                        pozoEntities -> {
                            return ok(toJson(pozoEntities));
                        }
                );
    }

    /**
     * Creación de un nuevo pozo según los parametros de la petición POST /pozos
     * @return el pozos agregado
     */
    public CompletionStage<Result> createPozo(){
        MessageDispatcher jdbcDispatcher = AkkaDispatcher.jdbcDispatcher;
        JsonNode nPozo = request().body().asJson();
        PozoEntity pozo = Json.fromJson( nPozo , PozoEntity.class ) ;
        return CompletableFuture.supplyAsync(
                ()->{
                    pozo.save();
                    return pozo;
                }
        ).thenApply(
                pozoEntity -> {
                    return ok(Json.toJson(pozoEntity));
                }
        );
    }

    /**
     * Borra un pozo con id particular dado por parametro. Atiende llamado de DELETE /pozo/{<[0-9]+>id}
     * @param id
     * @return OK de que el pozo fue borrado
     */
    public CompletionStage<Result> deletePozo(Long id) {
        MessageDispatcher jdbcDispatcher = AkkaDispatcher.jdbcDispatcher;

        return CompletableFuture.
                supplyAsync(
                        () -> {
                            PozoEntity.FINDER.ref(id).delete();
                            return PozoEntity.FINDER.byId(id);
                        }
                        , jdbcDispatcher)
                .thenApply(
                        campoEntity -> {
                            if(campoEntity == null)
                            {

                                return noContent();
                            }
                            else {
                                return badRequest();
                            }
                        }
                );
    }

    /**
     * Actualiza un pozo con id particular dado por parametro. Atiende llamado de PUT /pozos/: id
     * @param id identificador del pozo a actualizar
     * @return El pozo actualizado
     */
    public CompletionStage<Result> updatePozo(Long id) {
        MessageDispatcher jdbcDispatcher = AkkaDispatcher.jdbcDispatcher;

        JsonNode nPozo = request().body().asJson();
        PozoEntity pozo= Json.fromJson( nPozo , PozoEntity.class ) ;
        PozoEntity pozoViejo = PozoEntity.FINDER.byId(id);
        return CompletableFuture.supplyAsync(
                ()->{
                    if(pozoViejo == null)
                    {
                        return pozoViejo;
                    }else
                    {   pozo.setId(id);
                        pozo.update();
                        return pozo;
                    }
                }
        ).thenApply(
                pozoEntity -> {
                    if(pozoEntity == null)
                    {
                        return notFound();
                    }else
                    {
                    return ok(Json.toJson(pozoEntity));
                    }
                }

        );
    }

    /**
     * Obtiene un pozo con id particular dado por parametro. Atiende llamado de GET /pozos/{<[0-9]+>id}
     * @param id
     * @return El pozo obtenido
     */
    public CompletionStage<Result> getPozo(Long id, String periodo) {
        MessageDispatcher jdbcDispatcher = AkkaDispatcher.jdbcDispatcher;

        return CompletableFuture.
                supplyAsync(
                        () -> {

                            if(periodo != null){

                                //Keys: periodo
                                //Values: (diario, semanal, mensual, trimestral, semestral y anual)
                                PozoEntity respuesta = null;

                                if (periodo.equals("diario"))
                                {
                                    LocalDateTime ultimaFecha= LocalDateTime.now();
                                    LocalDateTime primerFecha = LocalDateTime.now().minusDays(1);
                                    respuesta = PozoEntity.FINDER.where().eq("id", id)
                                            .between("sensores.mediciones.fecha", Timestamp.valueOf(primerFecha), Timestamp.valueOf(ultimaFecha)).findUnique();
                                }else if (periodo.equals("semanal")){

                                    LocalDateTime ultimaFecha= LocalDateTime.now();
                                    LocalDateTime primerFecha = LocalDateTime.now().minusWeeks(1);
                                    respuesta = PozoEntity.FINDER.where().eq("id", id)
                                            .between("sensores.mediciones.fecha", Timestamp.valueOf(primerFecha), Timestamp.valueOf(ultimaFecha)).findUnique();
                                }
                                else if (periodo.equals("mensual"))
                                {
                                    LocalDateTime ultimaFecha= LocalDateTime.now();
                                    LocalDateTime primerFecha = LocalDateTime.now().minusMonths(1);
                                    respuesta = PozoEntity.FINDER.where().eq("id", id)
                                            .between("sensores.mediciones.fecha", Timestamp.valueOf(primerFecha), Timestamp.valueOf(ultimaFecha)).findUnique();
                                }
                                else if (periodo.equals("trimestral"))
                                {

                                    LocalDateTime ultimaFecha= LocalDateTime.now();
                                    LocalDateTime primerFecha = LocalDateTime.now().minusMonths(3);
                                    respuesta = PozoEntity.FINDER.where().eq("id", id)
                                            .between("sensores.mediciones.fecha", Timestamp.valueOf(primerFecha), Timestamp.valueOf(ultimaFecha)).findUnique();
                                }
                                else if (periodo.equals("semestral"))
                                {

                                    LocalDateTime ultimaFecha= LocalDateTime.now();
                                    LocalDateTime primerFecha = LocalDateTime.now().minusMonths(6);
                                    respuesta = PozoEntity.FINDER.where().eq("id", id)
                                            .between("sensores.mediciones.fecha", Timestamp.valueOf(primerFecha), Timestamp.valueOf(ultimaFecha)).findUnique();
                                }
                                else if (periodo.equals("anual"))
                                {

                                    LocalDateTime ultimaFecha= LocalDateTime.now();
                                    LocalDateTime primerFecha = LocalDateTime.now().minusYears(1);
                                    respuesta = PozoEntity.FINDER.where().eq("id", id)
                                            .between("sensores.mediciones.fecha", Timestamp.valueOf(primerFecha), Timestamp.valueOf(ultimaFecha)).findUnique();
                                }
                                else
                                    {

                                        return PozoEntity.FINDER.byId(id);
                                    }
                                return respuesta;

                            }
                            else
                                {
                                    return PozoEntity.FINDER.byId(id);
                            }


                        }
                        , jdbcDispatcher)
                .thenApply(
                        pozoEntity -> {
                            return ok(toJson(pozoEntity));
                        }
                );
    }

}