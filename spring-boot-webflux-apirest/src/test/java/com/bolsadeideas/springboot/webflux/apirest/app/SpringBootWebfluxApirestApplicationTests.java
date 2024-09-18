package com.bolsadeideas.springboot.webflux.apirest.app;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.BodyContentSpec;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;

import com.bolsadeideas.springboot.webflux.apirest.app.models.documents.Categoria;
import com.bolsadeideas.springboot.webflux.apirest.app.models.documents.Producto;
import com.bolsadeideas.springboot.webflux.apirest.app.models.services.ProductoService;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;

// Esta anotación configura a Spring para que utilice Junit(En este caso,la versión 4 de Junit),es decir,permite a Spring trabajar con pruebas unitarias basadas en Junit
@RunWith(SpringRunner.class)
// Esta anotación hace que se aplique a esta clase la configuración principal implementada en la clase principal de cualquier aplicación SpringBoot.En nuestro caso,la clase se llama "SpringBootWebfluxApirestApplication"
// Con "webEnvironment" indicamos el modo de arranque del servidor para nuestras pruebas unitarias.Puede ser usando un servidor real desplegado en un puerto http o simulando un servidor
// @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // En este caso, estamos indicando que se utilice para las pruebas unitarias un servidor real desplegado en un puerto http aleatorio que no esté previamente en uso
@AutoConfigureWebTestClient // Esta anotación siempre tiene que estar cuando se utiliza la anotación de abajo,@SpringBootTest,con el valor "SpringBootTest.WebEnvironment.MOCK" establecido en el atributo "webEnvironment"
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK) // En este caso, estamos indicando que se utilice para las pruebas unitarias un servidor simulado(no real)
public class SpringBootWebfluxApirestApplicationTests {
	
	// Recuperamos de la memoria o contendor de Spring el bean que implementa la interfaz "WebTestClient".Esta interfaz es propia de Spring y,por lo tanto,también es implementada por Spring.
	@Autowired
	private WebTestClient client; // Se trata de un cliente que nos proporciona Spring para poder realizar pruebas unitarias, mediante peticiones http, en aplicaciones donde se utilizan flujos reactivos Flux o Mono

	// Recuperamos de la memoria o contendor de Spring el bean que implementa la interfaz "ProductoService".Esta interfaz es implementada por la clase "ProductoServiceImpl"
	@Autowired
	private ProductoService productoService; // Este bean representa la capa Servicio para la clase entidad "Producto" que realiza operaciones CRUD en la base de datos a través de la capa Dao
	
	// La anotación @Value nos permite inyectar el valor de cualquier propiedad definida en el archivo de propiedades "application.properties"
	// Inyectamos el valor de la propiedad "config.base.endpoint" definida en el archivo de propiedades de la aplicación "application.properties"
	@Value("${config.base.endpoint}")
	private String url; // Esta propiedad contiene la ruta o path base del controlador que se está ejecutando actualmente.Tenemos dos controladores Api Rest en esta aplicación; uno(controlador con anotación @RestController) mapeado con la ruta base "/api/productos" y el otro(controlador que usa la técnica "Functional Endpoints") mapeado con la ruta base "/api/v2/productos"
	
