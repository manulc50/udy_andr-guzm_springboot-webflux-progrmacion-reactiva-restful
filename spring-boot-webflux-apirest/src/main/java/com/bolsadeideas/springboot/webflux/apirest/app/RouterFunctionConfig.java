package com.bolsadeideas.springboot.webflux.apirest.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.bolsadeideas.springboot.webflux.apirest.app.handler.ProductoHandler;

//Esta clase de configuración de Spring es otra alternativa para implementar una Api Rest con programación reactiva, mediante la técnica "Functional Endpoints", en lugar de usar la típica clase anotada con la anotación @RestController

@Configuration // Indicamos que esta clase es una clase de Configuración de Spring y,de esta manera,Spring va a almacenar un bean de esta clase en su contenedor o memoria
public class RouterFunctionConfig {
	
	// Este método nos permite configurar los mapeos de rutas para peticiones http a partir del método "route()" de la clase "RouterFunction" de Spring
	// El argumento que se pasa como parámetro a este método va a ser inyectado por Spring,ya que nuestra clase "ProductoHandler" fue anotada como un componente de Spring mediante la anotación @Component
	// Opcionalmente,podemos poner la anotación @Autowired para que se realice esta inyección,pero no es necesaria porque la realiza automáticamente Spring
	@Autowired
	@Bean // Con esta anotación almacenamos como un bean la salida o respuesta de este método en la memoria o contenedor de Spring para que sea gestionado por Spring
	public RouterFunction<ServerResponse> routes(ProductoHandler handler){
		// Invocamos al método "route()" de la clase "RouterFunctions" de Spring para configurar nuestros mapeos de rutas(los que hay a continuación) y devolvemos el resultado
		// Mapeamos las rutas o paths "/api/v2/productos" y "/api/v3/productos" para peticiones http de tipo Get con la ejecución de la función lambda que hay a continuación que se encarga de invocar al método "listar()" del bean "handler" para obtener un flujo reactivo Mono de tipo ServerResponse cuya respuesta contiene los datos de todos los productos de la base de datos.Al método "listar()" se le pasa como argumento el parámetro "request" que contiene toda la información de la petición http
		return RouterFunctions.route(RequestPredicates.GET("/api/v2/productos").or(RequestPredicates.GET("/api/v3/productos")),request -> handler.listar(request)) // Esta función lambda "request -> handler.listar(request)" se puede simplificar aún más por esta "handler::listar" ya que los parámetros de la función lambda solo es uno llamado "request" y coincide con el que se le pasa al método "listar() del bean "handler"
				// Mapeamos la ruta o path "/api/v2/productos/{id}" para peticiones http de tipo Get con la ejecución de la función lambda que hay a continuación que se encarga de invocar al método "ver()" del bean "handler" para obtener un flujo reactivo Mono de tipo ServerResponse cuya respuesta contiene los datos del producto localizado por su id de la base de datos.Al método "ver()" se le pasa como argumento el parámetro "request" que contiene toda la información de la petición http
				.andRoute(RequestPredicates.GET("/api/v2/productos/{id}"), request -> handler.ver(request)) // Esta función lambda "request -> handler.ver(request)" se puede simplificar aún más por esta "handler::ver" ya que los parámetros de la función lambda solo es uno llamado "request" y coincide con el que se le pasa al método "ver() del bean "handler"
				// Mapeamos la ruta o path "/api/v2/producto" para peticiones http de tipo Post con la ejecución de la función lambda que hay a continuación que se encarga de invocar al método "crear()" del bean "handler" para obtener un flujo reactivo Mono de tipo ServerResponse cuya respuesta contiene los datos del producto persistido en la base de datos.Al método "crear()" se le pasa como argumento el parámetro "request" que contiene toda la información de la petición http
				.andRoute(RequestPredicates.POST("/api/v2/productos"), handler::crear) // Esta función lambda "request -> handler.crear(request)" se puede simplificar aún más por esta "handler::crear" ya que los parámetros de la función lambda solo es uno llamado "request" y coincide con el que se le pasa al método "crear() del bean "handler"
				// Mapeamos la ruta o path "/api/v2/producto/v2" para peticiones http de tipo Post con la ejecución de la función lambda que hay a continuación que se encarga de invocar al método "crearConFoto()" del bean "handler" para obtener un flujo reactivo Mono de tipo ServerResponse cuya respuesta contiene los datos del producto persistido con su imagen en la base de datos.Al método "crearConFoto()" se le pasa como argumento el parámetro "request" que contiene toda la información de la petición http
				.andRoute(RequestPredicates.POST("/api/v2/productos/v2"), handler::crearConFoto) // Esta función lambda "request -> handler.crearConFoto(request)" se puede simplificar aún más por esta "handler::crearConFoto" ya que los parámetros de la función lambda solo es uno llamado "request" y coincide con el que se le pasa al método "crearConFoto() del bean "handler"
				// Mapeamos la ruta o path "/api/v2/producto/upload/{id}" para peticiones http de tipo Post con la ejecución de la función lambda que hay a continuación que se encarga de invocar al método "upload()" del bean "handler" para obtener un flujo reactivo Mono de tipo ServerResponse cuya respuesta contiene los datos del producto actualizado con una imagen en la base de datos.Al método "upload()" se le pasa como argumento el parámetro "request" que contiene toda la información de la petición http
				.andRoute(RequestPredicates.POST("/api/v2/productos/upload/{id}"), handler::upload) // Esta función lambda "request -> handler.upload(request)" se puede simplificar aún más por esta "handler::upload" ya que los parámetros de la función lambda solo es uno llamado "request" y coincide con el que se le pasa al método "upload() del bean "handler"
				// Mapeamos la ruta o path "/api/v2/productos/{id}" para peticiones http de tipo Put con la ejecución de la función lambda que hay a continuación que se encarga de invocar al método "editar()" del bean "handler" para obtener un flujo reactivo Mono de tipo ServerResponse cuya respuesta contiene los datos del producto actualizado en la base de datos.Al método "editar()" se le pasa como argumento el parámetro "request" que contiene toda la información de la petición http
				.andRoute(RequestPredicates.PUT("/api/v2/productos/{id}"), handler::editar) // Esta función lambda "request -> handler.editar(request)" se puede simplificar aún más por esta "handler::editar" ya que los parámetros de la función lambda solo es uno llamado "request" y coincide con el que se le pasa al método "editar() del bean "handler"
				// Mapeamos la ruta o path "/api/v2/productos/{id}" para peticiones http de tipo Delete con la ejecución de la función lambda que hay a continuación que se encarga de invocar al método "eliminar()" del bean "handler" para obtener un flujo reactivo Mono de tipo ServerResponse con una respuesta vacía.Al método "eliminar()" se le pasa como argumento el parámetro "request" que contiene toda la información de la petición http
				.andRoute(RequestPredicates.DELETE("/api/v2/productos/{id}"),  handler::eliminar); // Esta función lambda "request -> handler.eliminar(request)" se puede simplificar aún más por esta "handler::eliminar" ya que los parámetros de la función lambda solo es uno llamado "request" y coincide con el que se le pasa al método "eliminar() del bean "handler"
	}

}
