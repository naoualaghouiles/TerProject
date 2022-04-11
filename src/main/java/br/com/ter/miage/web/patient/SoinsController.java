package br.com.ter.miage.web.patient;


import br.com.ter.miage.dto.DirecteurDto;
import br.com.ter.miage.dto.SoinsDto;
import br.com.ter.miage.entities.Directeur;
import br.com.ter.miage.entities.Dossier_Soins;
import br.com.ter.miage.entities.Patient;
import br.com.ter.miage.entities.Soins;
import br.com.ter.miage.services.interfaces.DirecteurService;
import br.com.ter.miage.services.interfaces.DossierSoinsService;
import br.com.ter.miage.services.interfaces.PatientService;
import br.com.ter.miage.services.interfaces.SoinsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

@Controller
public class SoinsController {
    

    @Autowired
    PatientService patientService;

    @Autowired
    DossierSoinsService dossierSoinsService;


    @Autowired
    SoinsService soinsService;


    @GetMapping("/ajoutSoins")
    public String showAjoutAdminForm(Model model, @RequestParam("patientId") Long patientId) {

        model.addAttribute("patient", patientService.getPatientById(patientId));

        return "ajoutSoins";
    }


    @ModelAttribute("soins")
    public SoinsDto soinsAddDto () {
        return new SoinsDto();

    }


    @PostMapping("/ajoutSoins")
    public String addSoinsPost(@ModelAttribute("soins") SoinsDto soinsDto, @RequestParam("patientId") Long patientId) {
        Soins soins =soinsService.addSoin(soinsDto);

        Dossier_Soins dossierSoins = dossierSoinsService.ajouter(patientService.getPatientById(patientId), soins);
        if (dossierSoins!=null){
            return "redirect:/listeSoins?success";
        }else{
            return "redirect:/ajoutSoins?invalid";
        }

    }


    @GetMapping("/listeSoins")
    public String showListerPage( Model model) {
        Object object = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Patient patient = null;
        if (object instanceof Patient) {
            patient = (Patient) object;
            patient = patientService.getPatientById(patient.getIdPatient());
        }
        if (patient == null) {
            return "redirect:/login";
        } else {
            List<Soins> soinsList = dossierSoinsService.getPatientSoins(patient);
            model.addAttribute("patient", patient);
            model.addAttribute("soinsList", soinsList);
        }

        return "listeSoins";
    }

    @GetMapping("deleteSoins")
    public String deleteSoins(Model model,@RequestParam("idSoin") Long idSoin){
        Object object = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Patient patient = null;
        if (object instanceof Patient) {
            patient = (Patient) object;
            patient = patientService.getPatientById(patient.getIdPatient());
        }
        if (patient == null) {
            return "redirect:/login";
        } else {
            dossierSoinsService.deleteDossierSoins(patient, soinsService.getSoins(idSoin));
        }
        return "redirect:/listeSoins?deleteSuccess";
    }



}
