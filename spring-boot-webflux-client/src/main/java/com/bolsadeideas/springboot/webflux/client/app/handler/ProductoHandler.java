package com.bolsadeideas.springboot.webflux.client.app.handler;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.bolsadeideas.springboot.webflux.client.app.models.Producto;
import com.bolsadeideas.springboot.webflux.client.app.models.services.ProductoService;

import reactor.core.publisher.Mono;

//Esta clase contiene todos los métodos handler que se van a usar en nuestra clase de configuración de Spring "RouterConfig" para configurar el mapeo de rutas para peticiones http usando la técnica "Functional Endpoints"

@Component // Con esta anotación indicamos que esta clase es un componente de Spring y,por lo tanto,Spring va a almacenar un bean de esta clase en su contenedor o memoria para poder utilizarlo en otra clase del proyecto
public class ProductoHandler {
	
	// Recuperamos de la memoria o contendor de Spring el bean que implementa la interfaz "ProductoService".Esta interfaz es implementada por la clase "ProductoServiceImpl"
	@Autowired
	private ProductoService productoService; // Este bean representa la capa Servicio para realizar peticiones http a nuestra Api Rest sobre productos mediante nuestro cliente web
	
	// Método handler que devuelve los datos de todos los productos obtenidos desde nuestro cliente web
	// Este método se va a mapear con una ruta en el método "rutas()" de nuestra clase de configuración de Spring "RouterConfig"
	// A diferencia de un controlador Api Rest anotado con @RestControler, donde sus métodos handler devuelven flujos reactivos Mono de tipo "ResponseEntity",en este tipo de controlador,implementado con la técnica "Functional Endpoints",sus métodos handler van a devolver flujos reactivos Mono de tipo "ServerResponse"
	// Otra diferencia con respecto a un controlador Api Rest anotado con @RestController,para la obtención de los datos que viajan en el cuerpo de la petición y de la url o path,se utiliza la instancia de tipo "ServerRequest", que se le pasa como parámetro de entrada a este método.Sin embargo,En el controlador anotado con @RestController se usa para ello anotaciones como @RequestBody,@PathVariable y @RequestParam
	public Mono<ServerResponse> listar(ServerRequest request){
		// Creamos y devolvemos un flujo reactivo Mono con un elemento ServerResponse con la respuesta configurada con el estado OK(200),con el ContentType asociado al MediaType APPLICATION_JSON_UTF8(El cuerpo de la respuesta va en formato Json y con la codificación de caracters UTF-8) y,como cuerpo de la respuesta,un flujo reactivo Flux con los productos obtenidos de nuestro cliente web a través de la capa de Servicio mediante el bean "productoService"
		return ServerResponse.ok() // El estado OK(200) es el estado por defecto de la respuesta.Si no se especifica otro,se utiliza este estado
				// El ContentType con el MediaType APPLICATION_JSON es el valor por defecto para una respuesta.Si no se especifica otro(En este caso lo estamos haciendo dando el valor APPLICATION_JSON_UTF8 - El cuerpo de la respuesta va en formato Json y con la codificación de caracters UTF-8),se utiliza este valor
				// NOTA: "MediaType.APPLICATION_JSON_UTF8" está actualmente "deprecated".Ahora también se usa el valor "MediaType.APPLICATION_JSON" para tratar MediaTypes de tipo JSON + UTF_8
				.contentType(MediaType.APPLICATION_JSON)
				.body(productoService.findAll(),Producto.class); // Recuperamos de la capa Service mediante el bean 'productoService' el listado de productos como un stream reactivo de tipo Flux(varios items.Si fuese sólo un item,sería de tipo Mono).En esta caso,el método "body()" recibe un flujo reactivo Mono o Flux,o un dato de tipo BodyInserters,este último para cuando tenemos datos que no son flujos reactivos. Como el método "findAll()" devuelve directamente un flujo reactivo Flux,tenemos que especificar,además,el tipo o la clase a la que pertenecen los elementos de este flujo reactivo
	}
	
