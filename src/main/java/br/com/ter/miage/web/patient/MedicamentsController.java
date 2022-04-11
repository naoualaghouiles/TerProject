package br.com.ter.miage.web.patient;


import br.com.ter.miage.dto.MedicamentsDto;

import br.com.ter.miage.entities.*;
import br.com.ter.miage.services.interfaces.DossierMedicamentsService;
import br.com.ter.miage.services.interfaces.MedicamentsService;

import br.com.ter.miage.services.interfaces.PatientService;
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
public class MedicamentsController {


    @Autowired
    MedicamentsService medicamentsService;

    @Autowired
    PatientService patientService;

    @Autowired
    DossierMedicamentsService dossierMedicamentsService;


    @GetMapping("/ajoutMedicaments")
    public String showAjoutAdminForm(Model model, @RequestParam("patientId") Long patientId) {

        model.addAttribute("patient", patientService.getPatientById(patientId));

        return "ajoutMedicaments";
    }


    @ModelAttribute("medicaments")
    public MedicamentsDto medicamentsAddDto () {
        return new MedicamentsDto();

    }


    @PostMapping("/ajoutMedicaments")
    public String addMedicamentPost(@ModelAttribute("medicaments") MedicamentsDto medicamentsDto, @RequestParam("patientId") Long patientId) {
        Medicaments medicaments =medicamentsService.addMedicaments(medicamentsDto);

        Dossier_Medicaments dossierMedicaments = dossierMedicamentsService.ajouter(patientService.getPatientById(patientId), medicaments);
        if (dossierMedicaments!=null){
            return "redirect:/listeMedicaments?success";
        }else{
            return "redirect:/ajoutMedicaments?invalid";
        }

    }


    @GetMapping("/listeMedicaments")
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
            List<Medicaments> medicamentsList = dossierMedicamentsService.getPatientMedicaments(patient);
            model.addAttribute("patient", patient);
            model.addAttribute("medicamentsList",medicamentsList);
        }

        List<Medicaments> medicamentsList= medicamentsService.getAllMedicaments();



        return "listeMedicaments";
    }

    @GetMapping("deleteMedicaments")
    public String deleteMedicaments(Model model,@RequestParam("idMedicaments") Long idMedicaments){
        Object object = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Patient patient = null;
        if (object instanceof Patient) {
            patient = (Patient) object;
            patient = patientService.getPatientById(patient.getIdPatient());
        }
        if (patient == null) {
            return "redirect:/login";
        } else {
            dossierMedicamentsService.deleteDossierMedicaments(patient, medicamentsService.getMedicamentsByIdMedicaments(idMedicaments));
        }
        return "redirect:/listeMedicaments?deleteSuccess";
    }


}
