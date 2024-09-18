package com.bolsadeideas.springboot.webflux.client.app.models.services;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.bolsadeideas.springboot.webflux.client.app.models.Producto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service // Indicamos que esta clase se trata de una clase servicio de Spring.De esta manera,Spring va a almacenar un bean de esta clase en su memmoria o contenedor para poderlo inyectar en otra parte del proyecto
public class ProductoServiceImpl implements ProductoService{
	
	// Recuperamos de la memoria o contendor de Spring el bean que implementa la interfaz "WebClient.Builder".Esta interfaz es propia de Spring y,por lo tanto,también es implementada por Spring.
	// En vez de usar directamente una instancia de tipo WebClient,vamos a usar una instancia de tipo WebCliente.Builder ya que soporta balanceo de carga
	@Autowired 
	private WebClient.Builder client; // Este bean se trata de nuestro cliente web que está configurado para que se comunique con nuestra Api Rest sobre productos

	// Método que devuelve un flujo reactivo Flux con todos los productos como elementos obtenidos desde nuestro cliente web 
	@Override
	public Flux<Producto> findAll() {
		// El cliente realiza una petición http de tipo Get a la ruta o path base de su configuración
		// Antes,Con el cliente de tipo WebClient era "client.get()",pero ahora con el cliente de tipo WebClient.Builder es como se indica a continuación,es decir,añadiendo el método "build()" entre medias
		return client.build().get()
				// Indicamos que se consuman aquellas respuetas que contengan en la cabecera el MediaType "APPLICATION_JSON_UTF8"(El cuerpo de la respuesta va en formato Json y con la codificación de caracters UTF-8).Todos los métodos handler de nuestra Api Rest generarn respuestas con este MediaType en la cabecera
				// NOTA: "MediaType.APPLICATION_JSON_UTF8" está actualmente "deprecated".Ahora también se usa el valor "MediaType.APPLICATION_JSON" para tratar MediaTypes de tipo JSON + UTF_8
				.accept(MediaType.APPLICATION_JSON)
				// El método "exchange()" envía nuestro request al endpoint anterior y consume nuestra Api Rest obteniéndose una respuesta para dicho request o petición
				.exchange()
				// El método anterior nos devuelve un flujo reactivo Mono de tipo "ClientResponse" con la respuesta de nuestra petición http
				// Como necesitamos devolver un flujo reactivo Flux de tipo Producto con los productos obtenidos de la respuesta,con el operador "flatMapMany"(es "flatMapMany" y no "flatMap" porque en la transformación vamos a devolver un flujo reactivo Flux con los productos y no Mono) transformamos el flujo reactivo Mono anterior en otro flujo reactivo Flux que tiene como elemento otro flujo reactivo Flux con los productos como elementos.Como al final tenemos un flujo reactivo Flux con otro flujo reactivo Flux a su vez,con este operador,en lugar de "map",se va a realizar un procedimiento de aplanamiento para que definitivamente nos quede un único flujo reactivo Flux de elementos no reactivos
				.flatMapMany(response -> response.bodyToFlux(Producto.class)); // El método "bodyToFlux()" crea un flujo reactivo Flux con los productos obtenidos de la respuesta como elementos
	}

