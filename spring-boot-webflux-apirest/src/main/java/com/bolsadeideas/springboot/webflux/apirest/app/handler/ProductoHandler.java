package com.bolsadeideas.springboot.webflux.apirest.app.handler;

import java.io.File;
import java.net.URI;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.FormFieldPart;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.bolsadeideas.springboot.webflux.apirest.app.models.documents.Categoria;
import com.bolsadeideas.springboot.webflux.apirest.app.models.documents.Producto;
import com.bolsadeideas.springboot.webflux.apirest.app.models.services.ProductoService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

// Esta clase contiene todos los métodos handler que se van a usar en nuestra clase de configuración de Spring "RouterFunctionConfig" para configurar el mapeo de rutas para peticiones http usando la técnica "Functional Endpoints"

@Component // Con esta anotación indicamos que esta clase es un componente de Spring y,por lo tanto,Spring va a almacenar un bean de esta clase en su contenedor o memoria para poder utilizarlo en otra clase del proyecto
public class ProductoHandler {
	
	// Recuperamos de la memoria o contendor de Spring el bean que implementa la interfaz "ProductoService".Esta interfaz es implementada por la clase "ProductoServiceImpl"
	@Autowired
	private ProductoService productoService; // Este bean representa la capa Servicio para la clase entidad "Producto" que realiza operaciones CRUD en la base de datos a través de la capa Dao
	
	// Recuperamos de la memoria o contendor de Spring el bean que implementa la interfaz "Validator".Esta interfaz es propia de Spring y,por lo tanto,también es implementada por Spring.
	@Autowired
	private Validator validator; // Este bean propio de Spring nos permite validar los campos o propiedades de un objeto entidad(aquel que se mapea directamente con una colección de la base de datos) y también nos permite manejar los errores de validación que se puedan producir
	
	// La anotación @Value nos permite inyectar el valor de cualquier propiedad definida en el archivo de propiedades "application.properties"
	// Inyectamos en el objeto String "path" el valor de la propiedad "config.uploads.path", definida en el archivo de propiedades de la aplicación "application.properties", que contiene la ruta o path donde se van a subir las imágenes
	@Value("${config.uploads.path}")
	private String path;
	
	// Método handler que devuelve los datos de todos los productos de la base de datos
	// Este método se va a mapear con una ruta en el método "routes()" de nuestra clase de configuración de Spring "RouterFunctionConfig"
	// A diferencia de nuestro otro controlador Api Rest "ProductoController",cuya clase se anota con @RestControler y sus métodos handler devuelven flujos reactivos Mono de tipo "ResponseEntity",en este tipo de controlador,implementado con la técnica "Functional Endpoints",sus métodos handler van a devolver flujos reactivos Mono de tipo "ServerResponse"
	// Otra diferencia con respecto a nuestro otro controlador Api Rest "ProductoController" anotado con @RestController,para la obtención de los datos que viajan en el cuerpo de la petición y de la url o path,se utiliza la instancia de tipo "ServerRequest" que se le pasa como parámetro de entrada a este método.En el otro controlador se usaban para ello anotaciones como @RequestBody,@PathVariable y @RequestParam
	public Mono<ServerResponse> listar(ServerRequest request){
		// Creamos y devolvemos un flujo reactivo Mono con un elemento ServerResponse con la respuesta configurada con el estado OK(200),con el ContentType asociado al MediaType APPLICATION_JSON_UTF8(El cuerpo de la respuesta va en formato Json y con la codificación de caracters UTF-8) y,como cuerpo de la respuesta,un flujo reactivo Flux con los productos recuperados de la base de datos a través de la capa de Servicio mediante el bean "productoService"
		return ServerResponse.ok() // El estado OK(200) es el estado por defecto de la respuesta.Si no se especifica otro,se utiliza este estado
				.contentType(MediaType.APPLICATION_JSON_UTF8) // El ContentType con el MediaType APPLICATION_JSON es el valor por defecto para una respuesta.Si no se especifica otro(En este caso lo estamos haciendo dando el valor APPLICATION_JSON_UTF8 - El cuerpo de la respuesta va en formato Json y con la codificación de caracters UTF-8),se utiliza este valor
				.body(productoService.findAll(),Producto.class); // Recuperamos de la capa Service mediante el bean 'productoService' el listado de productos como un stream reactivo de tipo Flux(varios items.Si fuese sólo un item,sería de tipo Mono).En esta caso,el método "body()" recibe un flujo reactivo Mono o Flux,o un dato de tipo BodyInserters,este último para cuando tenemos datos que no son flujos reactivos. Como el método "findAll()" devuelve directamente un flujo reactivo Flux,tenemos que especificar,además,el tipo o la clase a la que pertenecen los elementos de este flujo reactivo
	}
	
