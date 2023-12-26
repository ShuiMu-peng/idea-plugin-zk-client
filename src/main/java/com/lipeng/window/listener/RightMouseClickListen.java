package com.lipeng.window.listener;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.JBMenuItem;
import com.intellij.openapi.ui.JBPopupMenu;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.ToolWindowId;
import com.lipeng.dialog.InsertNodeDialog;
import com.lipeng.dialog.UpdateNodeDialog;
import com.lipeng.util.ZkUtil;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author lipeng 2023/12/23
 */
public class RightMouseClickListen extends MouseAdapter {
    private final JTree dataTree;
    private final Project project;

    public RightMouseClickListen(JTree dataTree, Project project) {
        this.dataTree = dataTree;
        this.project = project;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        process(e);
    }

    private void process(MouseEvent e) {
        if (e.getButton() != 3) {
            return;
        }

        // 获取选中的节点
        int row = dataTree.getClosestRowForLocation(e.getX(), e.getY());
        dataTree.setSelectionRow(row);

        // JPopupMenu popupMenu = new JPopupMenu();
        JBPopupMenu popupMenu = new JBPopupMenu();

        initAddMethod(popupMenu);

        if (row != 0) {
            initUpdateMethod(popupMenu);

            // 仅叶子节点允许删除
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) dataTree.getLastSelectedPathComponent();
            if (selectedNode.isLeaf()) {
                initDeleteMethod(popupMenu);
            }
        }

        initRefreshMethod(popupMenu);

        popupMenu.show(e.getComponent(), e.getX(), e.getY());
    }

    private void initRefreshMethod(JPopupMenu popupMenu) {
        JMenuItem menuItem = new JBMenuItem("refresh");
        menuItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) dataTree.getLastSelectedPathComponent();
                selectedNode.removeAllChildren();
                ZkUtil.initChild(String.valueOf(dataTree.getModel().getRoot()), selectedNode, 0);
            }
        });
        popupMenu.add(menuItem);
    }

    private void initDeleteMethod(JPopupMenu popupMenu) {
        JMenuItem menuItem = new JBMenuItem("delete");
        menuItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) dataTree.getLastSelectedPathComponent();
                int yesNoDialog = Messages.showYesNoDialog("Confirm to delete this node？[" + ZkUtil.getSelectNodePath(selectedNode) + "]", "Tip", Messages.getQuestionIcon());
                if (yesNoDialog == JOptionPane.YES_OPTION) {
                    DefaultTreeModel model = (DefaultTreeModel) dataTree.getModel();
                    boolean b = ZkUtil.deleteNode(String.valueOf(model.getRoot()), selectedNode);
                    if (b) {
                        model.removeNodeFromParent(selectedNode);
                        new Notification(ToolWindowId.PROJECT_VIEW, "Delete success", NotificationType.INFORMATION).notify(project);
                    } else {
                        Messages.showMessageDialog("Delete failed", "Error", Messages.getErrorIcon());
                    }
                }
            }
        });
        popupMenu.add(menuItem);
    }

    private void initUpdateMethod(JPopupMenu popupMenu) {
        JMenuItem menuItem = new JBMenuItem("update");
        menuItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) dataTree.getLastSelectedPathComponent();
                new UpdateNodeDialog(dataTree, selectedNode, project).show();
            }
        });
        popupMenu.add(menuItem);
    }

    private void initAddMethod(JPopupMenu popupMenu) {
        JMenuItem menuItem = new JBMenuItem("add");
        menuItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) dataTree.getLastSelectedPathComponent();
                new InsertNodeDialog(dataTree, selectedNode, project).show();
            }
        });
        popupMenu.add(menuItem);
    }
}