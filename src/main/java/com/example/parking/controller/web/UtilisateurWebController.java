package com.example.parking.controller.web;

import com.example.parking.dto.request.UtilisateurRequest;
import com.example.parking.dto.response.UtilisateurResponse;
import com.example.parking.entity.TypeUtilisateur;
import com.example.parking.exception.ConflictException;
import com.example.parking.service.UtilisateurService;
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
@RequestMapping("/utilisateurs")
public class UtilisateurWebController {

    private final UtilisateurService utilisateurService;

    public UtilisateurWebController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    @GetMapping
    public String lister(Model model) {
        model.addAttribute("utilisateurs", utilisateurService.listerUtilisateurs());
        return "utilisateurs/liste";
    }

    @GetMapping("/nouveau")
    public String formulaire(Model model) {
        UtilisateurRequest request = new UtilisateurRequest();
        request.setTypeUtilisateur(TypeUtilisateur.STANDARD);
        model.addAttribute("utilisateurRequest", request);
        model.addAttribute("types", TypeUtilisateur.values());
        return "utilisateurs/formulaire";
    }

    @PostMapping
    public String creer(@Valid @ModelAttribute("utilisateurRequest") UtilisateurRequest request,
                        BindingResult result,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        if (!result.hasErrors()) {
            try {
                UtilisateurResponse utilisateur = utilisateurService.creerUtilisateur(request);
                redirectAttributes.addFlashAttribute("succesMessage", "Utilisateur " + utilisateur.getNom() + " créé.");
                return "redirect:/utilisateurs";
            } catch (ConflictException e) {
                result.rejectValue("email", "email.existe", e.getMessage());
            }
        }
        model.addAttribute("types", TypeUtilisateur.values());
        return "utilisateurs/formulaire";
    }
}