	// Método que devuelve un flujo reactivo Mono con el producto como elemento obtenido desde nuestro cliente web.Dicho producto se obtiene a partir del id que se le pasa como parámetro de entrada al método
	// El id tiene que ser de tipo String ya que el cliente web se comunica con nuestra Api Rest que accede a una base de datos MondgoDB y sus id's son alfanuméricos
	@Override
	public Mono<Producto> findById(String id) {
		// Creamos un Map para poder añadir el id que recibimos como parámetro de entrada en este método a la ruta o path de la petición http de tipo Get a nuestra Api Rest que vamos a realizar a continuación
		Map<String,Object> params = new HashMap<String,Object>();
		// Añadimos al map un atributo "id" con el id obtenido del parámetro de entrada de este método
		params.put("id",id);
		// El cliente realiza una petición http de tipo Get a la ruta o path base de su configuración junto con el id del producto que hemos añadido previamente al Map "params"
		// Antes,Con el cliente de tipo WebClient era "client.get().uri("/{id}",params)",pero ahora con el cliente de tipo WebClient.Builder es como se indica a continuación,es decir,añadiendo el método "build()" entre medias
		return client.build().get().uri("/{id}",params)
				// Indicamos que se consuman aquellas respuetas que contengan en la cabecera el MediaType "APPLICATION_JSON_UTF8"(El cuerpo de la respuesta va en formato Json y con la codificación de caracters UTF-8).Todos los métodos handler de nuestra Api Rest generarn respuestas con este MediaType en la cabecera
				// NOTA: "MediaType.APPLICATION_JSON_UTF8" está actualmente "deprecated".Ahora también se usa el valor "MediaType.APPLICATION_JSON" para tratar MediaTypes de tipo JSON + UTF_8
				.accept(MediaType.APPLICATION_JSON)
				/* Primera manera usando el método "exchange()" en vez de "retrieve()" */
				/*
				// El método "exchange()" envía nuestro request al endpoint anterior y consume nuestra Api Rest obteniéndose una respuesta para dicho request o petición
				.exchange()
				// El método anterior nos devuelve un flujo reactivo Mono de tipo "ClientResponse" con la respuesta de nuestra petición http
				// Como necesitamos devolver un flujo reactivo Mono de tipo Producto con el producto obtenido de la respuesta,con el operador "flatMap"(en este caso es "flatMap" y no "flatMapMany" porque en la transformación vamos a devolver un flujo reactivo Mono con el producto y no Flux) transformamos el flujo reactivo Mono anterior en otro flujo reactivo Mono que tiene como elemento otro flujo reactivo Mono con el producto como elemento.Como al final tenemos un flujo reactivo Mono con otro flujo reactivo Mono a su vez,con este operador,en lugar de "map",se va a realizar un procedimiento de aplanamiento para que definitivamente nos quede un único flujo reactivo Mono de un elemento no reactivo
				.flatMap(response -> response.bodyToMono(Producto.class)); // El método "bodyToMono()" crea un flujo reactivo Mono con el producto obtenido de la respuesta como elemento
				*/
				/* Segunda manera(más sencilla que usando el método "exchange()") usando el método "retrieve()" en lugar de "exchange()" */
				// El método "retrieve()" envía nuestro request al endpoint anterior y consume nuestra Api Rest obteniéndose una respuesta para dicho request o petición
				.retrieve()
				// El método anterior nos devuelve un objeto de tipo "ResponseSpec" con la respuesta de nuestra petición http
				// Como necesitamos devolver un flujo reactivo Mono de tipo Producto,con el método "bodyToMono()" creamos un flujo reactivo Mono con el producto obtenido de la respuesta como elemento
				.bodyToMono(Producto.class);
	}

	// Método que persite un producto en nuestra Api Rest mediante nuestro cliente web y devuelve un flujo reactivo Mono con el producto persistido como elemento
	@Override
	public Mono<Producto> save(Producto producto) {
		// El cliente realiza una petición http de tipo Post a la ruta o path base de su configuración
		// Antes,Con el cliente de tipo WebClient era "client.post()",pero ahora con el cliente de tipo WebClient.Builder es como se indica a continuación,es decir,añadiendo el método "build()" entre medias
		return client.build().post()
				// Indicamos que se consuman aquellas respuetas que contengan en la cabecera el MediaType "APPLICATION_JSON_UTF8"(El cuerpo de la respuesta va en formato Json y con la codificación de caracters UTF-8).Todos los métodos handler de nuestra Api Rest generarn respuestas con este MediaType en la cabecera
				// NOTA: "MediaType.APPLICATION_JSON_UTF8" está actualmente "deprecated".Ahora también se usa el valor "MediaType.APPLICATION_JSON" para tratar MediaTypes de tipo JSON + UTF_8
				.accept(MediaType.APPLICATION_JSON)
				// Indicamos que los datos que vamos a enviar en la petición http o request van con el MediaType "APPLICATION_JSON_UTF8"(El cuerpo de la petición va en formato Json y con la codificación de caracters UTF-8)
				// NOTA: "MediaType.APPLICATION_JSON_UTF8" está actualmente "deprecated".Ahora también se usa el valor "MediaType.APPLICATION_JSON" para tratar MediaTypes de tipo JSON + UTF_8
				.contentType(MediaType.APPLICATION_JSON)
				// Establecemos en el cuerpo de la petición un "BodyInserters" con el producto que se le pasa como parámetro de entrada a este método.El método "body()" puede recibir un flujo reactivo Mono o Flux,o un dato de tipo BodyInserters,este último para cuando tenemos datos que no son flujos reactivos
				// Hay dos alternativas;lo podemos hacer usando el método "body()" o el método "bodyValue()"
				//.body(BodyInserters.fromObject(producto))
				// Como el método "fromObject()" de "BodyInserters" está actualmente "deprecated", usamos, como alternativa, el método "bodyValue()" que recibe un elemento no reactivo
				.bodyValue(producto)
				// El método "retrieve()" envía nuestro request al endpoint anterior y consume nuestra Api Rest obteniéndose una respuesta para dicho request o petición
				.retrieve()
				// El método anterior nos devuelve un objeto de tipo "ResponseSpec" con la respuesta de nuestra petición http
				// Como necesitamos devolver un flujo reactivo Mono de tipo Producto,con el método "bodyToMono()" creamos un flujo reactivo Mono con el producto obtenido de la respuesta como elemento
				.bodyToMono(Producto.class);
	}

