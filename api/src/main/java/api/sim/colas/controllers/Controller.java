package api.sim.colas.controllers;

import api.sim.colas.dtos.ParametrosDto;
import api.sim.colas.services.Simulacion;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/colas")
@RequiredArgsConstructor
@CrossOrigin("*")
public class Controller {

    private final Simulacion simulacionService;

    @GetMapping("/simular")
    public ResponseEntity<?> simular(@RequestBody ParametrosDto parametros) {
        try {
            return ResponseEntity.ok(simulacionService.realizarSimulacion(parametros));

        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Error: " + e.getMessage());
        }
    }
}
