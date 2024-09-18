package com.bolsadeideas.springboot.webflux.apirest.app.controllers;

import java.io.File;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.support.WebExchangeBindException;

import com.bolsadeideas.springboot.webflux.apirest.app.models.services.ProductoService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import com.bolsadeideas.springboot.webflux.apirest.app.models.documents.Producto;

// Otra alternativa para implementar una Api Rest con programación reactiva es usar los Functional Endpoints(véase la implementación de la clase RouterFunctionConfig y las clases contenidas en el package com.bolsadeideas.springboot.webflux.apirest.app.handler)

@RestController // Anotamos esta clase como un controlador Rest de Spring.Esta anotacion incluye la anotación @Controller y, además, pone la anotación @ResponseBody en cualquier método handler definido en esta clase
@RequestMapping("/api/productos") // Todas las peticiones a este controlador tienen que ser a partir de la ruta base '/api/productos'
public class ProductoController {
	
	// Recuperamos de la memoria o contendor de Spring el bean que implementa la interfaz "ProductoService".Esta interfaz es implementada por la clase "ProductoServiceImpl"
	@Autowired
	private ProductoService productoService; // Este bean representa la capa Servicio para la clase entidad "Producto" que realiza operaciones CRUD en la base de datos a través de la capa Dao

	// La anotación @Value nos permite inyectar el valor de cualquier propiedad definida en el archivo de propiedades "application.properties"
	// Inyectamos en el objeto String "path" el valor de la propiedad "config.uploads.path", definida en el archivo de propiedades de la aplicación "application.properties", que contiene la ruta o path donde se van a subir las imágenes
	@Value("${config.uploads.path}")
	private String path;
	
	// Método handler que devuelve los datos de todos los productos de la base de datos
	// Método handler que responde las peticiones http de tipo Get para la ruta base,es decir, '/api/productos'
	// Tenemos dos opciones para devolver la respuesta de este método handler;una es devolver directamente un flujo reactivo Flux con los productos(Flux<Producto>),y la otra es, para manejar y personalizar más la respuesta, devolver un flujo reactivo Mono con un elemento ResponseEntity de un flujo reactivo Flux de los productos(Mono<ResponseEntity<Flux<Producto>>>)
	@GetMapping
	public Mono<ResponseEntity<Flux<Producto>>> index(){
		// Creamos y devolvemos un flujo reactivo Mono con un elemento ResponseEntity con la respuesta configurada con el estado OK(200),con el ContentType asociado al MediaType APPLICATION_JSON_UTF8(El cuerpo de la respuesta va en formato Json y con la codificación de caracters UTF-8) y,como cuerpo de la respuesta,un flujo reactivo Flux con los productos recuperados de la base de datos a través de la capa de Servicio mediante el bean "productoService"
		return Mono.just(ResponseEntity.ok() // El estado OK(200) es el estado por defecto de la respuesta.Si no se especifica otro,se utiliza este estado
				.contentType(MediaType.APPLICATION_JSON_UTF8) // El ContentType con el MediaType APPLICATION_JSON es el valor por defecto para una respuesta.Si no se especifica otro(En este caso lo estamos haciendo dando el valor APPLICATION_JSON_UTF8) ,se utiliza este valor
				.body(productoService.findAll())); // Recuperamos de la capa Service mediante el bean 'productoService' el listado de productos como un stream reactivo de tipo Flux(varios items.Si fuese sólo un item,sería de tipo Mono)
	}
	
	// Método handler que devuelve los datos de un producto de la base de datos a partir de su id, que es obtenido de la url o path de la petición http
	// Método handler que responde las peticiones http de tipo Get para la ruta base junto con el id del producto,es decir, '/api/productos/{id}'
	// Tenemos dos opciones para devolver la respuesta de este método handler;una es devolver directamente un flujo reactivo Mono con el producto localizado a partir de su id(Mono<Producto>),y la otra es, para manejar y personalizar más la respuesta, devolver un flujo reactivo Mono con un elemento ResponseEntity de un producto(Mono<ResponseEntity<Producto>>)
	// Con la anotación @PathVariable recuperamos la variable "id" de la ruta o path y la mapeamos con el argumento "id" de este método handler.Cuando el nombre de la variable es distinto al nombre del argumento,para realizar el mapeo, hay que usar el atributo "name" de la anotación con el nombre de la variable de la ruta o path.Si son iguales,no hace falta usar dicho atributo
	@GetMapping("/{id}")
	public Mono<ResponseEntity<Producto>> ver(@PathVariable String id){
		return productoService.findById(id) // Recuperamos de la base de datos el producto como un flujo reactivo Mono a partir del id recuperado de la url o path.Esto lo hacemos mediante el bean 'productoService'
				// Como necesitamos devolver un flujo reactivo Mono de un ResponseEntity para manejar y personalizar la respuesta,con el operador "map" transformamos el flujo reactivo Mono anterior en otro flujo reactivo Mono con un elemento ResponseEntity
				.map(producto -> ResponseEntity.ok() // Establecemos el estado de la respuesta en OK(200),que en realidad no hace falta porque es el valor por defecto del estado de una respuesta
						.contentType(MediaType.APPLICATION_JSON_UTF8) // Establecemos el ContentType de la respuesta con el MediaType APPLICATION_JSON_UTF8(El cuerpo de la respuesta va en formato Json y con la codificación de caracters UTF-8).El valor por defecto del ContenType de una respuesta es un MediaType con el valor APPLICATION_JSON
						.body(producto)) // Establecemos como cuerpo de la respuesta el producto recuperado de la base de datos
				// Con el método "defaultIfEmpty()",si el flujo reactivo Mono anterior tiene como elemento un elemento vacío o nulo porque no se ha localizado ningún producto a partir del id obtenido de la url o path,se sustituye por un elemento ResponseEntity con el estado de la respuesta NOT_FOUND(404) y su cuerpo vacío(sin datos)
				.defaultIfEmpty(ResponseEntity.notFound().build()); // El método "build()" nos permite crear un ResponseEntity con su cuerpo o body vacío(sin datos)
	}
	
