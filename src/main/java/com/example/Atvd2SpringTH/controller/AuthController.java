package com.example.Atvd2SpringTH.controller;

import com.example.Atvd2SpringTH.model.Usuario;
import com.example.Atvd2SpringTH.repository.UsuarioRepository;
import com.example.Atvd2SpringTH.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class AuthController {

    private final Map<String, String> codigosRecuperacao = new ConcurrentHashMap<>();
    private final SecureRandom secureRandom = new SecureRandom();

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmailService emailService;

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

    @PostMapping("/enviar-codigo")
    public String enviarCodigo(@RequestParam String email, Model model) {
        Usuario usuario = usuarioRepository.findByEmail(email);

        if (usuario == null) {
            model.addAttribute("erro", "E-mail não encontrado na base de dados.");
            return "esqueci-senha";
        }

        String codigo = String.format("%06d", secureRandom.nextInt(1_000_000));
        codigosRecuperacao.put(email, codigo);

        try {
            emailService.enviarCodigoRecuperacao(email, codigo);
        } catch (MailException | IllegalArgumentException exception) {
            codigosRecuperacao.remove(email);
            model.addAttribute("erro", "Não foi possível enviar o código. Confira a configuração do e-mail.");
            return "esqueci-senha";
        }

        model.addAttribute("email", email);
        model.addAttribute("sucesso", "Código enviado para o seu e-mail.");
        return "codigo";
    }

    @PostMapping("/verificar-codigo")
    public String verificarCodigo(@RequestParam String email,
                                  @RequestParam String codigo,
                                  Model model) {
        String codigoSalvo = codigosRecuperacao.get(email);

        if (codigoSalvo == null || !codigoSalvo.equals(codigo)) {
            model.addAttribute("email", email);
            model.addAttribute("erro", "Código inválido. Confira e tente novamente.");
            return "codigo";
        }

        codigosRecuperacao.remove(email);
        model.addAttribute("email", email);
        return "nova-senha";
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