	// Método handler que devuelve los datos de un producto de la base de datos a partir de su id, que es obtenido de la petición http "request"
	// Este método se va a mapear con una ruta en el método "routes()" de nuestra clase de configuración de Spring "RouterFunctionConfig"
	// A diferencia de nuestro otro controlador Api Rest "ProductoController",cuya clase se anota con @RestControler y sus métodos handler devuelven flujos reactivos Mono de tipo "ResponseEntity",en este tipo de controlador,implementado con la técnica "Functional Endpoints",sus métodos handler van a devolver flujos reactivos Mono de tipo "ServerResponse"
	// Otra diferencia con respecto a nuestro otro controlador Api Rest "ProductoController" anotado con @RestController,para la obtención de los datos que viajan en el cuerpo de la petición y de la url o path,se utiliza la instancia de tipo "ServerRequest" que se le pasa como parámetro de entrada a este método.En el otro controlador se usaban para ello anotaciones como @RequestBody,@PathVariable y @RequestParam
	public Mono<ServerResponse> ver(ServerRequest request){
		//Obtenemos de la url  o path de la petición http el id del producto
		String id = request.pathVariable("id");
		
		return productoService.findById(id) // Recuperamos de la base de datos el producto como un flujo reactivo Mono a partir del id recuperado de la url o path.Esto lo hacemos mediante el bean 'productoService'
				// Como necesitamos devolver un flujo reactivo Mono de un ServerResponse para manejar y personalizar la respuesta,con el operador "map" transformamos el flujo reactivo Mono anterior en otro flujo reactivo Mono con un elemento ServerResponse
				.flatMap(p -> ServerResponse.ok() // Establecemos el estado de la respuesta en OK(200),que en realidad no hace falta porque es el valor por defecto del estado de una respuesta
							.contentType(MediaType.APPLICATION_JSON_UTF8) // Establecemos el ContentType de la respuesta con el MediaType APPLICATION_JSON_UTF8(El cuerpo de la respuesta va en formato Json y con la codificación de caracters UTF-8).El valor por defecto del ContenType de una respuesta es un MediaType con el valor APPLICATION_JSON
							.body(BodyInserters.fromObject(p)) // Establecemos como cuerpo de la respuesta el producto recuperado de la base de datos.En esta caso,el método "body()" recibe un flujo reactivo Mono o Flux,o un dato de tipo BodyInserters,este último para cuando tenemos datos que no son flujos reactivos.Como nuestro dato "p" no es flujo reactivo,tenemos que usar el método "fromObject()" de la clase "BodyInserters"
				)
				// Con el método "switchIfEmpty()",si el flujo reactivo Mono anterior tiene como elemento un elemento vacío o nulo porque no se ha localizado ningún producto a partir del id obtenido de la url o path,sustituimos dicho flujo por otro flujo reactivo Mono con un elemento ServerResponse con el estado de la respuesta NOT_FOUND(404) y su cuerpo vacío(sin datos)
				.switchIfEmpty(ServerResponse.notFound().build()); // El método "build()" nos permite crear un ServerResponse con su cuerpo o body vacío(sin datos)
	}
	