	// Método handler que crea un nuevo producto(sin asociar ninguna imagen) en la base de datos a partir de los datos que se obtienen de la petición http
	// Método handler que responde las peticiones http de tipo Post para la ruta base,es decir, '/api/productos'
	// Tenemos dos opciones para devolver la respuesta de este método handler;una es devolver directamente un flujo reactivo Mono con el producto guardado en la base de datos(Mono<Producto>),y la otra es, para manejar y personalizar más la respuesta, devolver un flujo reactivo Mono con un elemento ResponseEntity de un producto(Mono<ResponseEntity<Producto>>)
	// Con la anotación @RequestBody indicamos que los datos del producto nos llegan en formato Json desde la petición http de tipo Post y Spring los mapea con los campos del elemento Producto del flujo reactivo Mono que se le pasa como argumento a este método
	// Con la anaotación @Valid habilitamos las validaciones de los campos del elemento Producto del flujo reactivo Mono que se la pasa como argumento a este método
	// Le pasamos como argumento un flujo reactivo Mono con un elemento Producto,y no directamente un elemento no reactivo de tipo Producto,porque para poder realizar la validación de los campos de un producto, antes de proceder al guardado en la base de datos,necesitamos el operador "onResumeError" de un flujo reactivo para poder capturar las excepciones o errores de validación y,de esta manera,poder manejarlos
	@PostMapping
	public Mono<ResponseEntity<Map<String,Object>>> crear(@Valid @RequestBody Mono<Producto> monoProducto){
		// Creamos un Map para devolver las distintas respuesta de este método handler,es decir,podemos devolver un flujo reactivo Mono con el producto que se acaba de crear en la base de datos,o bien, un flujo reactivo Mono con una lista de errores de validación de los campos de un producto antes de preceder a su salvado en la base de datos
		// También usamos este Map para devolver en la respuesta otro tipo de información como mensajes,fechas,estados,etc...
		Map<String,Object> respuesta = new HashMap<String,Object>();
		
		// Sobre el flujo reactivo Mono con el producto recibido de la petición http,con el operador "flatMap" transformamos este flujo reactivo Mono en otro flujo reactivo Mono cuyo elemento va a ser a su vez otro flujo reactivo Mono con un elemento ResponseEntity.Como al final tenemos un flujo reactivo Mono con otro flujo reactivo Mono a su vez,con este operador,en lugar de "map",se va a realizar un procedimiento de aplanamiento para que definitivamente nos quede un único flujo reactivo Mono de un elemento no reactivo
		return monoProducto.flatMap(producto -> {
			// Si la fecha del producto nos llega vacía desde la petición http,le asignamos la fecha actual del sistema
			if(producto.getCreateAt() == null)
				producto.setCreateAt(new Date());
			
			// Persistimos el producto recibido de la petición http en la base de datos mediante el bean 'productoService'
			return productoService.save(producto)
					// Como necesitamos devolver un flujo reactivo Mono de un ResponseEntity para manejar y personalizar la respuesta,con el operador "map" transformamos el flujo reactivo Mono anterior en otro flujo reactivo Mono con un elemento ResponseEntity
					.map(p -> {
						// Insertamos en el Map a modo de respuesta de este método handler el producto que acabamos de guardar en la base de datos,un mensaje de información al usuario con el texto "Producto creado con éxito" y la fecha de creación de esta respuesta con la fecha actual del sistema
						respuesta.put("producto",p);
						respuesta.put("mensaje","Producto creado con éxito");
						respuesta.put("timestamp",new Date());
						// Una vez establecida la respuesta,creamos y devolvemos un elemento ResponseEntity con dicha respuesta indicando también su estado y su ContentType
						return ResponseEntity.created(URI.create("/api/producto/".concat(p.getId()))) // Establecemos el estado de la respuesta en CREATED(200) y le pasamos la localización del producto que se acaba de crear usando la clase URI junto con la ruta o path de dicho producto.Esta localización va en la cabecera de la respuesta
							.contentType(MediaType.APPLICATION_JSON_UTF8) // Establecemos el ContentType de la respuesta con el MediaType APPLICATION_JSON_UTF8(El cuerpo de la respuesta va en formato Json y con la codificación de caracters UTF-8).El valor por defecto del ContenType de una respuesta es un MediaType con el valor APPLICATION_JSON
							.body(respuesta); // Establecemos como cuerpo de la respuesta el producto que acabamos de guardar en la base de datos
					});
		})
		// Con el operador "onErrorResume" capturamos y manejamos las excepciones producidas por errores de validación de los campos de un producto antes de proceder a su salvado en la base de datos
		// Este operador recibe un parámetro de tipo "Throwable",que es una excepción muy genérica,y,por lo tanto,tenemos que hacer un cast o conversión a un tipo de excepción más concreta.En este caso,hacemos el cast al tipo de excepción "WebExchangeBindException" que es la encargada de darnos la lista de errores de validación
		.onErrorResume(t -> {
			// Creamos un flujo reactivo Mono con la excepción "WebExchangeBindException" como elemento de dicho flujo a partir de la conversión de la excepción genérica "Throwable"
			return Mono.just(t).cast(WebExchangeBindException.class)
					// Con el operador "flatMap" transformamos el flujo reactivo Mono anterior en otro flujo reactivo Mono que tiene a su vez como elemento otro flujo reactivo Mono con un elemento List con la lista de objetos "FieldError" que se corresponden con los errores de validación de los campos del producto.Como al final tenemos un flujo reactivo Mono con otro flujo reactivo Mono a su vez,con este operador,en lugar de "map",se va a realizar un procedimiento de aplanamiento para que definitivamente nos quede un único flujo reactivo Mono de un elemento no reactivo
					.flatMap(e -> Mono.just(e.getFieldErrors()))
					// Con el operador "flatMapMany"(Usamos este operador en lugar de "flatMap" porque ahora vamos a crear un flujo reactivo Flux en vez de Mono) transformamos el flujo reactivo Mono anterior en un flujo reactivo Flux que tiene a su vez otro flujo reactivo Flux con elementos de tipo "FieldError" que son los errores de validación de los campos del producto.Como al final tenemos un flujo reactivo Flux con otro flujo reactivo Flux a su vez,con este operador,en lugar de "map",se va a realizar un procedimiento de aplanamiento para que definitivamente nos quede un único flujo reactivo Flux de elementos no reactivos
					.flatMapMany(error -> Flux.fromIterable(error))
					// Como queremos convertir cada elemento "FieldError" en un String,con el operador "map" transformamos el flujo reactivo Flux anterior en otro flujo reactivo Flux con Strings que contienen el campo del producto que tiene un error de validación y su correspondiente mensaje de error para cada uno de los elementos "FieldError"
					.map(fieldError -> "El campo " + fieldError.getField() + " " + fieldError.getDefaultMessage())
					// Con el operador "collectList",sobre el flujo reactivo Flux anterior creamos un flujo reactivo Mono agrupando todos los Strings anteriores en un elemento u objecto List(los agrupamos en una lista)
					.collectList()
					// Por último,como queremos devolver un flujo reactivo Mono con un elemento ResponseEntity,con el operador "flatMap" transformamos el flujo reactivo Mono anterior en otro flujo reactivo Mono que tiene a su vez otro flujo reactivo Mono con dicho elemento ResponseEntity.Como al final tenemos un flujo reactivo Mono con otro flujo reactivo Mono a su vez,con este operador,en lugar de "map",se va a realizar un procedimiento de aplanamiento para que definitivamente nos quede un único flujo reactivo Mono de un elemento no reactivo
					.flatMap(list -> { // Otra opción sería usar el operado "map" en lugar de "flatMap" y crear y devolver directamente un elemento ResponseEntity, con la sentencia "ResponseEntity.badRequest().body(respuesta)",en lugar de crear y devolver un flujo reactivo Mono con el elemento ResponseEntity tal y como está ahora
						// Insertamos en el Map a modo de respuesta de este método handler la lista de errores de validación del producto,la fecha de creación de esta respuesta con la fecha actual del sistema y el estado de la respuesta
						respuesta.put("errors",list);
						respuesta.put("timestamp",new Date());
						respuesta.put("status",HttpStatus.BAD_REQUEST.value());
						// Una vez establecida la respuesta,creamos y devolvemos un flujo reactivo Mono con un elemento ResponseEntity con dicha respuesta indicando también su estado en BAD_REQUEST(400)
						return Mono.just(ResponseEntity.badRequest().body(respuesta));
					});
		});
	}
	
