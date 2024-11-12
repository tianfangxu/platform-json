package com.tfx;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import org.jetbrains.annotations.NotNull;

/**
 * @author tianfx
 * @date 2024/11/4 18:37
 */
public class JspoRun extends AnAction {

    public JspoRun() {
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        // 获取项目基础路径
        if (anActionEvent.getProject() != null) {
            ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(anActionEvent.getProject());
            ToolWindow toolWindow = toolWindowManager.getToolWindow("Jspo");
            if (toolWindow != null) {
                toolWindow.show(new Runnable() {
                    @Override
                    public void run() {
                    }
                });
                if (toolWindow.getContentManager().getContentCount() < 1) {
                    MainUI mainUi = new MainUI();
                    mainUi.createToolWindowContent(anActionEvent.getProject(), toolWindow);
                }
            }
        }
    }
}
