package com.bolsadeideas.springboot.webflux.client.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.bolsadeideas.springboot.webflux.client.app.handler.ProductoHandler;

//Esta clase de configuración de Spring es otra alternativa para implementar una Api Rest con programación reactiva, mediante la técnica "Functional Endpoints", en lugar de usar la típica clase anotada con la anotación @RestController

@Configuration // Indicamos que esta clase es una clase de Configuración de Spring y,de esta manera,Spring va a almacenar un bean de esta clase en su contenedor o memoria
public class RouterConfig {
	
	// Este método nos permite configurar los mapeos de rutas para peticiones http a partir del método "route()" de la clase "RouterFunction" de Spring
	// El argumento que se pasa como parámetro a este método va a ser inyectado por Spring,ya que nuestra clase "ProductoHandler" fue anotada como un componente de Spring mediante la anotación @Component
	// Opcionalmente,podemos poner la anotación @Autowired para que se realice esta inyección,pero no es necesaria porque la realiza automáticamente Spring
	@Autowired
	@Bean // Con esta anotación almacenamos como un bean la salida o respuesta de este método en la memoria o contenedor de Spring para que sea gestionado por Spring
	public RouterFunction<ServerResponse> rutas(ProductoHandler handler){
		// Invocamos al método "route()" de la clase "RouterFunctions" de Spring para configurar nuestros mapeos de rutas(los que hay a continuación) y devolvemos el resultado
		// Mapeamos la ruta o path "/api/client" para peticiones http de tipo Get con la ejecución de la función lambda que hay a continuación que se encarga de invocar al método "listar()" del bean "handler" para obtener un flujo reactivo Mono de tipo ServerResponse cuya respuesta contiene los datos de todos los productos obtenidos de nuestro cliente web.Al método "listar()" se le pasa como argumento el parámetro "request" que contiene toda la información de la petición http
		return RouterFunctions.route(RequestPredicates.GET("/api/client"),request -> handler.listar(request)) // Esta función lambda "request -> handler.listar(request)" se puede simplificar aún más por esta "handler::listar" ya que los parámetros de la función lambda solo es uno llamado "request" y coincide con el que se le pasa al método "listar() del bean "handler"
				// Mapeamos la ruta o path "/api/client/{id}" para peticiones http de tipo Get con la ejecución de la función lambda que hay a continuación que se encarga de invocar al método "ver()" del bean "handler" para obtener un flujo reactivo Mono de tipo ServerResponse cuya respuesta contiene los datos del producto obtenido por nuestro cliente web a partir de su id.Al método "ver()" se le pasa como argumento el parámetro "request" que contiene toda la información de la petición http
				.andRoute(RequestPredicates.GET("/api/client/{id}"), request -> handler.ver(request)) // Esta función lambda "request -> handler.ver(request)" se puede simplificar aún más por esta "handler::ver" ya que los parámetros de la función lambda solo es uno llamado "request" y coincide con el que se le pasa al método "ver() del bean "handler"
				// Mapeamos la ruta o path "/api/client" para peticiones http de tipo Post con la ejecución de la función lambda que hay a continuación que se encarga de invocar al método "crear()" del bean "handler" para obtener un flujo reactivo Mono de tipo ServerResponse cuya respuesta contiene los datos del producto persistido a partir de nuestro cliente web.Al método "crear()" se le pasa como argumento el parámetro "request" que contiene toda la información de la petición http
				.andRoute(RequestPredicates.POST("/api/client"), handler::crear) // Esta función lambda "request -> handler.crear(request)" se puede simplificar aún más por esta "handler::crear" ya que los parámetros de la función lambda solo es uno llamado "request" y coincide con el que se le pasa al método "crear() del bean "handler"
				// Mapeamos la ruta o path "/api/client/upload/{id}" para peticiones http de tipo Post con la ejecución de la función lambda que hay a continuación que se encarga de invocar al método "upload()" del bean "handler" para obtener un flujo reactivo Mono de tipo ServerResponse cuya respuesta contiene los datos del producto actualizado con una imagen a partir de nuestro cliente web.Al método "upload()" se le pasa como argumento el parámetro "request" que contiene toda la información de la petición http
				.andRoute(RequestPredicates.POST("/api/client/upload/{id}"), handler::upload) // Esta función lambda "request -> handler.upload(request)" se puede simplificar aún más por esta "handler::upload" ya que los parámetros de la función lambda solo es uno llamado "request" y coincide con el que se le pasa al método "upload() del bean "handler"
				// Mapeamos la ruta o path "/api/client/{id}" para peticiones http de tipo Put con la ejecución de la función lambda que hay a continuación que se encarga de invocar al método "editar()" del bean "handler" para obtener un flujo reactivo Mono de tipo ServerResponse cuya respuesta contiene los datos del producto actualizado a partir de nuestro cliente web.Al método "editar()" se le pasa como argumento el parámetro "request" que contiene toda la información de la petición http
				.andRoute(RequestPredicates.PUT("/api/client/{id}"), handler::editar) // Esta función lambda "request -> handler.editar(request)" se puede simplificar aún más por esta "handler::editar" ya que los parámetros de la función lambda solo es uno llamado "request" y coincide con el que se le pasa al método "editar() del bean "handler"
				// Mapeamos la ruta o path "/api/client/{id}" para peticiones http de tipo Delete con la ejecución de la función lambda que hay a continuación que se encarga de invocar al método "eliminar()" del bean "handler" para obtener un flujo reactivo Mono de tipo ServerResponse con una respuesta vacía.Al método "eliminar()" se le pasa como argumento el parámetro "request" que contiene toda la información de la petición http
				.andRoute(RequestPredicates.DELETE("/api/client/{id}"),  handler::eliminar); // Esta función lambda "request -> handler.eliminar(request)" se puede simplificar aún más por esta "handler::eliminar" ya que los parámetros de la función lambda solo es uno llamado "request" y coincide con el que se le pasa al método "eliminar() del bean "handler"	
	}

}
