package models;

import com.avaje.ebean.Model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "medicionentity")
public class MedicionEntity extends Model{

    public static Finder<Long, MedicionEntity> FINDER = new Finder<>(MedicionEntity.class);

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE,generator = "Medicion")
    private Long id;
    private double valor;
    private Date fecha;


    public MedicionEntity() {
        this.id=null;
        this.valor =-1;
        fecha=null;
    }

    public MedicionEntity(Long id, double valor) {
        this.id = id;
        this.valor = valor;
        fecha = new Date(System.currentTimeMillis());
    }

    public MedicionEntity(Long id, double valor, Date fecha) {
        this.id = id;
        this.valor = valor;
        this.fecha = fecha;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public Date getFecha() {return fecha;}

    public void setFecha(Date fecha){ this.fecha=fecha;}

    @Override
    public String toString() {
        return "MedicionEntity{" +
                "id=" + id +
                ", valor='" + valor + "\'"+
                ", fecha="+fecha.getTime()+"}";
    }
}