	// Prueba unitaria para probar la obtención de nuestro listado de productos desde nuestra Api Rest
	// Este listado se obtiene realizando una petición http de tipo Get a la ruta o path base indicada en 'url'
	@Test
	public void listarTest() {
		// El cliente realiza una petición http de tipo Get a la ruta o path "/api/v2/productos"
		client.get().uri(url)
		// Indicamos que se consuman aquellas respuetas que contengan en la cabecera el MediaType "APPLICATION_JSON_UTF8"(El cuerpo de la respuesta va en formato Json y con la codificación de caracters UTF-8).Todos nuestros métodos handler de nuestro controlador generarn respuestas con este MediaType en la cabecera
		.accept(MediaType.APPLICATION_JSON_UTF8)
		// El método "exchange()" envía nuestro request al endpoint anterior y consume nuestra Api Rest obteniéndose una respuesta para dicho request o petición
		.exchange()
		// Indicamos el estado de la respuesta que esperamos recibir de la petición http.En este caso,esperamos recibir el estado OK(200)
		.expectStatus().isOk()
		// Indicamos la cabecera de la respuesta que esperamos recibir.En este caso,esperamos recibir en la cabecera de la respuesta un ContentType con un MediaType de tipo "APPLICATION_JSON_UTF8"
		.expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
		// Indicamos el cuerpo de la respuesta que esperamos recibir de la petición.En este caso,esperamos recibir en el cuerpo de la respuesta una lista de productos
		.expectBodyList(Producto.class)
		// Especificamos que el tamaño de la lista anterior tiene que ser 9,es decir,en el cuerpo de la respuesta tiene que venir una lista de 9 productos.Lo establecemos a 9 porque en la clase principal de configuraciñon de SpringBoot,"SpringBootWebfluxApirestApplication",hemos insertado justamente 9 productos en la base de datos
		//.hasSize(9); // Lo comentamos porque ya lo estamos comprobando en el siguiente método "consumeWith()" de otra manera distinta
		// El método "consumeWith()",mediante una función lambda, nos permite trabajar con la respuesta obtenida de la petición para verificar lo que queramos de dicha respuesta
		.consumeWith(respuesta -> {
			// Obtenemos la lista de productos de la respuesta
			List<Producto> productos = respuesta.getResponseBody();
			// Iteramos cada producto de la lista para mostrar su nombre por pantalla
			productos.forEach(p -> System.out.println(p.getNombre()));
			// Verificamos que el tamaño de la lista de productos obtenida de la respuesta sea mayor que 0
			Assertions.assertThat(productos.size() > 0).isTrue();
		});
	}
	
	// Prueba unitaria para probar la obtención de un producto determinado a partir de su id desde nuestra Api Rest
	// Un producto determinado se obtiene realizando una petición http de tipo Get a la ruta o path base indicada en 'url' + "/{id}"
	// Primera manera tratando el cuerpo de la respuesta como un objeto Json
	@Test
	public void verTest() {
		// Recuperamos de la base de datos el producto a partir del nombre "TV Panasonic Pantalla LCD".Esto lo hacemos mediante el bean 'productoService'
		// En una prueba unitaria no es conveniente trabajar suscribiéndonos(mediante el método "subscribe()") a un flujo reactivo u Observable para obtener o manipular sus elementos ya que se tiene que trabajar de manera síncrona dentro del contexto o hilo de este método,y no dentro del contexto o hilo de la suscripción al Observable,que sería asíncrono.
		// Por esta razón,usamos el método "block()" que bloquea la ejecución del hilo de este flujo reactivo Mono para obtener su elemento de tipo Producto
		Producto producto = productoService.findByNombre("TV Panasonic Pantalla LCD").block();
		
		// El cliente realiza una petición http de tipo Get a la ruta o path "/api/v2/productos/{id}".La parte variable de esta url o path,"{id}", se corresponde con el id del producto localizado anteriormente y se la pasamos a esta ruta o path con la sentencia "Collections.singletonMap("id",producto.getId())"
		client.get().uri(url + "/{id}",Collections.singletonMap("id",producto.getId()))
		// Indicamos que se consuman aquellas respuetas que contengan en la cabecera el MediaType "APPLICATION_JSON_UTF8"(El cuerpo de la respuesta va en formato Json y con la codificación de caracters UTF-8).Todos nuestros métodos handler de nuestro controlador generarn respuestas con este MediaType en la cabecera
		.accept(MediaType.APPLICATION_JSON_UTF8)
		// El método "exchange()" envía nuestro request al endpoint anterior y consume nuestra Api Rest obteniéndose una respuesta para dicho request o petición
		.exchange()
		// Indicamos el estado de la respuesta que esperamos recibir de la petición http.En este caso,esperamos recibir el estado OK(200)
		.expectStatus().isOk()
		// Indicamos la cabecera de la respuesta que esperamos recibir.En este caso,esperamos recibir en la cabecera de la respuesta un ContentType con un MediaType de tipo "APPLICATION_JSON_UTF8"
		.expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
		// Indicamos que esperamos recibir una respuesta de la petición como un objeto Json
		.expectBody()
		// Esperamos,mediante una expresión Json,que el id del producto del cuerpo de la respuesta no sea vacío
		.jsonPath("$.id").isNotEmpty()
		// Esperamos,mediante una expresión Json,que el nombre del producto del cuerpo de la respuesta sea identico a "TV Panasonic Pantalla LCD",que es el nombre del producto que hemos solicitado a nuestra Api Rest
		.jsonPath("$.nombre").isEqualTo("TV Panasonic Pantalla LCD");
	}
	
