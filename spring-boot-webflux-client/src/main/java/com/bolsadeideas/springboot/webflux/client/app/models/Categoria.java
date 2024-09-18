package com.bolsadeideas.springboot.webflux.client.app.models;

//Esta clase se trata de un DTO(Data Transfer Object) para poblar los datos del objeto Json que vamos a consumir con nuestro cliente web que se va a comunicar con nuestra Api Rest mediante peticiones http
//Por lo tanto, esta clase no se trata de una clase entidad porque no se mapea con ninguna colección de la base de datos

public class Categoria {
	
	private String id;
	private String nombre;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getNombre() {
		return nombre;
	}
	
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
}
