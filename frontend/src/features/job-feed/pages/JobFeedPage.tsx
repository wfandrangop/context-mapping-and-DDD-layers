import { useState, useEffect } from 'react';
import { Container, Row, Col, Spinner } from 'react-bootstrap';
import { JobCard } from '../components/JobCard';
import { getOpenJobs } from '../services/jobFeedService';
import { JobPost } from '../types/jobFeed.types';
import { JobDetailsDrawer } from '../../job-details/components/JobDetailsDrawer';

export const JobFeedPage = () => {
  const [jobs, setJobs] = useState<JobPost[]>([]);
  const [loading, setLoading] = useState(true);
  const [selectedJobId, setSelectedJobId] = useState<string | null>(null);
  const [drawerOpen, setDrawerOpen] = useState(false);

  useEffect(() => {
    getOpenJobs().then(data => {
      setJobs(data);
      setLoading(false);
    });
  }, []);

  const handleJobClick = (jobId: string) => {
    setSelectedJobId(jobId);
    setDrawerOpen(true);
  };

  const handleCloseDrawer = () => {
    setDrawerOpen(false);
    setSelectedJobId(null);
  };

  if (loading) {
    return (
      <div className="text-center py-5">
        <Spinner animation="border" variant="primary" />
        <p className="mt-2 text-muted">Cargando trabajos...</p>
      </div>
    );
  }

  return (
    <Container className="py-3">
      <h2 className="mb-4">Trabajos disponibles ({jobs.length})</h2>
      
      <Row>
        <Col>
          {jobs.map(job => (
            <JobCard key={job.id} job={job} onClick={() => handleJobClick(job.id)} />
          ))}
        </Col>
      </Row>
      
      <JobDetailsDrawer 
        jobId={selectedJobId}
        isOpen={drawerOpen}
        onClose={handleCloseDrawer}
      />
    </Container>
  );
};