	// Prueba unitaria para probar la obtención de un producto determinado a partir de su id desde nuestra Api Rest
	// Un producto determinado se obtiene realizando una petición http de tipo Get a la ruta o path base indicada en 'url' + "/{id}"
	// Segunda manera tratando el cuerpo de la respuesta como un objeto de tipo Producto
	@Test
	public void ver2Test() {
		// Recuperamos de la base de datos el producto a partir del nombre "TV Panasonic Pantalla LCD".Esto lo hacemos mediante el bean 'productoService'
		// En una prueba unitaria no es conveniente trabajar suscribiéndonos(mediante el método "subscribe()") a un flujo reactivo u Observable para obtener o manipular sus elementos ya que se tiene que trabajar de manera síncrona dentro del contexto o hilo de este método,y no dentro del contexto o hilo de la suscripción al Observable,que sería asíncrono.
		// Por esta razón,usamos el método "block()" que bloquea la ejecución del hilo de este flujo reactivo Mono para obtener su elemento de tipo Producto
		Producto producto = productoService.findByNombre("TV Panasonic Pantalla LCD").block();
		
		// El cliente realiza una petición http de tipo Get a la ruta o path "/api/v2/productos/{id}".La parte variable de esta url o path,"{id}", se corresponde con el id del producto localizado anteriormente y se la pasamos a esta ruta o path con la sentencia "Collections.singletonMap("id",producto.getId())"
		client.get().uri(url + "/{id}",Collections.singletonMap("id",producto.getId()))
		// Indicamos que se consuman aquellas respuetas que contengan en la cabecera el MediaType "APPLICATION_JSON_UTF8"(El cuerpo de la respuesta va en formato Json y con la codificación de caracters UTF-8).Todos nuestros métodos handler de nuestro controlador generarn respuestas con este MediaType en la cabecera
		.accept(MediaType.APPLICATION_JSON_UTF8)
		// El método "exchange()" envía nuestro request al endpoint anterior y consume nuestra Api Rest obteniéndose una respuesta para dicho request o petición
		.exchange()
		// Indicamos el estado de la respuesta que esperamos recibir de la petición http.En este caso,esperamos recibir el estado OK(200)
		.expectStatus().isOk()
		// Indicamos la cabecera de la respuesta que esperamos recibir.En este caso,esperamos recibir en la cabecera de la respuesta un ContentType con un MediaType de tipo "APPLICATION_JSON_UTF8"
		.expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
		// Indicamos el cuerpo de la respuesta que esperamos recibir de la petición.En este caso,esperamos recibir en el cuerpo de la respuesta un producto
		.expectBody(Producto.class)
		// El método "consumeWith()",mediante una función lambda, nos permite trabajar con la respuesta obtenida de la petición para verificar lo que queramos de dicha respuesta
		.consumeWith(respuesta -> {
			// Obtenemos el producto de la respuesta
			Producto p = respuesta.getResponseBody();
			// Verificamos que el id del productos obtenido de la respuesta no sea vacío
			Assertions.assertThat(p.getId()).isNotEmpty();
			// Verificamos que el nombre del producto obtenido de la respuesta es igual a "TV Panasonic Pantalla LCD",que es el nombre del producto que hemos solicitado a nuestra Api Rest
			Assertions.assertThat(p.getNombre()).isEqualTo("TV Panasonic Pantalla LCD");
		});
		
	}
	
