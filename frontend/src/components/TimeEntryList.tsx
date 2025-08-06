import React from 'react';
import { TimeEntry } from '../types';

interface TimeEntryListProps {
  timeEntries: TimeEntry[];
  showUserColumn?: boolean;
}

const TimeEntryList: React.FC<TimeEntryListProps> = ({ timeEntries, showUserColumn = false }) => {
  if (timeEntries.length === 0) {
    return (
      <div className="jira-time-entry-list">
        <p className="jira-no-entries">No time entries logged yet.</p>
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
    <div className="jira-time-entry-list">
      <h4 className="jira-time-entry-list-title">Time Entries</h4>
      <div className="jira-time-entries-table">
        <table className="jira-time-entries-table-content">
          <thead>
            <tr>
              <th className="jira-time-entry-header">Date</th>
              {showUserColumn && <th className="jira-time-entry-header">User</th>}
              <th className="jira-time-entry-header">Hours</th>
            </tr>
          </thead>
          <tbody>
            {timeEntries.map((entry) => (
              <tr key={entry.id} className="jira-time-entry-row">
                <td className="jira-time-entry-cell">{formatDate(entry.createdAt)}</td>
                {showUserColumn && <td className="jira-time-entry-cell">{entry.userName}</td>}
                <td className="jira-time-entry-cell jira-time-entry-hours">{entry.hoursSpent.toFixed(1)}h</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default TimeEntryList; 