	// Método handler que devuelve los datos de un producto obtenido de nuestro cliente web a partir de su id, que es obtenido de la petición http "request"
	// Este método se va a mapear con una ruta en el método "rutas()" de nuestra clase de configuración de Spring "RouterConfig"
	// A diferencia de un controlador Api Rest anotado con @RestControler, donde sus métodos handler devuelven flujos reactivos Mono de tipo "ResponseEntity",en este tipo de controlador,implementado con la técnica "Functional Endpoints",sus métodos handler van a devolver flujos reactivos Mono de tipo "ServerResponse"
	// Otra diferencia con respecto a un controlador Api Rest anotado con @RestController,para la obtención de los datos que viajan en el cuerpo de la petición y de la url o path,se utiliza la instancia de tipo "ServerRequest", que se le pasa como parámetro de entrada a este método.Sin embargo,En el controlador anotado con @RestController se usa para ello anotaciones como @RequestBody,@PathVariable y @RequestParam
	public Mono<ServerResponse> ver(ServerRequest request){
		//Obtenemos de la url  o path de la petición http el id del producto
		String id = request.pathVariable("id");
		
		// Con nuestro método privado "errorHandler()" añadimos el manejo de excepciones con estados de error NOT_FOUND(404) a lo que le pasamos como parámetro de entrada
		return errorHandler(productoService.findById(id) // Recuperamos de nuestro cliente web el producto como un flujo reactivo Mono a partir del id recuperado de la url o path.Esto lo hacemos mediante el bean 'productoService'
				// Como necesitamos devolver un flujo reactivo Mono de un ServerResponse para manejar y personalizar la respuesta,con el operador "map" transformamos el flujo reactivo Mono anterior en otro flujo reactivo Mono con un elemento ServerResponse
				.flatMap(p -> ServerResponse.ok() // Establecemos el estado de la respuesta en OK(200),que en realidad no hace falta porque es el valor por defecto del estado de una respuesta
						// Establecemos el ContentType de la respuesta con el MediaType APPLICATION_JSON_UTF8(El cuerpo de la respuesta va en formato Json y con la codificación de caracters UTF-8).El valor por defecto del ContenType de una respuesta es un MediaType con el valor APPLICATION_JSON
						// NOTA: "MediaType.APPLICATION_JSON_UTF8" está actualmente "deprecated".Ahora también se usa el valor "MediaType.APPLICATION_JSON" para tratar MediaTypes de tipo JSON + UTF_8	
						.contentType(MediaType.APPLICATION_JSON)
						// El método "bodyValue()" es una alternativa más sencilla a usar el método "body()"
						.bodyValue(p))); // Establecemos como cuerpo de la respuesta el producto recuperado de nuestro cliente web
	}
	
