package com.bolsadeideas.springboot.webflux.apirest.app.models.documents;

import javax.validation.constraints.NotEmpty;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

// Debido a que estamos usando una base de dato NoSQL(no relacional),aquí ya no manejamos entities,sino documents,aúnque la forma de trabajar con ellos es igual que en las entities

// Una clase Java con la anotación @Document va a ser un documento que va a ser mapeado a una colección de la base de datos no relacional MongoDB en formato Json(Es el formato que interpreta MongoDB)
@Document(collection="categorias") // Al igual que en las entities en bd relacionales,podemos dar nombre a la colección donde se van a mapear estos documentos con el parámetro collection
public class Categoria {
	
	@Id // Con esta anotación indicamos que esta propiedad de esta clase va a ser la clave primaria
	@NotEmpty // Anotacion para validar que no sea vacio(no nulo y que tenga contenido).Esta anotación solo es válida para objetos de tipo String.Para objetos de otro tipo,si se quiere validar que no sea vacío o nulo,hay que usar la anotación @NotNull
	private String id; // A diferencia de las bd relaciones donde los ids son númericos,en las no relaciones como MongoDB,los ids son alfanuméricos
	
	private String nombre;
	
	// Además de nuestro constructor personalizado para la propiedad "nombre",definimos un contructor vacío para que pueda ser manejado por Spring Data Mongo
	public Categoria() {
	}

	public Categoria(String nombre) {
		this.nombre = nombre;
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

}
