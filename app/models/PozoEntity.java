package models;

import com.avaje.ebean.Model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "pozoentity")
public class PozoEntity extends Model{

    public static Finder<Long, PozoEntity> FINDER = new Finder<>(PozoEntity.class);

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE,generator = "Pozo")
    private Long id;

    private int estado;

    //TODO relación entre sensores y pozo
    //@OneToMany(mappedBy = "pozo")
    //private SensorEntity[] sensores;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "campo_id")
    private CampoEntity campo;

    @OneToMany(mappedBy = "pozo")
    private List<ReporteEntity> reportes;

    public PozoEntity() {
    }

    public PozoEntity(int estado, CampoEntity campo, List<ReporteEntity> reportes) {
        this.estado = estado;
        this.campo = campo;
        this.reportes = reportes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public CampoEntity getCampo() {
        return campo;
    }

    public void setCampo(CampoEntity campo) {
        this.campo = campo;
    }

    public List<ReporteEntity> getReportes() {
        return reportes;
    }

    public void setReportes(List<ReporteEntity> reportes) {
        this.reportes = reportes;
    }

    @Override
    public String toString() {
        return "PozoEntity{" +
                "id=" + id +
        ", estado="+ estado+
        ", campo="+campo.toString()+
        ", reportes="+reportes.toString()+
                '}';
    }
}