	// Método handler que crea un nuevo producto(sin asociar ninguna imagen) en la base de datos a partir de los datos que se obtienen de la petición http "request"
	// Este método se va a mapear con una ruta en el método "routes()" de nuestra clase de configuración de Spring "RouterFunctionConfig"
	// A diferencia de nuestro otro controlador Api Rest "ProductoController",cuya clase se anota con @RestControler y sus métodos handler devuelven flujos reactivos Mono de tipo "ResponseEntity",en este tipo de controlador,implementado con la técnica "Functional Endpoints",sus métodos handler van a devolver flujos reactivos Mono de tipo "ServerResponse"
	// Otra diferencia con respecto a nuestro otro controlador Api Rest "ProductoController" anotado con @RestController,para la obtención de los datos que viajan en el cuerpo de la petición y de la url o path,se utiliza la instancia de tipo "ServerRequest" que se le pasa como parámetro de entrada a este método.En el otro controlador se usaban para ello anotaciones como @RequestBody,@PathVariable y @RequestParam
	public Mono<ServerResponse> crear(ServerRequest request){
		// Obtenemos del cuerpo de la petición http los datos del producto a persistir en la base de datos como un flujo reactivo Mono
		Mono<Producto> producto = request.bodyToMono(Producto.class);
		
		// Sobre el flujo reactivo Mono con el producto recibido de la petición http,con el operador "flatMap" transformamos este flujo reactivo Mono en otro flujo reactivo Mono cuyo elemento va a ser a su vez otro flujo reactivo Mono con un elemento ServerResponse.Como al final tenemos un flujo reactivo Mono con otro flujo reactivo Mono a su vez,con este operador,en lugar de "map",se va a realizar un procedimiento de aplanamiento para que definitivamente nos quede un único flujo reactivo Mono de un elemento no reactivo
		return producto.flatMap(p -> {
			// "errors" es una instancia de "BeanPropertyBindingResult" que implementa la interfaz de Spring "Errors" y nos da información sobre si hay o no hay errores de validación de los campos o propiedades de un objeto entidad
			// En este caso,estamos obtiendo información sobre errores de validación de los campos o propiedades del objeto entidad "p" que representa el producto que se desea persistir en la base de datos
			// Para crear dicha instancia,tenemos que pasarle al constructor de "BeanPropertyBindingResult" el objeto entidad a validar y el nombre de la clase a la que pertenece dicho objeto
			Errors errors = new BeanPropertyBindingResult(p,Producto.class.getName());
			// Con el método "validate()" del validador de Spring validamos el objeto entidad del producto "p"
			validator.validate(p,errors);
			// Si hay errores de validación...
			if(errors.hasErrors()) {
				// Creamos un flujo reactivo Flux a partir de la lista de errores devuelta por el método "getFieldErrors()" del objeto "errors"
				// Para crear un flujo reactivo Flux a partir de un objeto List,tenemos que usar el método "fromIterable()"
				return Flux.fromIterable(errors.getFieldErrors())
						// Como queremos convertir cada elemento "FieldError" en un String,con el operador "map" transformamos el flujo reactivo Flux anterior en otro flujo reactivo Flux con Strings que contienen el campo del producto que tiene un error de validación y su correspondiente mensaje de error para cada uno de los elementos "FieldError"
						.map(fieldError -> "El campo " + fieldError.getField() + " " + fieldError.getDefaultMessage())
						// Con el operador "collectList",sobre el flujo reactivo Flux anterior creamos un flujo reactivo Mono agrupando todos los Strings anteriores en un elemento u objecto List(los agrupamos en una lista)
						.collectList()
						// Por último,como queremos devolver un flujo reactivo Mono con un elemento ServerResponse,con el operador "flatMap" transformamos el flujo reactivo Mono anterior en otro flujo reactivo Mono que tiene a su vez otro flujo reactivo Mono con dicho elemento ServerResponse.Como al final tenemos un flujo reactivo Mono con otro flujo reactivo Mono a su vez,con este operador,en lugar de "map",se va a realizar un procedimiento de aplanamiento para que definitivamente nos quede un único flujo reactivo Mono de un elemento no reactivo
						.flatMap(list -> ServerResponse.badRequest().body(BodyInserters.fromObject(list))); // Creamos y devolvemos un flujo reactivo Mono con un elemento ServerResponse con la lista de errores personalizados en strings indicando también su estado en BAD_REQUEST(400).En esta caso,el método "body()" recibe un flujo reactivo Mono o Flux,o un dato de tipo BodyInserters,este último para cuando tenemos datos que no son flujos reactivos.Como nuestro dato "list" no es flujo reactivo,tenemos que usar el método "fromObject()" de la clase "BodyInserters"
			}
			// En caso contrario...
			else {
				// Si la fecha del producto nos llega vacía desde la petición http,le asignamos la fecha actual del sistema
				if(p.getCreateAt() == null)
					p.setCreateAt(new Date());
				
				// Persistimos el producto recibido de la petición http en la base de datos mediante el bean 'productoService'
				return productoService.save(p)
						// Como necesitamos devolver un flujo reactivo Mono de un ServerResponse para manejar y personalizar la respuesta,con el operador "flatMap" transformamos el flujo reactivo Mono anterior en otro flujo reactivo Mono que tiene a su vez otro flujo reactivo Mono con dicho elemento ServerResponse.Como al final tenemos un flujo reactivo Mono con otro flujo reactivo Mono a su vez,con este operador,en lugar de "map",se va a realizar un procedimiento de aplanamiento para que definitivamente nos quede un único flujo reactivo Mono de un elemento no reactivo
						.flatMap(pdb -> ServerResponse.created(URI.create("/api/v2/productos/".concat(pdb.getId()))) // Establecemos el estado de la respuesta en CREATED(200) y le pasamos la localización del producto que se acaba de crear usando la clase URI junto con la ruta o path de dicho producto.Esta localización va en la cabecera de la respuesta
								.contentType(MediaType.APPLICATION_JSON_UTF8) // Establecemos el ContentType de la respuesta con el MediaType APPLICATION_JSON_UTF8(El cuerpo de la respuesta va en formato Json y con la codificación de caracters UTF-8).El valor por defecto del ContenType de una respuesta es un MediaType con el valor APPLICATION_JSON
								.body(BodyInserters.fromObject(pdb))); // Establecemos como cuerpo de la respuesta el producto que acabamos de guardar en la base de datos.En esta caso,el método "body()" recibe un flujo reactivo Mono o Flux,o un dato de tipo BodyInserters,este último para cuando tenemos datos que no son flujos reactivos.Como nuestro dato "pdb" no es flujo reactivo,tenemos que usar el método "fromObject()" de la clase "BodyInserters"
			}	
		});
	}
	
