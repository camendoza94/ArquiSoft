package models;

import com.avaje.ebean.Model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "campoentity")
public class CampoEntity extends Model{

    public static Finder<Long, CampoEntity> FINDER = new Finder<>(CampoEntity.class);

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE,generator = "Campo")
    private Long id;

    private String nombre;

    private double latitud;

    private double longitud;

    //TODO relación entre región y campo
    //@ManyToOne(fetch=FetchType.LAZY, optional=false)
    //@JoinColumn(name="region_id")
    //private RegionEntity region;

    //TODO relación entre Usuarios y campo
    @OneToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="jefeproduccion_id")
    private UsuarioEntity jefeProduccion;

    @OneToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="jefecampo_id")
    private UsuarioEntity jefeCampo;

    @OneToMany(mappedBy = "campo")
    private List<PozoEntity> pozos;

    public CampoEntity() {

    }

    public CampoEntity(String nombre, double latitud, double longitud, List<PozoEntity> pozos) {

        this.nombre = nombre;
        this.latitud = latitud;
        this.longitud = longitud;
        this.pozos = pozos;
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

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public List<PozoEntity> getPozos() {
        return pozos;
    }

    public void setPozos(List<PozoEntity> pozos) { this.pozos = pozos; }

    @Override
    public String toString() {
        return "CampoEntity{" +
                "id=" + id +
        ", nombre="+ nombre+
        ", latitud="+latitud+
        ", longitud="+longitud+
        ", pozos="+pozos.toString()+
                '}';
    }
}