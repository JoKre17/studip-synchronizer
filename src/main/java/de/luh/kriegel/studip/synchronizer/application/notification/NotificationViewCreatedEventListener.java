package de.luh.kriegel.studip.synchronizer.application.notification;

public interface NotificationViewCreatedEventListener {

	public void onNotificationViewCreated(NotificationView notificationView);
	
	public class NotificationViewCreatedEvent {
		
		private NotificationView notificationView;
		
		public NotificationViewCreatedEvent(NotificationView notificationView) {
			this.notificationView = notificationView;
		}

		public NotificationView getNotificationView() {
			return notificationView;
		}
		
	}
}