	// Método handler que actualiza y asocia un producto de la base de datos, dado su id, con una imagen.Tanto el id como la imagen se obtienen de la petición http "request"
	// Este método se va a mapear con una ruta en el método "routes()" de nuestra clase de configuración de Spring "RouterFunctionConfig"
	// A diferencia de nuestro otro controlador Api Rest "ProductoController",cuya clase se anota con @RestControler y sus métodos handler devuelven flujos reactivos Mono de tipo "ResponseEntity",en este tipo de controlador,implementado con la técnica "Functional Endpoints",sus métodos handler van a devolver flujos reactivos Mono de tipo "ServerResponse"
	// Otra diferencia con respecto a nuestro otro controlador Api Rest "ProductoController" anotado con @RestController,para la obtención de los datos que viajan en el cuerpo de la petición y de la url o path,se utiliza la instancia de tipo "ServerRequest" que se le pasa como parámetro de entrada a este método.En el otro controlador se usaban para ello anotaciones como @RequestBody,@PathVariable y @RequestParam
	public Mono<ServerResponse> crearConFoto(ServerRequest request){
		// Obtenemos del cuerpo de la petición http el nombre, el precio y la categoría del producto a crear que viaja en los campos de un formulario FormData
		// El método "multipartData()" devuelve un flujo reactivo Mono que tiene como elemento un objeto de tipo "MultiValueMap<String, Part>" que se corresponde con el fomulario FormData que viaja en la petición http Post
		Mono<Producto> producto = request.multipartData()
				// El nombre,precio,id de la categoría y nombre de la categoría viajan en los campos "nombre","precio","categoria.id" y "categoria.nombre" respectivamente de dicho formulario FormData y, para obtener sus valores para crear un producto, tenemos que transformar este flujo reactivo Mono de tipo "MultiValueMap<String, Part>" en otro flujo reactivo Mono de tipo "Producto"
				.map(multipart -> {
					// Obtenemos los campos "nombre","precio","categoria.id" y "categoria.nombre" del formulario FormData como objetos de tipo "FormFieldPart"
					// Como el método "get()" nos devuelve el tipo de dato "Part", que es muy genérico, tenemos que hacer un casting o conversión al tipo de dato que nos interesa, que en este caso es "FormFieldPart", para posteriormente obtener sus valores
					FormFieldPart nombre = (FormFieldPart)multipart.toSingleValueMap().get("nombre");
					FormFieldPart precio = (FormFieldPart)multipart.toSingleValueMap().get("precio");
					FormFieldPart categoriaId = (FormFieldPart)multipart.toSingleValueMap().get("categoria.id");
					FormFieldPart categoriaNombre = (FormFieldPart)multipart.toSingleValueMap().get("categoria.nombre");
					
					// Creamos la categoría del producto usando el valor del campo "categoriaNombre" obtenido en el punto anterior y establecemos su identificador mediante el valor del campo "categoriaId"
					Categoria categoria = new Categoria(categoriaNombre.value());
					categoria.setId(categoriaId.value());
					
					// Devolvemos un nuevo producto mediante el valor del campo "nombre", el valor del campo "precio"(como este valor es un String y el constructor espera un dato de tipo Double, tenemos que convertirlo usando el método "valueOf()" de la clase "Double") y la categoría creada anteriormente
					return new Producto(nombre.value(),Double.valueOf(precio.value()),categoria);
				});
		
		// Obtenemos del cuerpo de la petición http la imagen que viaja en un campo de un formulario FormData
		// El método "multipartData()" devuelve un flujo reactivo Mono que tiene como elemento un objeto de tipo "MultiValueMap<String, Part>" que se corresponde con el fomulario FormData que viaja en la petición http Post
		return request.multipartData()
				// La imagen viaja en el campo "file" de dicho formulario FormData y, para obtenerla, tenemos que transformar este flujo reactivo Mono de tipo "MultiValueMap<String, Part>" en otro flujo reactivo Mono de tipo "Part" con el contenido del campo "file" de dicho formulario
				.map(multipart -> multipart.toSingleValueMap().get("file"))
				// Como el elemento del flujo reactivo Mono anterior es de tipo "Part", que es muy genérico, tenemos que hacer un casting para convertirlo al tipo más concreto que nos interesa, es decir, en este caso al tipo "FilePart" porque estamas tratando con archivos de imagen
				.cast(FilePart.class)
				// Con el operador flatMap,transformamos el flujo reactivo Mono anterior, con el objeto "FilePart" de la imagen como elemento, en otro flujo reactivo Mono cuyo elemento va a ser a su vez otro flujo reactivo Mono con los datos del producto obtenido de la petición http.Como al final tenemos un flujo reactivo Mono con otro flujo reactivo Mono a su vez,con este operador,en lugar de "map",se va a realizar un procedimiento de aplanamiento para que definitivamente nos quede un único flujo reactivo Mono de un elemento no reactivo
				.flatMap(file -> producto
						// Con el operador flatMap,transformamos el flujo reactivo Mono anterior, con el producto obtenido de la petición http como elemento, en otro flujo reactivo Mono cuyo elemento va a ser a su vez otro flujo reactivo Mono con los datos del producto guardado con la imagen en la base de datos.Como al final tenemos un flujo reactivo Mono con otro flujo reactivo Mono a su vez,con este operador,en lugar de "map",se va a realizar un procedimiento de aplanamiento para que definitivamente nos quede un único flujo reactivo Mono de un elemento no reactivo
						.flatMap(p -> {
							// Relacionamos la imagen que vamos a subir con el producto obtenido de la petición http
							// Cada imagen que relacionemos con un producto, tiene que tener un nombre único y, para ello, usamos la clase "UUDI" que nos permite generar un número aleatrio al que convertimos en un String para concatenarlo al nombre de dicha imagen mediante otro String "-"
							// Además, eliminamos los caracteres especiales ' ',':' y '\\' que tiene el nombre de la imagen cuando se selecciona para ser subida(el nombre de la imagen contiene toda la ruta o path donde se localiza)
							p.setFoto(UUID.randomUUID().toString() + "-" + file.filename().replace(" ","").replace(":","").replace("\\",""));
							// Antes de persistir el producto con su imagen en la base de datos, tenemos que establecer la fecha de creación de este producto y, para ello, usamos la fecha actual del sistema
							p.setCreateAt(new Date());
							// Mediante el método "transferTo(ruta)" del objeto "file" se realiza la subida a nuestro directorio de subidas cuya ruta nos la da el bean "path"(su valor se obtiene del archivo de configuración "application.properties" de este proyecto)
							// Para ello, tenemos que pasarle a dicho método una instancia de tipo File con la ruta completa de la subida, es decir, la ruta contenida en el bean "path" + el nombre único de la imagen obtenido en el paso anterior
							// Ahora solo nos falta persistir el producto con su imagen en la base de datos mediante el método "save()" del bean "productoService"
							// Como el método "transferTo(ruta)" del objeto "file" devuelve un flujo reactivo Mono con un elemento de tipo Void,después del operador "flatMap" no podemos usar un operador "map" para actualizar en la base de datos el producto junto con su imagen y obtener un flujo reactivo Mono con el producto actualizado como elemento porque, al ser el elemento de tipo Void,no va a emitirse para que se ejecute dicho operador "map"
							// Por esta razón,tenemos que crear el flujo reactivo Mono, con el producto persistido como elemento, dentro del operador "flatMap" usando el método "then()"(Es "then()" porque lo estamos aplicando sobre un flujo reactivo Mono.En cambio,se usa "thenMany()" si se aplica sobre un flujo reactivo Flux)
							return file.transferTo(new File(path + p.getFoto()))
									// Actualizamos los datos del producto con su imagen en la base de datos mediante el bean 'productoService'
									// El método "then" espera a que el flujo reactivo original termine de emitir su único elemento para comenzar a procesar un nuevo flujo reactivo Mono, es decir, comienza a procesar un nuevo flujo reactivo Mono cuando se produce el evento OnComplete del flujo reactivo original
					    			// Al final, el único elemento del flujo reactivo original es ignorado y se tiene en cuenta el elemento del nuevo flujo reactivo Mono
									// "thenMany" se usa para crear flujos reactivos Flux y "then" para crear flujos reactivos Mono cuando el flujo reactivo original ha finalizado de emitir su último elemento
									.then(productoService.save(p));
						}))
				// Como necesitamos devolver un flujo reactivo Mono de un ServerResponse para manejar y personalizar la respuesta,con el operador "flatMap" transformamos el flujo reactivo Mono anterior en otro flujo reactivo Mono que tiene a su vez otro flujo reactivo Mono con dicho elemento ServerResponse.Como al final tenemos un flujo reactivo Mono con otro flujo reactivo Mono a su vez,con este operador,en lugar de "map",se va a realizar un procedimiento de aplanamiento para que definitivamente nos quede un único flujo reactivo Mono de un elemento no reactivo
				.flatMap(p -> ServerResponse.created(URI.create("/api/v2/productos/".concat(p.getId()))) // Establecemos el estado de la respuesta en CREATED(200) y le pasamos la localización del producto que se acaba de persistir con la imagen usando la clase URI junto con la ruta o path de dicho producto.Esta localización va en la cabecera de la respuesta
						.contentType(MediaType.APPLICATION_JSON_UTF8) // Establecemos el ContentType de la respuesta con el MediaType APPLICATION_JSON_UTF8(El cuerpo de la respuesta va en formato Json y con la codificación de caracters UTF-8).El valor por defecto del ContenType de una respuesta es un MediaType con el valor APPLICATION_JSON
						.body(BodyInserters.fromObject(p))); // Establecemos como cuerpo de la respuesta el producto que acabamos de guardar con la imagen en la base de datos.En esta caso,el método "body()" recibe un flujo reactivo Mono o Flux,o un dato de tipo BodyInserters,este último para cuando tenemos datos que no son flujos reactivos.Como nuestro dato "p" no es flujo reactivo,tenemos que usar el método "fromObject()" de la clase "BodyInserters"	
	}
	
