package pj.gob.pe.judicial.model.sybase.dto;


import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Usuario SIJ Model")
@Entity
@SqlResultSetMapping(
        name = "UsuarioFullMapping",
        entities = @EntityResult(
                entityClass = UsuarioDTO.class,
                fields = {
                        @FieldResult(name = "dni", column = "c_dni"),
                        @FieldResult(name = "apePaterno", column = "c_ape_paterno"),
                        @FieldResult(name = "apeMaterno", column = "c_ape_materno"),
                        @FieldResult(name = "nombres", column = "c_nombres"),
                        @FieldResult(name = "usuario", column = "c_usuario"),
                })
)

@NamedNativeQueries({
        @NamedNativeQuery(
                name = "UsuarioQuery",
                query = " SELECT u.c_dni, " +
                        " u.c_ape_paterno, " +
                        " u.c_ape_materno, " +
                        " c_nombres, " +
                        " u.c_usuario " +
                        " FROM usuario u " +
                        " WHERE u.l_activo='S' " +
                        " and u.c_dni=:password " +
                        " and u.c_usuario=:username ",
                resultSetMapping = "UsuarioFullMapping"
        )
}) // @formatter:on


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioDTO implements Serializable{

    private static final long serialVersionUID = 1L;

    @Column(name="c_dni")
    private String dni;

    @Column(name="c_ape_paterno")
    private String apePaterno;

    @Column(name="c_ape_materno")
    private String apeMaterno;

    @Column(name="c_nombres")
    private String nombres;

    @Column(name="c_usuario")
    private String usuario;


}