	// Método handler que crea un nuevo producto, asociándole una imagen, en la base de datos a partir de los datos que se obtienen de la petición http
	// Método handler que responde las peticiones http de tipo Post para la ruta base + la ruta "/v2",es decir, '/api/productos/v2'
	// Tenemos dos opciones para devolver la respuesta de este método handler;una es devolver directamente un flujo reactivo Mono con el producto guardado en la base de datos(Mono<Producto>),y la otra es, para manejar y personalizar más la respuesta, devolver un flujo reactivo Mono con un elemento ResponseEntity de un producto(Mono<ResponseEntity<Producto>>)
	// En esta versión no podemos usar la anotación @RequestBody porque es para indicar que los datos de la petición http nos van a llegar como un objeto Json, pero, en este caso, como vamos a subir la imagen del producto al mismo tiempo que se crea el producto en la base de datos, la subida de imágenes no puede ir unida a dicha anotación, es decir, no puede viajar en un objeto Json
	// Ahora, en esta versión los datos del producto a crear y su imagen van en un formulario de tipo FormData
	// El objeto "file" de tipo "FilePart" anotado con @RequestPart va a almacenar los datos de imagen a subir cuando hagamos una petición http Post a la ruta '/api/productos/v2' para que se ejecute este método handler
	// Con la anaotación @Valid habilitamos las validaciones de los campos del elemento Producto del flujo reactivo Mono que se la pasa como argumento a este método
	// Le pasamos como argumento un flujo reactivo Mono con un elemento Producto,y no directamente un elemento no reactivo de tipo Producto,porque para poder realizar la validación de los campos de un producto, antes de proceder al guardado en la base de datos,necesitamos el operador "onResumeError" de un flujo reactivo para poder capturar las excepciones o errores de validación y,de esta manera,poder manejarlos
	@PostMapping("/v2")
	public Mono<ResponseEntity<Map<String,Object>>> crearConFoto(@Valid Mono<Producto> monoProducto,@RequestPart FilePart file){
		// Creamos un Map para devolver las distintas respuesta de este método handler,es decir,podemos devolver un flujo reactivo Mono con el producto que se acaba de crear en la base de datos,o bien, un flujo reactivo Mono con una lista de errores de validación de los campos de un producto antes de preceder a su salvado en la base de datos
		// También usamos este Map para devolver en la respuesta otro tipo de información como mensajes,fechas,estados,etc...
		Map<String,Object> respuesta = new HashMap<String,Object>();
		
		// Sobre el flujo reactivo Mono con el producto recibido de la petición http,con el operador "flatMap" transformamos este flujo reactivo Mono en otro flujo reactivo Mono cuyo elemento va a ser a su vez otro flujo reactivo Mono con un elemento ResponseEntity.Como al final tenemos un flujo reactivo Mono con otro flujo reactivo Mono a su vez,con este operador,en lugar de "map",se va a realizar un procedimiento de aplanamiento para que definitivamente nos quede un único flujo reactivo Mono de un elemento no reactivo
		return monoProducto.flatMap(producto -> {
			// Si la fecha del producto nos llega vacía desde la petición http,le asignamos la fecha actual del sistema
			if(producto.getCreateAt() == null)
				producto.setCreateAt(new Date());
			
			// Relacionamos la imagen que vamos a subir con el producto
			// Cada imagen que relacionemos con un producto, tiene que tener un nombre único y, para ello, usamos la clase "UUDI" que nos permite generar un número aleatrio al que convertimos en un String para concatenarlo al nombre de dicha imagen mediante otro String "-"
			// Además, eliminamos los caracteres especiales ' ',':' y '\\' que tiene el nombre de la imagen cuando se selecciona para ser subida(el nombre de la imagen contiene toda la ruta o path donde se localiza)
			producto.setFoto(UUID.randomUUID().toString() + "-" + file.filename().replace(" ","").replace(":","").replace("\\",""));
			
			// Mediante el método "transferTo(ruta)" del objeto "file" se realiza la subida a nuestro directorio de subidas cuya ruta nos la da el bean "path"(su valor se obtiene del archivo de configuración "application.properties" de este proyecto)
			// Para ello, tenemos que pasarle a dicho método una instancia de tipo File con la ruta completa de la subida, es decir, la ruta contenida en el bean "path" + el nombre único de la imagen obtenido en el paso anterior
			// Como el método "transferTo(ruta)" del objeto "file" devuelve un flujo reactivo Mono con un elemento de tipo Void,después del operador "flatMap" no podemos usar un operador "map" para guardar en la base de datos el producto junto con su imagen y obtener un flujo reactivo Mono con el producto persistido como elemento porque, al ser el elemento de tipo Void,no va a emitirse para que se ejecute dicho operador "map"
			// Por esta razón,tenemos que crear el flujo reactivo Mono con el producto guardado como elemento dentro del operado "flatMap" usando el método "then()"(Es "then()" porque lo estamos aplicando sobre un flujo reactivo Mono.En cambio,se usa "thenMany()" si se aplica sobre un flujo reactivo Flux)
			return file.transferTo(new File(path + producto.getFoto()))
					// Persistimos el producto recibido de la petición http junto con el nombre de la imagen en la base de datos mediante el bean 'productoService'
					// El método "then" espera a que el flujo reactivo original termine de emitir su único elemento para comenzar a procesar un nuevo flujo reactivo Mono, es decir, comienza a procesar un nuevo flujo reactivo Mono cuando se produce el evento OnComplete del flujo reactivo original
	    			// Al final, el único elemento del flujo reactivo original es ignorado y se tiene en cuenta el elemento del nuevo flujo reactivo Mono
					// "thenMany" se usa para crear flujos reactivos Flux y "then" para crear flujos reactivos Mono cuando el flujo reactivo original ha finalizado de emitir su último elemento
					.then(productoService.save(producto))
					// Como necesitamos devolver un flujo reactivo Mono de un ResponseEntity para manejar y personalizar la respuesta,con el operador "map" transformamos el flujo reactivo Mono anterior en otro flujo reactivo Mono con un elemento ResponseEntity
					.map(p -> {
						// Insertamos en el Map a modo de respuesta de este método handler el producto que acabamos de guardar en la base de datos,un mensaje de información al usuario con el texto "Producto creado con éxito" y la fecha de creación de esta respuesta con la fecha actual del sistema
						respuesta.put("producto",p);
						respuesta.put("mensaje","Producto creado con éxito");
						respuesta.put("timestamp",new Date());
						// Una vez establecida la respuesta,creamos y devolvemos un elemento ResponseEntity con dicha respuesta indicando también su estado y su ContentType
						return ResponseEntity.created(URI.create("/api/producto/".concat(p.getId()))) // Establecemos el estado de la respuesta en CREATED(200) y le pasamos la localización del producto que se acaba de crear usando la clase URI junto con la ruta o path de dicho producto.Esta localización va en la cabecera de la respuesta
							.contentType(MediaType.APPLICATION_JSON_UTF8) // Establecemos el ContentType de la respuesta con el MediaType APPLICATION_JSON_UTF8(El cuerpo de la respuesta va en formato Json y con la codificación de caracters UTF-8).El valor por defecto del ContenType de una respuesta es un MediaType con el valor APPLICATION_JSON
							.body(respuesta); // Establecemos como cuerpo de la respuesta el producto que acabamos de guardar en la base de datos
					});
		})
		// Con el operador "onErrorResume" capturamos y manejamos las excepciones producidas por errores de validación de los campos de un producto antes de proceder a su salvado en la base de datos
		// Este operador recibe un parámetro de tipo "Throwable",que es una excepción muy genérica,y,por lo tanto,tenemos que hacer un cast o conversión a un tipo de excepción más concreta.En este caso,hacemos el cast al tipo de excepción "WebExchangeBindException" que es la encargada de darnos la lista de errores de validación
		.onErrorResume(t -> {
			// Creamos un flujo reactivo Mono con la excepción "WebExchangeBindException" como elemento de dicho flujo a partir de la conversión de la excepción genérica "Throwable"
			return Mono.just(t).cast(WebExchangeBindException.class)
					// Con el operador "flatMap" transformamos el flujo reactivo Mono anterior en otro flujo reactivo Mono que tiene a su vez como elemento otro flujo reactivo Mono con un elemento List con la lista de objetos "FieldError" que se corresponden con los errores de validación de los campos del producto.Como al final tenemos un flujo reactivo Mono con otro flujo reactivo Mono a su vez,con este operador,en lugar de "map",se va a realizar un procedimiento de aplanamiento para que definitivamente nos quede un único flujo reactivo Mono de un elemento no reactivo
					.flatMap(e -> Mono.just(e.getFieldErrors()))
					// Con el operador "flatMapMany"(Usamos este operador en lugar de "flatMap" porque ahora vamos a crear un flujo reactivo Flux en vez de Mono) transformamos el flujo reactivo Mono anterior en un flujo reactivo Flux que tiene a su vez otro flujo reactivo Flux con elementos de tipo "FieldError" que son los errores de validación de los campos del producto.Como al final tenemos un flujo reactivo Flux con otro flujo reactivo Flux a su vez,con este operador,en lugar de "map",se va a realizar un procedimiento de aplanamiento para que definitivamente nos quede un único flujo reactivo Flux de elementos no reactivos
					.flatMapMany(error -> Flux.fromIterable(error))
					// Como queremos convertir cada elemento "FieldError" en un String,con el operador "map" transformamos el flujo reactivo Flux anterior en otro flujo reactivo Flux con Strings que contienen el campo del producto que tiene un error de validación y su correspondiente mensaje de error para cada uno de los elementos "FieldError"
					.map(fieldError -> "El campo " + fieldError.getField() + " " + fieldError.getDefaultMessage())
					// Con el operador "collectList",sobre el flujo reactivo Flux anterior creamos un flujo reactivo Mono agrupando todos los Strings anteriores en un elemento u objecto List(los agrupamos en una lista)
					.collectList()
					// Por último,como queremos devolver un flujo reactivo Mono con un elemento ResponseEntity,con el operador "flatMap" transformamos el flujo reactivo Mono anterior en otro flujo reactivo Mono que tiene a su vez otro flujo reactivo Mono con dicho elemento ResponseEntity.Como al final tenemos un flujo reactivo Mono con otro flujo reactivo Mono a su vez,con este operador,en lugar de "map",se va a realizar un procedimiento de aplanamiento para que definitivamente nos quede un único flujo reactivo Mono de un elemento no reactivo
					.flatMap(list -> { // Otra opción sería usar el operado "map" en lugar de "flatMap" y crear y devolver directamente un elemento ResponseEntity, con la sentencia "ResponseEntity.badRequest().body(respuesta)",en lugar de crear y devolver un flujo reactivo Mono con el elemento ResponseEntity tal y como está ahora
						// Insertamos en el Map a modo de respuesta de este método handler la lista de errores de validación del producto,la fecha de creación de esta respuesta con la fecha actual del sistema y el estado de la respuesta
						respuesta.put("errors",list);
						respuesta.put("timestamp",new Date());
						respuesta.put("status",HttpStatus.BAD_REQUEST.value());
						// Una vez establecida la respuesta,creamos y devolvemos un flujo reactivo Mono con un elemento ResponseEntity con dicha respuesta indicando también su estado en BAD_REQUEST(400)
						return Mono.just(ResponseEntity.badRequest().body(respuesta));
					});
		});
	}

