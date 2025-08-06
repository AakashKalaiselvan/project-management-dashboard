import React, { useState, useEffect } from 'react';
import { notificationApi } from '../services/api';
import { Notification } from '../types';

const NotificationBell: React.FC = () => {
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [unreadCount, setUnreadCount] = useState(0);
  const [showDropdown, setShowDropdown] = useState(false);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    loadNotifications();
    // Poll for new notifications every 30 seconds
    const interval = setInterval(loadNotifications, 30000);
    return () => clearInterval(interval);
  }, []);

  const loadNotifications = async () => {
    try {
      const [notificationsData, countData] = await Promise.all([
        notificationApi.getUnread(),
        notificationApi.getUnreadCount()
      ]);
      setNotifications(notificationsData);
      setUnreadCount(countData.unreadCount);
    } catch (error) {
      console.error('Error loading notifications:', error);
    }
  };

  const handleMarkAsRead = async (notificationId: number) => {
    try {
      await notificationApi.markAsRead(notificationId);
      await loadNotifications();
    } catch (error) {
      console.error('Error marking notification as read:', error);
    }
  };

  const handleMarkAllAsRead = async () => {
    setLoading(true);
    try {
      await notificationApi.markAllAsRead();
      await loadNotifications();
      setShowDropdown(false);
    } catch (error) {
      console.error('Error marking all notifications as read:', error);
    } finally {
      setLoading(false);
    }
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  return (
    <div className="jira-notification-bell">
      <button
        className="jira-notification-button"
        onClick={() => setShowDropdown(!showDropdown)}
        title="Notifications"
      >
        <span className="jira-bell-icon">🔔</span>
        {unreadCount > 0 && (
          <span className="jira-notification-badge">{unreadCount}</span>
        )}
      </button>

      {showDropdown && (
        <div className="jira-notification-dropdown">
          <div className="jira-notification-header">
            <h4 className="jira-notification-title">Notifications</h4>
            {unreadCount > 0 && (
              <button
                className="jira-mark-all-read-btn"
                onClick={handleMarkAllAsRead}
                disabled={loading}
              >
                {loading ? 'Marking...' : 'Mark All Read'}
              </button>
            )}
          </div>

          <div className="jira-notification-list">
            {notifications.length === 0 ? (
              <p className="jira-no-notifications">No unread notifications</p>
            ) : (
              notifications.map((notification) => (
                <div
                  key={notification.id}
                  className={`jira-notification-item ${!notification.read ? 'jira-unread' : ''}`}
                  onClick={() => handleMarkAsRead(notification.id)}
                >
                  <div className="jira-notification-message">
                    {notification.message}
                  </div>
                  <div className="jira-notification-time">
                    {formatDate(notification.createdAt)}
                  </div>
                </div>
              ))
            )}
          </div>

          {notifications.length > 0 && (
            <div className="jira-notification-footer">
              <small className="jira-notification-hint">
                Click to mark as read
              </small>
            </div>
          )}
        </div>
      )}
    </div>
  );
};

export default NotificationBell; 