package com.example.parking.controller.rest;

import com.example.parking.dto.request.UtilisateurRequest;
import com.example.parking.dto.response.UtilisateurResponse;
import com.example.parking.service.UtilisateurService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UtilisateurRestController {

    private final UtilisateurService utilisateurService;

    public UtilisateurRestController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    @PostMapping
    public ResponseEntity<UtilisateurResponse> creer(@Valid @RequestBody UtilisateurRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(utilisateurService.creerUtilisateur(request));
    }

    @GetMapping
    public ResponseEntity<List<UtilisateurResponse>> lister() {
        return ResponseEntity.ok(utilisateurService.listerUtilisateurs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UtilisateurResponse> obtenir(@PathVariable Long id) {
        return ResponseEntity.ok(utilisateurService.obtenirUtilisateur(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UtilisateurResponse> modifier(@PathVariable Long id,
                                                        @Valid @RequestBody UtilisateurRequest request) {
        return ResponseEntity.ok(utilisateurService.modifierUtilisateur(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        utilisateurService.supprimerUtilisateur(id);
        return ResponseEntity.noContent().build();
    }
}
