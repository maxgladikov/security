package dev.gladikov.resource.filter;

import reactor.core.publisher.Mono;

import java.util.function.Function;

public class Main {
    public static void main(String[] args) {
        Mono<Integer> integer =Mono.just (1);
        Mono<Integer> integerError = Mono.error (RuntimeException::new);
        Mono<String> string = integerError.flatMap (intToString);//.onErrorResume (Main::onError);
        string.subscribe (System.out::println, error -> System.out.println (error.getMessage ()), () -> System.out.println ("Completed"));

        integer.map (String::valueOf).subscribe (System.out::println);
    }

    public static Function<Integer,Mono<String>> intToString = i -> Mono.just (Integer.toString (i));

    private static Mono<String> onError(Throwable e){
        return Mono.just("error");
    }
}