	// Método que actualiza los datos de un producto a partir de su id en nuestra Api Rest mediante nuestro cliente web y devuelve un flujo reactivo Mono con el producto actualizado como elemento
	// El id tiene que ser de tipo String ya que el cliente web se comunica con nuestra Api Rest que accede a una base de datos MondgoDB y sus id's son alfanuméricos
	@Override
	public Mono<Producto> update(Producto producto, String id) {
		// El cliente realiza una petición http de tipo Put a la ruta o path base de su configuración junto con el id del producto que se lo añadimos usando la sentencia "Collections.singletonMap("id",id)"(alternativa más simple a usar un Map como en el caso del método "findById()")
		// Antes,Con el cliente de tipo WebClient era "client.put().uri("/{id}",Collections.singletonMap("id",id))",pero ahora con el cliente de tipo WebClient.Builder es como se indica a continuación,es decir,añadiendo el método "build()" entre medias
		return client.build().put().uri("/{id}",Collections.singletonMap("id",id))
				// Indicamos que se consuman aquellas respuetas que contengan en la cabecera el MediaType "APPLICATION_JSON_UTF8"(El cuerpo de la respuesta va en formato Json y con la codificación de caracters UTF-8).Todos los métodos handler de nuestra Api Rest generarn respuestas con este MediaType en la cabecera
				// NOTA: "MediaType.APPLICATION_JSON_UTF8" está actualmente "deprecated".Ahora también se usa el valor "MediaType.APPLICATION_JSON" para tratar MediaTypes de tipo JSON + UTF_8
				.accept(MediaType.APPLICATION_JSON)
				// Indicamos que los datos que vamos a enviar en la petición http o request van con el MediaType "APPLICATION_JSON_UTF8"(El cuerpo de la petición va en formato Json y con la codificación de caracters UTF-8)
				// NOTA: "MediaType.APPLICATION_JSON_UTF8" está actualmente "deprecated".Ahora también se usa el valor "MediaType.APPLICATION_JSON" para tratar MediaTypes de tipo JSON + UTF_8
				.contentType(MediaType.APPLICATION_JSON)
				// Establecemos en el cuerpo de la petición un "BodyInserters" con el producto que se le pasa como parámetro de entrada a este método.El método "body()" puede recibir un flujo reactivo Mono o Flux,o un dato de tipo BodyInserters,este último para cuando tenemos datos que no son flujos reactivos
				// Hay dos alternativas;lo podemos hacer usando el método "body()" o el método "bodyValue()"
				//.body(BodyInserters.fromObject(producto))
				// Como el método "fromObject()" de "BodyInserters" está actualmente "deprecated", usamos, como alternativa, el método "bodyValue()" que recibe un elemento no reactivo
				.bodyValue(producto)
				// El método "retrieve()" envía nuestro request al endpoint anterior y consume nuestra Api Rest obteniéndose una respuesta para dicho request o petición
				.retrieve()
				// El método anterior nos devuelve un objeto de tipo "ResponseSpec" con la respuesta de nuestra petición http
				// Como necesitamos devolver un flujo reactivo Mono de tipo Producto,con el método "bodyToMono()" creamos un flujo reactivo Mono con el producto obtenido de la respuesta como elemento
				.bodyToMono(Producto.class);
	}

	// Método que elimina de nuestra Api Rest el producto dado su id mediante nuestro cliente web  y devuelve un flujo reactivo Mono de tipo Void como elemento
	// El id tiene que ser de tipo String ya que el cliente web se comunica con nuestra Api Rest que accede a una base de datos MondgoDB y sus id's son alfanuméricos
	@Override
	public Mono<Void> delete(String id) {
		// El cliente realiza una petición http de tipo Delete a la ruta o path base de su configuración junto con el id del producto que se lo añadimos usando la sentencia "Collections.singletonMap("id",id)"(alternativa más simple a usar un Map como en el caso del método "findById()")
		// Antes,Con el cliente de tipo WebClient era "client.delete().uri("/{id}",Collections.singletonMap("id",id))",pero ahora con el cliente de tipo WebClient.Builder es como se indica a continuación,es decir,añadiendo el método "build()" entre medias
		return client.build().delete().uri("/{id}",Collections.singletonMap("id",id))
				// El método "retrieve()" envía nuestro request al endpoint anterior y consume nuestra Api Rest obteniéndose una respuesta para dicho request o petición
				.retrieve()
				// El método anterior nos devuelve un objeto de tipo "ResponseSpec" con la respuesta de nuestra petición http
				// Como necesitamos devolver un flujo reactivo Mono de tipo Void,con el método "bodyToMono()" creamos un flujo reactivo Mono con un elemento vacío
				.bodyToMono(Void.class);
	}

