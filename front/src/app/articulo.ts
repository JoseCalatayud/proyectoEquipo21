export class Articulo {

    constructor(public id: number,
        public nombre: string,
        public descripcion: string,
        public familia: string,
        public fotografia: string,
        public precioVenta: number,
        public precioCompra: number,
        public stock: number) {

    }

}
