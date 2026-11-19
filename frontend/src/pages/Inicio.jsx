import { useNavigate } from 'react-router-dom';
import { useSession } from '../session.jsx';

export default function Inicio() {
  const s = useSession();
  const nav = useNavigate();
  const entrar = (rol) => { s.setRol(rol); nav(`/${rol}`); };

  return (
    <div className="inicio">
      <h1>Food<span>Express</span></h1>
      <p className="lead">Pide en los restaurantes de tu zona y sigue tu pedido hasta que esté listo.</p>

      <div className="roles">
        <section className="rol">
          <h2>Cliente</h2>
          <p>Elige un restaurante, personaliza tus platos con extras y haz seguimiento a tus pedidos.</p>
          <label className="field">
            <span>Tu número de cliente</span>
            <input type="number" min="1" value={s.clienteId}
              onChange={(e) => s.setClienteId(Math.max(1, Number(e.target.value) || 1))} />
            <small>Tus pedidos se guardan con este número. Usa el mismo para ver tu historial.</small>
          </label>
          <button className="btn primary" onClick={() => entrar('cliente')}>Entrar como cliente</button>
        </section>

        <section className="rol">
          <h2>Empleado</h2>
          <p>Ve los pedidos de todos los clientes y avánzalos: preparación, listo y entregado.</p>
          <button className="btn primary" onClick={() => entrar('empleado')}>Entrar como empleado</button>
        </section>

        <section className="rol">
          <h2>Administrador</h2>
          <p>Crea restaurantes, categorías, platos y extras, y controla qué se puede pedir.</p>
          <button className="btn primary" onClick={() => entrar('admin')}>Entrar como administrador</button>
        </section>
      </div>
    </div>
  );
}
