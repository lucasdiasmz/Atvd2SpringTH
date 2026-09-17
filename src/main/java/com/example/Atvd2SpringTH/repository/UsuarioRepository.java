package com.example.Atvd2SpringTH.repository;

import com.example.Atvd2SpringTH.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Usuario findByEmail(String email);
    Usuario findByEmailAndSenha(String email, String senha);
}