	// Método handler que crea un nuevo producto mediante nuestro cliente web a partir de los datos que se obtienen de la petición http "request"
	// Este método se va a mapear con una ruta en el método "rutas()" de nuestra clase de configuración de Spring "RouterConfig"
	// A diferencia de un controlador Api Rest anotado con @RestControler, donde sus métodos handler devuelven flujos reactivos Mono de tipo "ResponseEntity",en este tipo de controlador,implementado con la técnica "Functional Endpoints",sus métodos handler van a devolver flujos reactivos Mono de tipo "ServerResponse"
	// Otra diferencia con respecto a un controlador Api Rest anotado con @RestController,para la obtención de los datos que viajan en el cuerpo de la petición y de la url o path,se utiliza la instancia de tipo "ServerRequest", que se le pasa como parámetro de entrada a este método.Sin embargo,En el controlador anotado con @RestController se usa para ello anotaciones como @RequestBody,@PathVariable y @RequestParam
	public Mono<ServerResponse> crear(ServerRequest request){
		// Obtenemos del cuerpo de la petición http los datos del producto a persistir como un flujo reactivo Mono
		Mono<Producto> producto = request.bodyToMono(Producto.class);
		
		// Sobre el flujo reactivo Mono con el producto recibido de la petición http,con el operador "flatMap" transformamos este flujo reactivo Mono en otro flujo reactivo Mono cuyo elemento va a ser a su vez otro flujo reactivo Mono con un elemento ServerResponse.Como al final tenemos un flujo reactivo Mono con otro flujo reactivo Mono a su vez,con este operador,en lugar de "map",se va a realizar un procedimiento de aplanamiento para que definitivamente nos quede un único flujo reactivo Mono de un elemento no reactivo
		return producto.flatMap(p -> {
			// Si la fecha del producto nos llega vacía desde la petición http,le asignamos la fecha actual del sistema
			if(p.getCreateAt() == null)
				p.setCreateAt(new Date());
			
			// Persistimos el producto recibido de la petición http mediante nuestro cliente web usando el bean 'productoService'
			return productoService.save(p);
		})
		// Como necesitamos devolver un flujo reactivo Mono de un ServerResponse para manejar y personalizar la respuesta,con el operador "flatMap" transformamos el flujo reactivo Mono anterior en otro flujo reactivo Mono que tiene a su vez otro flujo reactivo Mono con dicho elemento ServerResponse.Como al final tenemos un flujo reactivo Mono con otro flujo reactivo Mono a su vez,con este operador,en lugar de "map",se va a realizar un procedimiento de aplanamiento para que definitivamente nos quede un único flujo reactivo Mono de un elemento no reactivo
		.flatMap(p -> ServerResponse.created(URI.create("/api/client/".concat(p.getId()))) // Establecemos el estado de la respuesta en CREATED(200) y le pasamos la localización del producto que se acaba de crear usando la clase URI junto con la ruta o path de dicho producto.Esta localización va en la cabecera de la respuesta
				// Establecemos el ContentType de la respuesta con el MediaType APPLICATION_JSON_UTF8(El cuerpo de la respuesta va en formato Json y con la codificación de caracters UTF-8).El valor por defecto del ContenType de una respuesta es un MediaType con el valor APPLICATION_JSON
				// NOTA: "MediaType.APPLICATION_JSON_UTF8" está actualmente "deprecated".Ahora también se usa el valor "MediaType.APPLICATION_JSON" para tratar MediaTypes de tipo JSON + UTF_8	
				.contentType(MediaType.APPLICATION_JSON)
				// El método "bodyValue()" es una alternativa más sencilla a usar el método "body()"
				.bodyValue(p)) // Establecemos como cuerpo de la respuesta el producto que acabamos de crear usando nuestro cliente web
		// Con el operador "onErrorResume" capturamos y manejamos las excepciones producidas por nuestro cliente web en el momento de realizar la petición http a nuestra Api Rest de productos para crear un nuevo producto
		// Este operador recibe un parámetro de tipo "Throwable",que es una excepción muy genérica,y,por lo tanto,tenemos que hacer un cast o conversión a un tipo de excepción más concreta.En este caso,hacemos el cast al tipo de excepción "WebClientResponseException" que es la encargada de darnos la lista de errores por parte de nuestro cliente web
		.onErrorResume(error -> {
			// Convertimos la excepción genérica recibida en "error" en una excepción de tipo "WebClientResponseException"
			WebClientResponseException errorResponse = (WebClientResponseException)error;
			// Si la excepción contiene un estado de error de tipo BAD_REQUEST(400)...(Significa que nuestra Api Rest de productos ha dado un error de validación en alguno de los campos del nuevo producto que se desea crear)
			if(errorResponse.getStatusCode() == HttpStatus.BAD_REQUEST)
				// Creamos y devolvemos un flujo reactivo Mono con un elemento ServerResponse con la lista de errores devuelta por la excepción indicando también su estado en BAD_REQUEST(400)
				return ServerResponse.badRequest()
						// Establecemos el ContentType de la respuesta con el MediaType APPLICATION_JSON_UTF8(El cuerpo de la respuesta va en formato Json y con la codificación de caracters UTF-8).El valor por defecto del ContenType de una respuesta es un MediaType con el valor APPLICATION_JSON
						// NOTA: "MediaType.APPLICATION_JSON_UTF8" está actualmente "deprecated".Ahora también se usa el valor "MediaType.APPLICATION_JSON" para tratar MediaTypes de tipo JSON + UTF_8	
						.contentType(MediaType.APPLICATION_JSON)
						// El método "bodyValue()" es una alternativa más sencilla a usar el método "body()"
						.bodyValue(errorResponse.getResponseBodyAsString());  // Establecemos como cuerpo de la respuesta la lista de errores en formato String obtenida de la excepción
			// Si la escepción producida por nuestro cliente web contiene otro estado de error distinto a BAD_REQUEST(400)...
			return Mono.error(errorResponse); // Creamos y devolvemos un flujo reactivo Mono con un elemento ServerResponse con la información de dicha excepción
			
		});
	}
	
