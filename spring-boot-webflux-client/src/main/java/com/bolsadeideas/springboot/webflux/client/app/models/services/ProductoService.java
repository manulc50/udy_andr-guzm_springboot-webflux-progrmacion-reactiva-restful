package com.bolsadeideas.springboot.webflux.client.app.models.services;

import org.springframework.http.codec.multipart.FilePart;

import com.bolsadeideas.springboot.webflux.client.app.models.Producto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductoService {
	
	// Método que devuelve un flujo reactivo Flux con todos los productos como elementos obtenidos desde nuestro cliente web 
	public Flux<Producto> findAll();
	
	// Método que devuelve un flujo reactivo Mono con el producto como elemento obtenido desde nuestro cliente web.Dicho producto se obtiene a partir del id que se le pasa como parámetro de entrada al método
	// El id tiene que ser de tipo String ya que el cliente web se comunica con nuestra Api Rest que accede a una base de datos MondgoDB y sus id's son alfanuméricos
	public Mono<Producto> findById(String id);
	
	// Método que persite un producto en nuestra Api Rest mediante nuestro cliente web y devuelve un flujo reactivo Mono con el producto persistido como elemento
	public Mono<Producto> save(Producto producto);
	
	// Método que actualiza los datos de un producto a partir de su id en nuestra Api Rest mediante nuestro cliente web y devuelve un flujo reactivo Mono con el producto actualizado como elemento
	// El id tiene que ser de tipo String ya que el cliente web se comunica con nuestra Api Rest que accede a una base de datos MondgoDB y sus id's son alfanuméricos
	public Mono<Producto> update(Producto producto,String id);
	
	// Método que elimina de nuestra Api Rest el producto dado su id mediante nuestro cliente web  y devuelve un flujo reactivo Mono de tipo Void como elemento
	// El id tiene que ser de tipo String ya que el cliente web se comunica con nuestra Api Rest que accede a una base de datos MondgoDB y sus id's son alfanuméricos
	public Mono<Void> delete(String id);
	
	// Método que actualiza un producto a partir de su id en nuestra Api Rest asociándole una imagen mediante nuestro cliente web y devuelve un flujo reactivo Mono con el producto actualizado con dicha imagen como elemento
	// El argumento de entrada "file" de tipo "FilePart" se corresponde con el archivo imagen que se va a relacionar con el producto
	// El id tiene que ser de tipo String ya que el cliente web se comunica con nuestra Api Rest que accede a una base de datos MondgoDB y sus id's son alfanuméricos
	public Mono<Producto> upload(FilePart file,String id);

}
