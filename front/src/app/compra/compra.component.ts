export interface Compra {
  id: number;
  total: number;
  usuario: {
    id: number;
    username: string;
  };
  detalles: DetalleCompra[];
}

export class DetalleCompra {

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
