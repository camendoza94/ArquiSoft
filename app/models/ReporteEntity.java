package models;

import com.avaje.ebean.Model;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "reporteentity")
public class ReporteEntity extends Model{

    public static Finder<Long, ReporteEntity> FINDER = new Finder<>(ReporteEntity.class);

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE,generator = "Reporte")
    private Long id;

    private Date fechaGeneracion;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="campo_id")
    private CampoEntity campo;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="pozo_id")
    private PozoEntity pozo;

    public ReporteEntity() {
    }

    public ReporteEntity(Date fechaGeneracion, CampoEntity campo, PozoEntity pozo) {
        this.fechaGeneracion = fechaGeneracion;
        this.campo = campo;
        this.pozo = pozo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getFechaGeneracion() {
        return fechaGeneracion;
    }

    public void setFechaGeneracion(Date fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }

    public CampoEntity getCampo() {
        return campo;
    }

    public void setCampo(CampoEntity campo) {
        this.campo = campo;
    }

    public PozoEntity getPozo() {
        return pozo;
    }

    public void setPozo(PozoEntity pozo) {
        this.pozo = pozo;
    }

    @Override
    public String toString() {
        return "ReporteEntity{" +
                "id=" + id +
        ", fechaGeneracion="+ fechaGeneracion.toString()+
        ", campo="+campo.toString()+
        ", pozo="+pozo.toString()+
                '}';
    }
}