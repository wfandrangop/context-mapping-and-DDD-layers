import { Navbar, Nav, Container } from 'react-bootstrap';
import { useNavigate, useLocation } from 'react-router-dom';

export const BottomNavigation = () => {
  const navigate = useNavigate();
  const location = useLocation();

  const tabs = [
    { id: 'feed', label: 'Trabajos', icon: '🔍', path: '/' },
    { id: 'active', label: 'Mis trabajos', icon: '✅', path: '/active-jobs' },
    { id: 'profile', label: 'Perfil', icon: '👤', path: '/profile' },
  ];

  return (
    <Navbar fixed="bottom" bg="white" className="bottom-nav">
      <Container className="justify-content-around">
        {tabs.map(tab => (
          <Nav.Link
            key={tab.id}
            onClick={() => navigate(tab.path)}
            className={`bottom-nav-link ${location.pathname === tab.path ? 'active' : ''}`}
          >
            <div className="fs-4">{tab.icon}</div>
            <small>{tab.label}</small>
          </Nav.Link>
        ))}
      </Container>
    </Navbar>
  );
};
