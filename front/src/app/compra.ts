// src/app/compra.ts
export class Compra {
    id: number = 0;
    total: number = 0;
    usuario: {
      id: number;
      username: string;
    } = { id: 0, username: "" };
    detalles: DetalleCompra[] = [];
  }
  
  export class DetalleCompra {
    idArticulo: number = 0;
    nombreArticulo: string = "";
    descripcionArticulo: string = "";
    codigoBarrasArticulo: string = "";
    familiaArticulo: string = "";
    cantidad: number = 0;
    subtotal: number = 0;
  }
  