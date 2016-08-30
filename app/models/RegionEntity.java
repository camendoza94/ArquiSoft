package models;

import com.avaje.ebean.Model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "regionentity")
public class RegionEntity extends Model{

    public static Finder<Long, RegionEntity> FINDER = new Finder<>(RegionEntity.class);

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE,generator = "Region")
    private Long id;

    private String nombre;

    private double area;

    @OneToMany(fetch=FetchType.LAZY, optional=false)
    private List<CampoEntity> campos;

    public RegionEntity() {

    }

    public RegionEntity(String nombre, double area, List<CampoEntity> campos) {
        this.nombre = nombre;
        this.area = area;
        this.campos = campos;
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

    public double getArea() {
        return area;
    }

    public void setArea(double area) {
        this.area = area;
    }

    public List<CampoEntity> getCampos() {
        return campos;                                                                                                                                                                                                                                                                                                                                                                                                                          s;
    }

    public void setCamposEntity(List<CampoEntity> campo) {
        this.campos = campos;
    }


    @Override
    public String toString() {
        return "RegionEntity{" +
                "id=" + id +
                ", nombre="+ nombre+
                ", area="+area+'}';
    }
}