	// Método handler que actualiza un producto,dado su id  y dado los nuevos datos a editar,mediante nuestro cliente web.Tanto el id del producto como los nuevos datos a editar son obtenidos de la petición http "request"
	// Este método se va a mapear con una ruta en el método "rutas()" de nuestra clase de configuración de Spring "RouterConfig"
	// A diferencia de un controlador Api Rest anotado con @RestControler, donde sus métodos handler devuelven flujos reactivos Mono de tipo "ResponseEntity",en este tipo de controlador,implementado con la técnica "Functional Endpoints",sus métodos handler van a devolver flujos reactivos Mono de tipo "ServerResponse"
	// Otra diferencia con respecto a un controlador Api Rest anotado con @RestController,para la obtención de los datos que viajan en el cuerpo de la petición y de la url o path,se utiliza la instancia de tipo "ServerRequest", que se le pasa como parámetro de entrada a este método.Sin embargo,En el controlador anotado con @RestController se usa para ello anotaciones como @RequestBody,@PathVariable y @RequestParam
	public Mono<ServerResponse> editar(ServerRequest request){
		// Obtenemos del cuerpo de la petición http los datos del producto a persistir como un flujo reactivo Mono
		Mono<Producto> producto = request.bodyToMono(Producto.class);
		//Obtenemos de la url  o path de la petición http el id del producto
		String id = request.pathVariable("id");
		
		// Sobre el flujo reactivo Mono con el producto recibido de la petición http,con el operador "flatMap" transformamos este flujo reactivo Mono en otro flujo reactivo Mono cuyo elemento va a ser a su vez otro flujo reactivo Mono con el producto editado a partir de nuestro cliente web.Como al final tenemos un flujo reactivo Mono con otro flujo reactivo Mono a su vez,con este operador,en lugar de "map",se va a realizar un procedimiento de aplanamiento para que definitivamente nos quede un único flujo reactivo Mono de un elemento no reactivo
		// Con nuestro método privado "errorHandler()" añadimos el manejo de excepciones con estados de error NOT_FOUND(404) a lo que le pasamos como parámetro de entrada
		return errorHandler(producto.flatMap(p -> productoService.update(p,id)) // Actualizamos los datos del producto a partir de nuestro cliente web usando el bean 'productoService'
				// Como necesitamos devolver un flujo reactivo Mono de un ServerResponse para manejar y personalizar la respuesta,con el operador "flatMap" transformamos el flujo reactivo Mono anterior en otro flujo reactivo Mono que tiene a su vez otro flujo reactivo Mono con dicho elemento ServerResponse.Como al final tenemos un flujo reactivo Mono con otro flujo reactivo Mono a su vez,con este operador,en lugar de "map",se va a realizar un procedimiento de aplanamiento para que definitivamente nos quede un único flujo reactivo Mono de un elemento no reactivo
				.flatMap(p -> ServerResponse.created(URI.create("/api/client/".concat(p.getId()))) // Establecemos el estado de la respuesta en CREATED(200) y le pasamos la localización del producto que se va a editar usando la clase URI junto con la ruta o path de dicho producto.Esta localización va en la cabecera de la respuesta
				// Establecemos el ContentType de la respuesta con el MediaType APPLICATION_JSON_UTF8(El cuerpo de la respuesta va en formato Json y con la codificación de caracters UTF-8).El valor por defecto del ContenType de una respuesta es un MediaType con el valor APPLICATION_JSON
				// NOTA: "MediaType.APPLICATION_JSON_UTF8" está actualmente "deprecated".Ahora también se usa el valor "MediaType.APPLICATION_JSON" para tratar MediaTypes de tipo JSON + UTF_8	
				.contentType(MediaType.APPLICATION_JSON)
				// El método "bodyValue()" es una alternativa más sencilla a usar el método "body()"
				.bodyValue(p)));  // Establecemos como cuerpo de la respuesta el producto que acabamos de editar usando nuestro cliente web
	}
	
