package controllers;

import dispatchers.AkkaDispatcher;
import java.util.concurrent.CompletableFuture;
import static play.libs.Json.toJson;

import akka.dispatch.MessageDispatcher;
import play.mvc.*;
import java.util.concurrent.CompletionStage;
import play.libs.Json;
import com.fasterxml.jackson.databind.JsonNode;

public class ProductController extends Controller {
    public CompletionStage<Result> getProducts() {
        MessageDispatcher jdbcDispatcher = AkkaDispatcher.jdbcDispatcher;

        return CompletableFuture.
                supplyAsync(
                        () -> {
                            return ProductEntity.FINDER.all();
                        }
                        , jdbcDispatcher)
                .thenApply(
                        productEntities -> {
                            return ok(toJson(productEntities));
                        }
                );
    }

    public CompletionStage<Result> createProduct(){
        MessageDispatcher jdbcDispatcher = AkkaDispatcher.jdbcDispatcher;
        JsonNode nProduct = request().body().asJson();
        ProductEntity product = Json.fromJson( nProduct , ProductEntity.class ) ;
        return CompletableFuture.supplyAsync(
                ()->{
                    product.save();
                    return product;
                }
        ).thenApply(
                productEntity -> {
                    return ok(Json.toJson(productEntity));
                }
        );
    }

    public CompletionStage<Result> updateProduct(Long id){
        MessageDispatcher jdbcDispatcher = AkkaDispatcher.jdbcDispatcher;
        JsonNode nProduct = request().body().asJson();
        ProductEntity product = Json.fromJson( nProduct , ProductEntity.class ) ;
        return CompletableFuture.supplyAsync(
                ()->{
                    product.setId(id);
                    product.update();
                    return ok();
                }
        ).thenApply(
                statusHeader -> statusHeader
        );
    }


    public CompletionStage<Result> deleteProduct(Long id){
        MessageDispatcher jdbcDispatcher = AkkaDispatcher.jdbcDispatcher;
        return CompletableFuture.supplyAsync(
                () -> {
                    ProductEntity.FINDER.deleteById(id);
                    return ok();
                })
        .thenApply(
                statusHeader -> statusHeader
        );
    }

    public CompletionStage<Result> getProduct(Long id) {
        MessageDispatcher jdbcDispatcher = AkkaDispatcher.jdbcDispatcher;

        return CompletableFuture.
                supplyAsync(
                        () -> {
                            return ProductEntity.FINDER.byId(id);
                        }
                        , jdbcDispatcher)
                .thenApply(
                        productEntity -> {
                            return ok(toJson(productEntity));
                        }
                );
    }
}