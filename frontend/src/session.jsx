import { createContext, useContext, useEffect, useMemo, useState, useCallback } from 'react';

// No hay login: el rol y el ID de cliente se eligen en la pantalla de inicio y se guardan en el navegador.
const Ctx = createContext(null);
const read = (k, d) => { try { return JSON.parse(localStorage.getItem(k)) ?? d; } catch { return d; } };
const write = (k, v) => { try { localStorage.setItem(k, JSON.stringify(v)); } catch { /* sin storage */ } };

export function SessionProvider({ children }) {
  const [rol, setRol] = useState(() => read('fx.rol', null));
  const [clienteId, setClienteId] = useState(() => read('fx.clienteId', 1));
  const [cart, setCart] = useState(() => read('fx.cart', { restauranteId: null, lineas: [] }));
  const [toasts, setToasts] = useState([]);

  useEffect(() => write('fx.rol', rol), [rol]);
  useEffect(() => write('fx.clienteId', clienteId), [clienteId]);
  useEffect(() => write('fx.cart', cart), [cart]);

  const toast = useCallback((texto, tipo = 'info') => {
    const id = Math.random().toString(36).slice(2);
    setToasts((t) => [...t.slice(-3), { id, texto, tipo }]);
    setTimeout(() => setToasts((t) => t.filter((x) => x.id !== id)), 6000);
  }, []);

  const value = useMemo(() => ({
    rol, setRol, clienteId, setClienteId, toasts, toast,
    cart,
    agregar: (restauranteId, linea) =>
      setCart((c) => ({
        restauranteId,
        lineas: [...(c.restauranteId === restauranteId ? c.lineas : []), { ...linea, key: Math.random().toString(36).slice(2) }],
      })),
    quitar: (key) => setCart((c) => ({ ...c, lineas: c.lineas.filter((l) => l.key !== key) })),
    cambiarCantidad: (key, cantidad) =>
      setCart((c) => ({ ...c, lineas: c.lineas.map((l) => (l.key === key ? { ...l, cantidad } : l)) })),
    vaciar: () => setCart({ restauranteId: null, lineas: [] }),
  }), [rol, clienteId, toasts, toast, cart]);

  return <Ctx.Provider value={value}>{children}</Ctx.Provider>;
}
export const useSession = () => useContext(Ctx);
