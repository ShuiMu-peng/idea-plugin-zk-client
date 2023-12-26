package com.lipeng.window.listener;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import com.lipeng.util.ZkUtil;
import org.jetbrains.annotations.NotNull;

/**
 * @author lipeng 2023/12/26
 */
public class LifeCycleLister implements ProjectManagerListener {

    @Override
    public void projectClosed(@NotNull Project project) {
        ZkUtil.destroy();
    }
}
