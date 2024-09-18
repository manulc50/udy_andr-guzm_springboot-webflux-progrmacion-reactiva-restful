package com.bolsadeideas.springboot.webflux.apirest.app.models.dao;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.bolsadeideas.springboot.webflux.apirest.app.models.documents.Producto;

import reactor.core.publisher.Mono;

// En las bases de datos relaciones(no son reactivas) en las interfaces de la capa Dao extendiamos de CrudRepository o de sus derivados(PagingAndSortingRepository,JpaRepository)para el CRUD mediante la implementación que nos da Spring Data JPA
// Sin embargo,en las bases de datos no relaciones como MongoBD para aplicaciones reactivas como esta,las interfaces de la capa Dao tienen que extender de ReactiveCrudRepository o de sus derivados(ReactiveSortingRepository,ReactiveMongoRepository).Y al igual que ocurria con CrudRepository,Spring ya tiene implementadas de manera automática los métodos básicos para hacer el CRUD en la base de datos
// La interfaz ReactiveSortingRepository incluye las funcionalidades o método de la interfaz ReactiveCrudRepository,y a su vez, la interfaz ReactiveMongoRepository incluye las funcionalidades y métodos de la interfaz ReactiveSortingRepository
// Cuando usamos las interfaces de Spring Data MongoDB para implementar nuestro repositorio,no hace falta usar la anotación @Repository.Si usamos nuestra propia interfaz con nuestra propia implementación,sí tenemos que usar dicha anotación.
// A ReactiveMongoRepository hay que especificar el nombre de la clase que representa un documento y que está anotada con @Document sobre la que queremos realizar el CRUD en la base de datos y también tenemos que indicarle el tipo de Objeto correspondiente a la clave primaria de este documento
public interface ProductoDao extends ReactiveMongoRepository<Producto,String>{
	
	// Las consultas personalizadas en Spring Data MongoDB(al igual que ocurre en Spring Data JPA) se pueden realizar de dos manera;una que,respetando la nomenclatura y estructura indicada por Reactive Spring Data MongoDB,se implementa automáticamente por debajo sin tener que escribirla nosotros
	// Y la otra manera es no respetar la nomenclatura y estructura indicada por Spring Data MongoDB y escribir o implementar nosotros mismos la consulta usando la anotación @Query
	
	// Consulta personaliza implementada automáticamente por Spring por seguir su nomenclatura
	// Devuelve un flujo reactivo Mono con un producto localizado en la base de datos cuyo nombre coincide con el que se le pasa como parámetro a este método
	public Mono<Producto> findByNombre(String Nombre);
	
	// Consulta personaliza implementada de manera manual usando la anotación @Query por no seguir la nomenclatura indicada por Spring
	// Consulta que localiza un producto por su nombre en una colección de una base de datos MongoDB.'?0' hace referencia al primer y único parámetro que se le pasa a este método
	@Query("{ 'nombre' : ?0 }")
	public Mono<Producto> obtenerPorNombre(String Nombre);
} 
