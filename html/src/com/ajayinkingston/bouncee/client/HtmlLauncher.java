package com.ajayinkingston.bouncee.client;

import com.ajayinkingston.splats.Splats;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;

public class HtmlLauncher extends GwtApplication {
	
		public static Splats splats;
	
        @Override
        public GwtApplicationConfiguration getConfig () {
        	GwtApplicationConfiguration config = new GwtApplicationConfiguration(getWindowInnerWidth(), getWindowInnerHeight());
        	config.preferFlash = false;
        	
        	 Element element = Document.get().getElementById("embed-html");
             VerticalPanel panel = new VerticalPanel();
             panel.setWidth("100%");
             panel.setHeight("100%");
             panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
             panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
             element.appendChild(panel.getElement());
             config.rootPanel = panel;
             
//             tempWorkaroundForIssue4590();
             
            return config;
        }

        @Override
        public ApplicationListener createApplicationListener () {
        	setLoadingListener(new LoadingListener() {
                @Override
                public void beforeSetup() {

                }

                @Override
                public void afterSetup() {
                    setupResizeHook();
                }
            });
        	splats = new Splats(0);
            return splats;
        }
        
        @Override
        public void onModuleLoad() {
        	super.onModuleLoad();
//            tempWorkaroundForIssue4590();
        }
        
        native static int getWindowInnerWidth()/*-{
        	return $wnd.innerWidth;
    	}-*/;

        native static int getWindowInnerHeight()/*-{
        	return $wnd.innerHeight;
    	}-*/;
        
        native void setupResizeHook() /*-{
	        var htmlLauncher_onWindowResize = $entry(@com.ajayinkingston.bouncee.client.HtmlLauncher::handleResize());
	        $wnd.addEventListener('resize', htmlLauncher_onWindowResize, false);
    	}-*/;
        
        // somewhere in your GwtApplicationLuncher
        private native void tempWorkaroundForIssue4590() /*-{
            $wnd.soundManager.audioFormats.mp3.required = false;
            $wnd.soundManager.audioFormats.mp4.required = false;
        }-*/;
        
        public static void handleResize() {
            Element element = Document.get().getElementById("embed-html");
            NodeList<Element> nl = element.getElementsByTagName("canvas");
            Element canvas = nl.getItem(0);

        	canvas.getStyle().setWidth(getWindowInnerWidth(), Style.Unit.PX);
            canvas.getStyle().setHeight(getWindowInnerHeight(), Style.Unit.PX);
            
            splats.resize(getWindowInnerWidth(), getWindowInnerHeight());
        }

}