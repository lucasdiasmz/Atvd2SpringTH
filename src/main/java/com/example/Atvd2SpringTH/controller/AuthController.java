package com.example.Atvd2SpringTH.controller;

import com.example.Atvd2SpringTH.model.Usuario;
import com.example.Atvd2SpringTH.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/login")
    public String telaLogin() {
        return "login";
    }

    @PostMapping("/logar")
    public String logar(@RequestParam String email, @RequestParam String senha, Model model) {
        Usuario usuario = usuarioRepository.findByEmailAndSenha(email, senha);
        if (usuario != null) {
            model.addAttribute("nome", usuario.getNome());
            return "home"; // Redireciona para a página logada
        }
        model.addAttribute("erro", "Email ou senha inválidos!");
        return "login";
    }

    @GetMapping("/cadastro")
    public String telaCadastro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "cadastro";
    }

    @PostMapping("/cadastrar")
    public String cadastrar(@ModelAttribute Usuario usuario, Model model) {
        if (usuarioRepository.findByEmail(usuario.getEmail()) != null) {
            model.addAttribute("erro", "Este email já está em uso!");
            return "cadastro";
        }
        usuarioRepository.save(usuario);
        return "redirect:/login"; // Volta pro login após cadastrar
    }

    @GetMapping("/esqueci-senha")
    public String telaEsqueciSenha() {
        return "esqueci-senha";
    }

    @PostMapping("/reset-senha")
    public String resetarSenha(@RequestParam String email, @RequestParam String novaSenha, Model model) {
        Usuario usuario = usuarioRepository.findByEmail(email);
        if (usuario != null) {
            usuario.setSenha(novaSenha);
            usuarioRepository.save(usuario);
            model.addAttribute("sucesso", "Senha alterada com sucesso! Faça login.");
            return "login";
        }
        model.addAttribute("erro", "Email não encontrado na base de dados.");
        return "esqueci-senha";
    }
}