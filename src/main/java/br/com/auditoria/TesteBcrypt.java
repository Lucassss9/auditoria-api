package br.com.auditoria;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TesteBcrypt {

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String embaralhada = encoder.encode("minhasenha");

        System.out.println(embaralhada);
    }
}