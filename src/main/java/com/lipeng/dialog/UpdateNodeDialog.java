package com.lipeng.dialog;

import cn.hutool.core.util.StrUtil;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.lipeng.util.MsgUtil;
import com.lipeng.util.ZkUtil;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author lipeng 2023/12/22
 */
public class UpdateNodeDialog extends DialogWrapper {
    JTextField pathField;
    JTextField dataField;
    private final JTree dataTree;
    private final DefaultMutableTreeNode selectedNode;
    private final String host;

    public UpdateNodeDialog(JTree dataTree, DefaultMutableTreeNode selectedNode) {
        super(true);
        super.setSize(350, 180);

        setTitle("修改节点信息");

        this.dataTree = dataTree;
        this.selectedNode = selectedNode;
        DefaultTreeModel model = (DefaultTreeModel) dataTree.getModel();
        host = String.valueOf(model.getRoot());

        init();
    }

    @Override
    protected JComponent createCenterPanel() {
        if (Objects.isNull(selectedNode)) {
            return null;
        }
        JPanel dialogPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        pathField = new JTextField(20);
        pathField.setText(ZkUtil.getSelectNodePath(selectedNode));
        pathField.setEditable(false);

        dataField = new JTextField(20);
        dataField.setText(ZkUtil.getValue(host, selectedNode));
        EventQueue.invokeLater(() -> dataField.requestFocusInWindow());

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

        boolean result = ZkUtil.updateNode(host, path, dataField.getText());
        if (result) {
            TreeSelectionEvent selectionEvent = new TreeSelectionEvent(dataTree, new TreePath(selectedNode.getPath()), true, null, null);
            Arrays.stream(dataTree.getTreeSelectionListeners()).forEach(listener -> listener.valueChanged(selectionEvent));
            MsgUtil.print("Update success", NotificationType.INFORMATION);
        } else {
            MsgUtil.print("Update failed", NotificationType.ERROR);
        }
        this.close(0);
    }
}