	// Método que actualiza un producto a partir de su id en nuestra Api Rest asociándole una imagen mediante nuestro cliente web y devuelve un flujo reactivo Mono con el producto actualizado con dicha imagen como elemento
	// El argumento de entrada "file" de tipo "FilePart" se corresponde con el archivo imagen que se va a relacionar con el producto
	// El id tiene que ser de tipo String ya que el cliente web se comunica con nuestra Api Rest que accede a una base de datos MondgoDB y sus id's son alfanuméricos
	@Override
	public Mono<Producto> upload(FilePart file, String id) {
		// Creamos una instancia de tipo "MultipartBodyBuilder" que representa el cuerpo de la petición a nuestra Api Rest como un formulario FormData
		// Es de tipo "MultipartBodyBuilder" porque nuestra Api Rest esperan que le lleguen los datos dentro de un formulario FormData y no de otra manera
		MultipartBodyBuilder parts = new MultipartBodyBuilder();
		// Establecemos el archivo imagen "file", recibido como argumento de entrada en este método, dentro del cuerpo de la petición a nuestra Api Rest mediante la instancia "parts"
		// Como el contenido de este "FilePart" es un flujo reactivo de tipo Flux, que son asíncronos, tenemos que añadirlo a nuestra instancia "parts" como una parte asíncrona usando el método "asyncPart()"
		// Para ello, le pasamos a este método el nombre del campo del formulario FormData donde va a viajar el archivo imagen, que va a ser el nombre "file" porque así lo espera nuestra Api Rest, el contenido asíncrono con el archivo imagen, que nos lo da el método "content()" del argumento de entrada "file", y el tipo da dato de dicho contenido, que es de tipo "DataBuffer" porque así es el tipo del elemento del flujo reactivo devuelto por el método "content()"
		parts.asyncPart("file",file.content(),DataBuffer.class)
			// Además, tenemos que establecer en la cabecera de la petición a nuestra Api Rest la propiedad "Content-Disposition" con el nombre del arhcivo imagen porque el tipo de la petición es un FormData
			.headers(h -> h.setContentDispositionFormData("file",file.filename()));
		// El cliente realiza una petición http de tipo Post a la ruta o path base de su configuración junto con el texto "/upload/" y el id del producto que se lo añadimos usando la sentencia "Collections.singletonMap("id",id)"(alternativa más simple a usar un Map como en el caso del método "findById()")
		// Antes,Con el cliente de tipo WebClient era "client.post().uri("/upload/{id}",Collections.singletonMap("id",id))",pero ahora con el cliente de tipo WebClient.Builder es como se indica a continuación,es decir,añadiendo el método "build()" entre medias
		return client.build().post().uri("/upload/{id}",Collections.singletonMap("id",id))
				// Indicamos que los datos que vamos a enviar en la petición http o request van con el MediaType "MULTIPART_FORM_DATA"(El cuerpo de la petición va en un formulario FormData)
				.contentType(MediaType.MULTIPART_FORM_DATA)
				// Establecemos en el cuerpo de la petición un "BodyInserters" con el producto que se le pasa como parámetro de entrada a este método.El método "body()" puede recibir un flujo reactivo Mono o Flux,o un dato de tipo BodyInserters,este último para cuando tenemos datos que no son flujos reactivos
				// Hay dos alternativas;lo podemos hacer usando el método "body()" o el método "bodyValue()"
				//.body(BodyInserters.fromObject(producto))
				// Como el método "fromObject()" de "BodyInserters" está actualmente "deprecated", usamos, como alternativa, el método "bodyValue()" que recibe un elemento no reactivo
				.bodyValue(parts.build())
				// El método "retrieve()" envía nuestro request al endpoint anterior y consume nuestra Api Rest obteniéndose una respuesta para dicho request o petición
				.retrieve()
				// El método anterior nos devuelve un objeto de tipo "ResponseSpec" con la respuesta de nuestra petición http
				// Como necesitamos devolver un flujo reactivo Mono de tipo Producto,con el método "bodyToMono()" creamos un flujo reactivo Mono con el producto obtenido de la respuesta como elemento
				.bodyToMono(Producto.class);
	}

}
