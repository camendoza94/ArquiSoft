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
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import java.security.Key;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import static play.libs.Json.toJson;

/**
 * Clase controladora de la lista de deseos
 */

public class SensorController extends Controller  {
    @Group({Application.USER_ROLE, Application.ADMIN_ROLE})
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

    @Restrict(@Group(Application.ADMIN_ROLE))
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

    @Group({Application.USER_ROLE,Application.ADMIN_ROLE})
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

    @Restrict(@Group(Application.ADMIN_ROLE))
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

    @Restrict(@Group(Application.ADMIN_ROLE))
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

            MedicionEntity list = Json.fromJson(nMedida , MedicionEntity.class);

            return CompletableFuture.supplyAsync(
                    ()->{
                        SensorEntity s=SensorEntity.FINDER.byId(id);
                        s.addMedicion(list);
                        list.save();
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

        //String jsonstring = nMedidaCifrada.toString();
        //System.out.println("jsonstring: "+jsonstring);
        //byte[] llaveByte = DatatypeConverter.parseHexBinary("2B7E151628AED2A6");
        //SecretKey llave = new SecretKeySpec(llaveByte,"DES");
        //byte[] jsonstringByte = jsonstring.getBytes();

        JsonNode mensaje = nMedidaCifrada.get("mensaje");
        JsonNode hmac = nMedidaCifrada.get("hmac");
        String mensajeString = mensaje.toString();
        String hmacString = hmac.toString();

        System.out.println("mensaje recibido: "+mensajeString);

        byte[] llaveByte = DatatypeConverter.parseHexBinary("2B7E151628AED2A6");
        SecretKey llave = new SecretKeySpec(llaveByte,"DES");
        byte[] mensajeByte = DatatypeConverter.parseBase64Binary(mensajeString);
        byte[] hmacByte = DatatypeConverter.parseBase64Binary(hmacString);

        JsonNode nMedida = null;

        try {
            //byte[] mensajeDescifrado = symmetricEncryption(jsonstringByte, llave);
            //PrintWriter out = new PrintWriter("filename.txt");
            //String mensajeDescifradoString = DatatypeConverter.printBase64Binary(mensajeDescifrado);
            //out.println(mensajeDescifradoString);
            //out.close();

            //SecretKey llaveSimetrica = keyGenGenerator();
            //System.out.println("longitud llave sim: "+llaveSimetrica.getEncoded().length);
            //System.out.println("longitud llave: "+llave.getEncoded().length);
            //System.out.println("longitud llave sim string: "+llaveSimetrica.toString());
            //System.out.println("longitud llave string: "+llave.toString());
            //byte[] mensajeDescifrado = symmetricEncryption(mensajeByte, llaveSimetrica);
            //byte[] m2 = DatatypeConverter.parseBase64Binary(mensajeDescifradoString);
            //byte[] mD2 = symmetricDecryption(m2, llave);
            //System.out.println("mensaje descifradooo inverso: "+new String(mD2));

            byte[] mensajeDescifrado = symmetricDecryption(mensajeByte, llave);
            String mensajeDescifradoString = new String(mensajeDescifrado);
            System.out.println("mensaje descifrado: "+ mensajeDescifradoString);

            ObjectMapper mapper = new ObjectMapper();
            nMedida = mapper.readTree(mensajeDescifradoString);

            //System.out.println("sirvio json valor: "+ nMedida.get("valor").toString());
            //System.out.println("sirvio json fecha: "+ nMedida.get("fecha").toString());

            //byte[] hashPrueba = hmacDigest(mensajeDescifrado, llave);
            //String hashPruebaString = DatatypeConverter.printBase64Binary(hashPrueba);
            //PrintWriter out = new PrintWriter("filename.txt");
            //out.println(hashPruebaString);
            //out.close();

            boolean esIntegro = verificarIntegridad(mensajeDescifrado, llave, hmacByte);

            if(!esIntegro){
                nMedida = null;
            }

        } catch (Exception e) {
            //e.printStackTrace();
            nMedida = null;
        }

        return nMedida;
    }

    public static byte[] symmetricEncryption (byte[] msg, Key key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        byte[] initVector = DatatypeConverter.parseHexBinary("0000000000000000");
        IvParameterSpec iv = new IvParameterSpec(initVector);
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        return cipher.doFinal(msg);
    }

    public static byte[] symmetricDecryption (byte[] msg, Key key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        byte[] initVector = DatatypeConverter.parseHexBinary("0000000000000000");
        IvParameterSpec iv = new IvParameterSpec(initVector);
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        return cipher.doFinal(msg);
    }

    public static boolean verificarIntegridad(byte[] msg, Key key, byte [] hash ) throws Exception {
        byte [] nuevo = hmacDigest(msg, key);

        if (nuevo.length != hash.length) {
            return false;
        }
        for (int i = 0; i < nuevo.length ; i++) {
            if (nuevo[i] != hash[i]){
                return false;
            }
        }
        return true;
    }

    public static byte[] hmacDigest(byte[] msg, Key key) throws NoSuchAlgorithmException, InvalidKeyException, IllegalStateException, UnsupportedEncodingException {
        Mac mac = Mac.getInstance("HMACSHA256");
        mac.init(key);

        byte[] bytes = mac.doFinal(msg);
        return bytes;
    }

    public static SecretKey keyGenGenerator() throws NoSuchAlgorithmException {

        int tamLlave = 56;

        KeyGenerator keyGen;
        SecretKey key;
        keyGen = KeyGenerator.getInstance("DES");
        keyGen.init(tamLlave);
        key = keyGen.generateKey();
        return key;
    }

    @Group({Application.USER_ROLE, Application.ADMIN_ROLE})
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

    @Group({Application.USER_ROLE,Application.ADMIN_ROLE})
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

    @Group({Application.USER_ROLE,Application.ADMIN_ROLE})
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

    @Restrict(@Group(Application.ADMIN_ROLE))
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

    @Restrict(@Group(Application.ADMIN_ROLE))
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