	// Método handler que actualiza y asocia un producto de la base de datos, dado su id, que viaja en la url de la petición, con una imagen, que es obtenida de la petición http
	// Método handler que responde las peticiones http de tipo Post para la ruta base + la ruta "/upload" junto con el id del producto que vamos a relacionar con la imagen a subir ,es decir, '/api/productos/upload/{id}'
	// Tenemos dos opciones para devolver la respuesta de este método handler;una es devolver directamente un flujo reactivo Mono con el producto relacionado con la imagen subida(Mono<Producto>),y la otra es, para manejar y personalizar más la respuesta, devolver un flujo reactivo Mono con un elemento ResponseEntity de un producto(Mono<ResponseEntity<Producto>>)
	// Con la anotación @PathVariable recuperamos la variable "id" de la ruta o path y la mapeamos con el argumento "id" de este método handler.Cuando el nombre de la variable es distinto al nombre del argumento,para realizar el mapeo, hay que usar el atributo "name" de la anotación con el nombre de la variable de la ruta o path.Si son iguales,no hace falta usar dicho atributo
	// El objeto "file" de tipo "FilePart" anotado con @RequestPart va a almacenar los datos de imagen a subir cuando hagamos una petición http Post a la ruta '/api/productos/upload/{id}' para que se ejecute este método handler
	// Los datos de la imagen viajan en un formulario de tipo FormData
	@PostMapping("/upload/{id}")
	public Mono<ResponseEntity<Producto>> upload(@PathVariable String id,@RequestPart FilePart file){
		return productoService.findById(id) // Recuperamos de la base de datos el producto como un flujo reactivo Mono a partir del id recuperado de la url o path.Esto lo hacemos mediante el bean 'productoService'
				// Con el operador flatMap,transformamos el flujo reactivo Mono anterior con el producto localizado como elemento en otro flujo reactivo Mono cuyo elemento va a ser a su vez otro flujo reactivo Mono con los datos del producto actualizado con la imagen en la base de datos.Como al final tenemos un flujo reactivo Mono con otro flujo reactivo Mono a su vez,con este operador,en lugar de "map",se va a realizar un procedimiento de aplanamiento para que definitivamente nos quede un único flujo reactivo Mono de un elemento no reactivo
				.flatMap(p -> {
					// Relacionamos la imagen que vamos a subir con el producto localizado en la base de datos
					// Cada imagen que relacionemos con un producto, tiene que tener un nombre único y, para ello, usamos la clase "UUDI" que nos permite generar un número aleatrio al que convertimos en un String para concatenarlo al nombre de dicha imagen mediante otro String "-"
					// Además, eliminamos los caracteres especiales ' ',':' y '\\' que tiene el nombre de la imagen cuando se selecciona para ser subida(el nombre de la imagen contiene toda la ruta o path donde se localiza)
					p.setFoto(UUID.randomUUID().toString() + "-" + file.filename().replace(" ","").replace(":","").replace("\\",""));
					// Mediante el método "transferTo(ruta)" del objeto "file" se realiza la subida a nuestro directorio de subidas cuya ruta nos la da el bean "path"(su valor se obtiene del archivo de configuración "application.properties" de este proyecto)
					// Para ello, tenemos que pasarle a dicho método una instancia de tipo File con la ruta completa de la subida, es decir, la ruta contenida en el bean "path" + el nombre único de la imagen obtenido en el paso anterior
					// Ahora solo nos falta actualizar el producto con su imagen en la base de datos mediante el método "save()" del bean "productoService"
					// Como el método "transferTo(ruta)" del objeto "file" devuelve un flujo reactivo Mono con un elemento de tipo Void,después del operador "flatMap" no podemos usar un operador "map" para actualizar en la base de datos el producto junto con su imagen y obtener un flujo reactivo Mono con el producto actualizado como elemento porque, al ser el elemento de tipo Void,no va a emitirse para que se ejecute dicho operador "map"
					// Por esta razón,tenemos que crear el flujo reactivo Mono con el producto editado como elemento dentro del operado "flatMap" usando el método "then()"(Es "then()" porque lo estamos aplicando sobre un flujo reactivo Mono.En cambio,se usa "thenMany()" si se aplica sobre un flujo reactivo Flux)
					return file.transferTo(new File(path + p.getFoto()))
							// Actualizamos los datos del producto con su imagen en la base de datos mediante el bean 'productoService'
							// El método "then" espera a que el flujo reactivo original termine de emitir su único elemento para comenzar a procesar un nuevo flujo reactivo Mono, es decir, comienza a procesar un nuevo flujo reactivo Mono cuando se produce el evento OnComplete del flujo reactivo original
			    			// Al final, el único elemento del flujo reactivo original es ignorado y se tiene en cuenta el elemento del nuevo flujo reactivo Mono
							// "thenMany" se usa para crear flujos reactivos Flux y "then" para crear flujos reactivos Mono cuando el flujo reactivo original ha finalizado de emitir su último elemento
							.then(productoService.save(p));
				})
				// Como necesitamos devolver un flujo reactivo Mono de un ResponseEntity para manejar y personalizar la respuesta,con el operador "map" transformamos el flujo reactivo Mono anterior en otro flujo reactivo Mono con un elemento ResponseEntity con estado OK(200) y el producto como cuerpo de la respuesta
				.map(p -> ResponseEntity.ok(p))
				// Con el método "defaultIfEmpty()",si el flujo reactivo Mono anterior tiene como elemento un elemento vacío o nulo porque no se ha localizado ningún producto a partir del id obtenido de la url o path,se sustituye por un elemento ResponseEntity con el estado de la respuesta NOT_FOUND(404) y su cuerpo vacío(sin datos)
				.defaultIfEmpty(ResponseEntity.notFound().build()); // El método "build()" nos permite crear un ResponseEntity con su cuerpo o body vacío(sin datos)
	}
	
