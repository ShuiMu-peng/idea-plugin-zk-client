package com.lipeng.util;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowId;

/**
 * @author lipeng 2023/12/27
 */
public class MsgUtil {
    public static void setProject(Project project) {
        MsgUtil.project = project;
    }

    private static Project project;

    public static void print(String msg, NotificationType type) {
        new Notification(ToolWindowId.PROJECT_VIEW, msg, type).notify(project);
    }

}
