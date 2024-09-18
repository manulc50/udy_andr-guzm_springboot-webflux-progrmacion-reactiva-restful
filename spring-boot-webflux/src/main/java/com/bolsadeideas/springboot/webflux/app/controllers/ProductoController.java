package com.bolsadeideas.springboot.webflux.app.controllers;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Date;
import java.util.UUID;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;

import com.bolsadeideas.springboot.webflux.app.models.documents.Categoria;
import com.bolsadeideas.springboot.webflux.app.models.documents.Producto;
import com.bolsadeideas.springboot.webflux.app.models.services.ProductoService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

// Nuestras vistas implementadas con la varsión reactiva de Thymeleaf automáticamente se suscriben,creándose los Observadores,a los distintos flujos reactivos devueltos por los distintos métodos handler de este controlador

// Es necesario salvar la instancia "producto" como un atributo de la sesion para no perder la informacion hasta que se salve en la base de datos durante el proceso de edición.Esto es asi porque es necesario pasar por un par de controladores y vistas durante el procedimiento de edición
// El nombre de la instancia que tenemos que indicar en esta anotación tiene que ser el mismo que le estamos pasando a la vista mediante el método "addAttribute" del objeto Model usado en el método handler correspondiente a la edición de un producto
@SessionAttributes("producto")
//Anotación para registar esta clase como un controlador de Spring.Esto conlleva a que se cree un bean y se almacene en su memoria o contenedor
@Controller
public class ProductoController {

	// Habilitamos el uso del log para esta clase
	private static final Logger log = LoggerFactory.getLogger(ProductoController.class);
	
	// Recuperamos de la memoria o contendor de Spring el bean que implementa la interfaz "ProductoService".Esta interfaz es implementada por la clase "ProductoServiceImpl"
	@Autowired
	private ProductoService productoService; // Este bean representa la capa Servicio para la clase entidad "Producto" que realiza operaciones CRUD en la base de datos a través de la capa Dao
	
	// Obtenemos del archivo de configuración application.properties el valor de la propiedad "config.uploads.path"
	@Value("${config.uploads.path}")
	private String path;
	
	// Método que obtiene todas las categorías de la base de datos en un flujo reactivo Flux y lo devuelve asociado a un atributo de la vista llamado "categorias" para poder usarlo en dicha vista
	// La anotación @ModelAtrribute nos permite asociar los datos devueltos por un método a un atributo, cuyo nombre se indica en dicha anotación, para pasar dichos datos a la vista.En este caso,estamos asociando el flujo reactivo Flux con las categorías como elementos al atributo de la vista "categorias"
	// Esto es útil cuando es necesario hacer una precarga de datos en una vista para rellenar,por ejemplo,una lista desplegable de un formulario,un checkbox,...,o lo que sea
	@ModelAttribute("categorias")
	public Flux<Categoria> categorias(){
		return productoService.findAllCategoria(); // Recuperamos de la capa Service mediante el bean 'productoService' el listado de categorias como un stream reactivo de tipo Flux(varios items.Si fuese sólo un item,sería de tipo Mono)
	}
	
