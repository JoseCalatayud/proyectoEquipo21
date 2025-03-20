package es.santander.ascender.proyectoFinal2.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
//endpoint que devuelva ok
public class DummyController {
    @GetMapping("/dummy")
    public String dummy() {
        return "Hola que tal";
    }
}
