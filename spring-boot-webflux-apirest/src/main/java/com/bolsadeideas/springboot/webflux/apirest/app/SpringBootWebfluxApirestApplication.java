package com.bolsadeideas.springboot.webflux.apirest.app;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

import com.bolsadeideas.springboot.webflux.apirest.app.models.services.ProductoService;
import com.bolsadeideas.springboot.webflux.apirest.app.models.documents.Categoria;
import com.bolsadeideas.springboot.webflux.apirest.app.models.documents.Producto;

import reactor.core.publisher.Flux;

/* A diferencia de las bases de datos relaciones donde teníamos un archivo import.sql(en resources) con los inserts iniciales para poblar las tablas con los datos iniciales,
 * en este caso,con la base de datos MongoDB(no relaciones y reactivas),para insertar los documentos iniciales en las colecciones de dicha base de datos es necesario hacerlo usando la interfaz "CommandLineRunner"
 */

//Habilitamos el cliente Eureka de Netflix para que el servidor Eureka pueda descubrir y registrar este microservicio  
@EnableEurekaClient
// Esta interfaz nos permite ejecutar las tareas indicadas en el método run() antes de la aplicación Spring Boot desde el método main
@SpringBootApplication
public class SpringBootWebfluxApirestApplication implements CommandLineRunner{
	
	// Habilitamos el uso de log en esta clase
	private static final Logger log = LoggerFactory.getLogger(SpringBootWebfluxApirestApplication.class);
		
	// Recuperamos de la memoria o contendor de Spring el bean que implementa la interfaz "ProductoService".Esta interfaz es implementada por la clase "ProductoServiceImpl"
	@Autowired
	private ProductoService productoService; // Este bean representa la capa Servicio para la clase entidad "Producto" que realiza operaciones CRUD en la base de datos a través de la capa Dao
		
	@Autowired
	private ReactiveMongoTemplate mongoTemplate; // Este bean es propio de Spring y nos permite,entre otras cosas,eliminar una colección de una base de datos MongoDB

	public static void main(String[] args) {
		SpringApplication.run(SpringBootWebfluxApirestApplication.class, args);
	}
	
	@Override
	public void run(String... args) throws Exception {
		// Antes de insertar los datos de prueba en las colecciones "productos" y "categorias" de la base de datos MongoDB,eliminamos dichas colecciones con sus documentos anteriores para volver a crearlas a continuación
		// El método "dropCollection()" nos devuelve un flujo reactivo Mono,y por eso,tenemos que suscribirnos a este flujo para crear un Observador y se ejecute la tarea de eliminar las colecciones
		mongoTemplate.dropCollection("productos").subscribe();
		mongoTemplate.dropCollection("categorias").subscribe();
		
		// Creamos unas categorías de prueba
		Categoria electronico = new Categoria("Electrónico");
		Categoria deporte = new Categoria("Deporte");
		Categoria computacion = new Categoria("Computación");
		Categoria muebles = new Categoria("Muebles");
		
		// Como estamos usando una base de datos MongoDB que es reactiva,definimos un flujo o stream reactivo Flux con los datos de prueba tipo Categoria para insertarlos en la colección "categorias"
		Flux.just(electronico,deporte,computacion,muebles)
		// Usamos el operador "flatMap" en vez de "map" ya que el método "saveCategoria()" de nuestra capa Dao "productoService" nos devuelve un flujo reactivo Modo de tipo Categoria por cada categoría que se acaba de insertar en la base de datos
		// Entonces,al final tendríamos un flujo reactivo Flux de elementos que son a su vez flujos reactivos Mono de tipo Categoria y,por lo tanto,es necesario realizar un proceso de aplanamiento para obtener un único flujo reactivo Flux cuyos elementos sean directamente objetos no reactivos de tipo Categoria
		.flatMap(categoria -> productoService.saveCategoria(categoria)) // Persistimos el objeto 'categoria' en la base de datos
		// Con el método "doOnNext()" realizamos la tarea de escribir en el log a modo de información el nombre y el id de cada elemento de tipo Categoria emitido por este flujo reactivo
		.doOnNext(categoria -> log.info("Categoria creada: " + categoria.getNombre() + ",Id=" + categoria.getId()))
		// Con el método "thenMany" creamos otro flujo reactivo Flux de elementos tipo Producto para guardarlos posteriormente en la base de datos
		// El método "thenMany" espera a que el flujo reactivo original termine de emitir su último elemento para comenzar a procesar un nuevo flujo reactivo Flux, es decir, comienza a procesar un nuevo flujo reactivo Flux cuando se produce el evento OnComplete del flujo reactivo original
		// Al final, los elementos del flujo reactivo original son ignorados y se tienen en cuenta los elementos del nuevo flujo reactivo Flux
		// "thenMany" se usa para crear flujos reactivos Flux y "then" para crear flujos reactivos Mono cuando el flujo reactivo original ha finalizado de emitir su último elemento
		.thenMany(
			// Como estamos usando una base de datos MongoDB que es reactiva,definimos un flujo o stream reactivo Flux con los datos de prueba tipo Producto para insertarlos en la colección "productos"
			Flux.just(new Producto("TV Panasonic Pantalla LCD", 456.89,electronico),
					new Producto("Sony Camara HD Digital", 177.89,electronico),
					new Producto("Apple iPod", 46.89,electronico),
					new Producto("Sony Notebook", 846.89,computacion),
					new Producto("Hewlett Packard Multifuncional", 200.89,computacion),
					new Producto("Bianchi Bicicleta", 70.89,deporte),
					new Producto("HP Notebook Omen 17", 2500.89,computacion),
					new Producto("Mica Cómoda 5 Cajones", 150.89,muebles),
					new Producto("TV Sony Bravia OLED 4K Ultra HD", 2255.89,electronico))
			// Usamos el operador "flatMap" en vez de "map" ya que el método "save()" de nuestra capa Dao "productoDao" nos devuelve un flujo reactivo Modo de tipo Producto por cada producto que se acaba de insertar en la base de datos
			// Entonces,al final tendríamos un flujo reactivo Flux de elementos que son a su vez flujos reactivos Mono de tipo Producto y,por lo tanto,es necesario realizar un proceso de aplanamiento para obtener un único flujo reactivo Flux cuyos elementos sean directamente objetos no reactivos de tipo Producto
			.flatMap(producto ->{
				producto.setCreateAt(new Date()); // Registramos la fecha actual en la propiedad 'createAt' del producto antes de persistirlo en la base de datos
				return productoService.save(producto); // Persistimos el objeto 'producto' en la base de datos
			})
		)
		// Nos suscribimos,creándose un Observador de este flujo reactivo,y realizamos la tarea de escribir en el log a modo de información el nombre y el precio de cada elemento de tipo Producto emitido por este flujo reactivo
		.subscribe(producto -> log.info("Insert: " + producto.getNombre() + " " + producto.getPrecio()));
		
	}

}
