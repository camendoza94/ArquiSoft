package controllers;

import akka.dispatch.MessageDispatcher;
import com.fasterxml.jackson.databind.JsonNode;
import dispatchers.AkkaDispatcher;
import models.UsuarioEntity;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static play.libs.Json.toJson;

/**
 * Created by ca.mendoza968 on 30/08/2016.
 */
public class UsuarioController extends Controller {
    /**
     * Obtención de todos los usuarios por generación de petición GET /usuarios
     * @return Los usuarios del sistema
     */
    public CompletionStage<Result> getUsuarios() {
        MessageDispatcher jdbcDispatcher = AkkaDispatcher.jdbcDispatcher;

        return CompletableFuture.
                supplyAsync(
                        () -> {
                            return UsuarioEntity.FINDER.all();
                        }
                        , jdbcDispatcher)
                .thenApply(
                        productEntities -> {
                            return ok(toJson(productEntities));
                        }
                );
    }

    /**
     * Creación de un nuevo usuario según los parametros de la petición POST /usuarios
     * @return el usuario agregado
     */
    public CompletionStage<Result> createUsuario(){
        MessageDispatcher jdbcDispatcher = AkkaDispatcher.jdbcDispatcher;
        JsonNode nUsuario = request().body().asJson();
        UsuarioEntity usuario = Json.fromJson( nUsuario , UsuarioEntity.class ) ;
        return CompletableFuture.supplyAsync(
                ()->{
                    usuario.save();
                    return usuario;
                }
        ).thenApply(
                productEntity -> {
                    return ok(Json.toJson(productEntity));
                }
        );
    }

    /**
     * Actualiza un usuario con id particular dado por parametro. Atiende llamado de PUT /usuarios/: id
     * @param id identificador del usuario a actualizar
     * @return OK de que el item fue actualizado
     */
    public CompletionStage<Result> updateUsuario(Long id){
        MessageDispatcher jdbcDispatcher = AkkaDispatcher.jdbcDispatcher;
        JsonNode nUsuario = request().body().asJson();
        UsuarioEntity usuario = Json.fromJson( nUsuario , UsuarioEntity.class ) ;
        return CompletableFuture.supplyAsync(
                ()->{
                    usuario.setId(id);
                    usuario.update();
                    return ok();
                }
        ).thenApply(
                statusHeader -> statusHeader
        );
    }

    /**
     * Borra un usuario con id particular dado por parametro. Atiende llamado de DELETE /usuarios/{<[0-9]+>id}
     * @param id
     * @return OK de que el usuario fue borrado
     */
    public CompletionStage<Result> deleteUsuario(Long id){
        MessageDispatcher jdbcDispatcher = AkkaDispatcher.jdbcDispatcher;
        return CompletableFuture.supplyAsync(
                () -> {
                    UsuarioEntity.FINDER.deleteById(id);
                    return ok();
                })
                .thenApply(
                        statusHeader -> statusHeader
                );
    }

    /**
     * Obtiene un usuario con id particular dado por parametro. Atiende llamado de GET /usuarios/{<[0-9]+>id}
     * @param id
     * @return El usuario obtenido
     */
    public CompletionStage<Result> getUsuario(Long id) {
        MessageDispatcher jdbcDispatcher = AkkaDispatcher.jdbcDispatcher;

        return CompletableFuture.
                supplyAsync(
                        () -> {
                            return UsuarioEntity.FINDER.byId(id);
                        }
                        , jdbcDispatcher)
                .thenApply(
                        productEntity -> {
                            return ok(toJson(productEntity));
                        }
                );
    }
}