	// Método handler que actualiza y asocia un producto de la base de datos, dado su id, con una imagen.Tanto el id como la imagen se obtienen de la petición http "request"
	// Este método se va a mapear con una ruta en el método "routes()" de nuestra clase de configuración de Spring "RouterFunctionConfig"
	// A diferencia de nuestro otro controlador Api Rest "ProductoController",cuya clase se anota con @RestControler y sus métodos handler devuelven flujos reactivos Mono de tipo "ResponseEntity",en este tipo de controlador,implementado con la técnica "Functional Endpoints",sus métodos handler van a devolver flujos reactivos Mono de tipo "ServerResponse"
	// Otra diferencia con respecto a nuestro otro controlador Api Rest "ProductoController" anotado con @RestController,para la obtención de los datos que viajan en el cuerpo de la petición y de la url o path,se utiliza la instancia de tipo "ServerRequest" que se le pasa como parámetro de entrada a este método.En el otro controlador se usaban para ello anotaciones como @RequestBody,@PathVariable y @RequestParam
	public Mono<ServerResponse> upload(ServerRequest request){
		// Obtenemos de la url  o path de la petición http el id del producto
		String id = request.pathVariable("id");
		// Obtenemos del cuerpo de la petición http la imagen que viaja en un campo de un formulario FormData
		// El método "multipartData()" devuelve un flujo reactivo Mono que tiene como elemento un objeto de tipo "MultiValueMap<String, Part>" que se corresponde con el fomulario FormData que viaja en la petición http Post
		return request.multipartData()
				// La imagen viaja en el campo "file" de dicho formulario FormData y, para obtenerla, tenemos que transformar este flujo reactivo Mono de tipo "MultiValueMap<String, Part>" en otro flujo reactivo Mono de tipo "Part" con el contenido del campo "file" de dicho formulario
				.map(multipart -> multipart.toSingleValueMap().get("file"))
				// Como el elemento del flujo reactivo Mono anterior es de tipo "Part", que es muy genérico, tenemos que hacer un casting para convertirlo al tipo más concreto que nos interesa, es decir, en este caso al tipo "FilePart" porque estamas tratando con archivos de imagen
				.cast(FilePart.class)
				// Con el operador flatMap,transformamos el flujo reactivo Mono anterior con el objeto "FilePart" de la imagen como elemento en otro flujo reactivo Mono cuyo elemento va a ser a su vez otro flujo reactivo Mono con los datos del producto localizado en la base de datos.Como al final tenemos un flujo reactivo Mono con otro flujo reactivo Mono a su vez,con este operador,en lugar de "map",se va a realizar un procedimiento de aplanamiento para que definitivamente nos quede un único flujo reactivo Mono de un elemento no reactivo
				.flatMap(file -> productoService.findById(id)
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
							// Por esta razón,tenemos que crear el flujo reactivo Mono con el producto editado como elemento dentro del operador "flatMap" usando el método "then()"(Es "then()" porque lo estamos aplicando sobre un flujo reactivo Mono.En cambio,se usa "thenMany()" si se aplica sobre un flujo reactivo Flux)
							return file.transferTo(new File(path + p.getFoto()))
									// Actualizamos los datos del producto con su imagen en la base de datos mediante el bean 'productoService'
									// El método "then" espera a que el flujo reactivo original termine de emitir su único elemento para comenzar a procesar un nuevo flujo reactivo Mono, es decir, comienza a procesar un nuevo flujo reactivo Mono cuando se produce el evento OnComplete del flujo reactivo original
					    			// Al final, el único elemento del flujo reactivo original es ignorado y se tiene en cuenta el elemento del nuevo flujo reactivo Mono
									// "thenMany" se usa para crear flujos reactivos Flux y "then" para crear flujos reactivos Mono cuando el flujo reactivo original ha finalizado de emitir su último elemento
									.then(productoService.save(p));
						}))
				// Como necesitamos devolver un flujo reactivo Mono de un ServerResponse para manejar y personalizar la respuesta,con el operador "flatMap" transformamos el flujo reactivo Mono anterior en otro flujo reactivo Mono que tiene a su vez otro flujo reactivo Mono con dicho elemento ServerResponse.Como al final tenemos un flujo reactivo Mono con otro flujo reactivo Mono a su vez,con este operador,en lugar de "map",se va a realizar un procedimiento de aplanamiento para que definitivamente nos quede un único flujo reactivo Mono de un elemento no reactivo
				.flatMap(p -> ServerResponse.created(URI.create("/api/v2/productos/".concat(p.getId()))) // Establecemos el estado de la respuesta en CREATED(200) y le pasamos la localización del producto que se acaba de actualizar con la imagen usando la clase URI junto con la ruta o path de dicho producto.Esta localización va en la cabecera de la respuesta
						.contentType(MediaType.APPLICATION_JSON_UTF8) // Establecemos el ContentType de la respuesta con el MediaType APPLICATION_JSON_UTF8(El cuerpo de la respuesta va en formato Json y con la codificación de caracters UTF-8).El valor por defecto del ContenType de una respuesta es un MediaType con el valor APPLICATION_JSON
						.body(BodyInserters.fromObject(p))) // Establecemos como cuerpo de la respuesta el producto que acabamos de editar con la imagen en la base de datos.En esta caso,el método "body()" recibe un flujo reactivo Mono o Flux,o un dato de tipo BodyInserters,este último para cuando tenemos datos que no son flujos reactivos.Como nuestro dato "p" no es flujo reactivo,tenemos que usar el método "fromObject()" de la clase "BodyInserters"	
				// Con el método "switchIfEmpty()",si el flujo reactivo Mono anterior tiene como elemento un elemento vacío o nulo porque no se ha localizado ningún producto a partir del id obtenido de la url o path,sustituimos dicho flujo por otro flujo reactivo Mono con un elemento ServerResponse con el estado de la respuesta NOT_FOUND(404) y su cuerpo vacío(sin datos)
				.switchIfEmpty(ServerResponse.notFound().build()); // El método "build()" nos permite crear un ServerResponse con su cuerpo o body vacío(sin datos)
	}
	
	// Método handler que actualiza un producto de la base de datos, dado su id y dado los nuevos datos a editar, que son obtenidos de la petición http "request"
	// Este método se va a mapear con una ruta en el método "routes()" de nuestra clase de configuración de Spring "RouterFunctionConfig"
	// A diferencia de nuestro otro controlador Api Rest "ProductoController",cuya clase se anota con @RestControler y sus métodos handler devuelven flujos reactivos Mono de tipo "ResponseEntity",en este tipo de controlador,implementado con la técnica "Functional Endpoints",sus métodos handler van a devolver flujos reactivos Mono de tipo "ServerResponse"
	// Otra diferencia con respecto a nuestro otro controlador Api Rest "ProductoController" anotado con @RestController,para la obtención de los datos que viajan en el cuerpo de la petición y de la url o path,se utiliza la instancia de tipo "ServerRequest" que se le pasa como parámetro de entrada a este método.En el otro controlador se usaban para ello anotaciones como @RequestBody,@PathVariable y @RequestParam
	public Mono<ServerResponse> editar(ServerRequest request){
		// Obtenemos del cuerpo de la petición http los nuevos datos del producto a actualizar en la base de datos como un flujo reactivo Mono
		Mono<Producto> producto = request.bodyToMono(Producto.class);
		//Obtenemos de la url  o path de la petición http el id del producto
		String id = request.pathVariable("id");
		// Recuperamos de la base de datos el producto como un flujo reactivo Mono a partir del id recuperado de la url o path.Esto lo hacemos mediante el bean 'productoService'
		Mono<Producto> productoDb = productoService.findById(id);
		
		// Con el operador "zipWith" combinamos el flujo reactivo Mono "productoDb" con el flujo reactivo Mono "producto" y al final devolvemos el flujo reactivo Mono "productoDb" con los campos del producto que tiene como elemento actualizados a partir de los campos del producto contenido en el otro flujo reactivo Mono "producto"
		return productoDb.zipWith(producto, (db,req) -> {
			// Mapeamos los nuevos datos del producto que nos llegan desde el flujo reactivo Mono "producto" con los campos correspondientes del producto contenido en el flujo reactivo Mono "productoDb"
			// La fecha no se actualiza porque se trata de una fecha de creación del producto y no de edición
			// La foto del producto se guarda y se edita usando otro método handler especifico para ello ya que una foto se envía desde una petición http como un MultipartFile y no como un Json
			db.setNombre(req.getNombre());
			db.setPrecio(req.getPrecio());
			db.setCategoria(req.getCategoria());
			// Devolvemos el producto actualizado contenido en el flujo reactivo Mono "productoDb"
			return db;
		})
		// Como necesitamos devolver un flujo reactivo Mono de un ServerResponse para manejar y personalizar la respuesta,con el operador "flatMap" transformamos el flujo reactivo Mono anterior en otro flujo reactivo Mono que tiene a su vez otro flujo reactivo Mono con dicho elemento ServerResponse.Como al final tenemos un flujo reactivo Mono con otro flujo reactivo Mono a su vez,con este operador,en lugar de "map",se va a realizar un procedimiento de aplanamiento para que definitivamente nos quede un único flujo reactivo Mono de un elemento no reactivo
		.flatMap(p -> ServerResponse.created(URI.create("/api/v2/productos/".concat(p.getId()))) // Establecemos el estado de la respuesta en CREATED(200) y le pasamos la localización del producto que se va a editar usando la clase URI junto con la ruta o path de dicho producto.Esta localización va en la cabecera de la respuesta
				.contentType(MediaType.APPLICATION_JSON_UTF8) // Establecemos el ContentType de la respuesta con el MediaType APPLICATION_JSON_UTF8(El cuerpo de la respuesta va en formato Json y con la codificación de caracters UTF-8).El valor por defecto del ContenType de una respuesta es un MediaType con el valor APPLICATION_JSON
				.body(productoService.save(p),Producto.class)) // Actualizamos los datos del producto en la base de datos mediante el bean 'productoService'.En esta caso,el método "body()" recibe un flujo reactivo Mono o Flux,o un dato de tipo BodyInserters,este último para cuando tenemos datos que no son flujos reactivos. Como el método "save()" devuelve directamente un flujo reactivo Mono,tenemos que especificar,además,el tipo o la clase a la que pertenece el elemento de este flujo reactivo
		// Con el método "switchIfEmpty()",si el flujo reactivo Mono anterior tiene como elemento un elemento vacío o nulo porque no se ha localizado ningún producto a partir del id obtenido de la url o path,sustituimos dicho flujo por otro flujo reactivo Mono con un elemento ServerResponse con el estado de la respuesta NOT_FOUND(404) y su cuerpo vacío(sin datos)
		.switchIfEmpty(ServerResponse.notFound().build()); // El método "build()" nos permite crear un ServerResponse con su cuerpo o body vacío(sin datos)
		
	}
	
	// Método handler que elimina un producto de la base de datos, dado su id, que viaja en la url o path y lo obtenemos a través de la petición http "request"
	// Este método se va a mapear con una ruta en el método "routes()" de nuestra clase de configuración de Spring "RouterFunctionConfig"
	// A diferencia de nuestro otro controlador Api Rest "ProductoController",cuya clase se anota con @RestControler y sus métodos handler devuelven flujos reactivos Mono de tipo "ResponseEntity",en este tipo de controlador,implementado con la técnica "Functional Endpoints",sus métodos handler van a devolver flujos reactivos Mono de tipo "ServerResponse"
	// Otra diferencia con respecto a nuestro otro controlador Api Rest "ProductoController" anotado con @RestController,para la obtención de los datos que viajan en el cuerpo de la petición y de la url o path,se utiliza la instancia de tipo "ServerRequest" que se le pasa como parámetro de entrada a este método.En el otro controlador se usaban para ello anotaciones como @RequestBody,@PathVariable y @RequestParam
	public Mono<ServerResponse> eliminar(ServerRequest request){
		//Obtenemos de la url  o path de la petición http el id del producto
		String id = request.pathVariable("id");
		// Recuperamos de la base de datos el producto como un flujo reactivo Mono a partir del id recuperado de la url o path.Esto lo hacemos mediante el bean 'productoService'
		Mono<Producto> productoDb = productoService.findById(id);
		// Con el operador flatMap,transformamos el flujo reactivo Mono anterior con el producto localizado como elemento en otro flujo reactivo Mono cuyo elemento va a ser a su vez otro flujo reactivo Mono con un elemento ServerResponse de tipo Void.Como al final tenemos un flujo reactivo Mono con otro flujo reactivo Mono a su vez,con este operador,en lugar de "map",se va a realizar un procedimiento de aplanamiento para que definitivamente nos quede un único flujo reactivo Mono de un elemento no reactivo
		// Como el método "delete()" del bean "productoService" devuelve un flujo reactivo Mono con un elemento de tipo Void,después del operador "flatMap" no podemos usar un operador "map" para obtener un flujo reactivo Mono con un elemento ServerResponse porque, al ser el elemento de tipo Void,no va a emitirse para que se ejecute dicho operador "map"
		// Por esta razón,tenemos que crear el flujo reactivo Mono con el elemento ServerResponse dentro del operador "flatMap" usando el método "then()"(Es "then()" porque lo estamos aplicando sobre un flujo reactivo Mono.En cambio,se usa "thenMany()" si se aplica sobre un flujo reactivo Flux)
		return productoDb.flatMap(p -> productoService.delete(p)  // Eliminarmos de la base datos el producto localizado mediante el bean 'productoService'
				// Creamos un flujo reactivo Mono con un elemento ServerResponse de tipo Void con el estado de la respuesta establecido en NO_CONTENT(204)
				// El método "then" espera a que el flujo reactivo original termine de emitir su único elemento para comenzar a procesar un nuevo flujo reactivo Mono, es decir, comienza a procesar un nuevo flujo reactivo Mono cuando se produce el evento OnComplete del flujo reactivo original
    			// Al final, el único elemento del flujo reactivo original es ignorado y se tiene en cuenta el elemento del nuevo flujo reactivo Mono
				// "thenMany" se usa para crear flujos reactivos Flux y "then" para crear flujos reactivos Mono cuando el flujo reactivo original ha finalizado de emitir su último elemento
				.then(ServerResponse.noContent().build()))
				// Con el método "switchIfEmpty()",si el flujo reactivo Mono anterior tiene como elemento un elemento vacío o nulo porque no se ha localizado ningún producto a partir del id obtenido de la url o path,sustituimos dicho flujo por otro flujo reactivo Mono con un elemento ServerResponse con el estado de la respuesta NOT_FOUND(404) y su cuerpo vacío(sin datos)
				.switchIfEmpty(ServerResponse.notFound().build()); // El método "build()" nos permite crear un ServerResponse con su cuerpo o body vacío(sin datos)
	}

}