	// Método handler que elimina un producto,dado su id, a partir de nuestro cliente web. El id del producto viaja en la url o path y lo obtenemos a través de la petición http "request"
	// Este método se va a mapear con una ruta en el método "rutas()" de nuestra clase de configuración de Spring "RouterConfig"
	// A diferencia de un controlador Api Rest anotado con @RestControler, donde sus métodos handler devuelven flujos reactivos Mono de tipo "ResponseEntity",en este tipo de controlador,implementado con la técnica "Functional Endpoints",sus métodos handler van a devolver flujos reactivos Mono de tipo "ServerResponse"
	// Otra diferencia con respecto a un controlador Api Rest anotado con @RestController,para la obtención de los datos que viajan en el cuerpo de la petición y de la url o path,se utiliza la instancia de tipo "ServerRequest", que se le pasa como parámetro de entrada a este método.Sin embargo,En el controlador anotado con @RestController se usa para ello anotaciones como @RequestBody,@PathVariable y @RequestParam
	public Mono<ServerResponse> eliminar(ServerRequest request){
		//Obtenemos de la url  o path de la petición http el id del producto
		String id = request.pathVariable("id");
		// Como el método "delete()" del bean "productoService" devuelve un flujo reactivo Mono con un elemento de tipo Void,no podemos usar un operador "map" para obtener un flujo reactivo Mono con un elemento ServerResponse porque, al ser el elemento de tipo Void,no va a emitirse para que se ejecute dicho operador "map"
		// Por esta razón,tenemos que crear el flujo reactivo Mono con el elemento ServerResponse usando el método "then()"(Es "then()" porque lo estamos aplicando sobre un flujo reactivo Mono.En cambio,se usa "thenMany()" si se aplica sobre un flujo reactivo Flux)
		// Con nuestro método privado "errorHandler()" añadimos el manejo de excepciones con estados de error NOT_FOUND(404) a lo que le pasamos como parámetro de entrada
		return errorHandler(productoService.delete(id) // Eliminarmos el producto usando nuestro cliente web mediante el bean 'productoService'
				// Creamos un flujo reactivo Mono con un elemento ServerResponse de tipo Void con el estado de la respuesta establecido en NO_CONTENT(204)
				// El método "then" espera a que el flujo reactivo original termine de emitir su único elemento para comenzar a procesar un nuevo flujo reactivo Mono, es decir, comienza a procesar un nuevo flujo reactivo Mono cuando se produce el evento OnComplete del flujo reactivo original
    			// Al final, el único elemento del flujo reactivo original es ignorado y se tiene en cuenta el elemento del nuevo flujo reactivo Mono
				// "thenMany" se usa para crear flujos reactivos Flux y "then" para crear flujos reactivos Mono cuando el flujo reactivo original ha finalizado de emitir su último elemento
				.then(ServerResponse.noContent().build()));
	}
	
