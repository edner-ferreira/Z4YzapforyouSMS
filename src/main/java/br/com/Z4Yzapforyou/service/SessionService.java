package br.com.Z4Yzapforyou.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import br.com.Z4Yzapforyou.config.ConfigServer;
import br.com.Z4Yzapforyou.model.Session;
import io.netty.handler.codec.http.HttpHeaders;
import reactor.core.publisher.Mono;

@Service
public class SessionService {
	
	private static final String AcceptHeaderLocaleResolver = null;
	@Autowired
	private WebClient webClientZ4y;
	
	@Autowired
	private ConfigServer configServer;
	
	public void sendSMS(Session session) throws URISyntaxException, ParseException {
		JSONObject obj = new JSONObject();
		obj.put("phone_number", session.getNumber());
		obj.put("message", session.getMessage());
		obj.put("device_id", 125898);
		
		Mono monoSession = this.webClientZ4y
				.post()
				.uri("https://smsgateway.me/api/v4/message/send")
				.header("Authorization", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJhZG1pbiIsImlhdCI6MTYzMzM3Mzc1MCwiZXhwIjo0MTAyNDQ0ODAwLCJ1aWQiOjkwODAyLCJyb2xlcyI6WyJST0xFX1VTRVIiXX0.JTy-ABj68Smfy2owe_hi-PQ6rstkDm5ht5Z2lO8pJEw")
				.header(HttpHeaders.Names.CONTENT_TYPE, "application/json")
				.bodyValue("[" + obj + "]")
				.retrieve()
				.bodyToMono(String.class);
		
		
		String auxStrJson = (String) monoSession.block();
		JSONParser parser = new JSONParser();
		JSONObject json = (JSONObject) parser.parse(auxStrJson);
		
		System.out.println(json.get("status"));
		
		System.out.println("MSG enviada para - " + obj.get("phone_number"));
	}
	
	public void sendText(Session session) throws Exception{
//		JSONObject obj = new JSONObject();
//		
//		obj.put("phone", session.getNumber());
//		obj.put("message", session.getMessage());
//		String token = "$2b$10$qwxzu017XqFhtCkOd9cFHe_.2RJoiYWra0kQxpDWFctXd_qgmkemi";
////		obj.put("headers", "Authorization: Bearer" + t);
////		{headers: {Authorization: `Bearer 
//		System.out.println(obj.toString() + token);
//		
//		
//		Mono<Session> monoSession = this.webClientZ4y
//				.post()
//				.uri("/send-message", obj)
//				.header("Authorization", "Bearer " + token)
//				.retrieve()
//				.bodyToMono(Session.class);
//		
////		System.out.println(monoSession.toString());
//		monoSession.block();
		Mono<Session> monoSession = this.webClientZ4y
				.get()
				.uri("/sendText?number=" + session.getNumber() +"&messages=" + session.getMessage())
				.retrieve()
				.bodyToMono(Session.class);
		
		monoSession.block();
	}
	
	public void sendTextLista(Session session, MultipartFile file, String intervalo) throws Exception{
		Path path = Paths.get(configServer.pathFile, file.getOriginalFilename());
		file.transferTo(path);
		JSONParser parser = new JSONParser();
		JSONArray jsonObject = (JSONArray) parser.parse(new FileReader(path.toString()));
		Long valorInter;
		JSONObject obj1 = new JSONObject();
		
		if(!intervalo.equals("") && !intervalo.equals("0")) {
//			valorInter = Double.valueOf(intervalo) * 60000;
//			String[] aux = String.valueOf(valorInter).split("\\.");
//			intervalo = aux[0];
			valorInter = Long.valueOf(intervalo) * 1000;
			intervalo = String.valueOf(valorInter);
		}
		
		int i = 1;
		Iterator ite = jsonObject.iterator();	
		while (ite.hasNext()) {
            JSONObject obj = (JSONObject) ite.next();
    		
//            LocalDateTime data = LocalDateTime.now();
            String auxName = session.getMessage();
            String backupmessages = session.getMessage();
            
            if(auxName.contains("$NOME$")) {
            	session.setMessage(auxName.replace("$NOME$", (CharSequence) obj.get("name")));
            }
            
            obj1.put("phone_number", obj.get("number"));
    		obj1.put("message", session.getMessage());
    		obj1.put("device_id", 125898);
            Mono monoSession = this.webClientZ4y
    				.post()
    				.uri("https://smsgateway.me/api/v4/message/send")
    				.header("Authorization", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJhZG1pbiIsImlhdCI6MTYzMzM3Mzc1MCwiZXhwIjo0MTAyNDQ0ODAwLCJ1aWQiOjkwODAyLCJyb2xlcyI6WyJST0xFX1VTRVIiXX0.JTy-ABj68Smfy2owe_hi-PQ6rstkDm5ht5Z2lO8pJEw")
    				.header(HttpHeaders.Names.CONTENT_TYPE, "application/json")
    				.bodyValue("[" + obj1 + "]")
    				.retrieve()
    				.bodyToMono(String.class);
            
            monoSession.block();
            
    		System.out.println(i + "MSG enviada para - " + obj.get("number"));
    		i++;
    		session.setMessage(backupmessages);
    		if(!intervalo.equals("") && !intervalo.equals("0")) {
    			Thread.sleep(Long.valueOf(intervalo));
    		}
        }
		i = 1;
		File arq = new File(path.toString());
		arq.delete();
	}
	
	public Session getSession(Session session) throws Exception{
		Mono<Session> monoSession = this.webClientZ4y
				.get()
				.uri("/getSession?sessionName=" + session.getSessionName())
				.retrieve()
				.bodyToMono(Session.class);
		Session session1 = new Session();
		
		session1 = monoSession.block();

		return session1;
	}

	public boolean verificaNum(Session session) {
		Mono<Session> monoSession = this.webClientZ4y
				.post()
				.uri("/verificanum?number=" + session.getNumber())
				.retrieve()
				.bodyToMono(Session.class);
		Session session1 = new Session();
		
		session1 = monoSession.block();

		if(session1.getNumber().equals("undefined"))
			return false;
		return true;
	}
	
	public void verificaNumLista(Session session, MultipartFile file) throws Exception{
		Path path = Paths.get("/home/richard/Documents/workspace-spring-tool-suite-4-4.10.0.RELEASE/Z4Yzapforyou/tmp/", file.getOriginalFilename());
		file.transferTo(path);
		JSONParser parser = new JSONParser();
		JSONArray jsonObjectArray = (JSONArray) parser.parse(new FileReader(path.toString()));
		LocalDateTime data = LocalDateTime.now();
		BufferedWriter buffWriteCompjson = new BufferedWriter(new FileWriter("/home/richard/Documents/workspace-spring-tool-suite-4-4.10.0.RELEASE/Z4Yzapforyou/tmp/" + data.format(DateTimeFormatter.ofPattern("HH:mm:ss-dd-MM-yyyy")) + "_" + file.getOriginalFilename()));
		JSONObject jsonObject = new JSONObject();
		Iterator ite = jsonObjectArray.iterator();	

		buffWriteCompjson.append("["); 
		
		while (ite.hasNext()) {
            JSONObject obj = (JSONObject) ite.next();
            
            Mono<Session> monoSession = this.webClientZ4y
    				.post()
    				.uri("/verificanum?number=" + obj.get("number"))
    				.retrieve()
    				.bodyToMono(Session.class);
    		Session session1 = new Session();
    		
    		session1 = monoSession.block();
    		
    		if(!session1.getNumber().equals("undefined")) {
    			jsonObject.put("name", obj.get("name"));
    			jsonObject.put("number", obj.get("number"));
    			buffWriteCompjson.append(jsonObject.toString());
    		}
        }
		buffWriteCompjson.append("]");
		buffWriteCompjson.close();
		File arq = new File(path.toString());
		arq.delete();
	}

	public Session close(Session session) {
		Mono<Session> monoSession = this.webClientZ4y
				.get()
				.uri("/close?sessionName=" + session.getSessionName())
				.retrieve()
				.bodyToMono(Session.class);
		Session session1 = new Session();
		
		session1 = monoSession.block();
		return session1;
	}
	
	public void sendTextAndPdfLista(Session session, MultipartFile file1, MultipartFile file2, String intervalo) throws Exception{	
		byte[] bytes = file1.getBytes();
		Path path = Paths.get("/home/richard/Documents/workspace-spring-tool-suite-4-4.10.0.RELEASE/Z4Yzapforyou/tmp/", file1.getOriginalFilename());
		file1.transferTo(path);
		JSONParser parser = new JSONParser();
		JSONArray jsonObject = (JSONArray) parser.parse(new FileReader(path.toString()));
		Double valorInter;
		
		if(!intervalo.equals("") && !intervalo.equals("0")) {
			valorInter = Double.valueOf(intervalo) * 60000;
			String[] aux = String.valueOf(valorInter).split("\\.");
			intervalo = aux[0];
		}
		
		Iterator ite = jsonObject.iterator();	

		while (ite.hasNext()) {
            JSONObject obj = (JSONObject) ite.next();

            LocalDateTime data = LocalDateTime.now();
            String auxName = session.getMessage();
            if(auxName.contains("$NOME$")) {
            	session.setMessage(auxName.replace("$NOME$", (CharSequence) obj.get("name")));
            }
            
            Mono<Session> monoSession = this.webClientZ4y
    				.get()
    				.uri("/sendText?number=" + obj.get("number") + "&messages=" + session.getMessage())
    				.retrieve()
    				.bodyToMono(Session.class);
    		Session session1 = new Session();
    		
    		monoSession.block();
    		
    		Mono<Session> monoSession2 = this.webClientZ4y
    				.post()
    				.uri("/sendpdf?number=" + obj.get("number") + "&pathfile=" + file2.getOriginalFilename())
    				.retrieve()
    				.bodyToMono(Session.class);
    		Session session2 = new Session();
    		
    		monoSession2.block();
    		
    		if(!intervalo.equals("") && !intervalo.equals("0")) {
    			Thread.sleep(Long.valueOf(intervalo));
    		}
        }

		File arq = new File(path.toString());
		arq.delete();
	}
}
