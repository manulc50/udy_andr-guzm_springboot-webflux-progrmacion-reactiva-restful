package com.bolsadeideas.springboot.webflux.apirest.app.models.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import com.bolsadeideas.springboot.webflux.apirest.app.models.documents.Categoria;
import com.bolsadeideas.springboot.webflux.apirest.app.models.documents.Producto;

public interface ProductoService {
	
	// Método que devuelve un flujo reactivo Flux con todos los productos de la base de datos como elementos
	public Flux<Producto> findAll();
	
	// Método que devuelve un flujo reactivo Flux con todos los productos de la base de datos como elementos y con sus nombres en mayúscula
	public Flux<Producto> findAllConNombreUpperCase();
	
	// Método que devuelve un flujo reactivo Flux con todos los productos de la base de datos como elementos repetidos 5000 veces y con sus nombres en mayúscula
	public Flux<Producto> findAllConNombreUpperCaseRepeat();
	
	// Método que devuelve un flujo reactivo Mono con el producto de la base de datos como elemento que coincide con el id pasado como parámetro
	// El id tiene que ser de tipo String ya que manejamos, como base de datos, MongoDB y en este tipo de bases de datos el id es alfanumérico
	public Mono<Producto> findById(String id);
	
	// Método que devuelve un flujo reactivo Mono con el producto de la base de datos como elemento que coincide con el nombre pasado como parámetro
	public Mono<Producto> findByNombre(String Nombre);
	
	// Método que persite un producto en la base de datos y devuelve un flujo reactivo Mono con el producto persistido como elemento
	public Mono<Producto> save(Producto producto);
	
	// Método que elimina de la base de datos el producto que le pasamos como parámetro y devuelve un flujo reactivo Mono de tipo Void como elemento
	public Mono<Void> delete(Producto producto);
	
	// Método que devuelve un flujo reactivo Flux con todas las categorías de la base de datos como elementos
	public Flux<Categoria> findAllCategoria();
	
	// Método que devuelve un flujo reactivo Mono con la categoría de la base de datos como elemento que coincide con el id pasado como parámetro
	// El id tiene que ser de tipo String ya que manejamos, como base de datos, MongoDB y en este tipo de bases de datos el id es alfanumérico
	public Mono<Categoria> findCategoriaById(String id);
	
	// Método que devuelve un flujo reactivo Mono con la categoría de la base de datos como elemento que coincide con el nombre pasado como parámetro
	public Mono<Categoria> findCategoriaByNombre(String nombre);
	
	// Método que persite una categoría en la base de datos y devuelve un flujo reactivo Mono con la categoría persistida como elemento
	public Mono<Categoria> saveCategoria(Categoria categoria);
}