	// Método handler que actualiza un producto de la base de datos, dado su id,que es obtenido de la url de la petición, y dado los nuevos datos a editar, que son obtenidos de la petición http
	// Método handler que responde las peticiones http de tipo Put para la ruta base junto con el id del producto a editar,es decir, '/api/productos/{id}'
	// Tenemos dos opciones para devolver la respuesta de este método handler;una es devolver directamente un flujo reactivo Mono con el producto editado en la base de datos(Mono<Producto>),y la otra es, para manejar y personalizar más la respuesta, devolver un flujo reactivo Mono con un elemento ResponseEntity de un producto(Mono<ResponseEntity<Producto>>)
	// Con la anotación @RequestBody indicamos que los nuevos datos del producto a editar nos llegan en formato Json desde la petición http de tipo Post y Spring los mapea con los campos de la instancia "producto" de tipo "Producto"
	// Con la anotación @PathVariable recuperamos la variable "id" de la ruta o path y la mapeamos con el argumento "id" de este método handler.Cuando el nombre de la variable es distinto al nombre del argumento,para realizar el mapeo, hay que usar el atributo "name" de la anotación con el nombre de la variable de la ruta o path.Si son iguales,no hace falta usar dicho atributo
	@PutMapping("/{id}")
	public Mono<ResponseEntity<Producto>> editar(@RequestBody Producto producto,@PathVariable String id){
		return productoService.findById(id) // Recuperamos de la base de datos el producto como un flujo reactivo Mono a partir del id recuperado de la url o path.Esto lo hacemos mediante el bean 'productoService'
				// Con el operador flatMap,transformamos el flujo reactivo Mono anterior con el producto localizado como elemento en otro flujo reactivo Mono cuyo elemento va a ser a su vez otro flujo reactivo Mono con los datos del producto actualizado en la base de datos.Como al final tenemos un flujo reactivo Mono con otro flujo reactivo Mono a su vez,con este operador,en lugar de "map",se va a realizar un procedimiento de aplanamiento para que definitivamente nos quede un único flujo reactivo Mono de un elemento no reactivo
				.flatMap(p -> {
					// Mapeamos los nuevos datos del producto que nos llegan desde la petición http con los campos correspondientes del producto localizado de la base de datos
					// La fecha no se actualiza porque se trata de una fecha de creación del producto y no de edición
					// La foto del producto se guarda y se edita usando otro método handler especifico para ello ya que una foto se envía desde una petición http como un MultipartFile y no como un Json.Y aquí hemos anotado con @RequestBody a la instancia "producto" y,por lo tanto,únicamente podemos enviar datos en formato Json
					p.setNombre(producto.getNombre());
					p.setPrecio(producto.getPrecio());
					p.setCategoria(producto.getCategoria());
					return productoService.save(p); // Actualizamos los datos del producto en la base de datos mediante el bean 'productoService'
				})
				// Como necesitamos devolver un flujo reactivo Mono de un ResponseEntity para manejar y personalizar la respuesta,con el operador "map" transformamos el flujo reactivo Mono anterior en otro flujo reactivo Mono con un elemento ResponseEntity
				.map(p -> ResponseEntity.created(URI.create("/api/producto/".concat(p.getId()))) // Establecemos el estado de la respuesta en CREATED(200) y le pasamos la localización del producto que se acaba de editar usando la clase URI junto con la ruta o path de dicho producto.Esta localización va en la cabecera de la respuesta
						.contentType(MediaType.APPLICATION_JSON_UTF8) // Establecemos el ContentType de la respuesta con el MediaType APPLICATION_JSON_UTF8(El cuerpo de la respuesta va en formato Json y con la codificación de caracters UTF-8).El valor por defecto del ContenType de una respuesta es un MediaType con el valor APPLICATION_JSON
						.body(p)) // Establecemos como cuerpo de la respuesta el producto que acabamos de editar en la base de datos
				// Con el método "defaultIfEmpty()",si el flujo reactivo Mono anterior tiene como elemento un elemento vacío o nulo porque no se ha localizado ningún producto a partir del id obtenido de la url o path,se sustituye por un elemento ResponseEntity con el estado de la respuesta NOT_FOUND(404) y su cuerpo vacío(sin datos)
				.defaultIfEmpty(ResponseEntity.notFound().build()); // El método "build()" nos permite crear un ResponseEntity con su cuerpo o body vacío(sin datos)
	}
	
