package com.bolsadeideas.springboot.webflux.client.app;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

// Esta clase de configuración de Spring crea y configura nuestro cliente web para comunicarnos con nuestra Api Rest y registra este cliente como un bean en la memoria o contenedor de Spring

@Configuration // Indicamos que esta clase es una clase de Configuración de Spring y,de esta manera,Spring va a almacenar un bean de esta clase en su contenedor o memoria
public class AppConfig {
	
	// La anotación @Value nos permite inyectar el valor de cualquier propiedad definida en el archivo de propiedades "application.properties"
	// Inyectamos el valor de la propiedad "config.base.endpoint" definida en el archivo de propiedades de la aplicación "application.properties"
	@Value("${config.base.endpoint}")
	private String url; // Esta propiedad contiene la ruta o path base del controlador al que queremos conectar.Tenemos dos controladores Api Rest; uno(controlador con anotación @RestController) mapeado con la ruta base "/api/productos" y el otro(controlador que usa la técnica "Functional Endpoints") mapeado con la ruta base "/api/v2/productos"
	
	// Este método crea nuestro cliente web para que se comunique con nuestra Api Rest y registra un bean de este cliente en la memoria o contenedor de Spring para poder usarlo en otra parte de este proyecto
	// En vez de devolver una instancia de tipo WebClient,vamos a devolver una instancia de tipo WebCliente.Builder ya que soporta balanceo de carga
	@Bean // Con esta anotación almacenamos como un bean la salida o respuesta de este método en la memoria o contenedor de Spring para que sea gestionado por Spring
	@LoadBalanced // Con esta anotación habilitamos el balanceo de carga
	public WebClient.Builder registrarWebClient() {
		// Creamos y devolvemos nuestro cliente web que se va a conectar a nuestra Api Rest que se localiza en la ruta o path base indicada en "url"
		return WebClient.builder().baseUrl(url);
	}
}
