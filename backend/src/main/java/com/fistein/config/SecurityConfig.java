package com.fistein.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.beans.factory.annotation.Autowired;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    // JwtAuthenticationFilter, JWT tokenlarını doğrulamak ve kullanıcı kimliğini ayarlamak için kullanılır.
    private final JwtAuthenticationFilter jwtAuthFilter;
    // UserDetailsService, kullanıcı detaylarını (örneğin veritabanından) yüklemek için kullanılır.
    private final UserDetailsService userDetailsService;
    // PasswordEncoder, parolaları şifrelemek ve doğrulamak için kullanılır.
    private final PasswordEncoder passwordEncoder;
    // CorsConfigurationSource, CorsConfig sınıfında tanımlanan bean'i kullanır
    private final CorsConfigurationSource corsConfigurationSource;
    // CacheControlHeaderFilter, her response'a Cache-Control header'ı ekler.
    @Autowired
    private CacheControlHeaderFilter cacheControlHeaderFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CORS yapılandırmasını etkinleştirir ve CorsConfig'de tanımlanan corsConfigurationSource bean'ini kullanır.
                // Bu, tarayıcıların farklı domainlerden gelen istekleri kabul etmesini sağlar.
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                // CSRF (Cross-Site Request Forgery) korumasını devre dışı bırakır.
                // Genellikle API tabanlı uygulamalarda stateless olduğu için devre dışı bırakılır.
                .csrf(AbstractHttpConfigurer::disable)
                // HTTP isteklerini yetkilendirme kurallarını tanımlar.
                .authorizeHttpRequests(auth -> auth
                        // "/api/auth/**" altındaki tüm isteklere kimlik doğrulaması olmadan erişim izni verir.
                        .requestMatchers("/api/auth/**").permitAll()
                        // Diğer tüm isteklere kimlik doğrulaması yapılmış kullanıcıların erişmesine izin verir.
                        .anyRequest().authenticated()
                )
                // Oturum yönetimini yapılandırır. STATELESS, sunucunun istemci oturum durumunu tutmayacağı anlamına gelir.
                // Bu, RESTful API'ler ve JWT tabanlı kimlik doğrulama için tipiktir.
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // Kimlik doğrulama sağlayıcısını ayarlar. Bu, kullanıcı detaylarını ve parola kodlayıcısını kullanır.
                .authenticationProvider(authenticationProvider())
                // JWT kimlik doğrulama filtresini UsernamePasswordAuthenticationFilter'dan önce ekler.
                // Bu, her istekte JWT token'ını kontrol etmeyi sağlar.
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                // Cache-Control header filtresini UsernamePasswordAuthenticationFilter'dan önce ekler.
                .addFilterBefore(cacheControlHeaderFilter, UsernamePasswordAuthenticationFilter.class);

        // Oluşturulan SecurityFilterChain'i döndürür.
        return http.build();
    }

    // AuthenticationProvider bean'i, kullanıcı kimlik doğrulama mantığını sağlar.
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        // Kullanıcı detaylarını yüklemek için UserDetailsService'i ayarlar.
        authProvider.setUserDetailsService(userDetailsService);
        // Parolaları doğrulamak için PasswordEncoder'ı ayarlar.
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    // AuthenticationManager bean'i, kimlik doğrulama sürecini yönetir.
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        // AuthenticationConfiguration'dan AuthenticationManager'ı alır.
        return config.getAuthenticationManager();
    }
}
