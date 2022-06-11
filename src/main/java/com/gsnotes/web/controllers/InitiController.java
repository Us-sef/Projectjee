package com.gsnotes.web.controllers;

import com.gsnotes.utils.export.OperationDeliberation;
import com.gsnotes.utils.export.notesEtudiantExporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;


@Controller
public class InitiController {

	notesEtudiantExporter notes;

	@Autowired
	OperationDeliberation deliberation;

	@RequestMapping("/")
	public String index(Model model) {

		return "index";
	}

	@RequestMapping("/11")
	public String m() {

		return "deliber";
	}

	@RequestMapping("/deliberations")
	public void ModuleInfo(@RequestParam("anne") int annee, @RequestParam("niveau") String n,HttpServletResponse
						   response) throws IOException {

		notes=new notesEtudiantExporter("notes");
		deliberation.Processing(Long.valueOf(n),annee ,notes);
		response.setContentType("application/octect-stream");
		Date date=new Date();
		String header="Content-Disposition";
		String  value="attachment;filename=Deliberation_"+date+"_.xlsx";
		response.setHeader(header,value);

		notes.export(response);

	}
}
