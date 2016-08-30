/**
 * Created by jd.fandino10 on 21/08/2016.
 */
package controllers;

import dispatchers.AkkaDispatcher;
import java.util.concurrent.CompletableFuture;
import static play.libs.Json.toJson;

import akka.dispatch.MessageDispatcher;
import play.mvc.*;
import java.util.concurrent.CompletionStage;
import play.libs.Json;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Clase controladora de la lista de deseos
 */
public class WishListController  extends Controller  {

    public CompletionStage<Result> getWishLists() {
        MessageDispatcher jdbcDispatcher = AkkaDispatcher.jdbcDispatcher;

        return CompletableFuture.
                supplyAsync(
                        () -> {
                            return WishListEntity.FINDER.all();
                        }
                        ,jdbcDispatcher)
                .thenApply(
                        wishListEntities -> {
                            return ok(toJson(wishListEntities));
                        }
                );
    }
    public CompletionStage<Result> createWishList(){
        MessageDispatcher jdbcDispatcher = AkkaDispatcher.jdbcDispatcher;
        JsonNode nWishList = request().body().asJson();
        System.out.println(nWishList);
        WishListEntity list = Json.fromJson( nWishList , WishListEntity.class ) ;
        return CompletableFuture.supplyAsync(
                ()->{
                    list.save();
                    return list;
                }
        ).thenApply(
                wishListEntities -> {
                    return ok(Json.toJson(wishListEntities));
                }
        );
    }

    public CompletionStage<Result> getWishList(Long id) {
        MessageDispatcher jdbcDispatcher = AkkaDispatcher.jdbcDispatcher;

        return CompletableFuture.
                supplyAsync(
                        () -> {
                            return WishListEntity.FINDER.byId(id);
                        }
                        ,jdbcDispatcher)
                .thenApply(
                        wishListEntities -> {
                            return ok(toJson(wishListEntities));
                        }
                );
    }

    public CompletionStage<Result> deteleWishList(Long id) {
        MessageDispatcher jdbcDispatcher = AkkaDispatcher.jdbcDispatcher;

        return CompletableFuture.
                supplyAsync(
                        () -> {
                            WishListEntity.FINDER.deleteById(id);
                            return ok();
                        }
                        ,jdbcDispatcher)
                .thenApply(
                        wishListEntities -> {
                            return ok();
                        }
                );
    }

    public CompletionStage<Result> updateWishList(Long id){
        MessageDispatcher jdbcDispatcher = AkkaDispatcher.jdbcDispatcher;
        JsonNode nWishList = request().body().asJson();
        WishListEntity list = Json.fromJson( nWishList , WishListEntity.class ) ;
        return CompletableFuture.supplyAsync(
                ()->{
                    list.setId(id);
                    list.update();
                    return ok();
                }
        ).thenApply(
                wishListEntities -> {
                    return ok();
                }
        );
    }
}
