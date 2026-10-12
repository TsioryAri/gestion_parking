package com.example.parking.controller.web;

import com.example.parking.dto.request.PlaceRequest;
import com.example.parking.dto.response.PlaceResponse;
import com.example.parking.entity.StatutPlace;
import com.example.parking.entity.TypePlace;
import com.example.parking.exception.ConflictException;
import com.example.parking.service.PlaceService;
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
@RequestMapping("/places")
public class PlaceWebController {

    private final PlaceService placeService;

    public PlaceWebController(PlaceService placeService) {
        this.placeService = placeService;
    }

    @GetMapping
    public String lister(Model model) {
        model.addAttribute("places", placeService.listerPlaces());
        return "places/liste";
    }

    @GetMapping("/disponibles")
    public String disponibles(Model model) {
        model.addAttribute("places", placeService.listerPlacesDisponibles());
        return "places/disponibles";
    }

    @GetMapping("/nouvelle")
    public String formulaire(Model model) {
        PlaceRequest request = new PlaceRequest();
        request.setStatut(StatutPlace.LIBRE);
        model.addAttribute("placeRequest", request);
        preparerListes(model);
        return "places/formulaire";
    }

    @PostMapping
    public String creer(@Valid @ModelAttribute("placeRequest") PlaceRequest request,
                        BindingResult result,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        if (!result.hasErrors()) {
            try {
                PlaceResponse place = placeService.creerPlace(request);
                redirectAttributes.addFlashAttribute("succesMessage", "Place " + place.getNumero() + " créée.");
                return "redirect:/places";
            } catch (ConflictException e) {
                result.rejectValue("numero", "numero.existe", e.getMessage());
            }
        }
        preparerListes(model);
        return "places/formulaire";
    }

    private void preparerListes(Model model) {
        model.addAttribute("types", TypePlace.values());
        model.addAttribute("statuts", StatutPlace.values());
    }
}
