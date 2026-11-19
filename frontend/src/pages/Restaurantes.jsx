import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { api } from '../api.js';
import { Empty, ErrorBox } from '../components/ui.jsx';

export default function Restaurantes() {
  const [lista, setLista] = useState(null);
  const [error, setError] = useState('');

  useEffect(() => {
    api.restaurantes().then(setLista).catch((e) => setError(e.message));
  }, []);

  return (
    <>
      <h1 className="titulo">¿Qué se te antoja hoy?</h1>
      <ErrorBox error={error} />
      {!lista && !error && <p className="muted">Cargando restaurantes…</p>}
      {lista?.length === 0 && (
        <Empty titulo="Todavía no hay restaurantes" texto="Un administrador debe crear el primero." />
      )}
      <div className="grid">
        {lista?.map((r) => {
          const abierto = r.estadoOperativo === 'ABIERTO';
          return (
            <Link key={r.id} to={`/cliente/restaurante/${r.id}`} className="resto">
              <div className="resto-top">
                <h2>{r.nombre}</h2>
                <span className={`pill ${abierto ? 'ok' : 'off'}`}>{abierto ? 'Abierto' : 'Cerrado'}</span>
              </div>
              {r.descripcion && <p>{r.descripcion}</p>}
              <small>{r.direccion} · {r.telefono}</small>
            </Link>
          );
        })}
      </div>
    </>
  );
}