	// Método handler que responde las peticiones http de tipo Get para la ruta "/uploads/img/" + nombre_foto + extension_foto
	// La expresión ":.+" es una expresión regular que nos permite especificar la extensión del archivo de la imagen(.jpg, .png, etc...)
	// Con la anotación @PathVariable recuperamos la variable "nombreFoto" de la ruta o path y la mapeamos con el argumento "nombre" de este método handler.Cuando el nombre de la variable es distinto al nombre del argumento,para realizar el mapeo, hay que usar el atributo "name", o su alias "value", de la anotación con el nombre de la variable de la ruta o path.Si son iguales,no hace falta usar dicho atributo
	@GetMapping("/uploads/img/{nombreFoto:.+}")
	public Mono<ResponseEntity<Resource>> verFoto(@PathVariable(name="nombreFoto") String nombre) throws MalformedURLException{
		// Obtenemos la ruta absoluta donde se encuenta el archivo de la imagen dentro del directorio de subidas
		Path ruta = Paths.get(path).resolve(nombre).toAbsolutePath();
		
		// Creamos un recurso, que va a ser una imagen, a partir del URI de la ruta anterior
		Resource imagen = new UrlResource(ruta.toUri());
		
		// Creamos y devolvemos un flujo reactivo Mono con un elemento de tipo ResponseEntity que contendrá la respuesta a la petición http
		return Mono.just(
				ResponseEntity.ok() // Indicamos que el código de estado de la respuesta es OK(200)
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + imagen.getFilename() + "\"") // Añadimos la propiedad "Content-Disposition" a la cabecera de la respuesta con el valor "attachment; filename=" + nombre_imagen(entre comillas dobles) para indicar que en el cuerpo de esa respuesta se adjunta un archivo de imagen cuyo nombre es "imagen.getFilename()"
				.body(imagen) // Por último, establecemos la imagen en el cuerpo de la respuesta http	
				);
	}
	
	// Método handler que responde las peticiones http de tipo Get para la ruta "/ver/" + id_producto
	// Con la anotación @PathVariable recuperamos la variable "id" de la ruta o path y la mapeamos con el argumento "id" de este método handler.Cuando el nombre de la variable es distinto al nombre del argumento,para realizar el mapeo, hay que usar el atributo "name", o su alias "value", de la anotación con el nombre de la variable de la ruta o path.Si son iguales,no hace falta usar dicho atributo
	@GetMapping("/ver/{id}")
	public Mono<String> ver(Model model,@PathVariable String id){
		return productoService.findById(id) // Recuperamos de la base datos el producto como un flujo reactivo Mono a partir del id recuperado de la url o path.Esto lo hacemos mediante el bean 'productoService'
				// Con el método "doOnNext()" realizamos las siguientes tareas sobre el producto del flujo reactivo Mono
				.doOnNext(p -> {
					model.addAttribute("titulo","Detalle del Producto"); // Asociamos al atributo 'titulo' el texto 'Detalle del Producto' y se lo pasamos a la vista
					model.addAttribute("producto",p); // Asociamos al atributo 'producto' la instancia con los datos del producto recuperado de la base de datos y se lo pasamos a la vista
				})
				// Con el método "switchIfEmpty()",si el flujo reactivo Mono anterior tiene como elemento un elemento vacío o nulo porque no se ha localizado ningún producto a partir del id obtenido de la url o path,creamos un flujo reactivo Mono con un elemento Producto vacío
				.switchIfEmpty(Mono.just(new Producto())) // Otra opción equivalente sería usar el método "defaultIfEmpty" pasándole directamente una instancia de un producto
				// Con el operador "flatMap" transformamos el flujo reactivo Mono en otro flujo reactivo Mono cuyo elemento va a ser a su vez otro flujo reactivo Mono con una excepción de interrupción,en caso de que no exista el id del producto,o bien,otro flujo reactivo Mono con el producto,en caso contrario.Como al final vamos a tener un flujo reactivo Mono con otro flujo reactivo Mono a su vez,con este operador,en lugar de "map",se va a realizar un procedimiento de aplanamiento para que definitivamente nos quede un único flujo reactivo Mono de un elemento no reactivo
				// Esto es útil para gestionar errores que se puedan producir en un flujo reactivo
				.flatMap(p -> {
					if(p.getId() == null) // Si el producto no tiene id(se ha ejecutado antes la sentencia "defaultIfEmpty(new Producto())"),creamos un flujo reactivo Mono con una excepción de tipo "InterruptedException" como elemento y el mensaje "No existe el producto"
						return Mono.error(new InterruptedException("No existe el producto"));
					return Mono.just(p); // En caso contrario(no se ha ejecutado antes la sentencia "defaultIfEmpty(new Producto())"),creamos un flujo reactivo Mono con el producto localizado como elemento
				})
				// Con el método "then" creamos otro flujo reactivo Mono de tipo String con el nombre de la vista "ver", y lo devuelve
				// El método "then" espera a que el flujo reactivo original termine de emitir su único elemento para comenzar a procesar un nuevo flujo reactivo Mono, es decir, comienza a procesar un nuevo flujo reactivo Mono cuando se produce el evento OnComplete del flujo reactivo original
    			// Al final, el único elemento del flujo reactivo original es ignorado y se tiene en cuenta el elemento del nuevo flujo reactivo Mono
				// "thenMany" se usa para crear flujos reactivos Flux y "then" para crear flujos reactivos Mono cuando el flujo reactivo original ha finalizado de emitir su último elemento
				.then(Mono.just("ver"))
				// Con el método "onErrorResume()",si el flujo reactivo Mono emite un elemento de error,como en nuestro caso,una excepción de interrupción,se devuelve de manera reactiva,con un flujo reactivo Mono,un String con la redirección a la ruta o path "/listar" seguido de un parámetro de error con el mensaje "no existe el producto"(No puede haber espacios en una url o ruta,así que tenemos que usar el operador "+" para referirnos a los espacios)
				// Con "redirect:" no indicamos que se vaya directamente a una vista,sino que estamos indicando que se ejecute el método handler asociado a la ruta indicada junto a "redirect:".En este caso,estamos indicando que se ejecute el método handler "listar()" que está asociado a la ruta "/listar"
				.onErrorResume(err -> Mono.just("redirect:/listar?error=no+existe+el+producto"));
	}
	
	// Método handler que responde las peticiones http de tipo Get para las rutas "/listar" y "/"(inicio)
	@GetMapping({"/listar","/"})
	public Mono<String> listar(Model model){ // El objeto Model es necesario para pasar información a la vista asociada a la ruta "/listar"
		Flux<Producto> productos = productoService.findAllConNombreUpperCase(); // Recuperamos de la capa Service mediante el bean 'productoService' el listado de productos con sus nombres en mayúscula como un stream reactivo de tipo Flux(varios items.Si fuese sólo un item,sería de tipo Mono)
		// Nos suscribimos a este flujo reactivo,creándose un Observador,para realizar la tarea de escribir en el log a modo de información el nombre de cada producto emitido por este flujo reactivo
		productos.subscribe(prod -> log.info(prod.getNombre()));
		
		model.addAttribute("productos",productos); // Asociamos al atributo 'productos' el flujo reactivo Flux con los productos recuperado anteriormente de la capa Service "productoService" y se lo pasamos a la vista
		model.addAttribute("titulo","Listado de productos"); // Asociamos al atributo 'titulo' el texto 'Listado de productos' y se lo pasamos a la vista
		
		return Mono.just("listar"); // Devolvemos,de manera reactiva, con un flujo reactivo Mono el nombre de la vista a mostrar
	}
	
	// Método handler que responde las peticiones http de tipo Get para las rutas "/form"
	@GetMapping("/form")
	public Mono<String> crear(Model model){ // El objeto Model es necesario para pasar información a la vista asociada a la ruta "/form"
		
		model.addAttribute("producto",new Producto()); // Asociamos al atributo 'producto' una nueva instancia de un producto que va a almacenar los nuevos datos de un producto introducidos por el usuario en la vista a través de un formulario
		model.addAttribute("titulo","Formulario de producto"); // Asociamos al atributo 'titulo' el texto 'Formulario de producto' y se lo pasamos a la vista
		model.addAttribute("operacion","Crear"); // Asociamos al atributo 'operacion' el texto 'Crear' y se lo pasamos a la vista
		
		return Mono.just("form"); // Devolvemos,de manera reactiva, con un flujo reactivo Mono el nombre de la vista a mostrar
	}
	
	// Método handler que responde las peticiones http de tipo Post para las rutas "/form"
	// Usamos SessionStatus status para que la instancia 'producto' deje de ser un atributo de la sesion justo despues de guardar su informacion en la base de datos
	// Con la anaotación @Valid habilitamos las validaciones de los campos en la entidad Producto y en BindingResult tenemos el resultado de la validacion 
	// La anotación @RequestPart es necesaria para obtener de la petición http un archivo. Para poder recuperar el archivo de la petición http, el nombre del parámetro de entrada de este método de tipo FilePart tiene que ser igual que el nombre del input de tipo file del formulario de la vista, es decir, el parámetro de entrada se llama "file" porque el input de tipo file de la vista también se llama "file"
	// Si el nombre del parámetro de entrada del fichero se llamada distinto al nombre del input de tipo file de la vista, tenemos que indicar el nombre del input de tipo file de la vista en el atributo "name" de la anotación @RequestPart. Pero, como en este caso se llaman igual, se puede omitir dicho atributo en esa anotación
	// La instancia BindingResult siempre tiene que ir a continuación de la entidad que se va a validar con @Valid para que no haya problemas
	// El objeto "producto" es el que recibimos del formulario con los datos del producto introducidos por el usuario
	@PostMapping("/form")
	public Mono<String> guardar(@Valid Producto producto,BindingResult result,@RequestPart FilePart file,SessionStatus status,Model model){
		
		// Si hay errores en las validaciones...
		if (result.hasErrors()) {
			model.addAttribute("titulo","Errores en el formulario de productos"); // Asociamos al atributo 'titulo' el texto 'Errores en el formulario de productos' y se lo pasamos a la vista
			model.addAttribute("operacion","Guardar"); // Asociamos al atributo 'operacion' el texto 'Guardar' y se lo pasamos a la vista
			return Mono.just("form"); // Devolvemos,de manera reactiva, con un flujo reactivo Mono el nombre de la vista para volver al formulario de productos
		}
		// En caso contrario...
		else {
			// Cuando se invoque este método, significa que ya vamos a proceder a persistir o editar un producto en la base de datos,y como ya nos hemos asegurado de que en este punto vamos a tener todos los datos del producto, podemos liberar la instancia "producto" de ser un atributo de sesión
			status.setComplete();
			
			// Como solo tenemos el id de la categoría del producto y necesitamos su nombre,antes de guardar el producto en la base de datos,hay que localizar y recuperar de la base de datos dicha categoría a partir de su id mediante el bean 'productoService'
			return productoService.findCategoriaById(producto.getCategoria().getId())
					// Con el operador flatMap,transformamos el flujo reactivo Mono anterior con la categoría como elemento en otro flujo reactivo Mono cuyo elemento va a ser a su vez otro flujo reactivo Mono con los datos del producto guardado en la base de datos.Como al final tenemos un flujo reactivo Mono con otro flujo reactivo Mono a su vez,con este operador,en lugar de "map",se va a realizar un procedimiento de aplanamiento para que definitivamente nos quede un único flujo reactivo Mono de un elemento no reactivo
					.flatMap(c -> {
						// Si la fecha del producto no se ha introducido en el formulario y,por lo tanto,es nulo,asignamos a esta fecha la fecha actual del sistema
						if(producto.getCreateAt() == null)
							producto.setCreateAt(new Date());
						
						// Como la subida de una imagen es opcional, verificamos si viene en la petición http y, en caso afirmativo, establecemos el nombre de la foto en el producto
						// El nombre de la foto tiene que ser único para cada producto y, por esta razón, usamos la expresión "UUID.randomUUID().toString()" para generar un número aleatorio único que se concatenará al nombre de la foto para que dicho nombre sea también único
						if(!file.filename().isEmpty())
							producto.setFoto(UUID.randomUUID().toString() + "-" + file.filename()
							.replace(" ","") // Con el método "replace", nos aseguramos de que el nombre de la foto no tenga espacios
							// Estos 2 reemplazos son para que la subida de la imagen sea compatible con los navegadores Internet Explorer 10 y Edge
							.replace(":","")
							.replace("\\","")
							);
						// Asignamos la categoría recuperada de la base de datos al producto que se va a guardar
						producto.setCategoria(c);
						
						// Persistimos o editamos el producto recibido del formulario en la base de datos mediante el bean 'productoService'.Si el producto aún no tiene su id generado,se trata de una nueva creación y,entonces, el método "save()" va a persistirlo.Sin embargo,si el producto sí tiene el id generado,se trata de una edición y,en este caso,el método "save()" va a editarlo en la base de datos
						return productoService.save(producto);
					})
					// Con el método "doOnNext()" realizamos las siguientes tareas...
					.doOnNext(p -> {
						// Escribimos en el log a modo de información el nombre y el id de la categoría asignada al producto
						log.info("Categoría asignada: " + p.getCategoria().getNombre() + " Id:" + p.getCategoria().getId());
						// Escribimos en el log a modo de información el nombre y el id del producto que se acaba de persistir en la base de datos
						log.info("Producto guardado: " + p.getNombre() + " Id:" + p.getId());
					})
					.flatMap(p -> {
						// Como la subida de una imagen es opcional, verificamos si viene en la petición http
						if(!file.filename().isEmpty())
							// Realiza la subida de la imagen del producto en la ruta del directorio de subidas establecido en "path"
							return file.transferTo(new File(path + p.getFoto())); // Este método devuelve un flujo reactivo Mono con un elemento vacío o Void
						// Si no viene ninguna foto en la petición http, no hacemos nada devolviendo un flujo reactivo Mono vacío
						else
							return Mono.empty();
					})
					// Con el método "thenReturn" creamos otro flujo reactivo Mono de tipo String con la redirección a la ruta "/listar" seguido del parámetro "success" con el valor "producto guardado con éxito", y lo devuelve
					// El método "then" espera a que el flujo reactivo original termine de emitir su único elemento para comenzar a procesar un nuevo flujo reactivo Mono, es decir, comienza a procesar un nuevo flujo reactivo Mono cuando se produce el evento OnComplete del flujo reactivo original
	    			// Al final, el único elemento del flujo reactivo original es ignorado y se tiene en cuenta el elemento del nuevo flujo reactivo Mono
					// Con "redirect:" no indicamos que se vaya directamente a una vista,sino que estamos indicando que se ejecute el método handler asociado a la ruta indicada junto a "redirect:".En este caso,estamos indicando que se ejecute el método handler "listar()" que está asociado a la ruta "/listar"
					.thenReturn("redirect:/listar?success=producto+guardado+con+éxito"); // Otra opción equivalente sería poner la sentencia ".then(Mono.just("redirect:/listar?success=producto+guardado+con+éxito"))"
	
		}
	}
	
	// Método handler que responde las peticiones http de tipo Get para las rutas "/form" + id_producto
	// Con la anotación @PathVariable recuperamos la variable "id" de la ruta o path y la mapeamos con el argumento "id" de este método handler.Cuando el nombre de la variable es distinto al nombre del argumento,para realizar el mapeo, hay que usar el atributo "name", o su alias "value", de la anotación con el nombre de la variable de la ruta o path.Si son iguales,no hace falta usar dicho atributo
	@GetMapping("/form/{id}")
	public Mono<String> editar(@PathVariable String id,Model model){ // El objeto Model es necesario para pasar información a la vista asociada a la ruta "/form"
		Mono<Producto> productoMono = productoService.findById(id) // Recuperamos de la base de datos el producto como un flujo reactivo Mono a partir del id recuperado de la url o path.Esto lo hacemos mediante el bean 'productoService'
				// Con el método "doOnNext()" realizamos la tarea de escribir en el log a modo de información del nombre del producto que acabamos de recuperar de la base de datos
				.doOnNext(p -> log.info("Producto localizado:" + p.getNombre()))
				// Con el método "defaultIfEmpty()",si el flujo reactivo Mono anterior tiene como elemento un elemento vacío o nulo porque no se ha localizado ningún producto a partir del id obtenido de la url o path,creamos un flujo reactivo Mono con un elemento Producto vacío
				.defaultIfEmpty(new Producto()); // Otra opción equivalente sería usar el método "switchIfEmpty" pasándole un flujo reactivo Mono con una instancia de un producto
		
		model.addAttribute("producto",productoMono); // Asociamos al atributo 'producto' un flujo reactivo Mono con el producto recuperado de la base de datos como elemento y se lo pasamos a la vista para que se carguen sus datos en el formulario de edición
		model.addAttribute("titulo","Editar Producto"); // Asociamos al atributo 'titulo' el texto 'Editar Producto' y se lo pasamos a la vista
		model.addAttribute("operacion","Editar"); // Asociamos al atributo 'operacion' el texto 'Editar' y se lo pasamos a la vista
		
		return Mono.just("form"); // Devolvemos,de manera reactiva, con un flujo reactivo Mono el nombre de la vista a mostrar
	}
	
	// Método handler que responde las peticiones http de tipo Get para las rutas "/form-v2" + id_producto
	// Con la anotación @PathVariable recuperamos la variable "id" de la ruta o path y la mapeamos con el argumento "id" de este método handler.Cuando el nombre de la variable es distinto al nombre del argumento,para realizar el mapeo, hay que usar el atributo "name", o su alias "value", de la anotación con el nombre de la variable de la ruta o path.Si son iguales,no hace falta usar dicho atributo
	@GetMapping("/form-v2/{id}")
	public Mono<String> editarV2(@PathVariable String id,Model model){ // El objeto Model es necesario para pasar información a la vista asociada a la ruta "/form"
		return productoService.findById(id) // Recuperamos de la base datos el producto como un flujo reactivo Mono a partir del id recuperado de la url o path.Esto lo hacemos mediante el bean 'productoService'
				// Con el método "doOnNext()" realizamos las siguientes tareas sobre el producto del flujo reactivo Mono
				// En esta versión 2 del editar,el paso de atributos a la vista a través de la instancia "model" se está realizando en el contexto o hilo del flujo reactivo y no en el contexto o hilo del método handler.Esto provoca que el atributo "producto" no se guarde como un atributo de la sesión mediante la anotación a nivel de clase @SessionAttributes,y por lo tanto,si usamos realmente esta versión de editar,tenemos que usar en la vista la opción de pasar el id del producto como un input de tipo hidden(oculto)
				.doOnNext(p -> {
					log.info("Producto localizado:" + p.getNombre()); // Escribimos en el log a modo de información el nombre del producto que acabamos de recuperar de la base de datos
					model.addAttribute("producto",p); // Asociamos al atributo 'producto' la instancia con los datos del producto recuperado de la base de datos y se lo pasamos a la vista para que se carguen en el formulario de edición
					model.addAttribute("titulo","Editar Producto"); // Asociamos al atributo 'titulo' el texto 'Editar Producto' y se lo pasamos a la vista
					model.addAttribute("operacion","Editar"); // Asociamos al atributo 'operacion' el texto 'Editar' y se lo pasamos a la vista
				
				})
				// Con el método "defaultIfEmpty()",si el flujo reactivo Mono anterior tiene como elemento un elemento vacío o nulo porque no se ha localizado ningún producto a partir del id obtenido de la url o path,creamos un flujo reactivo Mono con un elemento Producto vacío
				.defaultIfEmpty(new Producto())  // Otra opción equivalente sería usar el método "switchIfEmpty" pasándole un flujo reactivo Mono con una instancia de un producto
				// Con el operador "flatMap" transformamos el flujo reactivo Mono en otro flujo reactivo Mono cuyo elemento va a ser a su vez otro flujo reactivo Mono con una excepción de interrupción,en caso de que no exista el id del producto,o bien,otro flujo reactivo Mono con el producto,en caso contrario.Como al final vamos a tener un flujo reactivo Mono con otro flujo reactivo Mono a su vez,con este operador,en lugar de "map",se va a realizar un procedimiento de aplanamiento para que definitivamente nos quede un único flujo reactivo Mono de un elemento no reactivo
				// Esto es útil para gestionar errores que se puedan producir en un flujo reactivo
				.flatMap(p -> {
					if(p.getId() == null) // Si el producto no tiene id(se ha ejecutado antes la sentencia "defaultIfEmpty(new Producto())"),creamos un flujo reactivo Mono con una excepción de tipo "InterruptedException" como elemento y el mensaje "No existe el producto"
						return Mono.error(new InterruptedException("No existe el producto"));
					return Mono.just(p); // En caso contrario(no se ha ejecutado antes la sentencia "defaultIfEmpty(new Producto())"),creamos un flujo reactivo Mono con el producto localizado como elemento
				})
				// Con el método "then" creamos otro flujo reactivo Mono de tipo String con el nombre de la vista "form", y lo devuelve
				// El método "then" espera a que el flujo reactivo original termine de emitir su único elemento para comenzar a procesar un nuevo flujo reactivo Mono, es decir, comienza a procesar un nuevo flujo reactivo Mono cuando se produce el evento OnComplete del flujo reactivo original
    			// Al final, el único elemento del flujo reactivo original es ignorado y se tiene en cuenta el elemento del nuevo flujo reactivo Mono
				// "thenMany" se usa para crear flujos reactivos Flux y "then" para crear flujos reactivos Mono cuando el flujo reactivo original ha finalizado de emitir su último elemento
				.then(Mono.just("form")) // Otra manera sería usar la sentencia ".thenReturn("/form")
				// Con el método "onErrorResume()",si el flujo reactivo Mono emite un elemento de error,como en nuestro caso,una excepción de interrupción,se devuelve de manera reactiva,con un flujo reactivo Mono,un String con la redirección a la ruta o path "/listar" seguido de un parámetro de error con el mensaje "no existe el producto"(No puede haber espacios en una url o ruta,así que tenemos que usar el operador "+" para referirnos a los espacios)
				// Con "redirect:" no indicamos que se vaya directamente a una vista,sino que estamos indicando que se ejecute el método handler asociado a la ruta indicada junto a "redirect:".En este caso,estamos indicando que se ejecute el método handler "listar()" que está asociado a la ruta "/listar"
				.onErrorResume(err -> Mono.just("redirect:/listar?error=no+existe+el+producto"));
	}
	
	// Método handler que responde las peticiones http de tipo Get para las rutas "/eliminar" + id_producto
	// Con la anotación @PathVariable recuperamos la variable "id" de la ruta o path y la mapeamos con el argumento "id" de este método handler.Cuando el nombre de la variable es distinto al nombre del argumento,para realizar el mapeo, hay que usar el atributo "name", o su alias "value", de la anotación con el nombre de la variable de la ruta o path.Si son iguales,no hace falta usar dicho atributo
	@GetMapping("/eliminar/{id}")
	public Mono<String> eliminar(@PathVariable String id){
		return productoService.findById(id) // Recuperamos de la base datos el producto como un flujo reactivo Mono a partir del id recuperado de la url o path.Esto lo hacemos mediante el bean 'productoService'
				// Con el método "defaultIfEmpty()",si el flujo reactivo Mono anterior tiene como elemento un elemento vacío o nulo porque no se ha localizado ningún producto a partir del id obtenido de la url o path,creamos un flujo reactivo Mono con un elemento Producto vacío
				.defaultIfEmpty(new Producto()) // Otra opción equivalente sería usar el método "switchIfEmpty" pasándole un flujo reactivo Mono con una instancia de un producto
				// Con el operador "flatMap" transformamos el flujo reactivo Mono en otro flujo reactivo Mono cuyo elemento va a ser a su vez otro flujo reactivo Mono con una excepción de interrupción,en caso de que no exista el id del producto,o bien,otro flujo reactivo Mono con el producto,en caso contrario.Como al final vamos a tener un flujo reactivo Mono con otro flujo reactivo Mono a su vez,con este operador,en lugar de "map",se va a realizar un procedimiento de aplanamiento para que definitivamente nos quede un único flujo reactivo Mono de un elemento no reactivo
				// Esto es útil para gestionar errores que se puedan producir en un flujo reactivo
				.flatMap(p -> {
					if(p.getId() == null) // Si el producto no tiene id(se ha ejecutado antes la sentencia "defaultIfEmpty(new Producto())"),creamos un flujo reactivo Mono con una excepción de tipo "InterruptedException" como elemento y el mensaje "No existe el producto"
						return Mono.error(new InterruptedException("No existe el producto"));
					return Mono.just(p); // En caso contrario(no se ha ejecutado antes la sentencia "defaultIfEmpty(new Producto())"),creamos un flujo reactivo Mono con el producto localizado como elemento
				})		
				// Si todo ha ido bien y el flujo anterior no ha emitido la excepción "InterruptedException",con el operador "flatMap" transformamos dicho flujo reactivo Mono en otro flujo reactivo Mono cuyo elemento va a ser a su vez otro flujo reactivo Mono de tipo Void, que es resultado de eliminar el producto localizado previamente de la base de datos usando el bean "productoService".Como al final vamos a tener un flujo reactivo Mono con otro flujo reactivo Mono a su vez,con este operador,en lugar de "map",se va a realizar un procedimiento de aplanamiento para que definitivamente nos quede un único flujo reactivo Mono de un elemento no reactivo
				.flatMap(p -> {
					log.info("Producto eliminado: " + p.getId() + " - " + p.getNombre()); // Escribe en el log a modo de información el id y el nombre del producto eliminado
					return productoService.delete(p); // Eliminarmos de la base datos el producto localizado mediante el bean 'productoService'
				})
				// Con el método "then" creamos otro flujo reactivo Mono de tipo String con la redirección a la ruta "/listar" seguido del parámetro "success" con el valor "producto eliminado con éxito", y lo devuelve
				// El método "then" espera a que el flujo reactivo original termine de emitir su único elemento para comenzar a procesar un nuevo flujo reactivo Mono, es decir, comienza a procesar un nuevo flujo reactivo Mono cuando se produce el evento OnComplete del flujo reactivo original
    			// Al final, el único elemento del flujo reactivo original es ignorado y se tiene en cuenta el elemento del nuevo flujo reactivo Mono
				// "thenMany" se usa para crear flujos reactivos Flux y "then" para crear flujos reactivos Mono cuando el flujo reactivo original ha finalizado de emitir su último elemento
				// Con "redirect:" no indicamos que se vaya directamente a una vista,sino que estamos indicando que se ejecute el método handler asociado a la ruta indicada junto a "redirect:".En este caso,estamos indicando que se ejecute el método handler "listar()" que está asociado a la ruta "/listar"
				.then(Mono.just("redirect:/listar?success=producto+eliminado+con+éxito")) // Otra manera sería usar la sentencia ".thenReturn("redirect:/listar?success=producto+eliminado+con+éxito")
				// Con el método "onErrorResume()",si el flujo reactivo Mono emite un elemento de error,como en nuestro caso,una excepción de interrupción,se devuelve de manera reactiva,con un flujo reactivo Mono,un String con la redirección a la ruta o path "/listar" seguido de un parámetro de error con el mensaje "no existe el producto a eliminar"(No puede haber espacios en una url o ruta,así que tenemos que usar el operador "+" para referirnos a los espacios)
				// Con "redirect:" no indicamos que se vaya directamente a una vista,sino que estamos indicando que se ejecute el método handler asociado a la ruta indicada junto a "redirect:".En este caso,estamos indicando que se ejecute el método handler "listar()" que está asociado a la ruta "/listar"
				.onErrorResume(err -> Mono.just("redirect:/listar?error=no+existe+el+producto+a+eliminar"));
	}
	
	/* En la versión reactiva de Thymeleaf,podemos configurar la contrapresión en la vistas para evitar cuellos de botella cuando el origen o fuente de los datos(este controlador) sufre demoras o hay una gran cantidad de datos.
	 * Existen 3 modos de manejar dicha contrapresión que vamos a ver en los siguientes métodos handler: Modo Data-Driver(bloques especificados por el número de elementos),Modo Chunked(bloques indicados por tamaño en bytes) y Modo Full(se trata del modo normal sin contrapresión)
	 */
	
	// Método handler que responde las peticiones http de tipo Get para las rutas "/listar-datadriver"
	// DataDriver es una forma recomendable de manejar la contrapresión en la vista que muestra los datos cuando estos sufren demoras o se trata de una gran cantidad de ellos.Nos permite mostrar directamente los datos en la vista por bloques(número de elementos del stream) en vez de esperar a que estén todos ellos listos para ser mostrados.
	// Los bloques se definen en número de elementos del stream y no en bytes(modo 'Chunked')
	@GetMapping("/listar-datadriver")
	public Mono<String> listarDataDriver(Model model){ // El objeto Model es necesario para pasar información a la vista asociada a la ruta "/listar"
		Flux<Producto> productos = productoService.findAllConNombreUpperCase() // Recuperamos de la capa Service mediante el bean 'productoService' el listado de productos con sus nombres en mayúscula como un stream reactivo de tipo Flux(varios items.Si fuese sólo un item,sería de tipo Mono)
		// Damos un retardo de 1 segundo entre cada elemento del stream para simular un contexto 'DataDriver'
		.delayElements(Duration.ofSeconds(1)); 
		
		// Nos suscribimos a este flujo reactivo,creándose un Observador,para realizar la tarea de escribir en el log a modo de información el nombre de cada producto emitido por este flujo reactivo
		productos.subscribe(prod -> log.info(prod.getNombre()));
		
		// Con la instancia 'ReactiveDataDriverContextVariable' configuramos un contexto de tipo 'DataDriver' con bloques de 2 en 2 productos para ser mostrados en la vista
		// Dicha instancia la asociamos al atributo 'productos' y se lo pasamos a la vista
		model.addAttribute("productos",new ReactiveDataDriverContextVariable(productos,2));
		model.addAttribute("titulo","Listado de productos"); // Asociamos al atributo 'titulo' el texto 'Listado de productos' y se lo pasamos a la vista
		
		return Mono.just("listar"); // Devolvemos,de manera reactiva, con un flujo reactivo Mono el nombre de la vista a mostrar
	}
	
	// Método handler que responde las peticiones http de tipo Get para las rutas "/listar-full"
	// Método handler que simula un entorno con un stream o flujo de datos muy grande sin configurar ningún contexto 'DataDriver'(bloques especificados por el número de elementos), ni 'Chunked'(bloques indicados por tamaño en bytes)
	@GetMapping("/listar-full")
	public Mono<String> listarFull(Model model){ // El objeto Model es necesario para pasar información a la vista asociada a la ruta "/listar"
		// Recuperamos de la capa Service mediante el bean 'productoService' el listado de productos con sus nombres en mayúscula como un stream reactivo de tipo Flux(varios items.Si fuese sólo un item,sería de tipo Mono) cuyos elementos van a estar repetidos 5000 veces
		// En este caso, la idea de repetir los elementos tantas veces es simular un entorno con un stream de datos muy grande sin configurar nada, ni 'DataDriver'(bloques especificados por el número de elementos), ni 'Chunked'(bloques indicados por tamaño en bytes)
		Flux<Producto> productos = productoService.findAllConNombreUpperCaseRepeat(); 

		// Nos suscribimos a este flujo reactivo,creándose un Observador,para realizar la tarea de escribir en el log a modo de información el nombre de cada producto emitido por este flujo reactivo
		productos.subscribe(prod -> log.info(prod.getNombre()));
		
		model.addAttribute("productos",productos); // Asociamos al atributo 'productos' el flujo reactivo Flux con los productos recuperado anteriormente de la capa Service "productoService" y se lo pasamos a la vista
		model.addAttribute("titulo","Listado de productos"); // Asociamos al atributo 'titulo' el texto 'Listado de productos' y se lo pasamos a la vista
		
		return Mono.just("listar"); // Devolvemos,de manera reactiva, con un flujo reactivo Mono el nombre de la vista a mostrar
	}
	
	// Método handler que responde las peticiones http de tipo Get para las rutas "/listar-chunked"
	// Método handler que simula un enotrno con un stream o flujo de datos muy grande configurando un contexto 'Chunked'(bloques indicados por tamaño en bytes)
	@GetMapping("/listar-chunked")
	public Mono<String> listarChunked(Model model){ // El objeto Model es necesario para pasar información a la vista asociada a la ruta "/listar-chunked"
		// Recuperamos de la capa Service mediante el bean 'productoService' el listado de productos con sus nombres en mayúscula como un stream reactivo de tipo Flux(varios items.Si fuese sólo un item,sería de tipo Mono) cuyos elementos van a estar repetidos 5000 veces
		// En este caso, la idea de repetir los elementos tantas veces es simular un entorno con un stream de datos muy grande con configuración'Chunked'(bloques indicados por tamaño en bytes)
		Flux<Producto> productos = productoService.findAllConNombreUpperCaseRepeat();
		
		// Nos suscribimos a este flujo reactivo,creándose un Observador,para realizar la tarea de escribir en el log a modo de información el nombre de cada producto emitido por este flujo reactivo
		productos.subscribe(prod -> log.info(prod.getNombre()));
		
		model.addAttribute("productos",productos); // Asociamos al atributo 'productos' el flujo reactivo Flux con los productos recuperado anteriormente de la capa Service "productoService" y se lo pasamos a la vista
		model.addAttribute("titulo","Listado de productos"); // Asociamos al atributo 'titulo' el texto 'Listado de productos' y se lo pasamos a la vista
		
		return Mono.just("listar-chunked"); // Devolvemos,de manera reactiva, con un flujo reactivo Mono el nombre de la vista a mostrar
	}

}
