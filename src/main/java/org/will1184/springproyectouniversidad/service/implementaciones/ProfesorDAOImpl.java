package org.will1184.springproyectouniversidad.service.implementaciones;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.will1184.springproyectouniversidad.model.entity.Persona;
import org.will1184.springproyectouniversidad.model.entity.Profesor;
import org.will1184.springproyectouniversidad.repository.PersonaRepository;
import org.will1184.springproyectouniversidad.repository.ProfesorRepository;
import org.will1184.springproyectouniversidad.service.contratos.ProfesorDAO;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProfesorDAOImpl extends PersonaDAOImpl implements ProfesorDAO {
    private ProfesorRepository repository;
    @Autowired
    public ProfesorDAOImpl(@Qualifier("profesorRepository") PersonaRepository repository) {
        super(repository);
        this.repository= (ProfesorRepository) repository;
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<Persona> findAll() {
        // Llamada al método findAll() del repositorio que devuelve todas las personas (incluidos profesores y alumnos)
        Iterable<Persona> personas = super.findAll();

        // Filtrar las personas para obtener solo las instancias de Profesor
        List<Persona> profesores = new ArrayList<>();
        for (Persona persona : personas) {
            if (persona instanceof Profesor) {
                profesores.add((Profesor) persona);
            }
        }
        return profesores;
    }
    @Override
    @Transactional(readOnly = true)
    public Iterable<Persona> buscarProfesoresPorCarrera(String carrera) {
         return  repository.buscarProfesoresPorCarrera(carrera);
    }
}