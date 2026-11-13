/** Modelos del cliente: reflejan los esquemas de docs/openapi.yaml (API de P09). */

export interface Producto {
  id: number;
  codigo: string;
  nombre: string;
  precio: number;
  existencia: number;
}

export interface PartidaPeticion {
  productoId: number;
  cantidad: number;
}

export interface VentaPeticion {
  partidas: PartidaPeticion[];
}

export interface PartidaRespuesta {
  productoId: number;
  nombre: string;
  cantidad: number;
  precioUnitario: number;
  subtotal: number;
}

export interface VentaRespuesta {
  folio: string;
  total: number;
  partidas: PartidaRespuesta[];
}

/** Formato único de error de la API. Los códigos SIN_CONEXION y ERROR_DESCONOCIDO los genera el cliente. */
export interface ErrorApi {
  timestamp?: string;
  status: number;
  code: string;
  message: string;
  path?: string;
  details: string[];
}
