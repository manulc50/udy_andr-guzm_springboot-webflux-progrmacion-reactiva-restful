package com.bolsadeideas.springboot.webflux.apirest.app.models.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bolsadeideas.springboot.webflux.apirest.app.models.dao.CategoriaDao;
import com.bolsadeideas.springboot.webflux.apirest.app.models.dao.ProductoDao;
import com.bolsadeideas.springboot.webflux.apirest.app.models.documents.Categoria;
import com.bolsadeideas.springboot.webflux.apirest.app.models.documents.Producto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service // Indicamos que esta clase se trata de una clase servicio de Spring.De esta manera,Spring va a almacenar un bean de esta clase en su memmoria o contenedor para poderlo inyectar en otra parte del proyecto
public class ProductoServiceImpl implements ProductoService{
	
	// Recuperamos de la memoria o contendor de Spring el bean que implementa la interfaz "ProductoDao".Esta interfaz es implementada por Spring al extender de la interfaz "ReactiveMongoRepository"
	@Autowired 
	private ProductoDao productoDao; // Este bean se trata del Dao para realizar CRUD en la colección "productos" mapeada con la clase entidad "Producto"

	// Recuperamos de la memoria o contendor de Spring el bean que implementa la interfaz "CategoriaDao".Esta interfaz es implementada por Spring al extender de la interfaz "ReactiveMongoRepository"
	@Autowired 
	private CategoriaDao categoriaDao; // Este bean se trata del Dao para realizar CRUD en la colección "categorias" mapeada con la clase entidad "Categoria"
	
	/* NOTA: Como se está usando una base de datos MongoDB, no hace falta utilizar la anotación @Transactional a nivel de método porque este tipo de bases de datos no son transaccionables.
	 *       Si por el contrario se usase bases de datos como MySQL o PostgreSQL, entonces sí que sería necesario usar dicha anotación */
	
	// Método que devuelve un flujo reactivo Flux con todos los productos de la base de datos como elementos
	@Override
	public Flux<Producto> findAll() {
		return productoDao.findAll(); // Accedemos a nuestra capa Dao "productoDao" para localizar todos los productos de la base de datos haciendo uso del método "findAll()"
	}
	
	// Método que devuelve un flujo reactivo Flux con todos los productos de la base de datos como elementos y con sus nombres en mayúscula
	@Override
	public Flux<Producto> findAllConNombreUpperCase() {
		return productoDao.findAll() // Accedemos a nuestra capa Dao "productoDao" para localizar todos los productos de la base de datos haciendo uso del método "findAll()"
		// Con el método 'map' transformamos el flujo reactivo Flux anterior en otro flujo Flux con el nombre de cada producto en letras mayúsculas
		.map(producto -> { 
			producto.setNombre(producto.getNombre().toUpperCase());
			return producto;
		});
	}
	
	// Método que devuelve un flujo reactivo Flux con todos los productos de la base de datos como elementos repetidos 5000 veces y con sus nombres en mayúscula
	@Override
	public Flux<Producto> findAllConNombreUpperCaseRepeat() {
		return findAllConNombreUpperCase().repeat(5000); // Obtenemos el listado de productos con sus nombres en mayúscula como un stream reactivo de tipo Flux y repetimos sus elementos 5000 veces para hacer un flujo reactivo con una gran cantidad de elementos
	}

	// Método que devuelve un flujo reactivo Mono con el producto de la base de datos como elemento que coincide con el id pasado como parámetro
	// El id tiene que ser de tipo String ya que manejamos, como base de datos, MongoDB y en este tipo de bases de datos el id es alfanumérico
	@Override
	public Mono<Producto> findById(String id) {
		return productoDao.findById(id); // Accedemos a nuestra capa Dao "productoDao" para localizar el producto de la base de datos cuyo id coincide con el que le pasamos como parámetro.Para ello,hacemos uso del método "findById()"
	}
	
	// Método que devuelve un flujo reactivo Mono con el producto de la base de datos como elemento que coincide con el nombre pasado como parámetro
	@Override
	public Mono<Producto> findByNombre(String Nombre) {
		return productoDao.obtenerPorNombre(Nombre); // Accedemos a nuestra capa Dao "productoDao" para localizar el producto de la base de datos cuyo nombre coincide con el que le pasamos como parámetro.Para ello,hacemos uso del método "obtenerPorNombre()"
	}

	// Método que persite un producto en la base de datos y devuelve un flujo reactivo Mono con el producto persistido como elemento
	@Override
	public Mono<Producto> save(Producto producto) {
		return productoDao.save(producto); // Accedemos a nuestra capa Dao "productoDao" para persistir un producto en la base de datos haciendo uso del método "save()"
	}

	// Método que elimina de la base de datos el producto que le pasamos como parámetro y devuelve un flujo reactivo Mono de tipo Void como elemento
	@Override
	public Mono<Void> delete(Producto producto) {
		return productoDao.delete(producto); // Accedemos a nuestra capa Dao "productoDao" para eliminar un producto de la base de datos haciendo uso del método "delete()"
	}

	// Método que devuelve un flujo reactivo Flux con todas las categorías de la base de datos como elementos
	@Override
	public Flux<Categoria> findAllCategoria() {
		return categoriaDao.findAll(); // Accedemos a nuestra capa Dao "categoriaDao" para localizar todas las categorías de la base de datos haciendo uso del método "findAll()"
	}

	// Método que devuelve un flujo reactivo Mono con la categoría de la base de datos como elemento que coincide con el id pasado como parámetro
	// El id tiene que ser de tipo String ya que manejamos, como base de datos, MongoDB y en este tipo de bases de datos el id es alfanumérico
	@Override
	public Mono<Categoria> findCategoriaById(String id) {
		return categoriaDao.findById(id); // Accedemos a nuestra capa Dao "categoriaDao" para localizar la categoría de la base de datos cuyo id coincide con el que le pasamos como parámetro.Para ello,hacemos uso del método "findById()"
	}
	
	// Método que devuelve un flujo reactivo Mono con la categoría de la base de datos como elemento que coincide con el nombre pasado como parámetro
	@Override
	public Mono<Categoria> findCategoriaByNombre(String nombre) {
		return categoriaDao.findByNombre(nombre); // Accedemos a nuestra capa Dao "categoriaDao" para localizar la categoría de la base de datos cuyo nombre coincide con el que le pasamos como parámetro.Para ello,hacemos uso del método "findByNombre()"
	}

	// Método que persite una categoría en la base de datos y devuelve un flujo reactivo Mono con la categoría persistida como elemento
	@Override
	public Mono<Categoria> saveCategoria(Categoria categoria) {
		return categoriaDao.save(categoria); // Accedemos a nuestra capa Dao "categoriaDao" para persistir una categoría en la base de datos haciendo uso del método "save()"
	}

}
