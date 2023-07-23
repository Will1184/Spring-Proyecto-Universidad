package org.will1184.springproyectouniversidad.controller;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import org.will1184.springproyectouniversidad.exception.BadRequestException;
import org.will1184.springproyectouniversidad.model.entity.Carrera;
import org.will1184.springproyectouniversidad.model.entity.Persona;
import org.will1184.springproyectouniversidad.model.entity.Profesor;
import org.will1184.springproyectouniversidad.service.contratos.CarreraDAO;
import org.will1184.springproyectouniversidad.service.contratos.PersonaDAO;
import org.will1184.springproyectouniversidad.service.contratos.ProfesorDAO;

import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/Profesores")
public class ProfesorController extends PersonaController{
    private final CarreraDAO carreraDAO;

    public ProfesorController(@Qualifier("profesorDAOImpl") PersonaDAO service, CarreraDAO carreraDAO) {
        super(service);
        this.carreraDAO = carreraDAO;
        nombreEntidad="Profesor";
    }
    @GetMapping("/profesores-carreras")
    public Iterable<Persona> buscarProfesoresPorCarrera(@RequestBody String carrera){
        return ((ProfesorDAO)service).buscarProfesoresPorCarrera(carrera);
    }

    @PutMapping("/{id}")
    public Persona actualizarProfesor(@PathVariable Integer id, @RequestBody Persona profesor){
        Persona profesorUpdate = null;
        Optional<Persona> oProfesor = service.findById(id);
        if(!oProfesor.isPresent()) {
            throw new BadRequestException(String.format("Profesor con id %d no existe", id));
        }
        profesorUpdate = oProfesor.get();
        profesorUpdate.setNombre(profesor.getNombre());
        profesorUpdate.setApellido(profesor.getApellido());
        profesorUpdate.setDireccion(profesor.getDireccion());
        return service.save(profesorUpdate);
    }

    @PutMapping("/{idProfesor}/carrera/{idCarrera}")
    public Persona asignarCarreraProfesor(@PathVariable Integer idProfesor, @PathVariable Integer idCarrera){
        Optional<Persona> oProfesor = service.findById(idProfesor);
        if(!oProfesor.isPresent()) {
            throw new BadRequestException(String.format("Profesor con id %d no existe", idProfesor));
        }
        Optional<Carrera> oCarrera = carreraDAO.findById(idCarrera);
        if(!oCarrera.isPresent()){
            throw new BadRequestException(String.format("Carrera con id %d no existe", idCarrera));
        }

        Persona profesor = oProfesor.get();
        Carrera carrera = oCarrera.get();

        ((Profesor)profesor).setCarreras((Set<Carrera>) carrera);

        return service.save(profesor);
    }
}
