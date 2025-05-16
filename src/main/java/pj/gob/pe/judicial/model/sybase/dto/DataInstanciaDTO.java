package pj.gob.pe.judicial.model.sybase.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Schema(description = "Data de Instancias SIJ Model")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataInstanciaDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name="c_instancia")
    private String codigoInstancia;

    @Column(name="c_distrito")
    private String codigoDistrito;

    @Column(name="c_provincia")
    private String codigoProvincia;

    @Column(name="c_org_jurisd")
    private String codigoOrganoJurisdiccional;

    @Column(name="x_nom_instancia")
    private String instancia;

    @Column(name="n_instancia")
    private Long nInstancia;

    @Column(name="x_ubicacion_fisica")
    private String ubicacion;

    @Column(name="x_corto")
    private String sigla;

    @Column(name="c_sede")
    private String codigoSede;

    @Column(name="c_ubigeo")
    private String codigoUbigeo;

    @Column(name="l_ind_baja")
    private String indicadorBaja;
}