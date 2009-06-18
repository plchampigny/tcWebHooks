package webhook.teamcity.extension;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import jetbrains.buildServer.serverSide.ProjectManager;
import jetbrains.buildServer.serverSide.SBuildType;
import jetbrains.buildServer.serverSide.settings.ProjectSettingsManager;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.web.openapi.PagePlaces;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import jetbrains.buildServer.web.openapi.buildType.BuildTypeTab;

import org.jetbrains.annotations.NotNull;

import webhook.teamcity.settings.WebHookProjectSettings;



public class WebHookBuildTabExtension extends BuildTypeTab {
	WebHookProjectSettings settings;
	ProjectSettingsManager projSettings;

	protected WebHookBuildTabExtension(
			PagePlaces pagePlaces, ProjectManager projectManager, ProjectSettingsManager settings, WebControllerManager manager) {
		//super(myTitle, myTitle, null, projectManager);
		super("webHooks", "WebHooks", manager, projectManager);
		this.projSettings = settings;
	}

	public boolean isAvailable(@NotNull HttpServletRequest request) {
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void fillModel(Map model, HttpServletRequest request,
			 @NotNull SBuildType buildType, SUser user) {
		this.settings = 
			(WebHookProjectSettings)this.projSettings.getSettings(buildType.getProject().getProjectId(), "webhooks");
    	String message = this.settings.getWebHooksAsString();
    	model.put("webHookCount", this.settings.getWebHooksCount());
    	if (this.settings.getWebHooksCount() == 0){
    		model.put("noWebHooks", "true");
    		model.put("webHooks", "false");
    	} else {
    		model.put("noWebHooks", "false");
    		model.put("webHooks", "true");
    		model.put("webHookList", this.settings.getWebHooksAsList());
    		model.put("webHooksDisabled", !this.settings.isEnabled());
    	}
    	model.put("messages", message);
    	model.put("messages2", "blasdflkdfl");
    	model.put("projectId", buildType.getProject().getProjectId());
    	model.put("projectName", buildType.getProject().getName());
	}

	@Override
	public String getIncludeUrl() {
		return "/plugins/WebHook/WebHook/buildWebHookTab.jsp";
	}


	
}
