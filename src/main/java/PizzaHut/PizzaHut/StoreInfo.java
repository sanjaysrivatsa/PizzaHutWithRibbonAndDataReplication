package PizzaHut.PizzaHut;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import net.minidev.json.JSONObject;

@RestController
@RequestMapping("/pizzas")
public class StoreInfo {

	//Model
	@Autowired
	ArrayList <Pizza>  pizzas;

	@Autowired
	RestTemplate  resttemp;
	
	@Bean
	public  RestTemplate  resttemp() {
		RestTemplate   rtemp=new RestTemplate();
		return  rtemp;
	}
	
	@Bean
	public ArrayList<Pizza>  pizzas()
	{
		ArrayList  <Pizza> arr=new ArrayList<Pizza>();
		Pizza p=new Pizza(); p.pizzaName="Veg Jubilant";
		p.pizzaType="Medium"; 
		Pizza p2=new Pizza(); p2.pizzaName="Deluxe Veggie Treat";
		p2.pizzaType="Large";
		arr.add(p); arr.add(p2);
		return  arr;
	}

	@RequestMapping(value="/info")
	public String getStoreInfo(HttpServletRequest  request, HttpServletResponse response)
	{
		String r="";
		for(Pizza p: pizzas)
		{
		r+="<tr><td>"+p.getPizzaType()+"</td> : <td>"+p.getPizzaName()+"</td></tr>";
		}
		String res="<html><body><B>Instance Name : " + request.getLocalName() + "<BR>";
		res += "<B>Port : </B>" + + request.getLocalPort() + "<BR>";
		res += "<table border=\"2\" ><thead><th>Pizza Type</th><th>Pizza Name</th></thead>";
		res+= r;
		res += "</table></body></html>";
		System.out.println("sever running" + request.getLocalPort());
		return res;
	}

	@PostMapping(value="/add", consumes=MediaType.APPLICATION_JSON_VALUE)
	public String  addPizza(@RequestBody Pizza   p, HttpServletRequest  request, HttpServletResponse response)
	{
		pizzas.add(p);
		int  localPort=request.getLocalPort();
		HashMap  hmap=new HashMap();
		hmap.put("pizzaName", p.getPizzaName());
		hmap.put("pizzaType", p.getPizzaType());
		System.out.println("hmap : " + hmap);
		String  json=JSONObject.toJSONString(hmap);
		System.out.println("json : " + json);
		System.out.println("JSON value in service @ port " + localPort);
		
		//We are constructing the header of the request packet.
		HttpHeaders  httphead=new HttpHeaders();
		httphead.setContentType(MediaType.APPLICATION_JSON);
		
		//HttEntity represents request packet. Request packet will have body and header
		HttpEntity <String>ent=new HttpEntity(json, httphead);
		if (localPort==9002)
		{
			String res=resttemp.exchange("http://localhost:9003/pizzas/add", 
					HttpMethod.POST,ent, java.lang.String.class).getBody();
			System.out.println("Sent data to 9003");

		}else if(localPort == 9003)
		{
			String res=resttemp.exchange("http://localhost:9004/pizzas/add", 
					HttpMethod.POST,ent, java.lang.String.class).getBody();
			System.out.println("Sent data to 9004");
			/*res=resttemp.exchange("http://localhost:9002/pizzas/add", 
					HttpMethod.POST,ent, java.lang.String.class).getBody();
			System.out.println("Sent data to 9002");*/
		}
		return "<HTML><BODY><B>Added the pizza</B></BODY></HTML>";
	}

	@PutMapping(value="/update", consumes= {"application/x-www-form-urlencoded;charset=UTF-8"})
	//@PutMapping(value="/update", consumes= MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public String  updatePizza(Pizza   formPizza, String  oldName)
	{
		int c=0;
		for (Pizza  pizza : pizzas)
		{
			String pname=pizza.getPizzaName();
			if (pname.equals(oldName)) {
				pizza.setPizzaName(formPizza.getPizzaName());
				pizzas.set(c, pizza);
			}
			c++;
		}
		return "<HTML><BODY><B>Updated the pizza by retrieving data from form</B></BODY></HTML>";
	}
}
