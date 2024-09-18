package com.bolsadeideas.springboot.webflux.app.models.documents;

import java.util.Date;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

// Debido a que estamos usando una base de dato NoSQL(no relacional),aquí ya no manejamos entities,sino documents,aúnque la forma de trabajar con ellos es igual que en las entities

// Una clase Java con la anotación @Document va a ser un documento que va a ser mapeado a una colección de la base de datos no relacional MongoDB en formato Json(Es el formato que interpreta MongoDB)
@Document(collection="productos") // Al igual que en las entities en bd relacionales,podemos dar nombre a la colección donde se van a mapear estos documentos con el parámetro collection
public class Producto {
	
	@Id // Con esta anotación indicamos que esta propiedad de esta clase va a ser la clave primaria
	private String id; // A diferencia de las bd relaciones donde los ids son númericos,en las no relaciones como MongoDB,los ids son alfanuméricos
	
	@NotEmpty // Validación de la propiedad nombre para que no sea vacía.Esta anotación solo es válida para objetos de tipo String.Para objetos de otro tipo,si se quiere validar que no sea vacío o nulo,hay que usar la anotación @NotNull
	private String nombre;
	
	@NotNull // Validación de la propiedad precio para que no sea nula.En los objetos que no sean de tipo String la validación de que no sea nulo o vacío hay que hacerlo con la anotación @NotNull en vez de @NotEmpty
	private Double precio;
	
	@DateTimeFormat(pattern="yyyy-MM-dd") // Con esta anotación configuramos la fecha para que se almacene con el formato o patrón "yyyy-MM-dd"
	private Date createAt;
	
	@Valid // Con esta anotación establecemos que se tiene que validar las propiedades del objeto "categoria" según las reglas o anotaciones de validación que se indican en su clase entidad,que es "Categoria"
	private Categoria categoria;
	
	private String foto;
	
	// Además de nuestros constructores personalizado para las propiedades "nombre","precio" y "categoria",definimos un contructor vacío para que pueda ser manejado por Spring Data Mongo
	public Producto(){
		
	}
	
	public Producto(String nombre, Double precio) {
		this.nombre = nombre;
		this.precio = precio;
	}
	
	public Producto(String nombre, Double precio,Categoria categoria) {
		this(nombre,precio); // Invoca al constructor de los 2 parámetros "nombre" y "precio"
		this.categoria = categoria;
	}

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

	public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

	public String getFoto() {
		return foto;
	}

	public void setFoto(String foto) {
		this.foto = foto;
	}

}