	// Prueba unitaria para probar la creación de un nuevo producto desde nuestra Api Rest
	// La creación de un producto se realiza haciendo una petición http de tipo Post a la ruta o path base indicada en 'url'
	// Primera manera tratando el cuerpo de la respuesta como un objeto Json
	@Test
	public void crearTest() {
		// Recuperamos de la base de datos la categoría a partir del nombre "muebles".Esto lo hacemos mediante el bean 'productoService'
		// En una prueba unitaria no es conveniente trabajar suscribiéndonos(mediante el método "subscribe()") a un flujo reactivo u Observable para obtener o manipular sus elementos ya que se tiene que trabajar de manera síncrona dentro del contexto o hilo de este método,y no dentro del contexto o hilo de la suscripción al Observable,que sería asíncrono.
		// Por esta razón,usamos el método "block()" que bloquea la ejecución del hilo de este flujo reactivo Mono para obtener su elemento de tipo Categoria
		Categoria categoria = productoService.findCategoriaByNombre("muebles").block();
		
		// Creamos un nuevo producto a partir de la categoría anterior para enviarlo en el cuerpo de la petición http a nuestra Api Rest
		Producto producto = new Producto("Mesa comedor",100.00,categoria);
		
		// El cliente realiza una petición http de tipo Post a la ruta o path "/api/v2/productos"
		BodyContentSpec bodySpec = client.post().uri(url)
		// Indicamos que los datos que vamos a enviar en la petición http o request van con el MediaType "APPLICATION_JSON_UTF8"(El cuerpo de la petición va en formato Json y con la codificación de caracters UTF-8)
		.contentType(MediaType.APPLICATION_JSON_UTF8)
		// Indicamos que se consuman aquellas respuetas que contengan en la cabecera el MediaType "APPLICATION_JSON_UTF8"(El cuerpo de la respuesta va en formato Json y con la codificación de caracters UTF-8).Todos nuestros métodos handler de nuestro controlador generarn respuestas con este MediaType en la cabecera
		.accept(MediaType.APPLICATION_JSON_UTF8)
		// Establecemos en el cuerpo de la petición un flujo reactivo Mono con el producto creado anteriormente como elemento.El método "body()" puede recibir un flujo reactivo Mono o Flux,o un dato de tipo BodyInserters,este último para cuando tenemos datos que no son flujos reactivos
		.body(Mono.just(producto),Producto.class)
		// El método "exchange()" envía nuestro request al endpoint anterior y consume nuestra Api Rest obteniéndose una respuesta para dicho request o petición
		.exchange()
		// Indicamos el estado de la respuesta que esperamos recibir de la petición http.En este caso,esperamos recibir el estado CREATED(201)
		.expectStatus().isCreated()
		// Indicamos la cabecera de la respuesta que esperamos recibir.En este caso,esperamos recibir en la cabecera de la respuesta un ContentType con un MediaType de tipo "APPLICATION_JSON_UTF8"
		.expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
		// Indicamos que esperamos recibir una respuesta de la petición como un objeto Json
		.expectBody();
		
		// Cuando usamos nuestro controlador implementado con la técnica "Functional Endpoints"
		if(url.equals("/api/v2/productos")){
			// Esperamos,mediante una expresión Json,que el id del producto del cuerpo de la respuesta no sea vacío
			bodySpec.jsonPath("$.id").isNotEmpty()
			// Esperamos,mediante una expresión Json,que el nombre del producto del cuerpo de la respuesta sea identico a "Mesa comedor",que es el nombre del producto que hemos enviado a nuestra Api Rest
			.jsonPath("$.nombre").isEqualTo("Mesa comedor")
			// Esperamos,mediante una expresión Json,que el nombre de la categoría del producto del cuerpo de la respuesta sea identico a "muebles",que es el nombre de la categoría del producto que hemos enviado a nuestra Api Rest
			.jsonPath("$.categoria.nombre").isEqualTo("muebles");
		}
		// Cuando usamos nuestro controlador implementado con la anotación @RestController.En este caso,las propiedades de los productos(id,nombre,categoría) están encapsuladas en otra propiedad llamada "producto"
		else if(url.equals("/api/productos")){
			// Esperamos,mediante una expresión Json,que el id del producto del cuerpo de la respuesta no sea vacío
			bodySpec.jsonPath("$.producto.id").isNotEmpty()
			// Esperamos,mediante una expresión Json,que el nombre del producto del cuerpo de la respuesta sea identico a "Mesa comedor",que es el nombre del producto que hemos enviado a nuestra Api Rest
			.jsonPath("$.producto.nombre").isEqualTo("Mesa comedor")
			// Esperamos,mediante una expresión Json,que el nombre de la categoría del producto del cuerpo de la respuesta sea identico a "muebles",que es el nombre de la categoría del producto que hemos enviado a nuestra Api Rest
			.jsonPath("$.producto.categoria.nombre").isEqualTo("muebles");
		}
	}
	
