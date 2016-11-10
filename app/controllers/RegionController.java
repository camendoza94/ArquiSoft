package controllers;

import akka.dispatch.MessageDispatcher;
import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import com.fasterxml.jackson.databind.JsonNode;
import dispatchers.AkkaDispatcher;
import models.MedicionEntity;
import models.RegionEntity;
import models.SensorEntity;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static play.libs.Json.toJson;
@Restrict(@Group(Application.USER_ROLE))
public class pRegionController extends Controller {

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


    /**
     * Hace el map reduce de los sensores para ver cual envía info más frecuentemente
     * @return el id del sensor
     */
    public SensorEntity mapReduceSensores(List<SensorEntity> sensorEntities) {

        List<Long> frecuencias_reduce = new ArrayList<Long>();

        //MAP
        for(int i=0; i<sensorEntities.size(); i++) {
            SensorEntity sensor_i = sensorEntities.get(i);
            List<MedicionEntity> mediciones = sensor_i.getMediciones();
            int num_mediciones = mediciones.size();

            if (num_mediciones > 0) {
                List<Date> fechas_map = new ArrayList<Date>();
                for (int j = 0; j < num_mediciones; j++) {
                    MedicionEntity m = mediciones.get(i);
                    fechas_map.add(m.getFecha());
                }

                long frecuencia_media = 0L;

                if(num_mediciones>1) {
                    for (int j = 1; j < num_mediciones; j++) {
                        long frecuencia = fechas_map.get(j).getTime() - fechas_map.get(j - 1).getTime();
                        frecuencia_media += frecuencia;
                    }
                    frecuencia_media = frecuencia_media / (num_mediciones - 1);
                }
                else{
                    frecuencia_media = -1L;
                }

                frecuencias_reduce.add(frecuencia_media);
            } else {
                sensorEntities.remove(i);
            }
        }

        //REDUCE
        if(sensorEntities.size() > 0) {
            int index_0 = -1;
            int index_f = -1;
            long min_frecuencia = frecuencias_reduce.get(0);

            for(int i=0; i<sensorEntities.size(); i++) {
                long frecuencia = frecuencias_reduce.get(i);
                if(frecuencia == 0L){
                    index_0 = i;
                }
                else{
                    if(frecuencia<=min_frecuencia){
                        min_frecuencia = frecuencia;
                        index_f = i;
                    }
                }
            }

            if(index_f != -1){
                return sensorEntities.get(index_f);
            }
            else{
                return sensorEntities.get(index_0);
            }
        }
        else {
            return null;
        }
    }

}