import React, { useState, useEffect } from 'react';
import toast from 'react-hot-toast';
import { Send, Users, ShieldCheck } from 'lucide-react';
import { createUser, updateUser } from '../services/api';

const UserForm = ({ fetchUsers, fetchRequests, editingUser, setEditingUser }) => {
  const [formData, setFormData] = useState({ name: '', email: '' });
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (editingUser) {
      setFormData({ name: editingUser.name, email: editingUser.email });
    } else {
      setFormData({ name: '', email: '' });
    }
  }, [editingUser]);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      if (editingUser) {
        await updateUser(editingUser.id, formData);
        toast.success('Update request sent to Telegram for approval! 🚀');
        setEditingUser(null);
      } else {
        await createUser(formData);
        toast.success('Creation request sent to Telegram for approval! 🚀');
      }
      setFormData({ name: '', email: '' });
      fetchRequests(); // Refresh pending requests immediately
    } catch (err) {
      toast.error(err.response?.data?.error || 'Something went wrong');
    } finally {
      setLoading(false);
    }
  };

  const cancelEdit = () => {
    setEditingUser(null);
    setFormData({ name: '', email: '' });
  };

  return (
    <div className="card mb-4">
      <h2 className="card-title">
        <Users size={20} className="text-primary" />
        {editingUser ? 'Edit User Request' : 'Add New User'}
      </h2>
      <p className="text-sm text-muted mb-4">
        <ShieldCheck size={14} className="inline mr-1" />
        All changes require Telegram admin approval.
      </p>
      
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label className="form-label">Full Name</label>
          <input
            type="text"
            name="name"
            value={formData.name}
            onChange={handleChange}
            className="form-input"
            placeholder="e.g. John Doe"
            required
            minLength={2}
          />
        </div>
        
        <div className="form-group">
          <label className="form-label">Email Address</label>
          <input
            type="email"
            name="email"
            value={formData.email}
            onChange={handleChange}
            className="form-input"
            placeholder="john@example.com"
            required
          />
        </div>

        <div className="flex gap-2">
          <button type="submit" className="btn btn-primary flex-1" disabled={loading}>
            {loading ? 'Sending...' : (
              <>
                <Send size={16} />
                Send Request
              </>
            )}
          </button>
          
          {editingUser && (
             <button type="button" className="btn" onClick={cancelEdit} style={{ border: '1px solid #e2e8f0' }}>
               Cancel
             </button>
          )}
        </div>
      </form>
    </div>
  );
};

export default UserForm;
