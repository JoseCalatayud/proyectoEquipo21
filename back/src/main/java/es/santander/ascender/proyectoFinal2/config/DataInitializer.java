package es.santander.ascender.proyectoFinal2.config;

import es.santander.ascender.proyectoFinal2.model.*;
import es.santander.ascender.proyectoFinal2.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Configuration
@Profile("!test") // No ejecutar en perfil de test
public class DataInitializer {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initDatabase(
            ArticuloRepository articuloRepository,
            UsuarioRepository usuarioRepository,
            VentaRepository ventaRepository,
            CompraRepository compraRepository) {

        return args -> {
            Random random = new Random();
            
            // Crear usuarios de prueba (2 admin + 5 user)
            if (usuarioRepository.count() == 0) {
                List<Usuario> usuarios = new ArrayList<>();
                
                // Administradores
                Usuario admin1 = new Usuario();
                admin1.setUsername("admin");
                admin1.setPassword(passwordEncoder.encode("admin123"));
                admin1.setRol(RolUsuario.ADMIN);
                usuarios.add(admin1);
                
                Usuario admin2 = new Usuario();
                admin2.setUsername("superadmin");
                admin2.setPassword(passwordEncoder.encode("admin456"));
                admin2.setRol(RolUsuario.ADMIN);
                usuarios.add(admin2);
                
                // Usuarios normales
                for (int i = 1; i <= 5; i++) {
                    Usuario user = new Usuario();
                    user.setUsername("user" + i);
                    user.setPassword(passwordEncoder.encode("user" + i + "123"));
                    user.setRol(RolUsuario.USER);
                    usuarios.add(user);
                }
                
                usuarioRepository.saveAll(usuarios);
                System.out.println("Se han creado " + usuarios.size() + " usuarios");
            }

            // Crear artículos de prueba (50 productos de electrónica y electrodomésticos)
            if (articuloRepository.count() == 0) {
                List<Articulo> articulos = new ArrayList<>();
                
                // Familias para los productos
                String[] familias = {"Electrónica", "Electrodomésticos", "Informática", "Telefonía", "Audio/Video"};
                
                // Productos por familia
                String[][] nombresPorFamilia = {
                    // Electrónica
                    {"Smart TV 32\"", "Smart TV 43\"", "Smart TV 50\"", "Smart TV 55\"", "Smart TV 65\"", 
                     "Reproductor DVD", "Home Cinema", "Barra de Sonido", "Proyector LED", "Auriculares Inalámbricos"},
                    // Electrodomésticos
                    {"Frigorífico Combi", "Lavavajillas", "Lavadora", "Secadora", "Horno Eléctrico", 
                     "Microondas", "Vitrocerámica", "Campana Extractora", "Aspiradora Robot", "Freidora de Aire"},
                    // Informática
                    {"Portátil Gaming", "Portátil Ultrabook", "PC Sobremesa", "Monitor 24\"", "Monitor 27\"", 
                     "Teclado Mecánico", "Ratón Gaming", "Webcam HD", "Disco Duro SSD", "Impresora Multifunción"},
                    // Telefonía
                    {"Smartphone Android", "iPhone", "Teléfono Inalámbrico", "Smartwatch", "Auriculares Bluetooth", 
                     "Cargador Inalámbrico", "Altavoz Bluetooth", "Power Bank", "Funda Móvil", "Protector Pantalla"},
                    // Audio/Video
                    {"Cámara Reflex", "Cámara Deportiva", "Micrófono USB", "Altavoces PC", "Radio Digital", 
                     "Amplificador Audio", "Tocadiscos", "Reproductor MP3", "Grabadora Digital", "Mesa Mezclas"}
                };
                
                // Crear 50 productos
                int productCounter = 1;
                for (int i = 0; i < 50; i++) {
                    int familiaIndex = i % 5; // Distribuir equilibradamente entre familias
                    int productoIndex = (i / 5) % 10; // Distribuir entre los productos de cada familia
                    
                    Articulo articulo = new Articulo();
                    String marca = "Marca" + (productCounter % 5 + 1); // 5 marcas diferentes
                    String nombreProducto = nombresPorFamilia[familiaIndex][productoIndex];
                    
                    articulo.setNombre(marca + " " + nombreProducto);
                    articulo.setDescripcion(marca + " " + nombreProducto + " - Modelo " + (2023 + (productCounter % 3)));
                    articulo.setCodigoBarras("84" + String.format("%08d", productCounter));
                    articulo.setFamilia(familias[familiaIndex]);
                    
                    // Precios según familia
                    double precioBase = switch (familiaIndex) {
                        case 0 -> 300 + random.nextDouble() * 700; // Electrónica: 300-1000
                        case 1 -> 200 + random.nextDouble() * 800; // Electrodomésticos: 200-1000
                        case 2 -> 400 + random.nextDouble() * 900; // Informática: 400-1300
                        case 3 -> 150 + random.nextDouble() * 850; // Telefonía: 150-1000
                        case 4 -> 100 + random.nextDouble() * 500; // Audio/Video: 100-600
                        default -> 99.99;
                    };
                    
                    articulo.setPrecioVenta(Math.round(precioBase * 100.0) / 100.0);
                    articulo.setStock(10 + random.nextInt(91)); // Stock entre 10 y 100
                    articulo.setPrecioPromedioPonderado(Math.round(precioBase * 0.75 * 100.0) / 100.0); // PVP al 75%
                    
                    // 10% de productos descatalogados
                    if (productCounter % 10 == 0) {
                        articulo.setBorrado(true);
                    }
                    
                    articulos.add(articulo);
                    productCounter++;
                }

                articuloRepository.saveAll(articulos);
                System.out.println("Se han creado " + articulos.size() + " artículos de ejemplo");

                // Obtener todos los usuarios y artículos para crear ventas y compras
                List<Usuario> usuarios = usuarioRepository.findAll();
                List<Articulo> todosArticulos = articuloRepository.findAll();
                Usuario admin = usuarioRepository.findByUsername("admin").orElseThrow();
                
                // Crear 50 ventas (algunas multilinea y otras no)
                List<Venta> ventas = new ArrayList<>();
                
                for (int i = 0; i < 50; i++) {
                    // Seleccionar un usuario aleatorio (que no sea admin)
                    Usuario usuario = usuarios.stream()
                        .filter(u -> !u.getRol().equals(RolUsuario.ADMIN) || random.nextDouble() < 0.2) // 20% de probabilidad de que sea admin
                        .skip(random.nextInt((int) usuarios.stream().filter(u -> !u.getRol().equals(RolUsuario.ADMIN) || random.nextDouble() < 0.2).count()))
                        .findFirst()
                        .orElse(usuarios.get(1)); // Por si acaso
                    
                    Venta venta = new Venta();
                    venta.setUsuario(usuario);
                    
                    // Fecha aleatoria en los últimos 90 días
                    LocalDateTime fecha = LocalDateTime.now().minusDays(random.nextInt(90));
                    venta.setFecha(fecha);
                    
                    // Decidir cuántas líneas tendrá la venta (1-5)
                    int numLineas = 1 + random.nextInt(5);
                    
                    // Lista para evitar duplicados
                    List<Long> articulosIncluidos = new ArrayList<>();
                    
                    for (int j = 0; j < numLineas; j++) {
                        // Seleccionar un artículo aleatorio no borrado y que no esté ya en la venta
                        Articulo articulo;
                        do {
                            articulo = todosArticulos.get(random.nextInt(todosArticulos.size()));
                        } while (articulo.isBorrado() || articulosIncluidos.contains(articulo.getId()));
                        
                        articulosIncluidos.add(articulo.getId());
                        
                        // Cantidad entre 1 y 5
                        int cantidad = 1 + random.nextInt(5);
                        
                        DetalleVenta detalle = new DetalleVenta(articulo, cantidad);
                        detalle.setVenta(venta);
                        venta.agregarDetalle(detalle);
                    }
                    
                    ventas.add(venta);
                }
                
                ventaRepository.saveAll(ventas);
                System.out.println("Se han creado " + ventas.size() + " ventas de ejemplo");
                
                // Crear 50 compras (algunas multilinea y otras no)
                List<Compra> compras = new ArrayList<>();
                
                for (int i = 0; i < 50; i++) {
                    // Las compras solo las hacen los admin
                    Usuario usuario = usuarios.stream()
                        .filter(u -> u.getRol().equals(RolUsuario.ADMIN))
                        .skip(random.nextInt((int) usuarios.stream().filter(u -> u.getRol().equals(RolUsuario.ADMIN)).count()))
                        .findFirst()
                        .orElse(admin); // Por si acaso
                    
                    Compra compra = new Compra();
                    compra.setUsuario(usuario);
                    
                    // Fecha aleatoria en los últimos 180 días
                    LocalDateTime fecha = LocalDateTime.now().minusDays(random.nextInt(180));
                    compra.setFecha(fecha);
                    
                    // Decidir cuántas líneas tendrá la compra (1-7)
                    int numLineas = 1 + random.nextInt(7);
                    
                    // Lista para evitar duplicados
                    List<Long> articulosIncluidos = new ArrayList<>();
                    
                    for (int j = 0; j < numLineas; j++) {
                        // Seleccionar un artículo aleatorio que no esté ya en la compra
                        Articulo articulo;
                        do {
                            articulo = todosArticulos.get(random.nextInt(todosArticulos.size()));
                        } while (articulosIncluidos.contains(articulo.getId()));
                        
                        articulosIncluidos.add(articulo.getId());
                        
                        // Cantidad entre 5 y 20
                        int cantidad = 5 + random.nextInt(16);
                        
                        // Precio unitario entre el 60% y el 85% del precio de venta
                        double precioCompra = articulo.getPrecioVenta() * (0.6 + random.nextDouble() * 0.25);
                        precioCompra = Math.round(precioCompra * 100.0) / 100.0;
                        
                        DetalleCompra detalle = new DetalleCompra(articulo, cantidad, precioCompra);
                        detalle.setCompra(compra);
                        compra.agregarDetalle(detalle);
                    }
                    
                    compras.add(compra);
                }
                
                compraRepository.saveAll(compras);
                System.out.println("Se han creado " + compras.size() + " compras de ejemplo");
            }
        };
    }
}
