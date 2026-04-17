import React from 'react';
import { Activity, Clock } from 'lucide-react';

const RequestsPanel = ({ requests }) => {
  
  const getStatusBadge = (status) => {
    switch (status) {
      case 'PENDING': return <span className="badge badge-pending">Pending Admin</span>;
      case 'APPROVED': return <span className="badge badge-success">Approved</span>;
      case 'REJECTED': return <span className="badge badge-danger">Rejected</span>;
      default: return null;
    }
  };

  const getActionFormat = (action) => {
    if (action === 'ADD') return <span style={{ color: 'var(--success)' }}>ADD</span>;
    if (action === 'UPDATE') return <span style={{ color: 'var(--primary)' }}>UPDATE</span>;
    if (action === 'DELETE') return <span style={{ color: 'var(--danger)' }}>DELETE</span>;
  };

  const parsePayload = (payloadString) => {
    try {
      return JSON.parse(payloadString);
    } catch {
      return {};
    }
  };

  return (
    <div className="card" style={{ height: '100%' }}>
      <h2 className="card-title">
        <Activity size={20} className="text-primary" />
        Live Audit Log
      </h2>
      <p className="text-sm text-muted mb-4 flex items-center gap-1">
        <Clock size={14} /> Auto-refreshing every 5 seconds
      </p>

      <div className="scroll-panel">
        {requests.length === 0 ? (
           <div style={{ padding: '2rem', textAlign: 'center', color: 'var(--text-muted)' }}>
             No requests have been made yet.
           </div>
        ) : (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
            {requests.map(req => {
              const data = parsePayload(req.payload);
              return (
                <div key={req.id} style={{
                  padding: '1rem',
                  border: '1px solid var(--border-color)',
                  borderRadius: 'var(--radius-md)',
                  background: req.status === 'PENDING' ? 'rgba(245, 158, 11, 0.05)' : 'white'
                }}>
                  <div className="flex justify-between items-center mb-2">
                    <span className="font-medium text-sm">
                      Req #{req.id} • {getActionFormat(req.actionType)}
                    </span>
                    {getStatusBadge(req.status)}
                  </div>
                  <div className="text-sm">
                    {data.name && <div><strong className="text-muted">Name:</strong> {data.name}</div>}
                    {data.email && <div><strong className="text-muted">Email:</strong> {data.email}</div>}
                    {data.userId && <div><strong className="text-muted">Target ID:</strong> #{data.userId}</div>}
                  </div>
                  <div className="text-muted text-sm mt-2" style={{ fontSize: '0.75rem' }}>
                    {new Date(req.createdAt).toLocaleString()}
                  </div>
                </div>
              );
            })}
          </div>
        )}
      </div>
    </div>
  );
};

export default RequestsPanel;
