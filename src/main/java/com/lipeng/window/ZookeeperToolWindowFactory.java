package com.lipeng.window;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.lipeng.util.MsgUtil;
import org.jetbrains.annotations.NotNull;

/**
 * @author lipeng 2023/12/20
 */
public class ZookeeperToolWindowFactory implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ZkWindow zkWindow = new ZkWindow();
        Content content = ContentFactory.getInstance().createContent(zkWindow.getContainer(), "", false);
        toolWindow.getContentManager().addContent(content);

        // 初始化消息工具对象
        MsgUtil.setProject(project);
    }

}
