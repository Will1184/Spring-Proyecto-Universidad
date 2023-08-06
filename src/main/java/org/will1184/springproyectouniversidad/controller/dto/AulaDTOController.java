package org.will1184.springproyectouniversidad.controller.dto;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.will1184.springproyectouniversidad.model.dto.AulaDTO;
import org.will1184.springproyectouniversidad.model.entity.Alumno;
import org.will1184.springproyectouniversidad.model.entity.Aula;
import org.will1184.springproyectouniversidad.model.entity.Pabellon;
import org.will1184.springproyectouniversidad.model.enums.Pizarron;
import org.will1184.springproyectouniversidad.model.mapper.mapstruct.AulaMapper;
import org.will1184.springproyectouniversidad.service.contratos.AulaDAO;
import org.will1184.springproyectouniversidad.service.contratos.PabellonDAO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/aulas")
@Tag(name = "Aulas", description = "Catálogo de aulas")
@ConditionalOnProperty(prefix = "app",name = "controller.enable-dto",havingValue = "true")
public class AulaDTOController extends  GenericDTOController<Aula,AulaDAO>{

    private final AulaMapper aulaMapper;
    private final PabellonDAO pabellonDAO;
    public AulaDTOController(AulaDAO service, AulaMapper aulaMapper, PabellonDAO pabellonDAO) {
        super(service, "aula");
        this.aulaMapper = aulaMapper;
        this.pabellonDAO = pabellonDAO;
    }

