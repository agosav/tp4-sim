package api.sim.colas.controllers;

import api.sim.colas.dtos.RequestDto;
import api.sim.colas.simulacion.Gestor;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/colas")
@RequiredArgsConstructor
@CrossOrigin("*")
public class Controller {

    private final Gestor gestorSimulacion;

    @PostMapping("/simular")
    public ResponseEntity<?> simular(@Valid @RequestBody RequestDto parametros, BindingResult result) {
        if (result.hasErrors()) {
            StringBuilder errores = new StringBuilder();
            for (FieldError error : result.getFieldErrors()) {
                errores.append(error.getField()).append(" ").append(error.getDefaultMessage()).append(". ");
            }
            return new ResponseEntity<>(String.valueOf(errores), HttpStatus.BAD_REQUEST);
        }

        try {
            return ResponseEntity.ok(gestorSimulacion.realizarSimulacion(parametros));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/rungekutta")
    public ResponseEntity<?> getTablaRK() {
        return ResponseEntity.ok(gestorSimulacion.getRungeKutta().getTabla());
    }
}
