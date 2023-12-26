package com.lipeng.dialog;

import cn.hutool.core.util.StrUtil;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.ToolWindowId;
import com.lipeng.util.ZkUtil;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.util.Objects;

/**
 * @author lipeng 2023/12/22
 */
public class InsertNodeDialog extends DialogWrapper {
    JTextField pathField;
    JTextField dataField;
    private final JTree dataTree;
    private final DefaultMutableTreeNode selectedNode;
    private final Project project;

    public InsertNodeDialog(JTree dataTree, DefaultMutableTreeNode selectedNode, Project project) {
        super(true);
        super.setSize(350, 180);

        setTitle("输入节点信息");

        this.project = project;
        this.dataTree = dataTree;
        this.selectedNode = selectedNode;

        init();
    }

    @Override
    protected JComponent createCenterPanel() {
        if (Objects.isNull(selectedNode)) {
            return null;
        }
        JPanel dialogPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        pathField = new JTextField(20);
        EventQueue.invokeLater(() -> pathField.requestFocusInWindow());

        dataField = new JTextField(20);

        JLabel pathLabel = new JLabel("* path：");
        JLabel dataLabel = new JLabel("  data：");

        dialogPanel.add(pathLabel);
        dialogPanel.add(pathField);
        dialogPanel.add(dataLabel);
        dialogPanel.add(dataField);

        return dialogPanel;
    }

    @Override
    protected void doOKAction() {
        String path = pathField.getText();
        if (StrUtil.isBlank(path)) {
            Messages.showErrorDialog("Can not be empty", "Error");
            return;
        }

        DefaultTreeModel model = (DefaultTreeModel) dataTree.getModel();
        boolean result = ZkUtil.createNode(String.valueOf(model.getRoot()), path, dataField.getText(), selectedNode);
        if (result) {
            selectedNode.add(new DefaultMutableTreeNode(path));
            model.nodeStructureChanged(selectedNode);
            new Notification(ToolWindowId.PROJECT_VIEW, "Insert success", NotificationType.INFORMATION).notify(project);
        } else {
            new Notification(ToolWindowId.PROJECT_VIEW, "Insert error", NotificationType.ERROR).notify(project);
        }
        this.close(0);
    }
}
