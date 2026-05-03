import PropTypes from 'prop-types';
import { useParams } from 'react-router-dom';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { getOrder, beginOrder, addPhoto, completeOrder } from '../services/serviceOrderApi';

const orderShape = PropTypes.shape({
  status: PropTypes.string.isRequired,
  workerId: PropTypes.string.isRequired,
  clientId: PropTypes.string.isRequired,
  photoUrls: PropTypes.arrayOf(PropTypes.string).isRequired,
});

const mutationShape = PropTypes.shape({
  mutate: PropTypes.func.isRequired,
});

function OrderHeader({ order }) {
  return (
    <section>
      <h1>Service Order</h1>
      <p>Status: {order.status}</p>
      <p>Worker: {order.workerId}</p>
      <p>Client: {order.clientId}</p>
    </section>
  );
}

OrderHeader.propTypes = { order: orderShape.isRequired };

function PhotoList({ photoUrls }) {
  return (
    <ul aria-label="Evidence photos">
      {photoUrls.map((url) => (
        <li key={url}>
          <img src={url} alt="Service evidence" />
        </li>
      ))}
    </ul>
  );
}

PhotoList.propTypes = { photoUrls: PropTypes.arrayOf(PropTypes.string).isRequired };

function OrderActions({ order, beginMut, photoMut, completeMut }) {
  const handleFile = (e) => {
    const file = e.target.files?.[0];
    if (file) {
      photoMut.mutate(file);
    }
  };

  return (
    <section aria-label="Order actions">
      {order.status === 'STARTED' && (
        <button type="button" onClick={() => beginMut.mutate()}>
          Begin Order
        </button>
      )}
      {order.status === 'IN_PROCESS' && (
        <>
          <label htmlFor="photo-input">Upload Evidence Photo</label>
          <input
            id="photo-input"
            type="file"
            accept="image/*"
            capture="environment"
            onChange={handleFile}
          />
        </>
      )}
      {order.status === 'IN_PROCESS' && order.photoUrls.length > 0 && (
        <button type="button" onClick={() => completeMut.mutate()}>
          Complete Order
        </button>
      )}
    </section>
  );
}

OrderActions.propTypes = {
  order: orderShape.isRequired,
  beginMut: mutationShape.isRequired,
  photoMut: mutationShape.isRequired,
  completeMut: mutationShape.isRequired,
};

export default function ServiceOrderInProgress() {
  const { id } = useParams();
  const qc = useQueryClient();

  const {
    data: order,
    isLoading,
    isError,
  } = useQuery({
    queryKey: ['order', id],
    queryFn: () => getOrder(id),
  });

  const invalidate = () => qc.invalidateQueries({ queryKey: ['order', id] });
  const beginMut = useMutation({ mutationFn: () => beginOrder(id), onSuccess: invalidate });
  const photoMut = useMutation({ mutationFn: (file) => addPhoto(id, file), onSuccess: invalidate });
  const completeMut = useMutation({ mutationFn: () => completeOrder(id), onSuccess: invalidate });

  if (isLoading) return <p>Loading...</p>;
  if (isError) return <p>Order not found.</p>;

  return (
    <main>
      <OrderHeader order={order} />
      <PhotoList photoUrls={order.photoUrls} />
      <OrderActions
        order={order}
        beginMut={beginMut}
        photoMut={photoMut}
        completeMut={completeMut}
      />
    </main>
  );
}
