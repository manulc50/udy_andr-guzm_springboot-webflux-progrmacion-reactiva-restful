package com.bolsadeideas.springboot.reactor.app;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.bolsadeideas.springboot.reactor.app.model.Comentarios;
import com.bolsadeideas.springboot.reactor.app.model.Usuario;
import com.bolsadeideas.springboot.reactor.app.model.UsuarioComentarios;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

//Para que esta aplicación de Spring Boot sea de escritorio o de línea de comandos, tiene que implementar la interfaz CommandLineRunner
//Esta interfaz nos permite ejecutar las tareas indicadas en el método run() antes de la aplicación Spring Boot desde el método main
@SpringBootApplication
public class SpringBootReactorApplication implements CommandLineRunner{
	
	// Habilitamos el uso del log en esta clase
	private static final Logger log = LoggerFactory.getLogger(SpringBootReactorApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SpringBootReactorApplication.class, args);
	}

	/* El patrón de diseño 'Observable' indica que hay uno o varios sujetos observables(Por ejemplo,' Observable<Cliente>' - Cliente sería un sujeto de Observable)
	 * que son escuchados o observados por los observadores(se suscriben a los observables) y que cuando sufren algún tipo de cambio(los observables),los observadores
	 * ejecutan una determinada tarea.
	 * Con este patrón, se trabaja de forma reactiva, que es asíncrona y no bloqueante.*/
	@Override
	public void run(String... args) throws Exception {
		// Un flujo reactivo de datos de tipo "Flux" maneja una colección de 0 a N elementos
		// Un flujo reactivo de datos de tipo "Mono" maneja una colección de 0 a 1 elementos
		
		/* Ejemplo 1  - Creación de un flujo reactivo Flux con el método "just()" */
		// ejemploCrearFluxJust();
		/* Ejemplo 2 - Método "subscribe()" */
		// ejemploSubscribe();
		/* Ejemplo 3 - Método "subscribe()" con gestión de errores */
		// ejemploSubscribeError();
		/* Ejemplo 4 - Método "subscribe()" con evento onComplete */
		// ejemploSubscribeOnComplete();
		/* Ejemplo 5 - Operador Map - Primero "doOnNext" y después "map" */
		// ejemploDoOnNextMap();
		/* Ejemplo 5 - Operador Map - Primero "map" y después "doOnNext" */
		// ejemploMapDoOnNext();
		/* Ejemplo 6 - Operador Map - Primero "map",después "doOnNext" y de nuevo "map" */
		// ejemploMapDoOnNextMap();
		/* Ejemplo 7 - Operador Map - Pasar de un stream de tipo String a uno de tipo Usuario */
		// ejemploMapString2Usuario();
		/* Ejemplo 8 - Operador Filter */
		// ejemploFilter();
		/* Ejemplo 9 - Los streams son inmutables */
		// ejemploInmutables();
		/* Ejemplo 10 - Crear un flujo reactivo Flux a partir de una lista */
		// ejemploCrearFluxFromIterable();
		/* Ejemplo 11 - Operador FlatMap*/
		// ejemploFlatMap();
		/* Ejemplo 12 - Pasar de un Flux de tipo Usuario a un Flux de tipo String*/
		// ejemploFluxUsuario2FluxString();
		/* Ejemplo 13 - Pasar de un Flux de tipo Usuario a un Mono de tipo List*/
		// ejemploCollectList();
		/* Ejemplo 14 - Combinar dos flujos reactivos con el operador "flatMap" */
		// ejemploUsuarioComentariosFlatMap();
		/* Ejemplo 15 - Combinar dos flujos reactivos con el operador "zipWith" */
		// ejemploUsuarioComentariosZipWith();
		/* Ejemplo 16 - Combinar dos flujos reactivos con el operador "zipWith" - Segunda forma */
		// ejemploUsuarioComentariosZipWithForma2();
		/* Ejemplo 17 - Combinar dos flujos reactivos con los operadores "zipWith" y "range" */
		// ejemploZipWithRangos();
		/* Ejemplo 18 - Combinar dos flujos reactivos con los operadores "zipWith" e "interval" */
		// ejemploZipWithInterval();
		/* Ejemplo 19 - Combinar dos flujos reactivos con el operador "delayElements" */
		// ejemploDelayElements();
		/* Ejemplo 20 - Flujo reactivo de intervalos infinitos con el operador "interval" */
		// ejemploIntervalInfinito();
		/* Ejemplo 21 - Flujo reactivo de intervalos infinitos usando el operador "create" */
		// ejemploIntervalDesdeCreate();
		/* Ejemplo 22 - Manejo de la contrapresión implementado la interfaz Subscriber*/
		// ejemploContrapresionSubscriber();
		/* Ejemplo 23 - Manejo de la contrapresión con el operador "limitRate"*/
		 ejemploContrapresionLimitRate();

	}
	
	private void ejemploCrearFluxJust(){
		// Creamos un flujo reactivo de tipo Flux de Strings.Este flujo es un Observable
		Flux<String> nombres = Flux.just("Andres","Pedro","Diego","Juan") // Usamos el método "just()" que nos permite crear un flujo reactivo a partir de una fuente de datos puesta directamente "a pelo" separados por comas
				.doOnNext(System.out::println); // Con el método "doOnNext()" indicamos que se muestre por pantalla cada elemento,que es un nombre,emitido por el Observable "nombres".'System.out::println' hace referencia al método 'println' de 'System.out' y es una manera más simplificada de poner la sentencia 'elemento -> System.out.println(elemento)' 
		
		// Si un Observable no tiene un Observador suscrito,no se va a ejecutar la cadena de ejecución indicada en el Observable.
		// El método "subscribe()" nos permite crear un Observador de un Observable
		// Creamos un Observador del Observable "nombres"
		nombres.subscribe();
		
	}
	
	private void ejemploSubscribe() {
		// Creamos un flujo reactivo de tipo Flux de Strings.Este flujo es un Observable
		Flux<String> nombres = Flux.just("Andres","Pedro","Diego","Juan") // Usamos el método "just()" que nos permite crear un flujo reactivo a partir de una fuente de datos puesta directamente "a pelo" separados por comas
				.doOnNext(System.out::println); // Con el método "doOnNext()" indicamos que se muestre por pantalla cada elemento,que es un nombre,emitido por el Observable "nombres".'System.out::println' hace referencia al método 'println' de 'System.out' y es una manera más simplificada de poner la sentencia del ejemplo 1 'elemento -> System.out.println(elemento)' 
		
		// Si un Observable no tiene un Observador suscrito,no se va a ejecutar la cadena de ejecución indicada en el Observable.
		// El método "subscribe()" nos permite crear un Observador de un Observable
		// Creamos un Observador del Observable "nombres"
		nombres.subscribe(log::info); // Al método "subscribe()" se le puede pasar una función lambda para que haga una determinada tarea por cada elemento recibido del Observale.En este caso,imprime en el log a modo de información cada nombre emitido por el Observable "nombres".'log::info' es la manera simplificada de poner la función lambda 'e -> log.info(e)'*/
	}
	
	private void ejemploSubscribeError() {
		// Creamos un flujo reactivo de tipo Flux de Strings.Este flujo es un Observable
		Flux<String> nombres = Flux.just("Andres","Pedro","","Diego","Juan") // Usamos el método "just()" que nos permite crear un flujo reactivo a partir de una fuente de datos puesta directamente "a pelo" separados por comas
				// Con el método "doOnNext()" indicamos que se haga una determinada tarea por cada elemento emitido por el Observable
				.doOnNext(e -> { // Cuando una función anónima de tipo flecha tiene más de una sentencia, hay que poner {}
					if(e.isEmpty())  // Comprueba si cada elemento del stream es vacío.
						throw new RuntimeException("El nombre no puede ser vacío"); // Y en caso afirmativo,lanza una excepción de tipo RuntimeException asociado a un mensaje personalizado.En este punto se suspende la ejecución al tratarse de una excepción de tipo "RuntimeException"
					System.out.println(e); // Por cada elemento del stream que no sea vacío, se imprime por pantalla
				});
		
		// Si un Observable no tiene un Observador suscrito,no se va a ejecutar la cadena de ejecución indicada en el Observable.
		// El método "subscribe()" nos permite crear un Observador de un Observable
		// Creamos un Observador del Observable "nombres"
		// Al método "subscribe()" se le puede pasar una función lambda para que haga una determinada tarea por cada elemento recibido del Observale.En este caso,imprime en el log a modo de información cada nombre emitido por el Observable "nombres".'log::info' es la manera simplificada de poner la función lambda 'e -> log.info(e)'
		// Si se le pasa un segundo parámetro al método 'subscribe()', es para gestionar errores producidos en el stream emitido por el Observable.En este caso,cuando se produce un error, se ejecuta la función anónima de tipo flecha 'error -> log.error(error.getMessage())' para imprimir en el log a modo error el mensaje de la excepción producida
		nombres.subscribe(log::info,error -> log.error(error.getMessage()));
	}
	
	private void ejemploSubscribeOnComplete() {
		// Creamos un flujo reactivo de tipo Flux de Strings.Este flujo es un Observable
		Flux<String> nombres = Flux.just("Andres","Pedro","Maria","Diego","Juan") // Usamos el método "just()" que nos permite crear un flujo reactivo a partir de una fuente de datos puesta directamente "a pelo" separados por comas
				// Con el método "doOnNext()" indicamos que se haga una determinada tarea por cada elemento emitido por el Observable
				.doOnNext(e -> { // Cuando una función anónima de tipo flecha tiene más de una sentencia, hay que poner {}
					if(e.isEmpty())  // Comprueba si cada elemento del stream es vacío.
						throw new RuntimeException("El nombre no puede ser vacío"); // Y en caso afirmativo,lanza una excepción de tipo RuntimeException asociado a un mensaje personalizado.En este punto se suspende la ejecución al tratarse de una excepción de tipo "RuntimeException"
					System.out.println(e); // Por cada elemento del stream que no sea vacío, se imprime por pantalla
				});
		
		// Si un Observable no tiene un Observador suscrito,no se va a ejecutar la cadena de ejecución indicada en el Observable.
		// El método "subscribe()" nos permite crear un Observador de un Observable
		// Creamos un Observador del Observable "nombres"
		// Al método "subscribe()" se le puede pasar una función lambda para que haga una determinada tarea por cada elemento recibido del Observale.En este caso,imprime en el log a modo de información cada nombre emitido por el Observable "nombres".'log::info' es la manera simplificada de poner la función lambda 'e -> log.info(e)'
		// Si se le pasa un segundo parámetro al método 'subscribe()', es para gestionar errores producidos en el stream emitido por el Observable.En este caso,cuando se produce un error, se ejecuta la función anónima de tipo flecha 'error -> log.error(error.getMessage())' para imprimir en el log a modo error el mensaje de la excepción producida
		// Si se le pasa un tercer parámetro al método 'subscribe()', es para lanzar un hilo('Runnable') que ejecute una tarea cuando el Observable ha terminado de emitir el último elemento del stream
		nombres.subscribe(log::info,error -> log.error(error.getMessage()),new Runnable(){ 

	    	// En este caso,cuando el Observable ha terminado de emitir el último elemento del stream,se va a mostrar en el log a modo de información un mensaje
			@Override
			public void run() {
				log.info("El Observable ha finalizado su ejecución con éxito.");
				
			}
	    	
	    });
	}
	
	private void ejemploDoOnNextMap() {
		 /* En este caso, se ejecuta primero lo indicado en el método 'doOnNext' y después,en segundo lugar,se ejecuta lo indicado en el método 'map'(esto es así porque sigue un orden,primero esta el método 'doOnNext' y luego 'map').Y por último, se ejecuta la tarea del Observador con cada elemento del stream ya transformado
		 * Los Observales son inmutables,es decir,que las transfromaciones indicadas en el método 'map' realmente no afectan al estado incial de los elemento de cada stream,sino que se hacen sobre instancias nuevas para no alterar dicho estado*/

		// Creamos un flujo reactivo de tipo Flux de Strings.Este flujo es un Observable
		Flux<String> nombres = Flux.just("Andres","Pedro","Maria","Diego","Juan") // Usamos el método "just()" que nos permite crear un flujo reactivo a partir de una fuente de datos puesta directamente "a pelo" separados por comas
				// Con el método "doOnNext()" indicamos que se haga una determinada tarea por cada elemento emitido por el Observable
				.doOnNext(e -> { // Cuando una función anónima de tipo flecha tiene más de una sentencia, hay que poner {}
					if(e.isEmpty())  // Comprueba si cada elemento del stream es vacío.
						throw new RuntimeException("El nombre no puede ser vacío"); // Y en caso afirmativo,lanza una excepción de tipo RuntimeException asociado a un mensaje personalizado.En este punto se suspende la ejecución al tratarse de una excepción de tipo "RuntimeException"
					System.out.println(e); // Por cada elemento del stream que no sea vacío, se imprime por pantalla
				})
				// Con el método 'map' podemos transformar cada elemento del stream.En este caso,cada elemento del stream,que es una cadena de caracteres,la transformamos a uppercase,es decir,todos sus caracteres en mayúscula
				.map(e -> e.toUpperCase()); // Como los Observables son inmutables,ningún operador como "map" puede modificar o alterar los datos del Observable inicial.Lo que hace el oeprador "map" es crear un Observable nuevo con el resultado final de la operación sin alterar el Observable inicial
		
		// Si un Observable no tiene un Observador suscrito,no se va a ejecutar la cadena de ejecución indicada en el Observable.
		// El método "subscribe()" nos permite crear un Observador de un Observable
		// Creamos un Observador del Observable "nombres"
		// Al método "subscribe()" se le puede pasar una función lambda para que haga una determinada tarea por cada elemento recibido del Observale.En este caso,imprime en el log a modo de información cada nombre emitido por el Observable "nombres".'log::info' es la manera simplificada de poner la función lambda 'e -> log.info(e)'
		// Si se le pasa un segundo parámetro al método 'subscribe()', es para gestionar errores producidos en el stream emitido por el Observable.En este caso,cuando se produce un error, se ejecuta la función anónima de tipo flecha 'error -> log.error(error.getMessage())' para imprimir en el log a modo error el mensaje de la excepción producida
		// Si se le pasa un tercer parámetro al método 'subscribe()', es para lanzar un hilo('Runnable') que ejecute una tarea cuando el Observable ha terminado de emitir el último elemento del stream
		nombres.subscribe(log::info,error -> log.error(error.getMessage()),new Runnable(){ 

	    	// En este caso,cuando el Observable ha terminado de emitir el último elemento del stream,se va a mostrar en el log a modo de información un mensaje
			@Override
			public void run() {
				log.info("El Observable ha finalizado su ejecución con éxito.");
				
			}
	    	
	    });
	}
	
	private void ejemploMapDoOnNext() {
		/* En este caso, se ejecuta primero lo indicado en el método 'map' y después,en segundo lugar,se ejecuta lo indicado en el método 'doOnNext'(esto es así porque sigue un orden,primero esta el método 'map' y luego 'doOnNext').Y por último, se ejecuta la tarea del Observador con cada elemento del stream ya transformado
		 * Los Observales son inmutables,es decir,que las transfromaciones indicadas en el método 'map' realmente no afectan al estado incial de los elemento de cada stream,sino que se hacen sobre instancias nuevas para no alterar dicho estado*/

		// Creamos un flujo reactivo de tipo Flux de Strings.Este flujo es un Observable
		Flux<String> nombres = Flux.just("Andres","Pedro","Maria","Diego","Juan") // Usamos el método "just()" que nos permite crear un flujo reactivo a partir de una fuente de datos puesta directamente "a pelo" separados por comas
				// Con el método 'map' podemos transformar cada elemento del stream.En este caso,cada elemento del stream,que es una cadena de caracteres,la transformamos a uppercase,es decir,todos sus caracteres en mayúscula
				.map(e -> e.toUpperCase()) // Como los Observables son inmutables,ningún operador como "map" puede modificar o alterar los datos del Observable inicial.Lo que hace el oeprador "map" es crear un Observable nuevo con el resultado final de la operación sin alterar el Observable inicial
				// Con el método "doOnNext()" indicamos que se haga una determinada tarea por cada elemento emitido por el Observable
				.doOnNext(e -> { // Cuando una función anónima de tipo flecha tiene más de una sentencia, hay que poner {}
					if(e.isEmpty())  // Comprueba si cada elemento del stream es vacío.
						throw new RuntimeException("El nombre no puede ser vacío"); // Y en caso afirmativo,lanza una excepción de tipo RuntimeException asociado a un mensaje personalizado.En este punto se suspende la ejecución al tratarse de una excepción de tipo "RuntimeException"
					System.out.println(e); // Por cada elemento del stream que no sea vacío, se imprime por pantalla
				});
		
		// Si un Observable no tiene un Observador suscrito,no se va a ejecutar la cadena de ejecución indicada en el Observable.
		// El método "subscribe()" nos permite crear un Observador de un Observable
		// Creamos un Observador del Observable "nombres"
		// Al método "subscribe()" se le puede pasar una función lambda para que haga una determinada tarea por cada elemento recibido del Observale.En este caso,imprime en el log a modo de información cada nombre emitido por el Observable "nombres".'log::info' es la manera simplificada de poner la función lambda 'e -> log.info(e)'
		// Si se le pasa un segundo parámetro al método 'subscribe()', es para gestionar errores producidos en el stream emitido por el Observable.En este caso,cuando se produce un error, se ejecuta la función anónima de tipo flecha 'error -> log.error(error.getMessage())' para imprimir en el log a modo error el mensaje de la excepción producida
		// Si se le pasa un tercer parámetro al método 'subscribe()', es para lanzar un hilo('Runnable') que ejecute una tarea cuando el Observable ha terminado de emitir el último elemento del stream
		nombres.subscribe(log::info,error -> log.error(error.getMessage()),new Runnable(){ 

	    	// En este caso,cuando el Observable ha terminado de emitir el último elemento del stream,se va a mostrar en el log a modo de información un mensaje
			@Override
			public void run() {
				log.info("El Observable ha finalizado su ejecución con éxito.");
				
			}
	    	
	    });
	}
	
	private void ejemploMapDoOnNextMap() {
		/* En este caso, se ejecuta primero lo indicado en el método 'map',después,en segundo lugar,se ejecuta lo indicado en el método 'doOnNext' y después,en tercer lugar, se ejecuta lo indicado en el otro 'map'(esto es así porque sigue un orden,primero esta el método 'map',luego 'doOnNext' y,por último, el otro 'map').Por último, se ejecuta la tarea del Observador con cada elemento del stream ya transformado
		 * Los Observales son inmutables,es decir,que las transfromaciones indicadas en el método 'map' realmente no afectan al estado incial de los elemento de cada stream,sino que se hacen sobre instancias nuevas para no alterar dicho estado*/

		// Creamos un flujo reactivo de tipo Flux de Strings.Este flujo es un Observable
		Flux<String> nombres = Flux.just("Andres","Pedro","Maria","Diego","Juan") // Usamos el método "just()" que nos permite crear un flujo reactivo a partir de una fuente de datos puesta directamente "a pelo" separados por comas
				// Con el método 'map' podemos transformar cada elemento del stream.En este caso,cada elemento del stream,que es una cadena de caracteres,la transformamos a uppercase,es decir,todos sus caracteres en mayúscula
				.map(e -> e.toUpperCase()) // Como los Observables son inmutables,ningún operador como "map" puede modificar o alterar los datos del Observable inicial.Lo que hace el oeprador "map" es crear un Observable nuevo con el resultado final de la operación sin alterar el Observable inicial
				// Con el método "doOnNext()" indicamos que se haga una determinada tarea por cada elemento emitido por el Observable
				.doOnNext(e -> { // Cuando una función anónima de tipo flecha tiene más de una sentencia, hay que poner {}
					if(e.isEmpty())  // Comprueba si cada elemento del stream es vacío.
						throw new RuntimeException("El nombre no puede ser vacío"); // Y en caso afirmativo,lanza una excepción de tipo RuntimeException asociado a un mensaje personalizado.En este punto se suspende la ejecución al tratarse de una excepción de tipo "RuntimeException"
					System.out.println(e); // Por cada elemento del stream que no sea vacío, se imprime por pantalla
				})
				.map(nombre -> nombre.toLowerCase()); // Volvemos a convertir cada elemento(cadena de caracteres) a lowercase(minúscula)
		
		// Si un Observable no tiene un Observador suscrito,no se va a ejecutar la cadena de ejecución indicada en el Observable.
		// El método "subscribe()" nos permite crear un Observador de un Observable
		// Creamos un Observador del Observable "nombres"
		// Al método "subscribe()" se le puede pasar una función lambda para que haga una determinada tarea por cada elemento recibido del Observale.En este caso,imprime en el log a modo de información cada nombre emitido por el Observable "nombres".'log::info' es la manera simplificada de poner la función lambda 'e -> log.info(e)'
		// Si se le pasa un segundo parámetro al método 'subscribe()', es para gestionar errores producidos en el stream emitido por el Observable.En este caso,cuando se produce un error, se ejecuta la función anónima de tipo flecha 'error -> log.error(error.getMessage())' para imprimir en el log a modo error el mensaje de la excepción producida
		// Si se le pasa un tercer parámetro al método 'subscribe()', es para lanzar un hilo('Runnable') que ejecute una tarea cuando el Observable ha terminado de emitir el último elemento del stream
		nombres.subscribe(log::info,error -> log.error(error.getMessage()),new Runnable(){ 

	    	// En este caso,cuando el Observable ha terminado de emitir el último elemento del stream,se va a mostrar en el log a modo de información un mensaje
			@Override
			public void run() {
				log.info("El Observable ha finalizado su ejecución con éxito.");
				
			}
	    	
	    });
	}
	
	private void ejemploMapString2Usuario() {
		/* En este caso, se ejecuta primero lo indicado en el método 'map',después,en segundo lugar,se ejecuta lo indicado en el método 'doOnNext' y después,en tercer lugar, se ejecuta lo indicado en el otro 'map'(esto es así porque sigue un orden,primero esta el método 'map',luego 'doOnNext' y,por último, el otro 'map').Por último, se ejecuta la tarea del Observador con cada elemento del stream ya transformado
		 * Los Observales son inmutables,es decir,que las transfromaciones indicadas en el método 'map' realmente no afectan al estado incial de los elemento de cada stream,sino que se hacen sobre instancias nuevas para no alterar dicho estado*/

		// Creamos un flujo reactivo de tipo Flux de objetos Usuario.Este flujo es un Observable
		Flux<Usuario> usuarios = Flux.just("Andres","Pedro","Maria","Diego","Juan") // Usamos el método "just()" que nos permite crear un flujo reactivo a partir de una fuente de datos puesta directamente "a pelo" separados por comas
				 // Con el método 'map' podemos transformar cada elemento del stream.En este caso,cada elemento del stream,que es una cadena de caracteres,la transformamos a un objeto Usuario con el nombre en uppercase,es decir,todos sus caracteres en mayúscula,y sin apellidos.Estamos transformando un stream<string> a un stream<Usuario>
				.map(nombre -> new Usuario(nombre.toUpperCase(),null)) // Como los Observables son inmutables,ningún operador como "map" puede modificar o alterar los datos del Observable inicial.Lo que hace el oeprador "map" es crear un Observable nuevo con el resultado final de la operación sin alterar el Observable inicial
				// Con el método "doOnNext()" indicamos que se haga una determinada tarea por cada elemento emitido por el Observable
				.doOnNext(usuario -> { // Cuando una función anónima de tipo flecha tiene más de una sentencia, hay que poner {}
					if(usuario == null)  // Comprueba si cada usuario del stream es vacío.
						throw new RuntimeException("El nombre no puede ser vacío"); // Y en caso afirmativo,lanza una excepción de tipo RuntimeException asociado a un mensaje personalizado.En este punto se suspende la ejecución al tratarse de una excepción de tipo "RuntimeException"
					System.out.println(usuario.getNombre()); // Por cada usuario del stream que no sea vacío, se imprime su nombre por pantalla
				})
				.map(usuario ->{ // Volvemos a convertir el nombre cada Usuario a lowercase(minúscula)
	    			String nombre = usuario.getNombre().toLowerCase();
	    			usuario.setNombre(nombre);
	    			return usuario;
	    		});
		
		// Si un Observable no tiene un Observador suscrito,no se va a ejecutar la cadena de ejecución indicada en el Observable.
		// El método "subscribe()" nos permite crear un Observador de un Observable
		// Creamos un Observador del Observable "usuarios"
		// Al método "subscribe()" se le puede pasar una función lambda para que haga una determinada tarea por cada elemento recibido del Observale.En este caso,imprime en el log a modo de información los datos de cada usuario emitido por el Observable "usuarios"
		// Si se le pasa un segundo parámetro al método 'subscribe()', es para gestionar errores producidos en el stream emitido por el Observable.En este caso,cuando se produce un error, se ejecuta la función anónima de tipo flecha 'error -> log.error(error.getMessage())' para imprimir en el log a modo error el mensaje de la excepción producida
		// Si se le pasa un tercer parámetro al método 'subscribe()', es para lanzar un hilo('Runnable') que ejecute una tarea cuando el Observable ha terminado de emitir el último elemento del stream
		usuarios.subscribe(usuario -> log.info(usuario.toString()),error -> log.error(error.getMessage()),new Runnable(){ 

	    	// En este caso,cuando el Observable ha terminado de emitir el último elemento del stream,se va a mostrar en el log a modo de información un mensaje
			@Override
			public void run() {
				log.info("El Observable ha finalizado su ejecución con éxito.");
				
			}
	    	
	    });
	}
	
	private void ejemploFilter() {
		/* En este caso, se ejecuta primero lo indicado en el método 'map',después,en segundo lugar,se ejecuta lo indicado en el método 'doOnNext' y después,en tercer lugar, se ejecuta lo indicado en el otro 'map'(esto es así porque sigue un orden,primero esta el método 'map',luego 'doOnNext' y,por último, el otro 'map').Por último, se ejecuta la tarea del Observador con cada elemento del stream ya transformado
		 * Los Observales son inmutables,es decir,que las transfromaciones indicadas en el método 'map' realmente no afectan al estado incial de los elemento de cada stream,sino que se hacen sobre instancias nuevas para no alterar dicho estado*/
		
		// Creamos un flujo reactivo de tipo Flux de objetos Usuario.Este flujo es un Observable
		Flux<Usuario> usuarios = Flux.just("Andres Guzman","Pedro Fulano","María Fulana","Diego Mengano","Juan Lorenzo","Bruce Willis", "Bruce Lee")  // Usamos el método "just()" que nos permite crear un flujo reactivo a partir de una fuente de datos puesta directamente "a pelo" separados por comas
				 // Con el método 'map' podemos transformar cada elemento del stream.En este caso,cada elemento del stream,que es una cadena de caracteres,la transformamos a un objeto Usuario con el nombre y el apellido en mayúscula.Estamos transformando un stream<string> a un stream<Usuario>
				.map(nombre -> new Usuario(nombre.split(" ")[0].toUpperCase(),nombre.split(" ")[1].toUpperCase())) // Como los Observables son inmutables,ningún operador como "map" puede modificar o alterar los datos del Observable inicial.Lo que hace el oeprador "map" es crear un Observable nuevo con el resultado final de la operación sin alterar el Observable inicial
				// Filtramos cada usuario del stream para quedarnos sólo con aquellos cuyo nombre sea 'bruce' ignorando minúsculas y mayúsculas
				.filter(usuario -> usuario.getNombre().equalsIgnoreCase("bruce"))
				// Con el método "doOnNext()" indicamos que se haga una determinada tarea por cada elemento emitido por el Observable
				.doOnNext(usuario -> { // Cuando una función anónima de tipo flecha tiene más de una sentencia, hay que poner {}
					if(usuario == null)  // Comprueba si cada usuario del stream es vacío.
						throw new RuntimeException("El nombre no puede ser vacío"); // Y en caso afirmativo,lanza una excepción de tipo RuntimeException asociado a un mensaje personalizado.En este punto se suspende la ejecución al tratarse de una excepción de tipo "RuntimeException"
					System.out.println(usuario.getNombre().concat(" ").concat(usuario.getApellido())); // Por cada usuario del stream que no sea vacío, se imprime  por pantalla su nombre y su apellido
				})
				.map(usuario ->{ // Volvemos a convertir el nombre cada Usuario a lowercase(minúscula)
	    			String nombre = usuario.getNombre().toLowerCase();
	    			usuario.setNombre(nombre);
	    			return usuario;
	    		});
		
		// Si un Observable no tiene un Observador suscrito,no se va a ejecutar la cadena de ejecución indicada en el Observable.
		// El método "subscribe()" nos permite crear un Observador de un Observable
		// Creamos un Observador del Observable "usuarios"
		// Al método "subscribe()" se le puede pasar una función lambda para que haga una determinada tarea por cada elemento recibido del Observale.En este caso,imprime en el log a modo de información los datos de cada usuario emitido por el Observable "usuarios"
		// Si se le pasa un segundo parámetro al método 'subscribe()', es para gestionar errores producidos en el stream emitido por el Observable.En este caso,cuando se produce un error, se ejecuta la función anónima de tipo flecha 'error -> log.error(error.getMessage())' para imprimir en el log a modo error el mensaje de la excepción producida
		// Si se le pasa un tercer parámetro al método 'subscribe()', es para lanzar un hilo('Runnable') que ejecute una tarea cuando el Observable ha terminado de emitir el último elemento del stream
		usuarios.subscribe(usuario -> log.info(usuario.toString()),error -> log.error(error.getMessage()),new Runnable(){ 

	    	// En este caso,cuando el Observable ha terminado de emitir el último elemento del stream,se va a mostrar en el log a modo de información un mensaje
			@Override
			public void run() {
				log.info("El Observable ha finalizado su ejecución con éxito.");
				
			}
	    	
	    });
	}
	
	private void ejemploInmutables() {
		/* En este caso, se ejecuta primero lo indicado en el método 'map',después,en segundo lugar,se ejecuta lo indicado en el método 'doOnNext' y después,en tercer lugar, se ejecuta lo indicado en el otro 'map'(esto es así porque sigue un orden,primero esta el método 'map',luego 'doOnNext' y,por último, el otro 'map').Por último, se ejecuta la tarea del Observador con cada elemento del stream ya transformado
		 * Los Observales son inmutables,es decir,que las transfromaciones indicadas en el método 'map' realmente no afectan al estado incial de los elemento de cada stream,sino que se hacen sobre instancias nuevas para no alterar dicho estado*/

		// Creamos un flujo reactivo de tipo Flux de Strings.Este flujo es un Observable
		Flux<String> nombres = Flux.just("Andres Guzman","Pedro Fulano","María Fulana","Diego Mengano","Juan Lorenzo","Bruce Willis", "Bruce Lee");  // Usamos el método "just()" que nos permite crear un flujo reactivo a partir de una fuente de datos puesta directamente "a pelo" separados por comas
		
		// Ahora,desde aquí hasta la parte indicada con la línea discontinua se corresponde con otro flujo o stream.Ya no forma parte del flujo inicial creado arriba */
		// Con el método 'map' podemos transformar cada elemento del stream.En este caso,cada elemento del stream,que es una cadena de caracteres,la transformamos a un objeto Usuario con el nombre y el apellido en mayúscula.Estamos transformando un stream<string> a un stream<Usuario>
		nombres.map(nombre -> new Usuario(nombre.split(" ")[0].toUpperCase(),nombre.split(" ")[1].toUpperCase())) // Como los Observables son inmutables,ningún operador como "map" puede modificar o alterar los datos del Observable inicial.Lo que hace el oeprador "map" es crear un Observable nuevo con el resultado final de la operación sin alterar el Observable inicial
			// Filtramos cada usuario del stream para quedarnos sólo con aquellos cuyo nombre sea 'bruce' ignorando minúsculas y mayúsculas
			.filter(usuario -> usuario.getNombre().equalsIgnoreCase("bruce"))
			// Con el método "doOnNext()" indicamos que se haga una determinada tarea por cada elemento emitido por el Observable
			.doOnNext(usuario -> { // Cuando una función anónima de tipo flecha tiene más de una sentencia, hay que poner {}
				if(usuario == null)  // Comprueba si cada usuario del stream es vacío.
					throw new RuntimeException("El nombre no puede ser vacío"); // Y en caso afirmativo,lanza una excepción de tipo RuntimeException asociado a un mensaje personalizado.En este punto se suspende la ejecución al tratarse de una excepción de tipo "RuntimeException"
				System.out.println(usuario.getNombre().concat(" ").concat(usuario.getApellido())); // Por cada usuario del stream que no sea vacío, se imprime  por pantalla su nombre y su apellido
			})
			.map(usuario ->{ // Volvemos a convertir el nombre cada Usuario a lowercase(minúscula)
	    		String nombre = usuario.getNombre().toLowerCase();
	    		usuario.setNombre(nombre);
	    		return usuario;
	    	});
		
		// ---------------------------------------------
		
		// Si un Observable no tiene un Observador suscrito,no se va a ejecutar la cadena de ejecución indicada en el Observable.
		// El método "subscribe()" nos permite crear un Observador de un Observable
		// Creamos un Observador del Observable "nombres"
		// Al método "subscribe()" se le puede pasar una función lambda para que haga una determinada tarea por cada elemento recibido del Observale.En este caso,imprime en el log a modo de información los datos de cada usuario emitido por el Observable "nombres"
		// Si se le pasa un segundo parámetro al método 'subscribe()', es para gestionar errores producidos en el stream emitido por el Observable.En este caso,cuando se produce un error, se ejecuta la función anónima de tipo flecha 'error -> log.error(error.getMessage())' para imprimir en el log a modo error el mensaje de la excepción producida
		// Si se le pasa un tercer parámetro al método 'subscribe()', es para lanzar un hilo('Runnable') que ejecute una tarea cuando el Observable ha terminado de emitir el último elemento del stream
		nombres.subscribe(usuario -> log.info(usuario.toString()),error -> log.error(error.getMessage()),new Runnable(){ 

	    	// En este caso,cuando el Observable ha terminado de emitir el último elemento del stream,se va a mostrar en el log a modo de información un mensaje
			@Override
			public void run() {
				log.info("El Observable ha finalizado su ejecución con éxito.");
				
			}
	    	
	    });
	}
	
	private void ejemploCrearFluxFromIterable() {
		/* En este caso, se ejecuta primero lo indicado en el método 'map',después,en segundo lugar,se ejecuta lo indicado en el método 'doOnNext' y después,en tercer lugar, se ejecuta lo indicado en el otro 'map'(esto es así porque sigue un orden,primero esta el método 'map',luego 'doOnNext' y,por último, el otro 'map').Por último, se ejecuta la tarea del Observador con cada elemento del stream ya transformado
		 * Los Observales son inmutables,es decir,que las transfromaciones indicadas en el método 'map' realmente no afectan al estado incial de los elemento de cada stream,sino que se hacen sobre instancias nuevas para no alterar dicho estado*/

		// Creamos una lista de nombres
		List<String> nombres = new ArrayList<String>();
		nombres.add("Andres Guzman");
		nombres.add("Pedro Fulano");
		nombres.add("María Fulana");
		nombres.add("Diego Mengano");
		nombres.add("Juan Lorenzo");
		nombres.add("Bruce Willis");
		nombres.add("Bruce Lee");
		
		// Creamos un flujo reactivo de tipo Flux de objetos Usuario.Este flujo es un Observable
		Flux<Usuario> usuarios = Flux.fromIterable(nombres)  // Usamos el método "fromIterable()" que nos permite crear un flujo reactivo a partir de una lista de datos
				 // Con el método 'map' podemos transformar cada elemento del stream.En este caso,cada elemento del stream,que es una cadena de caracteres,la transformamos a un objeto Usuario con el nombre y el apellido en mayúscula.Estamos transformando un stream<string> a un stream<Usuario>
				.map(nombre -> new Usuario(nombre.split(" ")[0].toUpperCase(),nombre.split(" ")[1].toUpperCase())) // Como los Observables son inmutables,ningún operador como "map" puede modificar o alterar los datos del Observable inicial.Lo que hace el oeprador "map" es crear un Observable nuevo con el resultado final de la operación sin alterar el Observable inicial
				// Filtramos cada usuario del stream para quedarnos sólo con aquellos cuyo nombre sea 'bruce' ignorando minúsculas y mayúsculas
				.filter(usuario -> usuario.getNombre().equalsIgnoreCase("bruce"))
				// Con el método "doOnNext()" indicamos que se haga una determinada tarea por cada elemento emitido por el Observable
				.doOnNext(usuario -> { // Cuando una función anónima de tipo flecha tiene más de una sentencia, hay que poner {}
					if(usuario == null)  // Comprueba si cada usuario del stream es vacío.
						throw new RuntimeException("El nombre no puede ser vacío"); // Y en caso afirmativo,lanza una excepción de tipo RuntimeException asociado a un mensaje personalizado.En este punto se suspende la ejecución al tratarse de una excepción de tipo "RuntimeException"
					System.out.println(usuario.getNombre().concat(" ").concat(usuario.getApellido())); // Por cada usuario del stream que no sea vacío, se imprime  por pantalla su nombre y su apellido
				})
				.map(usuario ->{ // Volvemos a convertir el nombre cada Usuario a lowercase(minúscula)
	    			String nombre = usuario.getNombre().toLowerCase();
	    			usuario.setNombre(nombre);
	    			return usuario;
	    		});
		
		// Si un Observable no tiene un Observador suscrito,no se va a ejecutar la cadena de ejecución indicada en el Observable.
		// El método "subscribe()" nos permite crear un Observador de un Observable
		// Creamos un Observador del Observable "usuarios"
		// Al método "subscribe()" se le puede pasar una función lambda para que haga una determinada tarea por cada elemento recibido del Observale.En este caso,imprime en el log a modo de información los datos de cada usuario emitido por el Observable "usuarios"
		// Si se le pasa un segundo parámetro al método 'subscribe()', es para gestionar errores producidos en el stream emitido por el Observable.En este caso,cuando se produce un error, se ejecuta la función anónima de tipo flecha 'error -> log.error(error.getMessage())' para imprimir en el log a modo error el mensaje de la excepción producida
		// Si se le pasa un tercer parámetro al método 'subscribe()', es para lanzar un hilo('Runnable') que ejecute una tarea cuando el Observable ha terminado de emitir el último elemento del stream
		usuarios.subscribe(usuario -> log.info(usuario.toString()),error -> log.error(error.getMessage()),new Runnable(){ 

	    	// En este caso,cuando el Observable ha terminado de emitir el último elemento del stream,se va a mostrar en el log a modo de información un mensaje
			@Override
			public void run() {
				log.info("El Observable ha finalizado su ejecución con éxito.");
				
			}
	    	
	    });
	}
	
	private void ejemploFlatMap() {
		/* En este caso, se ejecuta primero lo indicado en el método 'map',después,en segundo lugar,se ejecuta lo indicado en el método 'doOnNext' y después,en tercer lugar, se ejecuta lo indicado en el otro 'map'(esto es así porque sigue un orden,primero esta el método 'map',luego 'doOnNext' y,por último, el otro 'map').Por último, se ejecuta la tarea del Observador con cada elemento del stream ya transformado
		 * Los Observales son inmutables,es decir,que las transfromaciones indicadas en el método 'map' realmente no afectan al estado incial de los elemento de cada stream,sino que se hacen sobre instancias nuevas para no alterar dicho estado*/
		
		// Creamos una lista de nombres
		List<String> nombres = new ArrayList<String>();
		nombres.add("Andres Guzman");
		nombres.add("Pedro Fulano");
		nombres.add("María Fulana");
		nombres.add("Diego Mengano");
		nombres.add("Juan Lorenzo");
		nombres.add("Bruce Willis");
		nombres.add("Bruce Lee");
		
		// Creamos un flujo reactivo de tipo Flux de objetos Usuario.Este flujo es un Observable
		Flux<Usuario> usuarios = Flux.fromIterable(nombres)  // Usamos el método "fromIterable()" que nos permite crear un flujo reactivo a partir de una lista de datos
				 // Con el método 'map' podemos transformar cada elemento del stream.En este caso,cada elemento del stream,que es una cadena de caracteres,la transformamos a un objeto Usuario con el nombre y el apellido en mayúscula.Estamos transformando un stream<string> a un stream<Usuario>
				.map(nombre -> new Usuario(nombre.split(" ")[0].toUpperCase(),nombre.split(" ")[1].toUpperCase())) // Como los Observables son inmutables,ningún operador como "map" puede modificar o alterar los datos del Observable inicial.Lo que hace el oeprador "map" es crear un Observable nuevo con el resultado final de la operación sin alterar el Observable inicial
				// El operador "flatMap" es parecido al operador "map" pero se diferencian en que el operador "map" convierte un tipo no reactivo en otro no reactivo,sin embargo,el operador "flatMap" convierte un tipo no reactivo en otro sí reactivo y al final nos queda un flujo reactivo de objeto o items que a su vez son flujos reactivos que van a ser aplanados para que quede un único flujo reactivo de salida de objetos no reactivos
				// En este caso,pasamos de un Flux de objetos Usuarios a un Flux de objetos Mono de tipo Usuarios o vacíos,pero al final se produce un proceso de aplanamiento y nos queda un Flux de tipo Usuarios(los Mono de tipo empty o vacíos desaparecen en este proceso porque no emiten nada) cuyos nombre coinciden con "bruce"
				// Por lo tanto,el operador "flatMap" es otra alternativa al operador "filter"
				.flatMap(usuario -> {
					if(usuario.getNombre().equalsIgnoreCase("bruce")) //Filtramos cada usuario del stream para quedarnos sólo con aquellos cuyo nombre sea 'bruce' ignorando minúsculas y mayúsculas
						return Mono.just(usuario); // Y para aquellos cuyo nombre sea 'bruce', ignorando minúsculas y mayúsculas, creamos un flujo reactivo Mono para cada uno de ellos
					else
						return Mono.empty(); // Y para el resto,cremos un flujo reactivo Mono vacío,es decir,que no emite ningún objeto o item
				})
				// Volvemos a convertir el nombre cada Usuario a lowercase(minúscula)
				.map(usuario ->{
	    			String nombre = usuario.getNombre().toLowerCase();
	    			usuario.setNombre(nombre);
	    			return usuario;
	    		});
		
		// Si un Observable no tiene un Observador suscrito,no se va a ejecutar la cadena de ejecución indicada en el Observable.
		// El método "subscribe()" nos permite crear un Observador de un Observable
		// Creamos un Observador del Observable "usuarios"
		// Al método "subscribe()" se le puede pasar una función lambda para que haga una determinada tarea por cada elemento recibido del Observale.En este caso,imprime en el log a modo de información los datos de cada usuario emitido por el Observable "usuarios"
		// Si se le pasa un segundo parámetro al método 'subscribe()', es para gestionar errores producidos en el stream emitido por el Observable.En este caso,cuando se produce un error, se ejecuta la función anónima de tipo flecha 'error -> log.error(error.getMessage())' para imprimir en el log a modo error el mensaje de la excepción producida
		// Si se le pasa un tercer parámetro al método 'subscribe()', es para lanzar un hilo('Runnable') que ejecute una tarea cuando el Observable ha terminado de emitir el último elemento del stream
		usuarios.subscribe(usuario -> log.info(usuario.toString()),error -> log.error(error.getMessage()),new Runnable(){ 

	    	// En este caso,cuando el Observable ha terminado de emitir el último elemento del stream,se va a mostrar en el log a modo de información un mensaje
			@Override
			public void run() {
				log.info("El Observable ha finalizado su ejecución con éxito.");
				
			}
	    	
	    });
	}
	
	private void ejemploFluxUsuario2FluxString() {
		/* En este caso, se ejecuta primero lo indicado en el método 'map',después,en segundo lugar,se ejecuta lo indicado en el método 'doOnNext' y después,en tercer lugar, se ejecuta lo indicado en el otro 'map'(esto es así porque sigue un orden,primero esta el método 'map',luego 'doOnNext' y,por último, el otro 'map').Por último, se ejecuta la tarea del Observador con cada elemento del stream ya transformado
		 * Los Observales son inmutables,es decir,que las transfromaciones indicadas en el método 'map' realmente no afectan al estado incial de los elemento de cada stream,sino que se hacen sobre instancias nuevas para no alterar dicho estado*/
		
		// Creamos una lista de usuarios
		List<Usuario> usuariosList = new ArrayList<Usuario>();
		usuariosList.add(new Usuario("Andres","Guzman"));
		usuariosList.add(new Usuario("Pedro","Fulano"));
		usuariosList.add(new Usuario("María","Fulana"));
		usuariosList.add(new Usuario("Diego","Mengano"));
		usuariosList.add(new Usuario("Juan","Lorenzo"));
		usuariosList.add(new Usuario("Bruce","Willis"));
		usuariosList.add(new Usuario("Bruce","Lee"));
		
		// Creamos un flujo reactivo de tipo Flux de objetos String.Este flujo es un Observable
		Flux<String> nombres = Flux.fromIterable(usuariosList)  // Usamos el método "fromIterable()" que nos permite crear un flujo reactivo a partir de una lista de datos
				 // Con el método 'map' podemos transformar cada elemento del stream.En este caso,cada elemento del stream,que es un objeto Usuario,lo transformamos a un objeto String con el nombre y el apellido en mayúscula.Estamos transformando un stream<Usuario> a un stream<String>
				.map(usuario -> usuario.getNombre().toUpperCase() + " " + usuario.getApellido().toUpperCase()) // Como los Observables son inmutables,ningún operador como "map" puede modificar o alterar los datos del Observable inicial.Lo que hace el oeprador "map" es crear un Observable nuevo con el resultado final de la operación sin alterar el Observable inicial
				// El operador "flatMap" es parecido al operador "map" pero se diferencian en que el operador "map" convierte un tipo no reactivo en otro no reactivo,sin embargo,el operador "flatMap" convierte un tipo no reactivo en otro sí reactivo y al final nos queda un flujo reactivo de objeto o items que a su vez son flujos reactivos que van a ser aplanados para que quede un único flujo reactivo de salida de objetos no reactivos
				// En este caso,pasamos de un Flux de objetos String a un Flux de objetos Mono de tipo String o vacíos,pero al final se produce un proceso de aplanamiento y nos queda un Flux de tipo String(los Mono de tipo empty o vacíos desaparecen en este proceso porque no emiten nada) que son los nombre que coinciden con "bruce"
				// Por lo tanto,el operador "flatMap" es otra alternativa al operador "filter"
				.flatMap(nombre -> {
					if(nombre.contains("BRUCE")) //Filtramos cada usuario del stream para quedarnos sólo con aquellos cuyo nombre sea 'bruce' ignorando minúsculas y mayúsculas
						return Mono.just(nombre); // Y para aquellos cuyo nombre sea 'bruce', ignorando minúsculas y mayúsculas, creamos un flujo reactivo Mono para cada uno de ellos
					else
						return Mono.empty(); // Y para el resto,cremos un flujo reactivo Mono vacío,es decir,que no emite ningún objeto o item
				})
				// Volvemos a convertir el nombre a lowercase(minúscula)
				.map(nombre -> nombre.toLowerCase());
		
		// Si un Observable no tiene un Observador suscrito,no se va a ejecutar la cadena de ejecución indicada en el Observable.
		// El método "subscribe()" nos permite crear un Observador de un Observable
		// Creamos un Observador del Observable "nombres"
		// Al método "subscribe()" se le puede pasar una función lambda para que haga una determinada tarea por cada elemento recibido del Observale.En este caso,imprime en el log a modo de información los datos de cada usuario emitido por el Observable "nombres"
		// Si se le pasa un segundo parámetro al método 'subscribe()', es para gestionar errores producidos en el stream emitido por el Observable.En este caso,cuando se produce un error, se ejecuta la función anónima de tipo flecha 'error -> log.error(error.getMessage())' para imprimir en el log a modo error el mensaje de la excepción producida
		// Si se le pasa un tercer parámetro al método 'subscribe()', es para lanzar un hilo('Runnable') que ejecute una tarea cuando el Observable ha terminado de emitir el último elemento del stream
		nombres.subscribe(nombre -> log.info(nombre),error -> log.error(error.getMessage()),new Runnable(){ 

	    	// En este caso,cuando el Observable ha terminado de emitir el último elemento del stream,se va a mostrar en el log a modo de información un mensaje
			@Override
			public void run() {
				log.info("El Observable ha finalizado su ejecución con éxito.");
				
			}
	    	
	    });
	}
	
	private void ejemploCollectList() {
		/* En este caso, se ejecuta primero lo indicado en el método 'map',después,en segundo lugar,se ejecuta lo indicado en el método 'doOnNext' y después,en tercer lugar, se ejecuta lo indicado en el otro 'map'(esto es así porque sigue un orden,primero esta el método 'map',luego 'doOnNext' y,por último, el otro 'map').Por último, se ejecuta la tarea del Observador con cada elemento del stream ya transformado
		 * Los Observales son inmutables,es decir,que las transfromaciones indicadas en el método 'map' realmente no afectan al estado incial de los elemento de cada stream,sino que se hacen sobre instancias nuevas para no alterar dicho estado*/
		
		// Creamos una lista de usuarios
		List<Usuario> usuariosList = new ArrayList<Usuario>();
		usuariosList.add(new Usuario("Andres","Guzman"));
		usuariosList.add(new Usuario("Pedro","Fulano"));
		usuariosList.add(new Usuario("María","Fulana"));
		usuariosList.add(new Usuario("Diego","Mengano"));
		usuariosList.add(new Usuario("Juan","Lorenzo"));
		usuariosList.add(new Usuario("Bruce","Willis"));
		usuariosList.add(new Usuario("Bruce","Lee"));
		
		// Creamos un flujo reactivo de tipo Flux de objetos String.Este flujo es un Observable
		Mono<List<Usuario>> listUsuarios = Flux.fromIterable(usuariosList)  // Usamos el método "fromIterable()" que nos permite crear un flujo reactivo a partir de una lista de datos
				 // Con el operador 'collectList()' podemos pasar un flujo reactivo Flux de datos a un flujo reactivo Mono de tipo List.En este caso,estamos pasando un Flux de tipo Usuario a un Mono de tipo List<Usuario>
				// De esta manera,en lugar de emitir uno por uno cada usuario del flujo reactivo Flux,se emite una sola vez la lista de usuarios a través del flujo reactivo Mono
				.collectList();
		
		// Si un Observable no tiene un Observador suscrito,no se va a ejecutar la cadena de ejecución indicada en el Observable.
		// El método "subscribe()" nos permite crear un Observador de un Observable
		// Creamos un Observador del Observable "listUsuarios"
		// Al método "subscribe()" se le puede pasar una función lambda para que haga una determinada tarea por cada elemento recibido del Observale.En este caso,imprime en el log a modo de información los datos de cada usuario de la lista emitida por el Observable "listUsuarios"
		// Si se le pasa un segundo parámetro al método 'subscribe()', es para gestionar errores producidos en el stream emitido por el Observable
		// Si se le pasa un tercer parámetro al método 'subscribe()', es para lanzar un hilo('Runnable') que ejecute una tarea cuando el Observable ha terminado de emitir el último elemento del stream
		listUsuarios.subscribe(lista -> lista.forEach(item -> log.info(item.toString())));
	}
	
	private void ejemploUsuarioComentariosFlatMap() {
		// Creamos un flujo reactivo Mono de tipo Usuario
		// El método "fromCallable" nos permite crear un flujo reactivo Mono a partir de una función lambda que devuelva el dato para dicho flujo
		Mono<Usuario> usuarioMono = Mono.fromCallable(() -> new Usuario("John","Doe"));
		// Creamos un flujo reactivo Mono de tipo Comentarios
		// El método "fromCallable" nos permite crear un flujo reactivo Mono a partir de una función lambda que devuelva el dato para dicho flujo
		Mono<Comentarios> usuarioComentariosMono = Mono.fromCallable(() -> {
			Comentarios comentarios = new Comentarios();
			comentarios.addComentario("Hola Pepe, qué tal!");
			comentarios.addComentario("Mañana voy a la playa!");
			comentarios.addComentario("Estoy tomando el curso de spring con reactor");
			return comentarios;
		});
		
		// Combinamos los dos flujos reactivos Mono anteriores con el operador "flatMap" para obtener un flujo reactivo Mono de tipo UsuarioComentarios
		// Para ello,la sentencia 'usuarioComentariosMono.map(c -> new UsuarioComentarios(u,c))' va a devolver un flujo reactivo Mono de salida de tipo UsuarioComentarios con los datos del usuario(c) y los comentarios(c),pero,como es necesario los datos del usuario(c) para crear un objeto de UsuarioComentarios,tenemos que emitirlo con el operador "flatMap" porque al final vamos a tener un Mono de un Mono de tipo UsuarioComentarios y tenemos que aplanarlo para que nos quede únicamente un Mono de tipo UsuarioComentarios al que suscribirnos
		usuarioMono.flatMap(u -> usuarioComentariosMono.map(c -> new UsuarioComentarios(u,c)))
		// Finalmente, nos suscribimos al Observable Mono de tipo UsuarioComentarios, creando el Observador, y escribimos en el log a modo de información los datos del usuario y sus comentarios
		.subscribe(uc -> log.info(uc.toString()));
	}
	
	private void ejemploUsuarioComentariosZipWith() {
		// Otra manera de hacer lo mismo que el ejemplo del método "ejemploUsuarioComentariosZipWithForma2()"
		
		// Creamos un flujo reactivo Mono de tipo Usuario
		// El método "fromCallable" nos permite crear un flujo reactivo Mono a partir de una función lambda que devuelva el dato para dicho flujo
		Mono<Usuario> usuarioMono = Mono.fromCallable(() -> new Usuario("John","Doe"));
		// Creamos un flujo reactivo Mono de tipo Comentarios
		// El método "fromCallable" nos permite crear un flujo reactivo Mono a partir de una función lambda que devuelva el dato para dicho flujo
		Mono<Comentarios> usuarioComentariosMono = Mono.fromCallable(() -> {
			Comentarios comentarios = new Comentarios();
			comentarios.addComentario("Hola Pepe, qué tal!");
			comentarios.addComentario("Mañana voy a la playa!");
			comentarios.addComentario("Estoy tomando el curso de spring con reactor");
			return comentarios;
		});
		
		// Combinamos los dos flujos reactivos Mono anteriores con el operador "zipWith" para obtener un flujo reactivo Mono de tipo UsuarioComentarios
		// Para ello,sobre uno de los flujos reactivos que queremos combinar, aplicamos el operador "zipWith" y le pasamos como argumentos el otro flujo reactivo que se desea combinar y una función lambda que reciba dos parámetros que representen cada elemento de ambos flujos reactivos y se indique el procedimiento para obtener cada elemento del flujo reactivo de salida que combine los elementos pasados como parámetros a dicha función lambda
		// En este caso,obtenemos un flujo reactivo Mono de salida de tipo UsuarioComentarios,al que nos suscribimos después, que combina cada elemento de los flujos reactivos "usuarioMono" y "usuarioComentariosMono"
		usuarioMono.zipWith(usuarioComentariosMono,(u,c) -> new UsuarioComentarios(u,c))
		// Finalmente, nos suscribimos al Observable Mono de tipo UsuarioComentarios, creando el Observador, y escribimos en el log a modo de información los datos del usuario y sus comentarios
		.subscribe(uc -> log.info(uc.toString()));		
	}
	
	private void ejemploUsuarioComentariosZipWithForma2() {
		// Otra manera de hacer lo mismo que el ejemplo del método "ejemploUsuarioComentariosZipWith()"
		
		// Creamos un flujo reactivo Mono de tipo Usuario
		// El método "fromCallable" nos permite crear un flujo reactivo Mono a partir de una función lambda que devuelva el dato para dicho flujo
		Mono<Usuario> usuarioMono = Mono.fromCallable(() -> new Usuario("John","Doe"));
		// Creamos un flujo reactivo Mono de tipo Comentarios
		// El método "fromCallable" nos permite crear un flujo reactivo Mono a partir de una función lambda que devuelva el dato para dicho flujo
		Mono<Comentarios> usuarioComentariosMono = Mono.fromCallable(() -> {
			Comentarios comentarios = new Comentarios();
			comentarios.addComentario("Hola Pepe, qué tal!");
			comentarios.addComentario("Mañana voy a la playa!");
			comentarios.addComentario("Estoy tomando el curso de spring con reactor");
			return comentarios;
		});
		
		// A diferencia del ejemplo indicado en el método "ejemploUsuarioComentariosZipWith()",otra manera de combinar los dos flujos reactivos Mono anteriores con el operador "zipWith" para obtener un flujo reactivo Mono de tipo UsuarioComentarios es la siguiente:
		// Sobre uno de los flujos reactivos que queremos combinar, aplicamos el operador "zipWith" y le pasamos como argumento únicamente el otro flujo reactivo que se desea combinar.Esto creará un flujo reactivo con elementos de tipo Tuple(una tupla que combina los elementos de ambos flujos reactivos)
		// Sobre este flujo reactivo de salida de tipo Tuple,podemos aplicar el operador "map" para transformalo en otro flujo reactivo con elementos del tipo que nos interesa y que representa la combinación.En este caso,el tipo que representa la combinación es UsuarioComentarios
		usuarioMono.zipWith(usuarioComentariosMono)
		.map(tupla -> {
			Usuario u = tupla.getT1(); // Obtenemos el elemento de tipo Usuario de la tupla
			Comentarios c = tupla.getT2(); // Obtenemos el elemento de tipo Comentarios de la tupla
			return new UsuarioComentarios(u,c); // Creamos el elemento UsuarioComentarios pasándole los dos elementos anteriores para establecer la combinación
		})
		// Finalmente, nos suscribimos al Observable Mono de tipo UsuarioComentarios, creando el Observador, y escribimos en el log a modo de información los datos del usuario y sus comentarios
		.subscribe(uc -> log.info(uc.toString()));	
	}
	
	private void ejemploZipWithRangos() {
		// Creamos un flujo reactivo Flux con los números 1,2,3,4
		// Después,aplicamos el operador "map" al flujo anterior para obtener otro flujo Flux con la multiplicación por 2 de sus elementos numéricos
		// Este flujo Flux con el resultado de la multiplicación por 2 lo combinamos con otro flujo Flux creado con el operador "range" que genera una secuencia de números del 0 al 4
		// La combinación de ambos flujos reactivos Flux consiste en la creación de otro flujo Flux con elementos de tipo String con el texto "Primer Flux:%d - Segundo Flux:%d"
		// Por último,nos suscribimos,creándose el Observador,a este último flujo reactivo con la combinación y escribimos en el log a modo de información cada elemento de dicho flujo
		Flux.just(1,2,3,4)
		.map(i -> i*2)
		.zipWith(Flux.range(0,4),(uno,dos) -> String.format("Primer Flux:%d - Segundo Flux:%d",uno,dos))
		.subscribe(texto -> log.info(texto));
	}
	
	private void ejemploZipWithInterval() {
		// Otra manera de hacer lo mismo que el ejemplo del método "ejemploDelayElements()"
		
		// Creamos un flujo reactivo Flux con un rango de números desde el 1 hasta el 12 con el operador "range"
		Flux<Integer> rango = Flux.range(1,12);
		// Creamos un flujo reactivo Flux con intervalos de 1 segundo de tiempo mediante el operador "interval"
		Flux<Long> retraso = Flux.interval(Duration.ofSeconds(1));
		// Combinamos ambos flujos reactivos Flux anteriores y con el operador "map" transformamos el flujo reactivo Flux de elementos tipo Tuple(elementos resultantes de la combinación) en otro flujo reactivo Flux con los elementos del primer valor de cada tupla que se corresponde con cada número del flujo reactivo Flux del rango.No nos interesa meter en el flujo reactivo Flux final los intervalos de tiempo
		// Al combinar un flujo reactivo con otro que son intervalos de tiempo,se aplicarán las demoras o retrasos en las acciones que se indiquen para el flujo reactivo resultante de la combinación
		rango.zipWith(retraso)
		.map(tupla -> tupla.getT1())
		// Por último,indicamos que se ejecute la tarea de escribir  cada elemento del flujo reactivo resultante de la combinación en el log a modo de información
		.doOnNext(i -> log.info(i.toString()))
		// Como estamos trabajando con intervalos,para poder visualizar los números por consola cada 1 segundo de retraso,usamos el método "blockLast()",que se suscribe al flujo reactivo pero bloquea el hilo o thread cuando se ha emitido el último elemnto del flujo para que no se quede en segundo plano(el thread)
		// Usar este método no es adecuado para este tipo de aplicaciones donde se utilizan flujos reactivos que por definición son no bloqueantes.Lo suyo es usar,como siempre,el método "subscribe()",que se suscribe al flujo reactivo sin bloquear el hilo o thread.Pero para visualizar los números del rango por consola,lo dejamos con este método "blockLast()"
		//.subscribe();
		.blockLast();
	}
	
	public void ejemploDelayElements() {
		// Otra manera de hacer lo mismo que el ejemplo del método "ejemploZipWithInterval()"
		
		// Creamos un flujo reactivo Flux con un rango de números desde el 1 hasta el 12 con el operador "range"
		// Aplicamos a los elementos de este flujo un intervalo de 1 segundo con el operador "delayElements"
		// Indicamos que se aplique la tarea de escribir cada elemento del flujo reactivo en el log a modo de información.Esto se hará cada 1 segundo de intervalo para cada elemento del flujo debido al operador "delayElements"
		Flux<Integer> rango = Flux.range(1,12)
				.delayElements(Duration.ofSeconds(1))
				.doOnNext(i -> log.info(i.toString()));
		// Como estamos trabajando con intervalos,para poder visualizar los números por consola cada 1 segundo de retraso,usamos el método "blockLast()",que se suscribe al flujo reactivo pero bloquea el hilo o thread cuando se ha emitido el último elemnto del flujo para que no se quede en segundo plano(el thread)
		// Usar este método no es adecuado para este tipo de aplicaciones donde se utilizan flujos reactivos que por definición son no bloqueantes.Lo suyo es usar,como siempre,el método "subscribe()",que se suscribe al flujo reactivo sin bloquear el hilo o thread.Pero para visualizar los números del rango por consola,lo dejamos con este método "blockLast()"
		//rango.subscribe();
		rango.blockLast();
	}
	
	public void ejemploIntervalInfinito() throws InterruptedException {
		// Como estamos trabajando con intervalos,para poder visualizar los elementos por consola cada 1 segundo de retraso y el hilo o thread no se quede en segundo plano,en lugar de usar el método "blockLast()" como sí hicimos en otros ejemplos de flujos reactivos con intervalos",vamos a usar un contador de tipo CountDownLatch
		// Creamos un contador inicializado en 1
		CountDownLatch latch = new CountDownLatch(1);
		// Creamos un flujo reactivo Flux infinito de intervalos cada 1 segundo
		Flux.interval(Duration.ofSeconds(1))
		// Con el operador "flatMap" transformamos el flujo reactivo anterior por otro que va a tener como elementos otros flujos reactivos con el valor de cada intervalo o con una excepción de tipo InterruptedException para el intervalo número 8
		// Lo de la excepción se hace para poner fin a la emisión de elementos en el flujo de intervalos infinito cuando el valor del intervalo sea 8
		// Como al final tenemos un flujo reactivo cuyos elementos son otros flujos reactivos,tenemos que aplanarlo con "flatMap" para que nos quede un flujo reactivo de elementos no reactivos
		.flatMap(i -> {
			if(i == 8)
				return Flux.error(new InterruptedException("Solo hasta 8!"));
			else
				return Flux.just(i);
		})
		// Aquí,con el operador "map" transformamos el flujo reactivo anterior ,resultante de aplicar "flatMap", a otro flujo con elementos de tipo String con el texto "Hola " + el valor del intervalo
		.map(i -> "Hola " + i)
		// El operador "retry" nos permite volver a ejecutar las tareas del flujo reactivo tantas veces como se indique en su parámtero cuando ocurre un fallo o excepción en este
		.retry(2)
		// Cuando ocurra el evento "OnComplete",es decir,cuando el flujo reactivo haya finalizado de emitir elementos,el contador "latch" se baja poniéndose a 0(se inicializó previamente a 1)
		.doOnComplete(() -> latch.countDown())
		// Nos suscribimos al flujo reactivo,creándose el Observador, y realizamos la tarea de escribir en el log a modo de información cada elemento de dicho flujo
		// Debido a que uno de los elemento del flujo va a ser una excepción de interrupción,la gestionamos con la implementación del segundo argumento del método "subscribe()",que consite en escribir el mensaje de error de dicha excepción en el log a modo de error
		.subscribe(i -> log.info(i),error -> log.error(error.getMessage()));
		// Se ha sustituido el uso del método "blockLast()",que se suscribe al flujo y bloquea el hilo o thread, por el contador "latch"
		//.blockLast();
		
		// La ejecución se para aquí hasta que el contador "latch" se ponga a 0
		latch.await();
	}
	
	public void ejemploIntervalDesdeCreate() {
		// Creamos un flujo reactivo Flux con el operador "create"
		// El operador "create" recibe una función lambda donde hay que implementar el procedimiento para obtener los elementos de este flujo que se van a emitir
		Flux.create(emitter -> {
			// Como queremos crear un flujo reactivo que emita intervalos cada cierto tiempo,vamos a usar la clase Timer de Java
			Timer timer = new Timer();
			// Con el método "schedule()" implementamos la tarea que se va a ejecutar cada cierto tiempo
			// Esta tarea va a ser implementada por el método "run()" de la clase abstracta TimerTask y consiste en un contador inicializado a 0 que se va a incrementar en 1 cada cierto tiempo
			// Para no hacerlo infinito,hemos puesto la condición de que si el contador alcanza el valor 10,el timer se cancele y el flujo reactivo deje de emitir elementos invocando al método "complete()"
			timer.schedule(new TimerTask(){
				
				private Integer contador = 0; // Inicializamos el contador a 0

				@Override
				public void run() {
					emitter.next(++contador); // Incrementamos el contador en un 1 y se lo pasamos como un elemento al flujo reactivo mediante el método "next()"
					
					// Caso de finalización correcta del flujo reactivo Flux
					if(contador == 10) { // Si se alcanza el valor 10 del contador,cancelamos el timer y completamos el flujo reactivo finalizando la emisión de elementos 
						timer.cancel();
						emitter.complete();
					}
					
					// Caso de finalización errónea del flujo reactivo Flux
					if(contador == 5) { // Si se alcanza el valor 5 del contador,cancelamos el timer y emitimos un error como una excepción de tipo InterruptedException desde el flujo reactivo
						timer.cancel();
						emitter.error(new InterruptedException("Error, se ha interrumpido el flux en 5!"));
					}
					
				}
				
			},1000,1000); // La tarea implementada en el método "run()" se va a ejecutar pasados 1 segundo(segundo argumento del método "schedule()" en milisengundos)desde la invocación y se va a repetir cada 1 segundo(tercer argumento del método "schedule()" en milisengundos)
		})
		// Por último,nos suscribimos a este flujo reactivo,creándose el Observador",para que comienza la ejecución de sus tareas
		// Cada vez que se notifique la emisión de un elemento al Observador,va a realizar la tarea de escribir en el log a modo de información cada elemento emitido por el flujo reactivo,que van a ser los valores del contador cada 1 segundo de tiempo
		// Si se ha notificado de un error en el flujo reactivo como una excepción,el Observador va a realizar la tarea de escribir en el log a modo de error el mensaje de la excepción ocurrida(segundo argumento del método "subscribe()")
		// Y si se ha notificado la finalización de la emisión de elementos por parte del flujo reactivo,el Observador va a realizar la tarea de escribir en el log a modo de información el texto "Hemos terminado!"(tercer argumento del método "subscribe()")
		.subscribe(next -> log.info(next.toString()),error -> log.error(error.getMessage()),() -> log.info("Hemos terminado!"));
	}
	
	public void ejemploContrapresionSubscriber() {
		// Otra manera de hacer lo mismo que el ejemplo del método "ejemploContrapresionLimitRate()"
		
		// Creamos un flujo reactivo Flux generado con el rango de enteros que van desde el 1 hasta el 10 mediante el operador "range"
		Flux.range(1,10)
		// Invocando a este método,se van a mostrar por consola las trazas de los eventos por los que pasa el flujo reactivo una vez que se lanza la ejecución de sus tareas cuando un Observador se suscribe
		.log()
		// Por defecto,cuando se invoca al método "subscribe()" para suscribirnos al flujo reactivo creándose el Observador,la emisión y notificación de los elementos de dicho flujo reactivo de realizan a la vez
		// Pero si la máquina o el equipo tiene recursos o capacidades limitados y no puede soportar el tratamiento de todos los elementos emitidos y notificados a la vez,para evitar que se produzcan cuellos de botella, podemos manejar la contrapresión producida para que dicha emisión de elementos se realice en bloques de un tamaño determinado configurado por nosotros en vez de realizarse todos a la vez
		// Para porder manejar la contrapresión,una alternativa es pasarle al método "subscribe()" nuestra implementado de la interfaz "Subscriber"
		// La interfaz Subscriber es genérica y tenemos que indicarle de qué tipo son los elementos que se van a emitir en el flujo reactivo.En este caso,el tipo de los elementos es Integer(enteros)
		.subscribe(new Subscriber<Integer>() {
			
			private Subscription s;
			private static final int LIMITE = 5; // El Observador suscrito va a solicitar al flujo reactivo la emisión de la cantidad indicada en LIMITE de elementos a la vez para ser procesados
			private Integer consumido = 0; // Contador de elementos consumidos.Son aquellos que se emiten con el evento OnNext

			@Override
			public void onSubscribe(Subscription s) {
				this.s = s;
				// El método "request()" es el encargado de solicitar la cantidad de elementos al flujo reactivo por parte del Observador
				// Pasándole a este método el valor Long.MAX_VALUE, estamos indicando que se emitan todos los elementos del flujo a la vez,que es el valor por defecto de un Observador cuando se suscribe
				// s.request(Long.MAX_VALUE);
				// Pasándole a este método el valor de la constante LIMITE, estamos indicando que se emitan la cantidad indicada en LIMITE de elementos del flujo a la vez
				s.request(LIMITE);
				
			}

			// Tenemos que modificar el tipo de dato del parámetro que recibe este método por el tipo de los elementos que van aser emitidos por el flujo reactivo.En este caso,el tipo de los elementos es Integer(enteros) 
			// Este método es el encargado de realizar una determinada tarea cuando se emite cada elemento del flujo con el evento OnNext
			// En este caso,hemos indicado que se ejecute la tarea de escribir en el log a modo de información el valor de cada elemento,que va a ser un entero que va desde el 1 hasta el 10
			@Override
			public void onNext(Integer t) {
				log.info(t.toString());
				consumido++; // Si se ejecuta este método,es porque se ha producido el evento OnNext y,por lo tanto, el flujo reactivo ha emitido un nuevo elemento que ha sido consumido por el Observador suscrito.Entonces,incrementamos nuestro contador de elementos consumidos en una unidad
				if(consumido == LIMITE) { // Si nuestro contado de elementos consumidos alcanza nuestro límite establecido,reiniciamos el contador a 0 para que el Observador suscrito vuelva a solicitar otra cantidad indicada en LIMITE de elementos al flujo reactivo
					consumido = 0;
					s.request(LIMITE);
				}
				
			}

			@Override
			public void onError(Throwable t) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onComplete() {
				// TODO Auto-generated method stub
				
			}
			
		});
	}
	
	public void ejemploContrapresionLimitRate() {
		// Otra manera de hacer lo mismo que el ejemplo del método "ejemploContrapresionSubscriber()"
		
		// Creamos un flujo reactivo Flux generado con el rango de enteros que van desde el 1 hasta el 10 mediante el operador "range"
		Flux.range(1,10)
		// Invocando a este método,se van a mostrar por consola las trazas de los eventos por los que pasa el flujo reactivo una vez que se lanza la ejecución de sus tareas cuando un Observador se suscribe
		.log()
		// Por defecto,cuando se invoca al método "subscribe()" para suscribirnos al flujo reactivo creándose el Observador,la emisión y notificación de los elementos de dicho flujo reactivo de realizan a la vez
		// Pero si la máquina o el equipo tiene recursos o capacidades limitados y no puede soportar el tratamiento de todos los elementos emitidos y notificados a la vez,para evitar que se produzcan cuellos de botella, podemos manejar la contrapresión producida para que dicha emisión de elementos se realice en bloques de un tamaño determinado configurado por nosotros en vez de realizarse todos a la vez
		// Para porder manejar la contrapresión,una alternativa es usar el operador "limitRange" pasándole el número de elementos que debe solicitar el Observador suscrito al flujo reactivo
		.limitRate(2)
		// Por último, se suscribe al flujo reactivo creándose el Observador para que comience la emisión y consumo de elementos de dicho flujo
		.subscribe();
	}

}