	// Método handler que actualiza un producto,dado su id y dado un archivo imagen,mediante nuestro cliente web asociándole dicha imagen.Tanto el id del producto como el archivo imagen son obtenidos de la petición http "request"
	// Este método se va a mapear con una ruta en el método "rutas()" de nuestra clase de configuración de Spring "RouterConfig"
	// A diferencia de un controlador Api Rest anotado con @RestControler, donde sus métodos handler devuelven flujos reactivos Mono de tipo "ResponseEntity",en este tipo de controlador,implementado con la técnica "Functional Endpoints",sus métodos handler van a devolver flujos reactivos Mono de tipo "ServerResponse"
	// Otra diferencia con respecto a un controlador Api Rest anotado con @RestController,para la obtención de los datos que viajan en el cuerpo de la petición y de la url o path,se utiliza la instancia de tipo "ServerRequest", que se le pasa como parámetro de entrada a este método.Sin embargo,En el controlador anotado con @RestController se usa para ello anotaciones como @RequestBody,@PathVariable y @RequestParam
	public Mono<ServerResponse> upload(ServerRequest request){
		//Obtenemos de la url o path de la petición http el id del producto
		String id = request.pathVariable("id");
		// El método "multipartData()" devuelve un flujo reactivo Mono que tiene como elemento un objeto de tipo "MultiValueMap<String, Part>" que se corresponde con el fomulario FormData que viaja en la petición http Post
		// Con nuestro método privado "errorHandler()" añadimos el manejo de excepciones con estados de error NOT_FOUND(404) a lo que le pasamos como parámetro de entrada
		return errorHandler(request.multipartData()
				// Obtenemos del cuerpo de la petición http la imagen que viaja en un campo de un formulario FormData
				// La imagen viaja en el campo "file" de dicho formulario FormData y, para obtenerla, tenemos que transformar este flujo reactivo Mono de tipo "MultiValueMap<String, Part>" en otro flujo reactivo Mono de tipo "Part" con el contenido del campo "file" de dicho formulario
				.map(multipart -> multipart.toSingleValueMap().get("file"))
				// Como el elemento del flujo reactivo Mono anterior es de tipo "Part", que es muy genérico, tenemos que hacer un casting para convertirlo al tipo más concreto que nos interesa, es decir, en este caso al tipo "FilePart" porque estamas tratando con archivos de imagen
				.cast(FilePart.class)
				// Con el operador flatMap,transformamos el flujo reactivo Mono anterior con el objeto "FilePart" de la imagen como elemento en otro flujo reactivo Mono cuyo elemento va a ser a su vez otro flujo reactivo Mono con los datos del producto editado con la imagen mediante nuestro cliente web.Como al final tenemos un flujo reactivo Mono con otro flujo reactivo Mono a su vez,con este operador,en lugar de "map",se va a realizar un procedimiento de aplanamiento para que definitivamente nos quede un único flujo reactivo Mono de un elemento no reactivo
				.flatMap(file -> productoService.upload(file,id))
				// Como necesitamos devolver un flujo reactivo Mono de un ServerResponse para manejar y personalizar la respuesta,con el operador "flatMap" transformamos el flujo reactivo Mono anterior en otro flujo reactivo Mono que tiene a su vez otro flujo reactivo Mono con dicho elemento ServerResponse.Como al final tenemos un flujo reactivo Mono con otro flujo reactivo Mono a su vez,con este operador,en lugar de "map",se va a realizar un procedimiento de aplanamiento para que definitivamente nos quede un único flujo reactivo Mono de un elemento no reactivo
				.flatMap(p -> ServerResponse.created(URI.create("/api/v2/productos/".concat(p.getId()))) // Establecemos el estado de la respuesta en CREATED(200) y le pasamos la localización del producto que se acaba de actualizar con la imagen usando la clase URI junto con la ruta o path de dicho producto.Esta localización va en la cabecera de la respuesta
						// Establecemos el ContentType de la respuesta con el MediaType APPLICATION_JSON_UTF8(El cuerpo de la respuesta va en formato Json y con la codificación de caracters UTF-8).El valor por defecto del ContenType de una respuesta es un MediaType con el valor APPLICATION_JSON
						// NOTA: "MediaType.APPLICATION_JSON_UTF8" está actualmente "deprecated".Ahora también se usa el valor "MediaType.APPLICATION_JSON" para tratar MediaTypes de tipo JSON + UTF_8	
						.contentType(MediaType.APPLICATION_JSON)
						// El método "bodyValue()" es una alternativa más sencilla a usar el método "body()"
						.bodyValue(p))); // Establecemos como cuerpo de la respuesta el producto que acabamos de editar con la imagen usando nuestro cliente web
	}
	
