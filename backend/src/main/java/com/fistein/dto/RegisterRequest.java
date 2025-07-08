package com.fistein.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "İsim boş olamaz")
    @Size(min = 2, max = 50, message = "İsim 2-50 karakter arasında olmalıdır")
    @Pattern(regexp = "^[a-zA-ZğüşıöçĞÜŞİÖÇ\\s]+$", message = "İsim sadece harf içerebilir")
    private String name;
    
    @NotBlank(message = "E-posta boş olamaz")
    @Email(message = "Geçerli bir e-posta adresi giriniz")
    @Size(max = 100, message = "E-posta adresi 100 karakterden uzun olamaz")
    private String email;
    
    @NotBlank(message = "Şifre boş olamaz")
    @Size(min = 8, max = 128, message = "Şifre 8-128 karakter arasında olmalıdır")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$", 
             message = "Şifre en az bir küçük harf, bir büyük harf, bir rakam ve bir özel karakter içermelidir")
    private String password;
}
