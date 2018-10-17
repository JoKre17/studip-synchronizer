package de.luh.kriegel.studip.synchronizer.application.view;

import java.util.Set;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Worker.State;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSException;

public final class WebViewFitContent extends Region {

	final WebView webview = new WebView();
	final WebEngine webEngine = webview.getEngine();

	public WebViewFitContent(String content) {
		webview.setPrefHeight(5);
		webview.setPrefWidth(5);

		widthProperty().addListener(new ChangeListener<Object>() {
			@Override
			public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
				Double width = (Double) newValue;
				webview.setPrefWidth(width);
				adjustHeight();
			}
		});

		webview.getEngine().getLoadWorker().stateProperty().addListener(new ChangeListener<State>() {
			@Override
			public void changed(ObservableValue<? extends State> arg0, State oldState, State newState) {
				if (newState == State.SUCCEEDED) {
					adjustHeight();
				}
			}
		});

		webview.getChildrenUnmodifiable().addListener(new ListChangeListener<Node>() {
			@Override
			public void onChanged(Change<? extends Node> change) {
				Set<Node> scrolls = webview.lookupAll(".scroll-bar");
				for (Node scroll : scrolls) {
					scroll.setVisible(false);
				}
			}
		});

		setContent(content);
		getChildren().add(webview);
	}
	
	public WebView getWebView() {
		return webview;
	}

	public void setContent(final String content) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				webEngine.loadContent(getHtml(content));
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						adjustHeight();
					}
				});
			}
		});
	}

	@Override
	protected void layoutChildren() {
		double w = getWidth();
		double h = getHeight();
		layoutInArea(webview, 0, 0, w, h, 0, HPos.CENTER, VPos.CENTER);
	}

	private void adjustHeight() {
        Platform.runLater(new Runnable(){
            @Override                                
            public void run() {
                try {
                    Object result = webEngine.executeScript("document.getElementById('mydiv').offsetHeight");
                    if (result instanceof Integer) {
                        Integer i = (Integer) result;
                        webview.setPrefHeight(i.doubleValue() + 20);
                    }
                } catch (JSException e) { } 
            }               
        });
    }

	private String getHtml(String content) {
		return "<html><body>" + "<div id=\"mydiv\">" + content + "</div>" + "</body></html>";
	}

}