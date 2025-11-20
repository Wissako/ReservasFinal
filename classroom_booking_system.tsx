import React, { useState, useEffect } from 'react';
import { Calendar, LogOut, Plus, Trash2, Home, BookOpen, Clock, Users } from 'lucide-react';

const API_URL = 'http://localhost:8080';

export default function ReservasApp() {
  const [currentPage, setCurrentPage] = useState('login');
  const [token, setToken] = useState(localStorage.getItem('token') || '');
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  // Login/Register
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [nombre, setNombre] = useState('');
  const [isLogin, setIsLogin] = useState(true);

  // Reservas
  const [reservas, setReservas] = useState([]);
  const [aulas, setAulas] = useState([]);
  const [horarios, setHorarios] = useState([]);

  // Form data
  const [formData, setFormData] = useState({
    fecha: '',
    motivo: '',
    numAsistentes: '',
    aulaId: '',
    horariosIds: []
  });

  // Cargar perfil al iniciar
  useEffect(() => {
    if (token) {
      cargarPerfil();
      setCurrentPage('dashboard');
    }
  }, [token]);

  // Funciones de utilidad
  const showError = (msg) => { setError(msg); setTimeout(() => setError(''), 4000); };
  const showSuccess = (msg) => { setSuccess(msg); setTimeout(() => setSuccess(''), 3000); };

  // AUTH
  const handleLogin = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      const res = await fetch(`${API_URL}/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password })
      });
      if (!res.ok) throw new Error('Credenciales incorrectas');
      const data = await res.json();
      localStorage.setItem('token', data.token);
      setToken(data.token);
      showSuccess('¡Bienvenido!');
    } catch (err) {
      showError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleRegister = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      const res = await fetch(`${API_URL}/auth/register`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password, nombre })
      });
      if (!res.ok) throw new Error('Error en el registro');
      showSuccess('Registrado correctamente. Inicia sesión.');
      setIsLogin(true);
      setEmail('');
      setPassword('');
      setNombre('');
    } catch (err) {
      showError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const cargarPerfil = async () => {
    try {
      const res = await fetch(`${API_URL}/usuario/perfil`, {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (!res.ok) throw new Error('No autorizado');
      const data = await res.json();
      setUser(data);
    } catch (err) {
      logout();
    }
  };

  const logout = () => {
    setToken('');
    setUser(null);
    localStorage.removeItem('token');
    setCurrentPage('login');
    setFormData({ fecha: '', motivo: '', numAsistentes: '', aulaId: '', horariosIds: [] });
  };

  // RESERVAS
  const cargarReservas = async () => {
    try {
      const res = await fetch(`${API_URL}/reservas/mis-reservas`, {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (!res.ok) throw new Error('Error cargando reservas');
      const data = await res.json();
      setReservas(data);
    } catch (err) {
      showError(err.message);
    }
  };

  const cargarAulas = async () => {
    try {
      const res = await fetch(`${API_URL}/aulas`, {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (!res.ok) throw new Error('Error cargando aulas');
      const data = await res.json();
      setAulas(data);
    } catch (err) {
      showError(err.message);
    }
  };

  const cargarHorarios = async () => {
    try {
      const res = await fetch(`${API_URL}/horarios`, {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (!res.ok) throw new Error('Error cargando horarios');
      const data = await res.json();
      setHorarios(data);
    } catch (err) {
      showError(err.message);
    }
  };

  const crearReserva = async (e) => {
    e.preventDefault();
    if (formData.horariosIds.length === 0) {
      showError('Selecciona al menos un horario');
      return;
    }
    setLoading(true);
    try {
      const payload = {
        fecha: formData.fecha,
        motivo: formData.motivo,
        numAsistentes: parseInt(formData.numAsistentes),
        aulaId: parseInt(formData.aulaId),
        horariosIds: formData.horariosIds.map(Number)
      };
      const res = await fetch(`${API_URL}/reservas`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(payload)
      });
      if (!res.ok) throw new Error('Error creando reserva');
      showSuccess('Reserva creada exitosamente');
      setFormData({ fecha: '', motivo: '', numAsistentes: '', aulaId: '', horariosIds: [] });
      await cargarReservas();
      setCurrentPage('reservas');
    } catch (err) {
      showError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const eliminarReserva = async (id) => {
    if (!window.confirm('¿Estás seguro de eliminar esta reserva?')) return;
    setLoading(true);
    try {
      const res = await fetch(`${API_URL}/reservas/${id}`, {
        method: 'DELETE',
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (!res.ok) throw new Error('Error eliminando reserva');
      showSuccess('Reserva eliminada');
      await cargarReservas();
    } catch (err) {
      showError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const toggleHorario = (id) => {
    setFormData(prev => ({
      ...prev,
      horariosIds: prev.horariosIds.includes(id)
        ? prev.horariosIds.filter(h => h !== id)
        : [...prev.horariosIds, id]
    }));
  };

  // UI Components
  const AlertBox = ({ type, message }) => (
    <div className={`p-4 rounded-lg mb-4 ${type === 'error' ? 'bg-red-100 text-red-700' : 'bg-green-100 text-green-700'}`}>
      {message}
    </div>
  );

  const NavBar = () => (
    <nav className="bg-gradient-to-r from-blue-600 to-indigo-600 text-white p-4 shadow-lg">
      <div className="max-w-6xl mx-auto flex justify-between items-center">
        <div className="flex items-center gap-2">
          <BookOpen size={28} />
          <h1 className="text-2xl font-bold">Sistema de Reservas</h1>
        </div>
        {user && (
          <div className="flex items-center gap-4">
            <span className="text-sm">{user.email}</span>
            <button onClick={logout} className="flex items-center gap-1 bg-red-500 hover:bg-red-600 px-4 py-2 rounded">
              <LogOut size={18} /> Salir
            </button>
          </div>
        )}
      </div>
    </nav>
  );

  // PAGES
  if (!token) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-blue-500 via-indigo-600 to-purple-600">
        <NavBar />
        <div className="flex items-center justify-center min-h-[calc(100vh-70px)]">
          <div className="w-full max-w-md">
            <div className="bg-white rounded-lg shadow-2xl p-8">
              <h2 className="text-3xl font-bold text-center mb-8 text-gray-800">
                {isLogin ? 'Iniciar Sesión' : 'Registrarse'}
              </h2>
              {error && <AlertBox type="error" message={error} />}
              {success && <AlertBox type="success" message={success} />}
              <div className="space-y-4">
                {!isLogin && (
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">Nombre</label>
                    <input
                      type="text"
                      value={nombre}
                      onChange={(e) => setNombre(e.target.value)}
                      className="w-full px-4 py-2 border-2 border-gray-300 rounded-lg focus:outline-none focus:border-blue-500"
                      placeholder="Tu nombre"
                      required
                    />
                  </div>
                )}
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Email</label>
                  <input
                    type="email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    className="w-full px-4 py-2 border-2 border-gray-300 rounded-lg focus:outline-none focus:border-blue-500"
                    placeholder="tu@email.com"
                    required
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Contraseña</label>
                  <input
                    type="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    className="w-full px-4 py-2 border-2 border-gray-300 rounded-lg focus:outline-none focus:border-blue-500"
                    placeholder="••••••••"
                    required
                  />
                </div>
                <button
                  onClick={isLogin ? handleLogin : handleRegister}
                  disabled={loading}
                  className="w-full bg-gradient-to-r from-blue-600 to-indigo-600 text-white py-2 rounded-lg font-semibold hover:shadow-lg transition disabled:opacity-50"
                >
                  {loading ? 'Cargando...' : isLogin ? 'Iniciar Sesión' : 'Registrarse'}
                </button>
              </div>
              <p className="text-center mt-4">
                {isLogin ? '¿No tienes cuenta?' : '¿Ya tienes cuenta?'}
                <button
                  onClick={() => {
                    setIsLogin(!isLogin);
                    setError('');
                    setSuccess('');
                  }}
                  className="text-blue-600 font-semibold ml-1 hover:underline"
                >
                  {isLogin ? 'Registrarse' : 'Inicia sesión'}
                </button>
              </p>
            </div>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-100">
      <NavBar />
      
      {/* Sidebar */}
      <div className="max-w-6xl mx-auto flex gap-4 p-4">
        <div className="w-48 bg-white rounded-lg shadow p-4 h-fit sticky top-4">
          <div className="space-y-2">
            <button
              onClick={() => { setCurrentPage('dashboard'); cargarReservas(); }}
              className={`w-full flex items-center gap-2 px-4 py-2 rounded transition ${
                currentPage === 'dashboard' ? 'bg-blue-600 text-white' : 'hover:bg-gray-100'
              }`}
            >
              <Home size={18} /> Dashboard
            </button>
            <button
              onClick={() => { setCurrentPage('reservas'); cargarReservas(); }}
              className={`w-full flex items-center gap-2 px-4 py-2 rounded transition ${
                currentPage === 'reservas' ? 'bg-blue-600 text-white' : 'hover:bg-gray-100'
              }`}
            >
              <Calendar size={18} /> Mis Reservas
            </button>
            <button
              onClick={() => { setCurrentPage('nueva-reserva'); cargarAulas(); cargarHorarios(); }}
              className={`w-full flex items-center gap-2 px-4 py-2 rounded transition ${
                currentPage === 'nueva-reserva' ? 'bg-blue-600 text-white' : 'hover:bg-gray-100'
              }`}
            >
              <Plus size={18} /> Nueva Reserva
            </button>
            <button
              onClick={() => { setCurrentPage('aulas'); cargarAulas(); }}
              className={`w-full flex items-center gap-2 px-4 py-2 rounded transition ${
                currentPage === 'aulas' ? 'bg-blue-600 text-white' : 'hover:bg-gray-100'
              }`}
            >
              <BookOpen size={18} /> Aulas
            </button>
            <button
              onClick={() => { setCurrentPage('horarios'); cargarHorarios(); }}
              className={`w-full flex items-center gap-2 px-4 py-2 rounded transition ${
                currentPage === 'horarios' ? 'bg-blue-600 text-white' : 'hover:bg-gray-100'
              }`}
            >
              <Clock size={18} /> Horarios
            </button>
          </div>
        </div>

        {/* Main Content */}
        <div className="flex-1">
          {error && <AlertBox type="error" message={error} />}
          {success && <AlertBox type="success" message={success} />}

          {/* Dashboard */}
          {currentPage === 'dashboard' && (
            <div className="bg-white rounded-lg shadow p-6">
              <h2 className="text-2xl font-bold mb-6">Bienvenido, {user?.nombre || 'Usuario'}</h2>
              <div className="grid grid-cols-2 gap-4">
                <div className="bg-blue-50 p-4 rounded-lg">
                  <p className="text-gray-600">Rol</p>
                  <p className="text-2xl font-bold text-blue-600">{user?.roles.includes('ADMIN') ? 'Administrador' : 'Profesor'}</p>
                </div>
                <div className="bg-green-50 p-4 rounded-lg">
                  <p className="text-gray-600">Email</p>
                  <p className="text-lg font-semibold text-green-600">{user?.email}</p>
                </div>
              </div>
            </div>
          )}

          {/* Mis Reservas */}
          {currentPage === 'reservas' && (
            <div className="bg-white rounded-lg shadow p-6">
              <h2 className="text-2xl font-bold mb-4">Mis Reservas</h2>
              {reservas.length === 0 ? (
                <p className="text-gray-500 text-center py-8">No tienes reservas aún</p>
              ) : (
                <div className="space-y-4">
                  {reservas.map((r) => (
                    <div key={r.id} className="border-l-4 border-blue-500 p-4 bg-gray-50 rounded hover:shadow transition">
                      <div className="flex justify-between items-start">
                        <div className="flex-1">
                          <h3 className="font-bold text-lg text-blue-600">{r.aula.nombre}</h3>
                          <p className="text-gray-600"><strong>Fecha:</strong> {new Date(r.fecha).toLocaleString('es-ES')}</p>
                          <p className="text-gray-600"><strong>Motivo:</strong> {r.motivo}</p>
                          <p className="text-gray-600 flex items-center gap-1"><Users size={16} /> <strong>Asistentes:</strong> {r.numAsistentes}</p>
                          <p className="text-gray-600"><strong>Horarios:</strong> {r.horarios.map(h => `${h.diaSemana} S${h.sesionDia}`).join(', ')}</p>
                        </div>
                        <button
                          onClick={() => eliminarReserva(r.id)}
                          className="bg-red-500 hover:bg-red-600 text-white px-4 py-2 rounded flex items-center gap-2 transition"
                        >
                          <Trash2 size={18} /> Eliminar
                        </button>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          )}

          {/* Nueva Reserva */}
          {currentPage === 'nueva-reserva' && (
            <div className="bg-white rounded-lg shadow p-6">
              <h2 className="text-2xl font-bold mb-4">Nueva Reserva</h2>
              <div className="space-y-4 max-w-2xl">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Fecha y Hora</label>
                  <input
                    type="datetime-local"
                    value={formData.fecha}
                    onChange={(e) => setFormData({ ...formData, fecha: e.target.value })}
                    className="w-full px-4 py-2 border-2 border-gray-300 rounded-lg focus:outline-none focus:border-blue-500"
                    required
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Motivo</label>
                  <textarea
                    value={formData.motivo}
                    onChange={(e) => setFormData({ ...formData, motivo: e.target.value })}
                    className="w-full px-4 py-2 border-2 border-gray-300 rounded-lg focus:outline-none focus:border-blue-500"
                    rows="3"
                    placeholder="Describe la actividad"
                    required
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Número de Asistentes</label>
                  <input
                    type="number"
                    value={formData.numAsistentes}
                    onChange={(e) => setFormData({ ...formData, numAsistentes: e.target.value })}
                    className="w-full px-4 py-2 border-2 border-gray-300 rounded-lg focus:outline-none focus:border-blue-500"
                    min="1"
                    required
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Aula</label>
                  <select
                    value={formData.aulaId}
                    onChange={(e) => setFormData({ ...formData, aulaId: e.target.value })}
                    className="w-full px-4 py-2 border-2 border-gray-300 rounded-lg focus:outline-none focus:border-blue-500"
                    required
                  >
                    <option value="">Selecciona un aula</option>
                    {aulas.map(a => (
                      <option key={a.id} value={a.id}>
                        {a.nombre} (Capacidad: {a.capacidad})
                      </option>
                    ))}
                  </select>
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">Horarios (selecciona al menos uno)</label>
                  <div className="grid grid-cols-2 gap-2">
                    {horarios.map(h => (
                      <label key={h.id} className="flex items-center gap-2 p-2 border rounded hover:bg-gray-50 cursor-pointer">
                        <input
                          type="checkbox"
                          checked={formData.horariosIds.includes(h.id)}
                          onChange={() => toggleHorario(h.id)}
                          className="w-4 h-4"
                        />
                        <span className="text-sm">{h.diaSemana} S{h.sesionDia} ({h.horaInicio}-{h.horaFin})</span>
                      </label>
                    ))}
                  </div>
                </div>
                <button
                  onClick={crearReserva}
                  disabled={loading}
                  className="w-full bg-gradient-to-r from-blue-600 to-indigo-600 text-white py-2 rounded-lg font-semibold hover:shadow-lg transition disabled:opacity-50"
                >
                  {loading ? 'Creando...' : 'Crear Reserva'}
                </button>
              </div>
            </div>
          )}

          {/* Aulas */}
          {currentPage === 'aulas' && (
            <div className="bg-white rounded-lg shadow p-6">
              <h2 className="text-2xl font-bold mb-4">Aulas Disponibles</h2>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                {aulas.map(a => (
                  <div key={a.id} className="border-l-4 border-green-500 p-4 bg-gray-50 rounded">
                    <h3 className="font-bold text-lg text-green-600">{a.nombre}</h3>
                    <p className="text-gray-600"><strong>Capacidad:</strong> {a.capacidad} personas</p>
                    <p className="text-gray-600"><strong>Ordenadores:</strong> {a.esAulaDeOrdenadores ? `Sí (${a.numeroOrdenadores})` : 'No'}</p>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* Horarios */}
          {currentPage === 'horarios' && (
            <div className="bg-white rounded-lg shadow p-6">
              <h2 className="text-2xl font-bold mb-4">Horarios Disponibles</h2>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                {horarios.map(h => (
                  <div key={h.id} className="border-l-4 border-purple-500 p-4 bg-gray-50 rounded">
                    <h3 className="font-bold text-lg text-purple-600">{h.diaSemana} - Sesión {h.sesionDia}</h3>
                    <p className="text-gray-600"><strong>Horario:</strong> {h.horaInicio} - {h.horaFin}</p>
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}