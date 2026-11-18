const cop = new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP', maximumFractionDigits: 0 });
export const money = (n) => cop.format(Number(n || 0));

export const fecha = (iso) =>
  iso ? new Date(iso).toLocaleString('es-CO', { day: '2-digit', month: 'short', hour: '2-digit', minute: '2-digit' }) : '';

export const ESTADOS = {
  RECIBIDO: { label: 'Recibido', siguiente: 'EN_PREPARACION', accion: 'Empezar a preparar' },
  EN_PREPARACION: { label: 'En preparación', siguiente: 'LISTO', accion: 'Marcar como listo' },
  LISTO: { label: 'Listo para recoger', siguiente: 'ENTREGADO', accion: 'Marcar como entregado' },
  ENTREGADO: { label: 'Entregado' },
  CANCELADO: { label: 'Cancelado' },
};
export const PASOS = ['RECIBIDO', 'EN_PREPARACION', 'LISTO', 'ENTREGADO'];
export const cancelable = (estado) => estado === 'RECIBIDO' || estado === 'EN_PREPARACION';
export const IMPUESTO = 0.08; // solo para el estimado del carrito; el total real lo calcula Pedidos

export const DIAS = ['LUNES', 'MARTES', 'MIERCOLES', 'JUEVES', 'VIERNES', 'SABADO', 'DOMINGO'];
export const diaLabel = (d) => d.charAt(0) + d.slice(1).toLowerCase().replace('miercoles', 'miércoles').replace('sabado', 'sábado');
