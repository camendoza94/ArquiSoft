/**
 * Created by jd.fandino10 on 21/08/2016.
 */
package controllers;

import akka.dispatch.MessageDispatcher;
import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dispatchers.AkkaDispatcher;
import models.MedicionEntity;
import models.PozoEntity;
import models.SensorEntity;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import java.security.Key;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import static play.libs.Json.toJson;

/**
 * Clase controladora de la lista de deseos
 */

public class SensorController extends Controller  {
    @Restrict(@Group(Application.USER_ROLE))
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
    @Restrict(@Group(Application.USER_ROLE))
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
    @Restrict(@Group(Application.USER_ROLE))
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
    @Restrict(@Group(Application.USER_ROLE))
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
    @Restrict(@Group(Application.USER_ROLE))
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
        JsonNode nMedidaCifrada = request().body().asJson();

        JsonNode nMedida = descifrarJsonMedicion(nMedidaCifrada);

        if(nMedida != null){
            MedicionEntity list = Json.fromJson( nMedida , MedicionEntity.class ) ;
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
        else{
            return CompletableFuture.supplyAsync(
                    ()->{
                        return unauthorized("No esta autorizado a registrar esa medicion o hizo una peticion invalida.");
                    }
            );
        }
    }

    private JsonNode descifrarJsonMedicion(JsonNode nMedidaCifrada) {

        JsonNode mensaje = nMedidaCifrada.get("mensaje");
        JsonNode hmac = nMedidaCifrada.get("hmac");

        int tamanioLlave = 64;
        byte[] llaveByte = DatatypeConverter.parseHexBinary("2B7E151628AED2A6ABF7158809CF4F3C");

        System.out.println("longitudddd "+llaveByte.length);

        //Key llave = new SecretKeySpec(llaveByte,0,tamanioLlave, "DES");
        Key llave = new SecretKeySpec(llaveByte,"DES");
        byte[] mensajeByte = mensaje.toString().getBytes();
        byte[] hmacByte = hmac.toString().getBytes();

        boolean esIntegro = false;

        //TODO mirar si es integro
        esIntegro = true; //quitar
//        try {
//            byte[] hmacDescifrado = symmetricEncryption(hmacByte, llave);
//            System.out.println("hmac descifradooo: "+new String(hmacDescifrado));
//        } catch (Exception e) {
//            //jmm
//        }

        JsonNode nMedida = null;

        if(esIntegro){
            try {
                byte[] mensajeDescifrado = symmetricEncryption(mensajeByte, llave);
                System.out.println("mensjae descifradooo: "+new String(mensajeDescifrado));
                ObjectMapper mapper = new ObjectMapper();
                nMedida = mapper.valueToTree(mensajeDescifrado);
                //String primerSplit = mensajeDescifrado.split(":")[1];
                //String segundoSplit = primerSplit.split(":")[1];
                //int valor = Integer.parseInt(primerSplit.split(",")[0]);
                //long fecha = Long.parseLong(segundoSplit.split("}")[0]);
                //ObjectMapper mapper = new ObjectMapper();
                //nMedida = mapper.valueToTree("{\"valor\":"+valor+"}");
            } catch (Exception e) {
                e.printStackTrace();
                nMedida = null;
            }
        }

        return nMedida;
    }

    public static byte[] symmetricEncryption (byte[] msg, Key key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        byte[] initVector = DatatypeConverter.parseHexBinary("0000000000000000");
        IvParameterSpec iv = new IvParameterSpec(initVector);
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        return cipher.doFinal(msg);
    }

    @Restrict(@Group(Application.USER_ROLE))
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
    @Restrict(@Group(Application.USER_ROLE))
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
    @Restrict(@Group(Application.USER_ROLE))
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
    @Restrict(@Group(Application.USER_ROLE))
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
    @Restrict(@Group(Application.USER_ROLE))
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
