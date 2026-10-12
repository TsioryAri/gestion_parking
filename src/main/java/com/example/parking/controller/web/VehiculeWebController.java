package com.example.parking.controller.web;

import com.example.parking.dto.request.VehiculeRequest;
import com.example.parking.dto.response.VehiculeResponse;
import com.example.parking.entity.TypeVehicule;
import com.example.parking.exception.ConflictException;
import com.example.parking.exception.ResourceNotFoundException;
import com.example.parking.service.UtilisateurService;
import com.example.parking.service.VehiculeService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/vehicules")
public class VehiculeWebController {

    private final VehiculeService vehiculeService;
    private final UtilisateurService utilisateurService;

    public VehiculeWebController(VehiculeService vehiculeService, UtilisateurService utilisateurService) {
        this.vehiculeService = vehiculeService;
        this.utilisateurService = utilisateurService;
    }

    @GetMapping
    public String lister(Model model) {
        model.addAttribute("vehicules", vehiculeService.listerVehicules());
        return "vehicules/liste";
    }

    @GetMapping("/nouveau")
    public String formulaire(Model model) {
        VehiculeRequest request = new VehiculeRequest();
        request.setTypeVehicule(TypeVehicule.VOITURE);
        model.addAttribute("vehiculeRequest", request);
        preparerListes(model);
        return "vehicules/formulaire";
    }

    @PostMapping
    public String creer(@Valid @ModelAttribute("vehiculeRequest") VehiculeRequest request,
                        BindingResult result,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        if (!result.hasErrors()) {
            try {
                VehiculeResponse vehicule = vehiculeService.creerVehicule(request);
                redirectAttributes.addFlashAttribute("succesMessage",
                        "Véhicule " + vehicule.getImmatriculation() + " enregistré.");
                return "redirect:/vehicules";
            } catch (ConflictException e) {
                result.rejectValue("immatriculation", "immatriculation.existe", e.getMessage());
            } catch (ResourceNotFoundException e) {
                result.rejectValue("utilisateurId", "proprietaire.introuvable", e.getMessage());
            }
        }
        preparerListes(model);
        return "vehicules/formulaire";
    }

    private void preparerListes(Model model) {
        model.addAttribute("types", TypeVehicule.values());
        model.addAttribute("utilisateurs", utilisateurService.listerUtilisateurs());
    }
}
