import React, { useState, useEffect } from 'react';
import { Shield } from 'lucide-react';
import { getUsers, getRequests } from './services/api';
import UserForm from './components/UserForm';
import UserTable from './components/UserTable';
import RequestsPanel from './components/RequestsPanel';

function App() {
  const [users, setUsers] = useState([]);
  const [requests, setRequests] = useState([]);
  const [editingUser, setEditingUser] = useState(null);

  const fetchUsers = async () => {
    try {
      const data = await getUsers();
      setUsers(data);
    } catch (err) {
      console.error("Failed to fetch users");
    }
  };

  const fetchRequests = async () => {
    try {
      const data = await getRequests();
      setRequests(data);
      // We also update users list here because if a request was approved,
      // the user table needs to reflect the change automatically.
      fetchUsers();
    } catch (err) {
      console.error("Failed to fetch requests");
    }
  };

  // Initial load
  useEffect(() => {
    fetchUsers();
    fetchRequests();

    // Setup polling for live updates from Telegram
    const interval = setInterval(() => {
      fetchRequests();
    }, 5000);

    return () => clearInterval(interval);
  }, []);

  return (
    <div className="app-container">
      <header className="header">
        <div className="header-icon">
          <Shield size={32} />
        </div>
        <div>
          <h1>Secure Approval System</h1>
          <p className="text-muted mt-1">Actions require Telegram multi-factor authorization</p>
        </div>
      </header>

      <div className="grid-layout">
        {/* Left Column: Data and Forms */}
        <div className="flex" style={{ flexDirection: 'column', gap: '2rem' }}>
          <UserForm 
            fetchUsers={fetchUsers} 
            fetchRequests={fetchRequests} 
            editingUser={editingUser}
            setEditingUser={setEditingUser}
          />
          <UserTable 
            users={users} 
            setEditingUser={setEditingUser} 
            fetchRequests={fetchRequests} 
          />
        </div>

        {/* Right Column: Live Audit Log */}
        <div>
           <RequestsPanel requests={requests} />
        </div>
      </div>
    </div>
  );
}

export default App;
