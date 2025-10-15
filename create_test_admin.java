import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class create_test_admin {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // Generar hash para la nueva contraseña de prueba
        String testPassword = "test123";
        String encodedPassword = encoder.encode(testPassword);
        
        System.out.println("=== NUEVO USUARIO ADMINISTRADOR DE PRUEBA ===");
        System.out.println("Username: testadmin");
        System.out.println("Contraseña original: " + testPassword);
        System.out.println("Contraseña encriptada: " + encodedPassword);
        System.out.println("===============================================");
    }
}