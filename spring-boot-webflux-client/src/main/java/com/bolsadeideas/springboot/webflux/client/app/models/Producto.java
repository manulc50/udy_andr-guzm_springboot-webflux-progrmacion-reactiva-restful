package com.bolsadeideas.springboot.webflux.client.app.models;

import java.util.Date;

// Esta clase se trata de un DTO(Data Transfer Object) para poblar los datos del objeto Json que vamos a consumir con nuestro cliente web que se va a comunicar con nuestra Api Rest mediante peticiones http
// Por lo tanto, esta clase no se trata de una clase entidad porque no se mapea con ninguna colección de la base de datos

public class Producto {

	private String id;
	private String nombre;
	private Double precio;
	private Date createAt;
	private String foto;
	private Categoria categoria;
	
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
	
	public Double getPrecio() {
		return precio;
	}
	
	public void setPrecio(Double precio) {
		this.precio = precio;
	}
	
	public Date getCreateAt() {
		return createAt;
	}
	
	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}
	
	public String getFoto() {
		return foto;
	}
	
	public void setFoto(String foto) {
		this.foto = foto;
	}
	
	public Categoria getCategoria() {
		return categoria;
	}
	
	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}
}
