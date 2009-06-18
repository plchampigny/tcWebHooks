package webhook.teamcity.settings;

import java.util.Iterator;
import java.util.List;

import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.serverSide.MainConfigProcessor;
import jetbrains.buildServer.serverSide.SBuildServer;

import org.jdom.Element;

import webhook.WebHookProxyConfig;


public class WebHookMainSettings implements MainConfigProcessor {
	private WebHookMainConfig webHookMainConfig;
	private SBuildServer server;

	public WebHookMainSettings(SBuildServer server){
		Loggers.SERVER.debug(this.getClass().getName() + " :: Constructor called");
		this.server = server;
		webHookMainConfig = new WebHookMainConfig();
	}

    public void register(){
        Loggers.SERVER.debug(this.getClass().getName() + ":: Registering");
        server.registerExtension(MainConfigProcessor.class, "webhooks", this);
    }
    
	public String getProxyListasString(){
		return this.webHookMainConfig.getProxyListasString();
	}
	
    @SuppressWarnings("unchecked")
    public void readFrom(Element rootElement)
    /* Is passed an Element by TC, and is expected to persist it to the settings object.
     * Old settings should be overwritten.
     */
    {
    	Loggers.SERVER.info("WebHookMainSettings: re-reading main settings");
    	Loggers.SERVER.debug(this.getClass().getName() + ":readFrom :: " + rootElement.toString());
    	WebHookMainConfig tempConfig = new WebHookMainConfig();
    	Element webhooksElement = rootElement.getChild("webhooks");
    	if(webhooksElement != null){
			Element proxyElement = webhooksElement.getChild("proxy");
	        if(proxyElement != null)
	        {
	        	if (proxyElement.getAttribute("proxyShortNames") != null){
	        		tempConfig.setProxyShortNames(Boolean.parseBoolean(proxyElement.getAttributeValue("proxyShortNames")));
	        	}
	        	
	        	if (proxyElement.getAttribute("host") != null){
	        		tempConfig.setProxyHost(proxyElement.getAttributeValue("host"));
	        	}
	        	
	        	if (proxyElement.getAttribute("port") != null){
	        		tempConfig.setProxyPort(Integer.parseInt(proxyElement.getAttributeValue("port")));
	        	}
	
	        	if (proxyElement.getAttribute("username") != null){
	        		tempConfig.setProxyUsername(proxyElement.getAttributeValue("username"));
	        	}
	
	        	if (proxyElement.getAttribute("password") != null){
	        		tempConfig.setProxyPassword(proxyElement.getAttributeValue("password"));
	        	}
	
	    		List<Element> namedChildren = proxyElement.getChildren("noproxy");
	            if(namedChildren.size() > 0) {
					for(Iterator<Element> i = namedChildren.iterator(); i.hasNext();)
			        {
						Element e = i.next();
						String url = e.getAttributeValue("url");
						tempConfig.addNoProxyUrl(url);
						Loggers.SERVER.debug(this.getClass().getName() + ":readFrom :: noProxyUrl " + url);
			        }
		        }
	    	}
    	}
        this.webHookMainConfig = tempConfig;
    }

    public void writeTo(Element parentElement)
    /* Is passed an (probably empty) Element by TC, which is expected to be populated from the settings
     * in memory. 
     */
    {
    	Loggers.SERVER.info("WebHookMainSettings: re-writing main settings");
    	Loggers.SERVER.debug(this.getClass().getName() + ":writeTo :: " + parentElement.toString());
    	Element el = new Element("webhooks");
        if(	  webHookMainConfig != null 
           && webHookMainConfig.getProxyHost() != null && webHookMainConfig.getProxyHost().length() > 0
           && webHookMainConfig.getProxyPort() != null && webHookMainConfig.getProxyPort() > 0 )
        {
        	el.addContent(webHookMainConfig.getAsElement());
			Loggers.SERVER.debug(this.getClass().getName() + "writeTo :: proxyHost " + webHookMainConfig.getProxyHost().toString());
			Loggers.SERVER.debug(this.getClass().getName() + "writeTo :: proxyPort " + webHookMainConfig.getProxyPort().toString());
        }
        parentElement.addContent(el);
    }
    
    public String getProxyForUrl(String url){
    	return this.webHookMainConfig.getProxyConfigForUrl(url).getProxyHost();
    }

    
	public void dispose() {
		Loggers.SERVER.debug(this.getClass().getName() + ":dispose() called");
	}

	public WebHookProxyConfig getProxyConfigForUrl(String url) {
		return this.webHookMainConfig.getProxyConfigForUrl(url);	}
}
