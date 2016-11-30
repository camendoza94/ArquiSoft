package controllers;

import akka.dispatch.MessageDispatcher;
import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import com.fasterxml.jackson.databind.JsonNode;
import dispatchers.AkkaDispatcher;
import models.CampoEntity;
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
@Restrict(@Group(Application.ADMIN_ROLE))
public class UsuarioController extends Controller {
    /**
     * Obtención de todos los usuarios por generación de petición GET /usuarios
     * @return Los usuarios del sistema
     */
    public CompletionStage<Result> getUsuarios() {
        MessageDispatcher jdbcDispatcher = AkkaDispatcher.jdbcDispatcher;

        return CompletableFuture.
                supplyAsync(
                        () -> UsuarioEntity.FINDER.all()
                        , jdbcDispatcher)
                .thenApply(
                        productEntities -> ok(toJson(productEntities))
                );
    }

    /**
     * Creación de un nuevo usuario según los parametros de la petición POST /usuarios
     * @return el usuario agregado
     */
    public CompletionStage<Result> createUsuario(Long id){
        MessageDispatcher jdbcDispatcher = AkkaDispatcher.jdbcDispatcher;
        JsonNode nUsuario = request().body().asJson();
        UsuarioEntity usuario = Json.fromJson( nUsuario , UsuarioEntity.class ) ;
        return CompletableFuture.supplyAsync(
                ()->{
                    CampoEntity campo = CampoEntity.FINDER.byId(id);
                    String tipo = usuario.getRoles().toString();
                    if(tipo.equalsIgnoreCase("Jefe de Campo")){
                        campo.setJefeCampo(usuario);
                    } else if(tipo.equalsIgnoreCase("Jefe de Producción")) {
                        campo.setJefeProduccion(usuario);
                    } else {
                        return null;
                    }
                    usuario.save();
                    campo.update();
                    return usuario;
                }
        ).thenApply(
                usuarioEntity -> ok(Json.toJson(usuarioEntity))
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
        UsuarioEntity usuario = Json.fromJson(nUsuario, UsuarioEntity.class);
        UsuarioEntity old = UsuarioEntity.FINDER.byId(id);
        return CompletableFuture.supplyAsync(
                () -> {
                    if (old != null) {
                        usuario.setId(id);
                        usuario.update();
                        return usuario;
                    }
                    return null;
                }
        ).thenApply(
                usuarioEntity -> {
                    if (usuarioEntity == null) {
                        return notFound();
                    }
                    return ok(toJson(usuarioEntity));
                }
        );
    }

    /**
     * Borra un usuario con id particular dado por parametro. Atiende llamado de DELETE /usuarios/{<[0-9]+>id}
     * @param id
     * @return OK de que el usuario fue borrado
     */
    public CompletionStage<Result> deleteUsuario(Long id) {
        MessageDispatcher jdbcDispatcher = AkkaDispatcher.jdbcDispatcher;
        return CompletableFuture.supplyAsync(
                () -> {
                    UsuarioEntity.FINDER.ref(id).delete();
                    return UsuarioEntity.FINDER.byId(id);
                })
                .thenApply(
                        usuarioEntity -> {
                            if (usuarioEntity != null) {
                                return notFound();
                            }
                            return ok(toJson(usuarioEntity));
                        }
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
                        () -> UsuarioEntity.FINDER.byId(id)
                        , jdbcDispatcher)
                .thenApply(
                        usuarioEntity -> {
                            if(usuarioEntity == null){
                                return notFound();
                            }
                            return ok(toJson(usuarioEntity));
                        }
                );
    }
}
