package models;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "sensorentity")
public class SensorEntity extends Model{

    public static Finder<Long, SensorEntity> FINDER = new Finder<>(SensorEntity.class);

    public static final int FLUIDO = 0;
    public static final int ENERGIA = 1;
    public static final int TEMPERATURA = 2;
    public static final int EMERGENCIA = 3;

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE,generator = "Sensor")
    private Long id;
    private String nombre;
    private int tipo_sensor;
    @OneToMany(cascade = CascadeType.ALL)
    private List<MedicionEntity> mediciones;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JsonBackReference
    private PozoEntity pozo;


    public SensorEntity() {
        this.id=null;
        this.nombre ="NO NAME";
    }

    public SensorEntity(Long id) {
        this();
        this.id = id;
    }

    public SensorEntity(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getTipo() {return tipo_sensor;}

    public void setTipo(int tipo_sensor){ this.tipo_sensor=tipo_sensor;}

    public List<MedicionEntity> getMediciones(){ return mediciones;}

    public void setMediciones(List<MedicionEntity> mediciones){ this.mediciones=mediciones;}

    public void addMedicion(MedicionEntity medicion){ medicion.setSensor(this);}//this.mediciones.add(medicion); }


    public PozoEntity getPozo() {
        return pozo;
    }

    public void setPozo(PozoEntity pozo) {
        this.pozo = pozo;
    }

    @Override
    public String toString() {
        return "SensorEntity{" +
                "id=" + id +
                ", nombre='" + nombre + "\'"+
                ", tipo="+tipo_sensor+
                ", mediciones="+mediciones.toString()+"}";
    }
}