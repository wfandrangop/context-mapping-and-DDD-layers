import { Card, Badge } from 'react-bootstrap';
import { JobPost } from '../types/jobFeed.types';

interface JobCardProps {
  job: JobPost;
  onClick: () => void;
}

const urgencyColors = {
  IMMEDIATE: 'danger',
  HIGH: 'warning',
  MEDIUM: 'info',
  LOW: 'success',
};

const urgencyLabels = {
  IMMEDIATE: '⚠️ Urgente',
  HIGH: '🔴 Alta',
  MEDIUM: '🟡 Media',
  LOW: '🟢 Baja',
};

export const JobCard = ({ job, onClick }: JobCardProps) => {
  return (
    <Card 
      className="mb-3 card-hover cursor-pointer"
      onClick={onClick}
    >
      <Card.Body>
        <div className="d-flex justify-content-between align-items-start mb-2">
          <Card.Title className="mb-0">{job.title}</Card.Title>
          <Badge bg={urgencyColors[job.urgency]}>
            {urgencyLabels[job.urgency]}
          </Badge>
        </div>
        
        <Card.Text className="text-muted small">
          {job.description}
        </Card.Text>
        
        <div className="d-flex justify-content-between align-items-center mt-3">
          <div>
            <span className="text-muted small">Presupuesto</span>
            <h4 className="text-success mb-0">S/ {job.budget}</h4>
          </div>
          <div className="text-end">
            <span className="text-muted small">Ubicación</span>
            <p className="mb-0">📍 {job.location}</p>
          </div>
        </div>
        
        <div className="mt-3">
          {job.requiredSkills.slice(0, 3).map((skill, idx) => (
            <Badge key={idx} bg="secondary" className="me-1">
              {skill}
            </Badge>
          ))}
          {job.requiredSkills.length > 3 && (
            <span className="text-muted small">
              +{job.requiredSkills.length - 3} más
            </span>
          )}
        </div>
      </Card.Body>
    </Card>
  );
};