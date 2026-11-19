import { useEffect } from 'react';
import { ESTADOS, PASOS } from '../format.js';

export function Modal({ titulo, onClose, children, ancho }) {
  useEffect(() => {
    const k = (e) => e.key === 'Escape' && onClose();
    window.addEventListener('keydown', k);
    return () => window.removeEventListener('keydown', k);
  }, [onClose]);
  return (
    <div className="overlay" onMouseDown={(e) => e.target === e.currentTarget && onClose()}>
      <div className="modal" role="dialog" aria-modal="true" aria-label={titulo} style={ancho ? { maxWidth: ancho } : undefined}>
        <div className="modal-head">
          <h3>{titulo}</h3>
          <button className="btn ghost sm" onClick={onClose} aria-label="Cerrar">Cerrar</button>
        </div>
        {children}
      </div>
    </div>
  );
}

export const Field = ({ label, children, hint }) => (
  <label className="field">
    <span>{label}</span>
    {children}
    {hint && <small>{hint}</small>}
  </label>
);

export const ErrorBox = ({ error }) => (error ? <div className="error" role="alert">{error}</div> : null);
export const Empty = ({ titulo, texto, children }) => (
  <div className="empty"><strong>{titulo}</strong>{texto && <p>{texto}</p>}{children}</div>
);

export function EstadoBadge({ estado }) {
  return <span className={`estado e-${estado}`}>{ESTADOS[estado]?.label || estado}</span>;
}

// Línea de progreso del pedido (el cliente ve en qué paso va)
export function Progreso({ estado }) {
  if (estado === 'CANCELADO') return <div className="progreso cancelado"><span>Pedido cancelado</span></div>;
  const idx = PASOS.indexOf(estado);
  return (
    <ol className="progreso" aria-label={`Estado: ${ESTADOS[estado]?.label}`}>
      {PASOS.map((p, i) => (
        <li key={p} className={i < idx ? 'hecho' : i === idx ? 'actual' : ''}>
          <i />{ESTADOS[p].label}
        </li>
      ))}
    </ol>
  );
}

export function ItemsPedido({ pedido }) {
  return (
    <ul className="items">
      {pedido.items.map((it) => (
        <li key={it.id}>
          <b>{it.cantidad}×</b> {it.nombrePlato}
          {it.extras?.length > 0 && <em> + {it.extras.map((e) => e.nombreExtra).join(', ')}</em>}
          {it.notas && <div className="nota">“{it.notas}”</div>}
        </li>
      ))}
    </ul>
  );
}
