package de.luh.kriegel.studip.synchronizer.application.event;

import java.io.IOException;

import de.luh.kriegel.studip.synchronizer.application.notification.NotificationView;
import de.luh.kriegel.studip.synchronizer.application.view.WebViewFitContent;
import de.luh.kriegel.studip.synchronizer.event.Event;
import de.luh.kriegel.studip.synchronizer.event.EventView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;

public class HtmlEventEntryView implements EventView {

	private WebViewFitContent webViewFitContent;
	
	@FXML
	private StackPane heightLimitingStackPane;
	
	private Event event;
	private Node contentNode;
	
	public HtmlEventEntryView(String htmlContent, Event event) {
		
		this.event = event;
		
		FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/HtmlEventEntry.fxml"));
		loader.setController(this);

		try {
			contentNode = loader.load();

			Platform.runLater(() -> {
				
				webViewFitContent = new WebViewFitContent(NotificationView.HTML_STYLE + htmlContent);
				heightLimitingStackPane.getChildren().add(webViewFitContent);
//				WebEngine webEngine = webView.getEngine();
//				webEngine.loadContent(NotificationView.HTML_STYLE + htmlContent);
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	public Event getEvent() {
		return event;
	}
	
	@Override
	public Node getEventNode() {
		return contentNode;
	}
	
	public WebView getWebView() {
		return webViewFitContent.getWebView();
	}
	
}