	// Método para reutilizar el código de manejo de excepciones con estado de error NOT_FOUND(404) por parte de nuestro cliente web
	// Este método lo vamos a usar en los método handler "upload","eliminar","editar" y "ver" porque son los que hacen,a través de las peticiones http hechas por nuestro cliente web,que nuestra Api Rest de productos pueda generar la excepción con estado de error NOT_FOUND(404) en el momento de localizar un producto determinado en la base de datos
	private Mono<ServerResponse> errorHandler(Mono<ServerResponse> response){
		// Con el operador "onErrorResume" capturamos y manejamos las excepciones producidas por nuestro cliente web en el momento de realizar la petición http a nuestra Api Rest de productos
		// Este operador recibe un parámetro de tipo "Throwable",que es una excepción muy genérica,y,por lo tanto,tenemos que hacer un cast o conversión a un tipo de excepción más concreta.En este caso,hacemos el cast al tipo de excepción "WebClientResponseException" que es la encargada de darnos la lista de errores por parte de nuestro cliente web
		return response.onErrorResume(error -> {
			// Convertimos la excepción genérica recibida en "error" en una excepción de tipo "WebClientResponseException"
			WebClientResponseException errorResponse = (WebClientResponseException)error;
			// Si la excepción contiene un estado de error de tipo NOT_FOUND(404)...(Significa que nuestra Api Rest de productos ha dado un error en el momento de localizar el producto en la base de datos)
			if(errorResponse.getStatusCode() == HttpStatus.NOT_FOUND) {
				// Creamos un Map para devolver varios mensajes de error y otro tipo de información como mensajes,fechas,estados,etc...
				Map<String,Object> body = new HashMap<String,Object>();
				// Insertamos en el Map el texto "No existe el producto - " + el mensaje de error obtenido de la excepción,la fecha actual del sistema y el estado de error obtenido también de la excepción
				body.put("error","No existe el producto - " + errorResponse.getMessage());
				body.put("timestamp",new Date());
				body.put("status",errorResponse.getStatusCode().value());
				// Creamos y devolvemos un flujo reactivo Mono con un elemento ServerResponse
				return ServerResponse.status(HttpStatus.NOT_FOUND) // Establecemos el estado de la respuesta NOT_FOUND(404)
						// El método "bodyValue()" es una alternativa más sencilla a usar el método "body()"
						.bodyValue(body);  // Establecemos el cuerpo de la respuesta con el Map creado anteriormente
			}
			// Si la excepción producida por nuestro cliente web contiene otro estado de error distinto a NOT_FOUND(404)...
			return Mono.error(errorResponse); // Creamos y devolvemos un flujo reactivo Mono con un elemento ServerResponse con la información de dicha excepción
					
		});
	}
	
}
