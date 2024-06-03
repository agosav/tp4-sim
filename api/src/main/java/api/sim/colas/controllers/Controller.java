package api.sim.colas.controllers;

import api.sim.colas.dtos.ParametrosDto;
import api.sim.colas.simulacion.Gestor;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/colas")
@RequiredArgsConstructor
@CrossOrigin("*")
public class Controller {

    private final Gestor gestorSimulacion;

    @PostMapping("/simular")
    public ResponseEntity<?> simular(@Valid @RequestBody ParametrosDto parametros, BindingResult result) {
        if (result.hasErrors()) {
            List<String> errores = new ArrayList<>();
            for (FieldError error : result.getFieldErrors()) {
                errores.add(error.getField() + " " + error.getDefaultMessage());
            }
            return new ResponseEntity<>(errores, HttpStatus.BAD_REQUEST);
        }

        try {
            return ResponseEntity.ok(gestorSimulacion.realizarSimulacion(parametros));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