    @Operation(summary = "Obtiene todos los registros")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "todos los registros de alumnos",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Alumno.class)))),
    })
    @GetMapping
    public ResponseEntity<?> findAllAulas(){
        Map<String, Object> mensaje = new HashMap<>();
        List<Aula> aulas = super.findAll();
        List<AulaDTO> dtos = aulas.stream()
                        .map(aulaMapper::mapAula)
                        .collect(Collectors.toList());

        mensaje.put("succes", Boolean.TRUE);
        mensaje.put("data", dtos);
        return ResponseEntity.ok().body(mensaje);
    }

    @Operation(summary = "Obtiene el registro por id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro obtenido",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Alumno.class)))),
            @ApiResponse(responseCode = "400", description = "No existe registro con id",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Alumno.class)))),
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> findAulaId(@PathVariable Integer id) {

        Map<String, Object> mensaje = new HashMap<>();
        Optional<Aula> optionalAula= super.findId(id);
        Aula aula;
        AulaDTO dto;

        if (optionalAula == null || optionalAula.isEmpty()) {
            mensaje.put("succes", Boolean.FALSE);
            mensaje.put("mensaje", String.format("No existe %s con Id %d", nombre_entidad, id));
            return ResponseEntity.badRequest().body(mensaje);
        }else {
            aula = optionalAula.get();
        }
        dto = aulaMapper.mapAula(aula);

        mensaje.put("succes", Boolean.TRUE);
        mensaje.put("data", dto);
        return ResponseEntity.ok().body(mensaje);
    }

    @Operation(summary = "Crea un registro")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "se creo el registro de alumno",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Alumno.class)))),
            @ApiResponse(responseCode = "422", description = "No se pudo crear el registro: Datos no validos",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Alumno.class)))),
    })
    @PostMapping
    public ResponseEntity<?> createAula(@Valid @RequestBody AulaDTO aulaDTO, BindingResult result){

        Map<String,Object> mensaje = new HashMap<>();

        if (result.hasErrors()){
            mensaje.put("success",Boolean.FALSE);
            mensaje.put("validaciones",super.obtenerValidaciones(result));
            return ResponseEntity.unprocessableEntity().body(mensaje);
        }

        Aula aula = aulaMapper.mapAula(aulaDTO);

        mensaje.put("success",Boolean.TRUE);
        mensaje.put("data",super.createEntidad(aula));
        return ResponseEntity.status(HttpStatus.CREATED).body(mensaje);
    }

    @Operation(summary = "Modifica un registro")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Se modifico el registro ",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Alumno.class)))),
            @ApiResponse(responseCode = "422", description = "No se pudo crear el registro: Datos no validos",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Alumno.class)))),
            @ApiResponse(responseCode = "400", description = "No existe registro con id",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Alumno.class)))),
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAula(@PathVariable Integer id,
                                              @Valid @RequestBody AulaDTO aulaDTO,BindingResult result){

        Map<String,Object> mensaje = new HashMap<>();
        Optional<Aula> optionalAula = super.findId(id);
        Aula aula;
        AulaDTO dto;

        if (result.hasErrors()){
            mensaje.put("success",Boolean.FALSE);
            mensaje.put("validaciones",super.obtenerValidaciones(result));
            return ResponseEntity.unprocessableEntity().body(mensaje);
        }

        if(optionalAula == null || optionalAula.isEmpty()) {
            mensaje.put("success",Boolean.FALSE);
            mensaje.put("mensaje",String.format("%s con id %d no existe",nombre_entidad, id));
            return ResponseEntity.badRequest().body(mensaje);
        }else {
            aula = optionalAula.get();
        }
        dto = aulaMapper.mapAula(aula);
        dto.setNroAula(aulaDTO.getNroAula());
        dto.setPizarron(aulaDTO.getPizarron());
        dto.setMedidas(aulaDTO.getMedidas());
        dto.setCantPupitres(aulaDTO.getCantPupitres());
        aula = aulaMapper.mapAula(dto);

        mensaje.put("success",Boolean.TRUE);
        mensaje.put("data",super.createEntidad(aula));
        return ResponseEntity.ok().body(mensaje);
    }

    @Operation(summary = "Borra un registro ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Se borro el registro ",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Alumno.class)))),
            @ApiResponse(responseCode = "400", description = "No existe registro con id",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Alumno.class)))),
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAlumnoId(@PathVariable Integer id){

        Map<String,Object> mensaje = new HashMap<>();
        Optional<Aula> optionalAula = super.findId(id);

        if(optionalAula == null || optionalAula.isEmpty()) {
            mensaje.put("success", Boolean.FALSE);
            mensaje.put("mensaje",  String.format("No existe %s con Id %d", nombre_entidad, id));
            return ResponseEntity.badRequest().body(mensaje);
        }

        super.deleteById(id);
        mensaje.put("success",Boolean.TRUE);
        mensaje.put("mensaje",  String.format("Se borro %s con Id %d", nombre_entidad, id));
        return ResponseEntity.status(HttpStatus.OK).body(mensaje);
    }

    @Operation(summary = "Encuentra aulas por pizarron")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Se encontro el registro ",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Alumno.class)))),
            @ApiResponse(responseCode = "400", description = "No existe registro con ese pizarron",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Alumno.class)))),
    })
    @PostMapping("/aulas-pizarras")
    public  ResponseEntity<?>findAulasByPizarron(@Valid @RequestBody Pizarron pizarron,BindingResult result){
        Map<String,Object> mensaje = new HashMap<>();

        if (result.hasErrors()){
            mensaje.put("success",Boolean.FALSE);
            mensaje.put("validaciones",super.obtenerValidaciones(result));
            return ResponseEntity.badRequest().body(mensaje);
        }
        List<Aula> aulas = (List<Aula>) service.findAulasByPizarron(pizarron);
        if (aulas.isEmpty()){
            mensaje.put("success",Boolean.FALSE);
            mensaje.put("mensaje",String.format("No existen aulas con pizarron: %s",pizarron));
            return ResponseEntity.badRequest().body(mensaje);
        }
        List<AulaDTO> dtos = aulas.stream()
                .map(aulaMapper::mapAula)
                .collect(Collectors.toList());

        mensaje.put("success",Boolean.TRUE);
        mensaje.put("data",dtos);
        return ResponseEntity.ok().body(mensaje);
    }

    @Operation(summary = "Encuentra aulas por pabellon")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Se encontro el registro ",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Alumno.class)))),
            @ApiResponse(responseCode = "400", description = "No existe registro con ese pabellon",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Alumno.class)))),
    })
    @PostMapping("/aulas-pabellon")
    public ResponseEntity<?> findAulasByPabellonNombre(@RequestBody String nombre){
        Map<String,Object> mensaje = new HashMap<>();
        List<Aula> aulas = (List<Aula>) service.findAulasByPabellonNombre(nombre);
        if (aulas.isEmpty()){
            mensaje.put("success",Boolean.FALSE);
            mensaje.put("mensaje",String.format("No existen aulas en ese pabellon: %s",nombre));
            return ResponseEntity.badRequest().body(mensaje);
        }
        List<AulaDTO> dtos = aulas.stream()
                        .map(aulaMapper::mapAula)
                        .collect(Collectors.toList());

        mensaje.put("success",Boolean.TRUE);
        mensaje.put("data",dtos);
        return ResponseEntity.ok().body(mensaje);
    }
    @Operation(summary = "Encuentra aulas por nroAula")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Se encontro el registro ",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Alumno.class)))),
            @ApiResponse(responseCode = "400", description = "No existe registro con ese nro",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Alumno.class)))),
    })
    @GetMapping("/nroaulas/{nroAula}")
    public ResponseEntity<?> findAulaByNroAula(@PathVariable Integer nroAula){
        Map<String,Object> mensaje = new HashMap<>();
        Optional<Aula> optionalAula = service.findAulaByNroAula(nroAula);
        Aula aula;
        AulaDTO dto;

        if(optionalAula == null || optionalAula.isEmpty()) {
            mensaje.put("success", Boolean.FALSE);
            mensaje.put("mensaje",  String.format("No existe %s con numero de aula: %d", nombre_entidad, nroAula));
            return ResponseEntity.badRequest().body(mensaje);
        }else {
            aula = optionalAula.get();
        }
        dto = aulaMapper.mapAula(aula);

        mensaje.put("success",Boolean.TRUE);
        mensaje.put("data",dto);
        return ResponseEntity.ok().body(mensaje);
    }

    @Operation(summary = "Asigna aula a un pabellon")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Se encontro el registro ",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Alumno.class)))),
            @ApiResponse(responseCode = "400", description = "No existe registro con ese id",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Alumno.class)))),
    })
    @PutMapping("/{idAula}/pabellon/{idPabellon}")
    public ResponseEntity<?> assignPabellonAula(@PathVariable Integer idAula, @PathVariable Integer idPabellon){

        Map<String,Object> mensaje = new HashMap<>();
        Optional<Aula> optionalAula = service.findById(idAula);
        Optional<Pabellon> oPabellon = pabellonDAO.findById(idPabellon);
        Aula aula;
        Pabellon pabellon;

        if(optionalAula == null || optionalAula.isEmpty()) {
            mensaje.put("success",Boolean.FALSE);
            mensaje.put("mensaje",String.format("%s con id %d no existe",nombre_entidad, idAula));
            return ResponseEntity.badRequest().body(mensaje);
        }

        if(oPabellon.isEmpty()){
            mensaje.put("success",Boolean.FALSE);
            mensaje.put("mensaje",String.format("%s con id %d no existe",nombre_entidad, idAula));
            return ResponseEntity.badRequest().body(mensaje);
        }

        pabellon = oPabellon.get();
        aula = optionalAula.get();
        aula.setPabellon(pabellon);

        mensaje.put("success",Boolean.TRUE);
        mensaje.put("data",super.createEntidad(aula));
        return ResponseEntity.ok().body(mensaje);
    }

}
