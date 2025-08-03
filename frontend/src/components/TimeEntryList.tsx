import React from 'react';
import { TimeEntry } from '../types';

interface TimeEntryListProps {
  timeEntries: TimeEntry[];
  showUserColumn?: boolean;
}

const TimeEntryList: React.FC<TimeEntryListProps> = ({ timeEntries, showUserColumn = false }) => {
  if (timeEntries.length === 0) {
    return (
      <div className="time-entry-list">
        <p className="no-entries">No time entries logged yet.</p>
      </div>
    );
  }

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  return (
    <div className="time-entry-list">
      <h4>Time Entries</h4>
      <div className="time-entries-table">
        <table>
          <thead>
            <tr>
              <th>Date</th>
              {showUserColumn && <th>User</th>}
              <th>Hours</th>
            </tr>
          </thead>
          <tbody>
            {timeEntries.map((entry) => (
              <tr key={entry.id}>
                <td>{formatDate(entry.createdAt)}</td>
                {showUserColumn && <td>{entry.userName}</td>}
                <td>{entry.hoursSpent.toFixed(1)}h</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default TimeEntryList; 