	// Prueba unitaria para probar la creación de un nuevo producto desde nuestra Api Rest
	// La creación de un producto se realiza haciendo una petición http de tipo Post a la ruta o path base indicada en 'url'
	// Segunda manera tratando el cuerpo de la respuesta como un objeto de tipo Producto
	@Test
	public void crear2Test() {
		// Recuperamos de la base de datos la categoría a partir del nombre "muebles".Esto lo hacemos mediante el bean 'productoService'
		// En una prueba unitaria no es conveniente trabajar suscribiéndonos(mediante el método "subscribe()") a un flujo reactivo u Observable para obtener o manipular sus elementos ya que se tiene que trabajar de manera síncrona dentro del contexto o hilo de este método,y no dentro del contexto o hilo de la suscripción al Observable,que sería asíncrono.
		// Por esta razón,usamos el método "block()" que bloquea la ejecución del hilo de este flujo reactivo Mono para obtener su elemento de tipo Categoria
		Categoria categoria = productoService.findCategoriaByNombre("muebles").block();
		
		// Creamos un nuevo producto a partir de la categoría anterior para enviarlo en el cuerpo de la petición http a nuestra Api Rest
		Producto producto = new Producto("Mesa comedor",100.00,categoria);
		
		// El cliente realiza una petición http de tipo Post a la ruta o path "/api/v2/productos"
		ResponseSpec responseSpec = client.post().uri(url)
		// Indicamos que los datos que vamos a enviar en la petición http o request van con el MediaType "APPLICATION_JSON_UTF8"(El cuerpo de la petición va en formato Json y con la codificación de caracters UTF-8)
		.contentType(MediaType.APPLICATION_JSON_UTF8)
		// Indicamos que se consuman aquellas respuetas que contengan en la cabecera el MediaType "APPLICATION_JSON_UTF8"(El cuerpo de la respuesta va en formato Json y con la codificación de caracters UTF-8).Todos nuestros métodos handler de nuestro controlador generarn respuestas con este MediaType en la cabecera
		.accept(MediaType.APPLICATION_JSON_UTF8)
		// Establecemos en el cuerpo de la petición un flujo reactivo Mono con el producto creado anteriormente como elemento.El método "body()" puede recibir un flujo reactivo Mono o Flux,o un dato de tipo BodyInserters,este último para cuando tenemos datos que no son flujos reactivos
		.body(Mono.just(producto),Producto.class)
		// El método "exchange()" envía nuestro request al endpoint anterior y consume nuestra Api Rest obteniéndose una respuesta para dicho request o petición
		.exchange()
		// Indicamos el estado de la respuesta que esperamos recibir de la petición http.En este caso,esperamos recibir el estado CREATED(201)
		.expectStatus().isCreated()
		// Indicamos la cabecera de la respuesta que esperamos recibir.En este caso,esperamos recibir en la cabecera de la respuesta un ContentType con un MediaType de tipo "APPLICATION_JSON_UTF8"
		.expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8);
		
