export class Articulo {
    id: number = 0;
    nombre: string = "";
    descripcion: string = "";
    familia: string = "";
    fotografia: string = "";
    precioVenta: number = 0;
    codigoBarras: string = "";
    stock: number = 0;
    borrado: boolean = false; // Agregamos la propiedad borrado
}