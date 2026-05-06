import { useState, useEffect } from 'react';
import { getReputation, TradeReputationResponse } from '../services/profileService';
import { Container, Card, Spinner, Row, Col, Badge } from 'react-bootstrap';

export const ProfilePage = () => {
  const [reputation, setReputation] = useState<TradeReputationResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [notFound, setNotFound] = useState(false);

  // TODO: Después del login, esto vendrá del usuario autenticado
  const profileId = localStorage.getItem('profileId') || 'test-profile-id';

  useEffect(() => {
    loadReputation();
  }, []);

  const loadReputation = async () => {
    setLoading(true);
    setNotFound(false);
    try {
      const data = await getReputation(profileId);
      setReputation(data);
    } catch (error: any) {
      if (error.message === 'NOT_FOUND') {
        setNotFound(true);
      }
      console.error('Error:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="text-center py-5">
        <Spinner animation="border" variant="primary" />
      </div>
    );
  }

  if (notFound) {
    return (
      <Container className="py-4" style={{ maxWidth: '500px' }}>
        <Card className="text-center p-4 shadow-sm">
          <div className="fs-1 mb-3">🆕</div>
          <h3>¡Bienvenido!</h3>
          <p className="text-muted">Aún no tienes reputación</p>
          <p className="small text-muted">Completa tu primer trabajo para comenzar</p>
        </Card>
      </Container>
    );
  }

  if (!reputation) {
    return null;
  }

  const getBadgeVariant = (badge: string) => {
    if (badge === 'GOLD') return 'warning';
    if (badge === 'SILVER') return 'secondary';
    return 'success';
  };

  return (
    <Container className="py-4" style={{ maxWidth: '500px' }}>
      <Card className="shadow-sm mb-4">
        <Card.Body className="text-center p-4">
          <div className="display-1 mb-2">⭐</div>
          <h2 className="display-4 fw-bold text-primary">{reputation.confidenceScore}</h2>
          <p className="text-muted">Puntaje de confianza</p>
          
          <Row className="mt-4">
            <Col>
              <div className="fs-3 fw-bold text-success">{reputation.successfulJobs}</div>
              <small className="text-muted">Exitosos</small>
            </Col>
            <Col>
              <div className="fs-3 fw-bold text-danger">{reputation.cancelledJobs}</div>
              <small className="text-muted">Cancelados</small>
            </Col>
          </Row>
        </Card.Body>
      </Card>

      {reputation.badges.length > 0 && (
        <Card className="shadow-sm">
          <Card.Body>
            <h5 className="mb-3">🏅 Medallas</h5>
            <div className="d-flex gap-2 flex-wrap">
              {reputation.badges.map((badge) => (
                <Badge key={badge} bg={getBadgeVariant(badge)} pill className="fs-6 py-2 px-3">
                  {badge}
                </Badge>
              ))}
            </div>
          </Card.Body>
        </Card>
      )}
    </Container>
  );
};