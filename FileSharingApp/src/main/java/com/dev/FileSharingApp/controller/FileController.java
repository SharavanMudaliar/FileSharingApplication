package com.dev.FileSharingApp.controller;

import com.dev.FileSharingApp.model.SharedFile;
import com.dev.FileSharingApp.repository.SharedFileRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.Optional;

@Controller
public class FileController {
    @Value("${file.upload-dir}")
    private String uploadDir;

    private final SharedFileRepository repo;

    public FileController(SharedFileRepository repo) {
        this.repo = repo;
    }

    // This mapping makes /logout always redirect
    @GetMapping("/logout")
    public String logoutRedirect() {
        // Always show the green message on the login page:
        return "redirect:/login?logout";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    private String getCurrentUserId(OAuth2AuthenticationToken authentication) {
        if (authentication == null || !authentication.isAuthenticated()) return null;
        String userId = authentication.getPrincipal().getAttribute("email");
        if (userId == null) userId = authentication.getPrincipal().getAttribute("login");
        return userId;
    }

    @GetMapping("/")
    public String listFiles(Model model, HttpServletRequest req, OAuth2AuthenticationToken authentication,
                            @ModelAttribute("success") String success,
                            @ModelAttribute("error") String error) {
        String serverRoot = req.getRequestURL().toString().replace(req.getRequestURI(), "");
        String userId = getCurrentUserId(authentication);
        model.addAttribute("username", userId);

        List<Map<String, String>> fileList = repo.findAll().stream()
                .filter(meta -> userId != null && userId.equals(meta.getOwnerId()))
                .map(meta -> {
                    String status = System.currentTimeMillis() > meta.getUploadTime() + meta.getExpiryMillis() ? "Expired" : "Active";
                    String expiry = String.format("%.2f min", (meta.getUploadTime() + meta.getExpiryMillis() - System.currentTimeMillis())/(60*1000.0));
                    Map<String,String> map = new HashMap<>();
                    map.put("id", meta.getId());
                    map.put("name", meta.getOrigName());
                    map.put("link", serverRoot + "/download/" + meta.getId());
                    map.put("status", status);
                    map.put("expiry", expiry);
                    return map;
                }).collect(Collectors.toList());
        model.addAttribute("files", fileList);

        model.addAttribute("logoutUrl", "/logout");

        if (!success.isEmpty()) model.addAttribute("success", success);
        if (!error.isEmpty()) model.addAttribute("error", error);

        return "index";
    }

    @PostMapping("/upload")
    public String uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value="expiry", required=false) Integer expiryMinutes,
            OAuth2AuthenticationToken authentication,
            RedirectAttributes redirectAttributes) {
        String userId = getCurrentUserId(authentication);
        if (userId == null) {
            redirectAttributes.addFlashAttribute("error", "error.mustBeLoggedIn");
            return "redirect:/";
        }

        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "error.noFileSelected");
            return "redirect:/";
        }
        try {
            File folder = new File(uploadDir);
            if (!folder.exists()) folder.mkdirs();

            String origName = StringUtils.cleanPath(file.getOriginalFilename());
            String id = UUID.randomUUID().toString() + origName.substring(origName.lastIndexOf('.'));
            Path path = Paths.get(uploadDir, id);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            long expiryMillis = (expiryMinutes != null && expiryMinutes > 0)
                    ? expiryMinutes * 60 * 1000L
                    : 24 * 60 * 60 * 1000L;
            repo.save(new SharedFile(id, origName, System.currentTimeMillis(), expiryMillis, userId));
            redirectAttributes.addFlashAttribute("success", "success.upload");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "error.uploadFailed");
        }
        return "redirect:/";
    }

    @GetMapping("/download/{id:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String id, OAuth2AuthenticationToken authentication, RedirectAttributes redirectAttributes) throws IOException {
        String userId = getCurrentUserId(authentication);
        Optional<SharedFile> metaOpt = repo.findById(id);
        if (metaOpt.isEmpty() || !userId.equals(metaOpt.get().getOwnerId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        SharedFile meta = metaOpt.get();
        if (System.currentTimeMillis() > meta.getUploadTime() + meta.getExpiryMillis()) {
            return ResponseEntity.status(HttpStatus.GONE).build();
        }
        Path filePath = Paths.get(uploadDir, id);
        Resource resource = new UrlResource(filePath.toUri());
        if(!resource.exists()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + meta.getOrigName() + "\"")
                .body(resource);
    }
}
