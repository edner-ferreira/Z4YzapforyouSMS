package br.com.Z4Yzapforyou.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.tomcat.util.http.fileupload.FileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.json.simple.JSONValue;

import br.com.Z4Yzapforyou.model.Session;
import br.com.Z4Yzapforyou.service.SessionService;

@Controller
@RequestMapping(value="/")
public class SessionController {
	
	@Autowired
	private SessionService sessionService;
	
	private Session session = new Session("session1");
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
    public String getPostNew() {
        return "index.html";
    }
	
//	@GetMapping("/sendSMS")
//    public String SendSMS() throws URISyntaxException {
//		sessionService.sendSMS();
//        return "index.html";
//    }
	
	@GetMapping("/messages")
	public String messages() {
		try {
			return "messages.html";
		}catch (Exception e) {
			System.out.println("Error internal 500");
			return "error-500.html";
		}
	}
	
	@GetMapping("/sendText")
	public String sendText(@ModelAttribute("Forms") @Validated Session session2) {
		try {
				sessionService.sendSMS(session2);
				return "messages.html";
		}catch (Exception e) {
			System.out.println("Error internal 500");
			return "error-500.html";
		}
	}
	
	@GetMapping("/enviarmessagelista")
	public String enviarmessagelista() {
		try {
			return "messageslista.html";
		}catch (Exception e) {
			System.out.println("Error internal 500");
			return "error-500.html";
		}	
	}
	
	@PostMapping("/enviarlista") 
	public String enviarlista(@ModelAttribute("Forms") @Validated Session session2, @RequestParam("files") MultipartFile file, @RequestParam("intervalo") String intervalo) throws IOException, ParseException {		
		try {
			sessionService.sendTextLista(session2, file, intervalo);
			return "messageslista.html";
		}catch (Exception e) {
			System.out.println("Error internal 500");
			return "error-500.html";
		}
	}
	
	
	@GetMapping("/verificanumero")
	public String verificanumero() {
		try {
			Session session1 = new Session();
			
			session1 = sessionService.getSession(session);	
			if(session1.getState() != null && !session1.getState().equals("CLOSED") && !session1.getStatus().equals("notLogged")) {
				session.setState(session1.getState());
				return "verificanumero.html";
			}
			session.setState(session1.getState());
			session.setStatus(session1.getStatus());
			return "index.html";
		}catch (Exception e) {
			System.out.println("Error internal 500");
		}
		return "error-500.html";
	}
	
	@PostMapping("/verificanum")
	public ModelAndView verificaNum(@ModelAttribute("Forms") @Validated Session session2) {
		ModelAndView mv;
		boolean statusNun = false;
		
		try {
			Session session1 = new Session();
			
			session1 = sessionService.getSession(session);	
			if(session1.getState() != null && !session1.getState().equals("CLOSED") && !session1.getStatus().equals("notLogged")) {
				statusNun = sessionService.verificaNum(session2);
				mv = new ModelAndView("verificanumero.html");
				mv.addObject("verificaNum", session2.getNumber());
				mv.addObject("statusInit", "true");
				mv.addObject("verificaNumStatus", statusNun);
				return mv;
			}
			mv = new ModelAndView("index.html");
			return mv;
		}catch (Exception e) {
			System.out.println("Error internal 500");
		}
		mv = new ModelAndView("error-500.html");
		return mv;
	}
	
	@GetMapping("/verificanumerolista")
	public String verificanumerolista() {
		try {
			Session session1 = new Session();
			
			session1 = sessionService.getSession(session);	
			if(session1.getState() != null && !session1.getState().equals("CLOSED") && !session1.getStatus().equals("notLogged")) {
				session.setState(session1.getState());
				return "verificanumerolista.html";
			}
			session.setState(session1.getState());
			session.setStatus(session1.getStatus());
			return "index.html";
		}catch (Exception e) {
			System.out.println("Error internal 500");
		}
		return "error-500.html";	
	}
	
	@PostMapping("/verificanumlista") 
	public String verificanumlista(@ModelAttribute("Forms") @Validated Session session2, @RequestParam("files") MultipartFile file) throws IOException, ParseException {		
		try {
			Session session1 = new Session();
			
			session1 = sessionService.getSession(session);	
			if(session1.getState() != null && !session1.getState().equals("CLOSED") && !session1.getStatus().equals("notLogged")) {
				sessionService.verificaNumLista(session2, file);
				return "verificanumerolista.html";
			}
//			session.setState(session1.getState());
			return "index.html";
		}catch (Exception e) {
			System.out.println("Error internal 500");
		}
		return "error-500.html";
	}
	
	@GetMapping("/close")
	public String close() {
		try {
			Session session1 = new Session();
			
			session1 = sessionService.getSession(session);	
			if(session1.getState() != null && !session1.getState().equals("CLOSED") && !session1.getStatus().equals("notLogged")) {
				Session session2 = new Session();
				System.out.println(session.toString());
				session2 = sessionService.close(session);
				session.setState(session2.getState());
				session.setStatus(session2.getStatus());
				return "index.html";
			}
			session.setState(session1.getState());
			session.setStatus(session1.getStatus());
			return "index.html";
		}catch (Exception e) {
			System.out.println("Error internal 500");
		}
		return "error-500.html";
	}
	
	@GetMapping("/sendTextAndPdf")
	public String sendTextAndPdf() {
		try {
			Session session1 = new Session();
			
			session1 = sessionService.getSession(session);	
			if(session1.getState() != null && !session1.getState().equals("CLOSED") && !session1.getStatus().equals("notLogged")) {
				session.setState(session1.getState());
				return "sendtextandpdflista.html";
			}
			session.setState(session1.getState());
			return "index.html";
		}catch (Exception e) {
			System.out.println("Error internal 500");
		}
		return "error-500.html";
	}
	
	@PostMapping("/sendtextandpdflista") 
	public String sendtextandpdflista(@ModelAttribute("Forms") @Validated Session session2, @RequestParam("files1") MultipartFile file1, @RequestParam("files2") MultipartFile file2, @RequestParam("intervalo") String intervalo) throws IOException, ParseException {		
		try {
			Session session1 = new Session();
			
			session1 = sessionService.getSession(session);	
			if(session1.getState() != null && !session1.getState().equals("CLOSED") && !session1.getStatus().equals("notLogged")) {
				sessionService.sendTextAndPdfLista(session2, file1, file2, intervalo);
				return "sendtextandpdflista.html";
			}
//			session.setState(session1.getState());
			return "index.html";
		}catch (Exception e) {
			System.out.println("Error internal 500");
		}
		return "error-500.html";
	}
}
