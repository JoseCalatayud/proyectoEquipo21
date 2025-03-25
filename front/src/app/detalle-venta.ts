export class DetalleVenta {

    constructor(
      public idArticulo: number,
      public nombreArticulo: string,
      public descripcionArticulo: string,
      public codigoBarrasArticulo: string,
      public familiaArticulo: string,
      public cantidad: number,
      public subtotal: number
    ) {}
  }
  