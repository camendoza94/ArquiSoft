package controllers;

import akka.dispatch.MessageDispatcher;
import com.fasterxml.jackson.databind.JsonNode;
import dispatchers.AkkaDispatcher;
import models.CampoEntity;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static play.libs.Json.toJson;

public class CampoController extends Controller {

    /**
     * Obtención de todos los campos por generación de petición GET /campos
     * @return Los campos
     */
    public CompletionStage<Result> getCampos() {
        MessageDispatcher jdbcDispatcher = AkkaDispatcher.jdbcDispatcher;

        return CompletableFuture.
                supplyAsync(
                        () -> {
                            return CampoEntity
                                    .FINDER.all();
                        }
                        , jdbcDispatcher)
                .thenApply(
                        campoEntities -> {
                            return ok(toJson(campoEntities));
                        }
                );
    }

    /**
     * Creación de un nuevo campo según los parametros de la petición POST /campos
     * @return el campo agregado
     */
    public CompletionStage<Result> createCampo(){
        MessageDispatcher jdbcDispatcher = AkkaDispatcher.jdbcDispatcher;
        JsonNode nItem = request().body().asJson();
        CampoEntity campo = Json.fromJson( nItem , CampoEntity.class ) ;
        return CompletableFuture.supplyAsync(
                ()->{
                    campo.save();
                    return campo;
                }
        ).thenApply(
                campoEntity -> {
                    return ok(Json.toJson(campoEntity));
                }
        );
    }

    /**
     * Borra un campo con id particular dado por parametro. Atiende llamado de DELETE /campos/{<[0-9]+>id}
     * @param id
     * @return OK de que el campo fue borrado
     */
    public CompletionStage<Result> deleteCampo(Long id) {
        MessageDispatcher jdbcDispatcher = AkkaDispatcher.jdbcDispatcher;

        return CompletableFuture.
                supplyAsync(
                        () -> {
                            CampoEntity.FINDER.ref(id).delete();
                            return CampoEntity.FINDER.byId(id);
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
     * Actualiza un campo con id particular dado por parametro. Atiende llamado de PUT /campos/: id
     * @param id identificador del campo a actualizar
     * @return El campo actualizado
     */
    public CompletionStage<Result> updateCampo(Long id) {
        MessageDispatcher jdbcDispatcher = AkkaDispatcher.jdbcDispatcher;

        JsonNode nCampo = request().body().asJson();
        CampoEntity campo = Json.fromJson( nCampo , CampoEntity.class ) ;
        CampoEntity campoViejo = CampoEntity.FINDER.byId(id);
        return CompletableFuture.supplyAsync(
                ()->{
                    if(campoViejo == null)
                    {
                        return campoViejo;
                    }else
                    {   campo.setId(id);
                        campo.update();
                        return campo;
                    }
                }
        ).thenApply(
                campoEntity -> {
                    if(campoEntity == null)
                    {
                        return notFound();
                    }else
                    {
                    return ok(Json.toJson(campoEntity));
                    }
                }

        );
    }

    /**
     * Obtiene un campo con id particular dado por parametro. Atiende llamado de GET /campos/{<[0-9]+>id}
     * @param id
     * @return El campo obtenido
     */
    public CompletionStage<Result> getCampo(Long id) {
        MessageDispatcher jdbcDispatcher = AkkaDispatcher.jdbcDispatcher;

        return CompletableFuture.
                supplyAsync(
                        () -> {
                            return CampoEntity.FINDER.byId(id);
                        }
                        , jdbcDispatcher) 
                .thenApply(
                        campoEntity -> {
                            return ok(toJson(campoEntity));
                        }
                );
    }

}