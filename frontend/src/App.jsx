import { Navigate, NavLink, Route, Routes, useNavigate } from 'react-router-dom';
import { useSession } from './session.jsx';
import Inicio from './pages/Inicio.jsx';
import Restaurantes from './pages/Restaurantes.jsx';
import MenuRestaurante from './pages/MenuRestaurante.jsx';
import MisPedidos from './pages/MisPedidos.jsx';
import Cocina from './pages/Cocina.jsx';
import Admin from './pages/Admin.jsx';

const NAV = {
  cliente: [['/cliente', 'Restaurantes'], ['/cliente/pedidos', 'Mis pedidos']],
  empleado: [['/empleado', 'Pedidos']],
  admin: [['/admin', 'Administración']],
};
const ROL_LABEL = { cliente: 'Cliente', empleado: 'Empleado', admin: 'Administrador' };

function Guard({ rol, children }) {
  const s = useSession();
  return s.rol === rol ? children : <Navigate to="/" replace />;
}

export default function App() {
  const s = useSession();
  const nav = useNavigate();
  return (
    <>
      {s.rol && (
        <header className="topbar">
          <button className="brand" onClick={() => nav(`/${s.rol === 'cliente' ? 'cliente' : s.rol}`)}>
            Food<span>Express</span>
          </button>
          <nav>
            {NAV[s.rol].map(([to, label]) => (
              <NavLink key={to} to={to} end>{label}</NavLink>
            ))}
          </nav>
          <div className="who">
            <span className="chip">
              {ROL_LABEL[s.rol]}{s.rol === 'cliente' ? ` #${s.clienteId}` : ''}
            </span>
            <button className="btn ghost sm" onClick={() => { s.setRol(null); nav('/'); }}>Cambiar rol</button>
          </div>
        </header>
      )}
      <main className="page">
        <Routes>
          <Route path="/" element={<Inicio />} />
          <Route path="/cliente" element={<Guard rol="cliente"><Restaurantes /></Guard>} />
          <Route path="/cliente/restaurante/:id" element={<Guard rol="cliente"><MenuRestaurante /></Guard>} />
          <Route path="/cliente/pedidos" element={<Guard rol="cliente"><MisPedidos /></Guard>} />
          <Route path="/empleado" element={<Guard rol="empleado"><Cocina /></Guard>} />
          <Route path="/admin" element={<Guard rol="admin"><Admin /></Guard>} />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </main>
      <div className="toasts" aria-live="polite">
        {s.toasts.map((t) => (
          <div key={t.id} className={`toast ${t.tipo}`}>{t.texto}</div>
        ))}
      </div>
    </>
  );
}
