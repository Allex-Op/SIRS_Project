package sirs.api.hospital;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class test {

    @GetMapping("/hello-world")
    public String helloWorld() {
        return "Hello there buddy, how are u";
    }
}
