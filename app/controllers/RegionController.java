package controllers;

import akka.dispatch.MessageDispatcher;
import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import com.fasterxml.jackson.databind.JsonNode;
import dispatchers.AkkaDispatcher;
import models.RegionEntity;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static play.libs.Json.toJson;
@Restrict(@Group(Application.USER_ROLE))
public class RegionController extends Controller {

    /**
     * Obtención de todos los regiones por generación de petición GET /regiones
     * @return Los regiones
     */
    public CompletionStage<Result> getRegiones() {
        MessageDispatcher jdbcDispatcher = AkkaDispatcher.jdbcDispatcher;

        return CompletableFuture.
                supplyAsync(
                        () -> {
                            return RegionEntity
                                    .FINDER.all();
                        }
                        , jdbcDispatcher)
                .thenApply(
                        regionEntities -> {
                            return ok(toJson(regionEntities));
                        }
                );
    }

    /**
     * Creación de un nuevo region según los parametros de la petición POST /regiones
     * @return el region agregado
     */
    public CompletionStage<Result> createRegion(){
        MessageDispatcher jdbcDispatcher = AkkaDispatcher.jdbcDispatcher;
        JsonNode nItem = request().body().asJson();
        RegionEntity region = Json.fromJson( nItem , RegionEntity.class ) ;
        return CompletableFuture.supplyAsync(
                ()->{
                    region.save();
                    return region;
                }
        ).thenApply(
                regionEntity -> {
                    return ok(Json.toJson(regionEntity));
                }
        );
    }

    /**
     * Borra un region con id particular dado por parametro. Atiende llamado de DELETE /regiones/{<[0-9]+>id}
     * @param id
     * @return OK de que el region fue borrado
     */
    public CompletionStage<Result> deleteRegion(Long id) {
        MessageDispatcher jdbcDispatcher = AkkaDispatcher.jdbcDispatcher;

        return CompletableFuture.
                supplyAsync(
                        () -> {
                            RegionEntity.FINDER.ref(id).delete();
                            return RegionEntity.FINDER.byId(id);
                        }
                        , jdbcDispatcher)
                .thenApply(
                        regionEntity -> {
                            if(regionEntity == null)
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
     * Actualiza un region con id particular dado por parametro. Atiende llamado de PUT /regiones/: id
     * @param id identificador del region a actualizar
     * @return El region actualizado
     */
    public CompletionStage<Result> updateRegion(Long id) {
        MessageDispatcher jdbcDispatcher = AkkaDispatcher.jdbcDispatcher;

        JsonNode nRegion = request().body().asJson();
        RegionEntity region = Json.fromJson( nRegion , RegionEntity.class ) ;
        RegionEntity regionVieja = RegionEntity.FINDER.byId(id);
        return CompletableFuture.supplyAsync(
                ()->{
                    if(regionVieja == null)
                    {
                        return regionVieja;
                    }else
                    {   region.setId(id);
                        region.update();
                        return region;
                    }
                }
        ).thenApply(
                regionEntity -> {
                    if(regionEntity == null)
                    {
                        return notFound();
                    }else
                    {
                    return ok(Json.toJson(regionEntity));
                    }
                }

        );
    }

    /**
     * Obtiene un region con id particular dado por parametro. Atiende llamado de GET /regiones/{<[0-9]+>id}
     * @param id
     * @return El region obtenido
     */
    public CompletionStage<Result> getRegion(Long id) {
        MessageDispatcher jdbcDispatcher = AkkaDispatcher.jdbcDispatcher;

        return CompletableFuture.
                supplyAsync(
                        () -> {
                            return RegionEntity.FINDER.byId(id);
                        }
                        , jdbcDispatcher) 
                .thenApply(
                        regionEntity -> {
                            return ok(toJson(regionEntity));
                        }
                );
    }

}