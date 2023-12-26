package com.lipeng.window;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

/**
 * @author lipeng 2023/12/20
 */
public class ZookeeperToolWindowFactory implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ZkWindow zkWindow = new ZkWindow(project, toolWindow);
        Content content = ContentFactory.getInstance().createContent(zkWindow.getContainer(), "", false);
        toolWindow.getContentManager().addContent(content);
    }

}