	// Método handler que elimina un producto de la base de datos, dado su id, que viaja en la url o path de la petición http
	// Método handler que responde las peticiones http de tipo Delete para la ruta base junto con el id del producto a eliminar,es decir, '/api/productos/{id}'
	// Tenemos dos opciones para devolver la respuesta de este método handler;una es devolver directamente un flujo reactivo Mono con un elemento de tipo Void(significa nada o vacío)(es así porque el método encargado de eliminar un producto de la base de datos devuelve un flujo reactivo Mono con un elemento Void)(Mono<Void>),y la otra es, para manejar y personalizar más la respuesta, devolver un flujo reactivo Mono con un elemento ResponseEntity de un elemtento tipo Void(Mono<ResponseEntity<Void>>)
	// Con la anotación @PathVariable recuperamos la variable "id" de la ruta o path y la mapeamos con el argumento "id" de este método handler.Cuando el nombre de la variable es distinto al nombre del argumento,para realizar el mapeo, hay que usar el atributo "name" de la anotación con el nombre de la variable de la ruta o path.Si son iguales,no hace falta usar dicho atributo
	@DeleteMapping("/{id}")
	public Mono<ResponseEntity<Void>> eliminar(@PathVariable String id){
		return productoService.findById(id) // Recuperamos de la base de datos el producto como un flujo reactivo Mono a partir del id recuperado de la url o path.Esto lo hacemos mediante el bean 'productoService'
				// Con el operador flatMap,transformamos el flujo reactivo Mono anterior con el producto localizado como elemento en otro flujo reactivo Mono cuyo elemento va a ser a su vez otro flujo reactivo Mono con un elemento ResponseEntity de tipo Void.Como al final tenemos un flujo reactivo Mono con otro flujo reactivo Mono a su vez,con este operador,en lugar de "map",se va a realizar un procedimiento de aplanamiento para que definitivamente nos quede un único flujo reactivo Mono de un elemento no reactivo
				// Como el método "delete()" del bean "productoService" devuelve un flujo reactivo Mono con un elemento de tipo Void,después del operador "flatMap" no podemos usar un operador "map" para obtener un flujo reactivo Mono con un elemento ResponseEntity porque, al ser el elemento de tipo Void,no va a emitirse para que se ejecute dicho operador "map"
				// Por esta razón,tenemos que crear el flujo reactivo Mono con el elemento ResponseEntity dentro del operado "flatMap" usando el método "then()"(Es "then()" porque lo estamos aplicando sobre un flujo reactivo Mono.En cambio,se usa "thenMany()" si se aplica sobre un flujo reactivo Flux)
				.flatMap(p -> productoService.delete(p) // Eliminarmos de la base datos el producto localizado mediante el bean 'productoService'
						// Creamos un flujo reactivo Mono con un elemento ResponseEntity de tipo Void con el estado de la respuesta establecido en NO_CONTENT(204)
						// El método "then" espera a que el flujo reactivo original termine de emitir su único elemento para comenzar a procesar un nuevo flujo reactivo Mono, es decir, comienza a procesar un nuevo flujo reactivo Mono cuando se produce el evento OnComplete del flujo reactivo original
		    			// Al final, el único elemento del flujo reactivo original es ignorado y se tiene en cuenta el elemento del nuevo flujo reactivo Mono
						// "thenMany" se usa para crear flujos reactivos Flux y "then" para crear flujos reactivos Mono cuando el flujo reactivo original ha finalizado de emitir su último elemento
						.then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT))))
				// Con el método "defaultIfEmpty()",si el flujo reactivo Mono anterior tiene como elemento un elemento vacío o nulo porque no se ha localizado ningún producto a partir del id obtenido de la url o path,se sustituye por un elemento ResponseEntity con el estado de la respuesta NOT_FOUND(404) y su cuerpo vacío(sin datos)
				.defaultIfEmpty(new ResponseEntity<Void>(HttpStatus.NOT_FOUND));
	}
}