		// Cuando usamos nuestro controlador implementado con la técnica "Functional Endpoints"
		if(url.equals("/api/v2/productos")) {
			// Indicamos el cuerpo de la respuesta que esperamos recibir de la petición.En este caso,esperamos recibir en el cuerpo de la respuesta un producto
			responseSpec.expectBody(Producto.class)
			// El método "consumeWith()",mediante una función lambda, nos permite trabajar con la respuesta obtenida de la petición para verificar lo que queramos de dicha respuesta
			.consumeWith(respuesta -> {
				// Obtenemos el producto de la respuesta
				Producto p = respuesta.getResponseBody();
				// Verificamos que el id del productos obtenido de la respuesta no sea vacío
				Assertions.assertThat(p.getId()).isNotEmpty();
				// Verificamos que el nombre del producto obtenido de la respuesta es igual a "Mesa comedor",que es el nombre del producto que hemos enviado a nuestra Api Rest
				Assertions.assertThat(p.getNombre()).isEqualTo("Mesa comedor");
				// Verificamos que el nombre de la categoría del producto obtenido de la respuesta es igual a "muebles",que es el nombre de la categoría del producto que hemos enviado a nuestra Api Rest
				Assertions.assertThat(p.getCategoria().getNombre()).isEqualTo("muebles");
			});
		}
		// Cuando usamos nuestro controlador implementado con la anotación @RestController
		else if(url.equals("/api/productos")) {
			// Indicamos el cuerpo de la respuesta que esperamos recibir de la petición.En este caso,esperamos recibir en el cuerpo de la respuesta un Map de pares <String,Object> donde se encuentra,entre otras,la propiedad que nos interesa evaluar,que es "producto"
			responseSpec.expectBody(new ParameterizedTypeReference<LinkedHashMap<String,Object>>(){})
			// El método "consumeWith()",mediante una función lambda, nos permite trabajar con la respuesta obtenida de la petición para verificar lo que queramos de dicha respuesta
			.consumeWith(respuesta -> {
				// Obtenemos el objecto de la propiedad "producto" del Map de la respuesta
				Object o = respuesta.getResponseBody().get("producto");
				// Convertimos el objeto genérico anterior a un objeto de tipo Producto
				Producto p = new ObjectMapper().convertValue(o,Producto.class);
				// Verificamos que el id del producto obtenido de la respuesta no sea vacío
				Assertions.assertThat(p.getId()).isNotEmpty();
				// Verificamos que el nombre del producto obtenido de la respuesta es igual a "Mesa comedor",que es el nombre del producto que hemos enviado a nuestra Api Rest
				Assertions.assertThat(p.getNombre()).isEqualTo("Mesa comedor");
				// Verificamos que el nombre de la categoría del producto obtenido de la respuesta es igual a "muebles",que es el nombre de la categoría del producto que hemos enviado a nuestra Api Rest
				Assertions.assertThat(p.getCategoria().getNombre()).isEqualTo("muebles");
			});
		}

	}
	
	// Prueba unitaria para probar la edición de un producto desde nuestra Api Rest
	// La edición de un producto se realiza haciendo una petición http de tipo Put a la ruta o path base indicada en 'url' + "/{id}"
	@Test
	public void editarTest() {	
		// Recuperamos de la base de datos el producto a partir del nombre "Sony Notebook".Esto lo hacemos mediante el bean 'productoService'
		// En una prueba unitaria no es conveniente trabajar suscribiéndonos(mediante el método "subscribe()") a un flujo reactivo u Observable para obtener o manipular sus elementos ya que se tiene que trabajar de manera síncrona dentro del contexto o hilo de este método,y no dentro del contexto o hilo de la suscripción al Observable,que sería asíncrono.
		// Por esta razón,usamos el método "block()" que bloquea la ejecución del hilo de este flujo reactivo Mono para obtener su elemento de tipo Producto
		Producto producto = productoService.findByNombre("Sony Notebook").block();
		
		// Recuperamos de la base de datos la categoría a partir del nombre "electrónico".Esto lo hacemos mediante el bean 'productoService'
		// En una prueba unitaria no es conveniente trabajar suscribiéndonos(mediante el método "subscribe()") a un flujo reactivo u Observable para obtener o manipular sus elementos ya que se tiene que trabajar de manera síncrona dentro del contexto o hilo de este método,y no dentro del contexto o hilo de la suscripción al Observable,que sería asíncrono.
		// Por esta razón,usamos el método "block()" que bloquea la ejecución del hilo de este flujo reactivo Mono para obtener su elemento de tipo Categoria
		Categoria categoria = productoService.findCategoriaByNombre("electrónico").block();
		
		// Creamos un nuevo producto con los nuevos datos que van a ser editados y que van a viajar en el cuerpo de la petición http o request
		Producto productoEditado = new Producto("Asus Notebook",700.00,categoria);
		
		// El cliente realiza una petición http de tipo Put a la ruta o path "/api/v2/productos/{id}".La parte variable de esta url o path,"{id}", se corresponde con el id del producto localizado anteriormente y se la pasamos a esta ruta o path con la sentencia "Collections.singletonMap("id",producto.getId())"
		client.put().uri(url + "/{id}",Collections.singletonMap("id",producto.getId()))
		// Indicamos que los datos que vamos a enviar en la petición http o request van con el MediaType "APPLICATION_JSON_UTF8"(El cuerpo de la petición va en formato Json y con la codificación de caracters UTF-8)
		.contentType(MediaType.APPLICATION_JSON_UTF8)
		// Indicamos que se consuman aquellas respuetas que contengan en la cabecera el MediaType "APPLICATION_JSON_UTF8"(El cuerpo de la respuesta va en formato Json y con la codificación de caracters UTF-8).Todos nuestros métodos handler de nuestro controlador generarn respuestas con este MediaType en la cabecera
		.accept(MediaType.APPLICATION_JSON_UTF8)
		// Establecemos en el cuerpo de la petición un flujo reactivo Mono con el producto creado anteriormente como elemento que contiene los nuevos datos para ser actualizados.El método "body()" puede recibir un flujo reactivo Mono o Flux,o un dato de tipo BodyInserters,este último para cuando tenemos datos que no son flujos reactivos
		.body(Mono.just(productoEditado),Producto.class)
		// El método "exchange()" envía nuestro request al endpoint anterior y consume nuestra Api Rest obteniéndose una respuesta para dicho request o petición
		.exchange()
		// Indicamos el estado de la respuesta que esperamos recibir de la petición http.En este caso,esperamos recibir el estado CREATED(201)
		.expectStatus().isCreated()
		// Indicamos la cabecera de la respuesta que esperamos recibir.En este caso,esperamos recibir en la cabecera de la respuesta un ContentType con un MediaType de tipo "APPLICATION_JSON_UTF8"
		.expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
		// Indicamos que esperamos recibir una respuesta de la petición como un objeto Json
		.expectBody()
		// Esperamos,mediante una expresión Json,que el id del producto del cuerpo de la respuesta no sea vacío
		.jsonPath("$.id").isNotEmpty()
		// Esperamos,mediante una expresión Json,que el nombre del producto del cuerpo de la respuesta sea identico a "Asus Notebook",que es el nuevo nombre del producto que hemos enviado a nuestra Api Rest
		.jsonPath("$.nombre").isEqualTo("Asus Notebook")
		// Esperamos,mediante una expresión Json,que el nombre de la categoría del producto del cuerpo de la respuesta sea identico a "electrónico",que es el nombre de la nueva categoría del producto que hemos enviado a nuestra Api Rest
		.jsonPath("$.categoria.nombre").isEqualTo("electrónico");
	}
	
	// Prueba unitaria para probar la eliminación de un producto determinado a partir de su id desde nuestra Api Rest
	// La eliminación de un producto se realiza haciendo una petición http de tipo Delete a la ruta o path base indicada en 'url' + "/{id}"
	@Test
	public void eliminarTest() {
		// Recuperamos de la base de datos el producto a partir del nombre "Mica Cómoda 5 Cajones".Esto lo hacemos mediante el bean 'productoService'
		// En una prueba unitaria no es conveniente trabajar suscribiéndonos(mediante el método "subscribe()") a un flujo reactivo u Observable para obtener o manipular sus elementos ya que se tiene que trabajar de manera síncrona dentro del contexto o hilo de este método,y no dentro del contexto o hilo de la suscripción al Observable,que sería asíncrono.
		// Por esta razón,usamos el método "block()" que bloquea la ejecución del hilo de este flujo reactivo Mono para obtener su elemento de tipo Producto
		Producto producto = productoService.findByNombre("Mica Cómoda 5 Cajones").block();
		
		// El cliente realiza una petición http de tipo Delete a la ruta o path "/api/v2/productos/{id}".La parte variable de esta url o path,"{id}", se corresponde con el id del producto localizado anteriormente y se la pasamos a esta ruta o path con la sentencia "Collections.singletonMap("id",producto.getId())"
		client.delete().uri(url + "/{id}",Collections.singletonMap("id",producto.getId()))
		// El método "exchange()" envía nuestro request al endpoint anterior y consume nuestra Api Rest obteniéndose una respuesta para dicho request o petición
		.exchange()
		// Indicamos el estado de la respuesta que esperamos recibir de la petición http.En este caso,esperamos recibir el estado NO_CONTENT(204)
		.expectStatus().isNoContent()
		// Indicamos que esperamos recibir una respuesta de la petición como un objeto Json
		.expectBody()
		// Verificamos que el cuerpo de la respuesta sea vacío
		.isEmpty();
	}
}
