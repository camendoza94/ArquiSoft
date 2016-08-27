package controllers;

import akka.dispatch.MessageDispatcher;
import com.fasterxml.jackson.databind.JsonNode;
import dispatchers.AkkaDispatcher;
import models.ItemEntity;
import models.ProductEntity;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import scala.xml.PrettyPrinter;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static play.libs.Json.toJson;

public class ItemController extends Controller {

    /**
     * Obtención de todos los items por generación de petición GET /items
     * @return Los items
     */
    public CompletionStage<Result> getItems() {
        MessageDispatcher jdbcDispatcher = AkkaDispatcher.jdbcDispatcher;

        return CompletableFuture.
                supplyAsync(
                        () -> {
                            return ItemEntity.FINDER.all();
                        }
                        , jdbcDispatcher)
                .thenApply(
                        itemEntities -> {
                            return ok(toJson(itemEntities));
                        }
                );
    }

    /**
     * Creación de un nuevo item según los parametros de la petición POST /items
     * @return el item agregado
     */
    public CompletionStage<Result> createItem(){
        MessageDispatcher jdbcDispatcher = AkkaDispatcher.jdbcDispatcher;
        JsonNode nItem = request().body().asJson();
        ItemEntity item = Json.fromJson( nItem , ItemEntity.class ) ;
        return CompletableFuture.supplyAsync(
                ()->{
                    item.save();
                    return item;
                }
        ).thenApply(
                itemEntity -> {
                    return ok(Json.toJson(itemEntity));
                }
        );
    }

    //TODO delete, update, get particular

    /**
     * Borra un item con id particular dado por parametro. Atiende llamado de DELETE /items/{<[0-9]+>id}
     * @param id
     * @return OK de que el item fue borrado
     */
    public CompletionStage<Result> deleteItem(Long id) {
        MessageDispatcher jdbcDispatcher = AkkaDispatcher.jdbcDispatcher;

        return CompletableFuture.
                supplyAsync(
                        () -> {
                            ItemEntity.FINDER.ref(id).delete();
                            return ItemEntity.FINDER.byId(id);
                        }
                        , jdbcDispatcher)
                .thenApply(
                        itemEntity -> {
                            if(itemEntity == null)
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
     * Actualiza un item con id particular dado por parametro. Atiende llamado de PUT /items/: id
     * @param id identificador del item a actualizar
     * @return El item actualizado
     */
    public CompletionStage<Result> updateItem(Long id) {
        MessageDispatcher jdbcDispatcher = AkkaDispatcher.jdbcDispatcher;

        JsonNode nItem = request().body().asJson();
        ItemEntity item = Json.fromJson( nItem , ItemEntity.class ) ;
        ItemEntity itemViejo = ItemEntity.FINDER.byId(id);
        return CompletableFuture.supplyAsync(
                ()->{
                    if(itemViejo == null)
                    {
                        return itemViejo;
                    }else
                    {   item.setId(id);
                        item.update();
                        return item;
                    }
                }
        ).thenApply(
                itemEntity -> {
                    if(itemEntity == null)
                    {
                        return notFound();
                    }else
                    {
                    return ok(Json.toJson(itemEntity));
                    }
                }

        );
    }

    /**
     * Obtiene un item con id particular dado por parametro. Atiende llamado de GET /items/{<[0-9]+>id}
     * @param id
     * @return El item obtenido
     */
    public CompletionStage<Result> getItem(Long id) {
        MessageDispatcher jdbcDispatcher = AkkaDispatcher.jdbcDispatcher;

        return CompletableFuture.
                supplyAsync(
                        () -> {
                            return ItemEntity.FINDER.byId(id);
                        }
                        , jdbcDispatcher)
                .thenApply(
                        itemEntity -> {
                            return ok(toJson(itemEntity));
                        }
                );
    }

}