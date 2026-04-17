import React from 'react';
import { Edit2, Trash2, Database } from 'lucide-react';
import toast from 'react-hot-toast';
import { deleteUser } from '../services/api';

const UserTable = ({ users, setEditingUser, fetchRequests }) => {

  const handleDelete = async (user) => {
    if (window.confirm(`Are you sure you want to request deletion for ${user.name}?`)) {
      try {
        await deleteUser(user.id);
        toast.success(`Delete request sent for ${user.name} 🚀`);
        fetchRequests();
      } catch (err) {
        toast.error('Failed to send delete request');
      }
    }
  };

  return (
    <div className="card">
      <div className="flex items-center justify-between mb-4">
        <h2 className="card-title mb-0">
          <Database size={20} className="text-primary" />
          Active Users Database
        </h2>
        <span className="badge badge-success px-3">{users.length} Total</span>
      </div>
      
      <div className="table-container">
        {users.length === 0 ? (
          <div style={{ padding: '3rem', textAlign: 'center', color: 'var(--text-muted)' }}>
            No users in the database yet. Add one and approve it!
          </div>
        ) : (
          <table className="table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Name</th>
                <th>Email</th>
                <th style={{ textAlign: 'right' }}>Actions</th>
              </tr>
            </thead>
            <tbody>
              {users.map(user => (
                <tr key={user.id}>
                  <td className="font-medium text-muted">#{user.id}</td>
                  <td className="font-medium">{user.name}</td>
                  <td>{user.email}</td>
                  <td style={{ textAlign: 'right' }}>
                    <div className="flex justify-end gap-2">
                       <button 
                         className="btn btn-icon" 
                         onClick={() => setEditingUser(user)}
                         title="Edit User"
                       >
                         <Edit2 size={16} />
                       </button>
                       <button 
                         className="btn btn-icon danger" 
                         onClick={() => handleDelete(user)}
                         title="Delete User"
                       >
                         <Trash2 size={16} />
                       </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
};

export default UserTable;
