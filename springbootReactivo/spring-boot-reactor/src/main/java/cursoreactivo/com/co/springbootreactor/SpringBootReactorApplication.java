package cursoreactivo.com.co.springbootreactor;

import cursoreactivo.com.co.springbootreactor.models.Comentario;
import cursoreactivo.com.co.springbootreactor.models.Usuario;
import cursoreactivo.com.co.springbootreactor.models.UsuarioConComentarios;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.CountDownLatch;

@SpringBootApplication
public class SpringBootReactorApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SpringBootReactorApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SpringBootReactorApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        /*ejemploIterable();
        ejemploFlatMap();
        ejemploToString();
        ejemploCollectList();
        ejemploUsuarioComentariosFlatMap();
        ejemploUsuarioComentariosZipWith();
        ejemploUsuarioComentariosZipWithForma2();
        ejemploZipWithRangos();
        ejemploInterval();
        ejemploDelayElements();
        ejemploIntervaloInfinito();
        ejemploIntervaloDesdeCreate();*/
        ejemploContraPresion();
    }

    //La contra presion o back pressure es cuando el subscriber le dice al productor que envie menos para evitar la sobrecarga
    public void ejemploContraPresion() {
//        limitRate es para determinar cuantos elementos se quieren por lote
        Flux.range(1, 10)
                .log()
                .limitRate(2)
                .subscribe();

        /*
        Implementando la sobrecarga de metodos
        Flux.range(1, 10)
                .log()
                .subscribe(new Subscriber<Integer>() {

                    private Subscription s;
                    private Integer limite = 5;
                    private Integer consumido = 0;

                    @Override
                    public void onSubscribe(Subscription subscription) {
                        this.s = subscription;
                        subscription.request(limite);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        log.info(integer.toString());
                        consumido++;
                        if (consumido == limite) {
                            consumido = 0;
                            s.request(limite);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });*/
    }


    public void ejemploIntervaloDesdeCreate() {
        Flux.create(emitter -> {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {

                private Integer contador = 0;

                @Override
                public void run() {
                    //cada evento que se quiere emitir se hace con el next
                    //se termina con el complete
                    emitter.next(++contador);
                    if (contador == 10) {
                        timer.cancel();
                        emitter.complete();
                    }

                    if (contador == 5) {
                        timer.cancel();
                        emitter.error(new InterruptedException("Error, se ha detenido el flux en 5"));
                    }
                }
            }, 1000, 1000);
        })
                /*
                Asi es manejando todo por aparte
                .doOnNext(next -> log.info(next.toString()))
                .doOnComplete(() -> log.info("Hemos Terminado"))
                .subscribe();
                */
                //Asi es manejando todo en el subscribe, el mensaje, el error y el doOnComplete
                .subscribe(next -> log.info(next.toString()),
                        error -> log.error(error.getMessage()),
                        () -> log.info("Hemos Terminado"));
    }

    public void ejemploIntervaloInfinito() throws InterruptedException {

        //La linea de abajo sirve para que tenga que esperar , algo parecido al blockLast()
        CountDownLatch latch = new CountDownLatch(1);

        Flux.interval(Duration.ofSeconds(1))
                .doOnTerminate(() -> latch.countDown())
                .flatMap(i -> (i >= 5) ? Flux.error(new InterruptedException("Solo hasta 5")) : Flux.just(i))
                .map(i -> String.format("Hola %d", i))
//                .doOnNext(s -> log.info(s))
                .retry(2)
                .subscribe(s -> log.info(s), e -> log.error(e.getMessage()));
        latch.await();
    }

    public void ejemploDelayElements() throws InterruptedException {
        Flux<Integer> rango = Flux.range(1, 12)
                .delayElements(Duration.ofSeconds(1))
                .doOnNext(i -> log.info(i.toString()));
//        rango.blockLast();
        rango.subscribe();
        Thread.sleep(13000);
    }

    public void ejemploInterval() {
        Flux<Integer> rango = Flux.range(1, 12);
        Flux<Long> retraso = Flux.interval(Duration.ofSeconds(1));

        rango.zipWith(retraso, (ra, re) -> ra)
                .doOnNext(i -> log.info(i.toString()))
                .blockLast();
    }

    public void ejemploZipWithRangos() {
        Flux.just(1, 2, 3, 4)
                .map(i -> (i * 2))
                .zipWith(Flux.range(0, 4), (uno, dos) -> String.format("Primer Flux: %d, Segundo Flux: %d", uno, dos))
                .subscribe(texto -> log.info(texto));
    }

    public void ejemploUsuarioComentariosZipWithForma2() {
        Mono<Usuario> usuarioMono = Mono.fromCallable(() -> new Usuario("Jhon", "Doe"));
        Mono<Comentario> comentarioMono = Mono.fromCallable(() -> {
            Comentario comentario = new Comentario();
            comentario.addComentarios("hola pepe, que tal");
            comentario.addComentarios("Mañana voy a la playa");
            comentario.addComentarios("Estoy tomando el curso spring reactor");
            return comentario;
        });

        Mono<UsuarioConComentarios> usuarioConComentariosMono = usuarioMono
                .zipWith(comentarioMono)
                .map(tuple -> {
                    Usuario u = tuple.getT1();
                    Comentario c = tuple.getT2();
                    return new UsuarioConComentarios(u, c);
                });

        usuarioConComentariosMono.subscribe(uc -> log.info(uc.toString()));
    }

    public void ejemploUsuarioComentariosZipWith() {
        Mono<Usuario> usuarioMono = Mono.fromCallable(() -> new Usuario("Jhon", "Doe"));
        Mono<Comentario> comentarioMono = Mono.fromCallable(() -> {
            Comentario comentario = new Comentario();
            comentario.addComentarios("hola pepe, que tal");
            comentario.addComentarios("Mañana voy a la playa");
            comentario.addComentarios("Estoy tomando el curso spring reactor");
            return comentario;
        });

        usuarioMono.zipWith(comentarioMono, (usuario, comentariosUsuario) -> new UsuarioConComentarios(usuario, comentariosUsuario))
                .subscribe(uc -> log.info(uc.toString()));
    }

    public void ejemploUsuarioComentariosFlatMap() {
        Mono<Usuario> usuarioMono = Mono.fromCallable(() -> new Usuario("Jhon", "Doe"));
        Mono<Comentario> comentarioMono = Mono.fromCallable(() -> {
            Comentario comentario = new Comentario();
            comentario.addComentarios("hola pepe, que tal");
            comentario.addComentarios("Mañana voy a la playa");
            comentario.addComentarios("Estoy tomando el curso spring reactor");
            return comentario;
        });

        usuarioMono.flatMap(usuario -> comentarioMono.map(comentario -> new UsuarioConComentarios(usuario, comentario)))
                .subscribe(uc -> log.info(uc.toString()));
    }

    public void ejemploCollectList() throws Exception {

        List<Usuario> usuariosList = new ArrayList<>();
        usuariosList.add(new Usuario("Alejo", "Zuluaga"));
        usuariosList.add(new Usuario("Angelica", "Orozco"));
        usuariosList.add(new Usuario("Juan", "Mosquera"));
        usuariosList.add(new Usuario("Jhon", "Mancera"));
        usuariosList.add(new Usuario("Bruce", "willis"));
        usuariosList.add(new Usuario("Bruce", "lee"));

        Flux.fromIterable(usuariosList)
                .collectList()
                .subscribe(lista -> lista.forEach(item -> log.info(item.toString())));
    }

    public void ejemploToString() throws Exception {

        List<Usuario> usuariosList = new ArrayList<>();
        usuariosList.add(new Usuario("Alejo", "Zuluaga"));
        usuariosList.add(new Usuario("Angelica", "Orozco"));
        usuariosList.add(new Usuario("Juan", "Mosquera"));
        usuariosList.add(new Usuario("Jhon", "Mancera"));
        usuariosList.add(new Usuario("Bruce", "willis"));
        usuariosList.add(new Usuario("Bruce", "lee"));

        Flux.fromIterable(usuariosList)
                .map(usuario -> usuario.getNombre().toUpperCase().concat(" ").concat(usuario.getApellido().toUpperCase()))
                .flatMap(nombre -> {
                    if (nombre.contains("bruce".toUpperCase())) {
                        return Mono.just(nombre);
                    } else {
                        return Mono.empty();
                    }
                })
                .map(nombre -> nombre.toLowerCase())
                .subscribe(u -> log.info(u.toString()));
    }

    public void ejemploFlatMap() throws Exception {

        List<String> usuariosList = new ArrayList<>();
        usuariosList.add("Alejo Zuluaga");
        usuariosList.add("Angelica Orozco");
        usuariosList.add("Juan Mosquera");
        usuariosList.add("Jhon Mancera");
        usuariosList.add("Bruce willis");
        usuariosList.add("Bruce lee");

        Flux.fromIterable(usuariosList)
                .map(nombre -> new Usuario(nombre.split(" ")[0], nombre.split(" ")[1]))
                .flatMap(usuario -> {
                    if (usuario.getNombre().equalsIgnoreCase("bruce")) {
                        return Mono.just(usuario);
                    } else {
                        return Mono.empty();
                    }
                })
                .map(usuario -> {
                    String nombre = usuario.getNombre().toLowerCase();
                    usuario.setNombre(nombre);
                    return usuario;
                })
                .subscribe(u -> log.info(u.toString()));
    }

    public void ejemploIterable() throws Exception {

        /**
         * Apartir de una lista
         */

        List<String> usuariosList = new ArrayList<>();
        usuariosList.add("Alejo Zuluaga");
        usuariosList.add("Angelica Orozco");
        usuariosList.add("Juan Mosquera");
        usuariosList.add("Jhon Mancera");
        usuariosList.add("Bruce willis");
        usuariosList.add("Bruce lee");

        Flux<String> nombres = Flux.fromIterable(usuariosList);

        Flux<Usuario> usuarios = nombres.map(nombre -> new Usuario(nombre.split(" ")[0], nombre.split(" ")[1]))
                .filter(usuario -> usuario.getNombre().equalsIgnoreCase("Bruce"))
                .doOnNext(elemento -> {
                    if (elemento == null) {
                        throw new RuntimeException("NOMBRES NO PUEDEN SER VACIOS");
                    }
                    System.out.println(elemento.getNombre().concat(" ").concat(elemento.getApellido()));
                })
                .map(usuario -> {
                    String nombre = usuario.getNombre().toLowerCase();
                    usuario.setNombre(nombre);
                    return usuario;
                });

        /*
        FLUX DE USUARIOS Y FLUX DE STRING PARA VER QUE SON INMUTABLES

        Flux<String> nombres = Flux.just("Alejo Zuluaga", "Angelica Orozco", "Juan Mosquera", "Jhon Mancera", "Bruce willis", "bruce lee");

        Flux<Usuario> usuarios = nombres.map(nombre -> new Usuario(nombre.split(" ")[0], nombre.split(" ")[1]))
                .filter(usuario -> usuario.getNombre().equalsIgnoreCase("Bruce"))
                .doOnNext(elemento -> {
                    if (elemento == null) {
                        throw new RuntimeException("NOMBRES NO PUEDEN SER VACIOS");
                    }
                    System.out.println(elemento.getNombre().concat(" ").concat(elemento.getApellido()));
                })
                .map(usuario -> {
                    String nombre = usuario.getNombre().toLowerCase();
                    usuario.setNombre(nombre);
                    return usuario;
                });*/

        /*
        FLUX DE USUARIOS
        Flux<Usuario> nombres = Flux.just("Alejo Zuluaga", "Angelica Orozco", "Juan Mosquera", "Jhon Mancera", "Bruce willis","bruce lee")
//                .map(nombre -> new Usuario(nombre.toUpperCase(), null))
                .map(nombre -> new Usuario(nombre.split(" ")[0],nombre.split(" ")[1]))
                .filter(usuario -> usuario.getNombre().equalsIgnoreCase("Bruce"))
                .doOnNext(elemento -> {
//                    if (elemento.isEmpty()) {
                    if (elemento == null) {
                        throw new RuntimeException("NOMBRES NO PUEDEN SER VACIOS");
                    }
                    System.out.println(elemento.getNombre().concat(" ").concat(elemento.getApellido()));
                })
                .map(usuario -> {
                    String nombre = usuario.getNombre().toLowerCase();
                    usuario.setNombre(nombre);
                    return usuario;
                });*/
//                .doOnNext(System.out::println); //referencia de metodo
        nombres.subscribe(e -> log.info(e.toString()),
                error -> log.error(error.getMessage()),
                new Runnable() {
                    @Override
                    public void run() {
                        log.info("Ha finalizado la ejecucion del observable con exito");
                    }
                });

        usuarios.subscribe(e -> log.info(e.toString()),
                error -> log.error(error.getMessage()),
                new Runnable() {
                    @Override
                    public void run() {
                        log.info("Ha finalizado la ejecucion del observable con exito");
                    }
                });
    }
}
