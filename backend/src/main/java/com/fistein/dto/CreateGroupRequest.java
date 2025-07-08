package com.fistein.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CreateGroupRequest {
    @NotBlank(message = "Grup adı boş olamaz")
    @Size(min = 2, max = 100, message = "Grup adı 2-100 karakter arasında olmalıdır")
    @Pattern(regexp = "^[a-zA-ZğüşıöçĞÜŞİÖÇ0-9\\s\\-_]+$", message = "Grup adı sadece harf, rakam, boşluk, tire ve alt çizgi içerebilir")
    private String name;
    
    @Size(max = 500, message = "Açıklama 500 karakterden uzun olamaz")
    private String description;
}