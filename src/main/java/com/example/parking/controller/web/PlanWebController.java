package com.example.parking.controller.web;

import com.example.parking.entity.StatutPlace;
import com.example.parking.service.PlaceService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
public class PlanWebController {

    private final PlaceService placeService;

    public PlanWebController(PlaceService placeService) {
        this.placeService = placeService;
    }

    @GetMapping("/")
    public String accueil() {
        return "redirect:/plan";
    }

    @GetMapping("/plan")
    public String plan(Model model) {
        Map<StatutPlace, Long> compteurs = placeService.compterParStatut();
        model.addAttribute("plan", placeService.obtenirPlanParZone());
        model.addAttribute("libres", compteurs.get(StatutPlace.LIBRE));
        model.addAttribute("reservees", compteurs.get(StatutPlace.RESERVEE));
        model.addAttribute("occupees", compteurs.get(StatutPlace.OCCUPEE));
        model.addAttribute("horsService", compteurs.get(StatutPlace.HORS_SERVICE));
        return "plan";
    }
}
