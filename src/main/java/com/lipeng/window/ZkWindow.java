package com.lipeng.window;

import cn.hutool.core.util.StrUtil;
import com.intellij.icons.AllIcons;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.JBColor;
import com.intellij.ui.treeStructure.Tree;
import com.lipeng.consts.GlobalConstants;
import com.lipeng.dialog.InsertHostDialog;
import com.lipeng.util.MsgUtil;
import com.lipeng.util.ZkUtil;
import com.lipeng.window.listener.AddChildSelectionListener;
import com.lipeng.window.listener.RightMouseClickListen;
import com.lipeng.window.listener.ShowDataInfoSelectionListener;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * @author lipeng 2023/12/20
 */
public class ZkWindow {
    private Tree dataTree;
    @Getter
    private JPanel container;
    private JTable nodeTableInfo;
    private JScrollPane scrollPane;
    private JScrollPane scrollPane2;
    private JToolBar toolBar;
    private ComboBox<String> zkHostListComBox;
    private final DefaultActionGroup actionGroup = new DefaultActionGroup();

    public ZkWindow() {
        scrollPane.setBorder(BorderFactory.createLineBorder(JBColor.lightGray, 1));
        scrollPane2.setBorder(BorderFactory.createLineBorder(JBColor.lightGray, 1));

        initTree();

        initToolBar();

        initZkHostList();
    }


    private void initToolBar() {
        toolBar.setFloatable(false);
        ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar("zk", actionGroup, true);
        actionToolbar.setTargetComponent(toolBar);
        toolBar.add(actionToolbar.getComponent());

        initAddButton();

        initRemoveButton();

        System.out.println("init tool bar");
    }

    private void initRemoveButton() {
        actionGroup.add(new AnAction("Remove Current Configuration ", "Remove current configuration", AllIcons.General.Remove) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
                String host = String.valueOf(zkHostListComBox.getSelectedItem());
                if (GlobalConstants.CLOSE_ITEM.equals(host)) {
                    MsgUtil.print("The default configuration cannot be removed", NotificationType.WARNING);
                    return;
                }

                int choice = Messages.showYesNoDialog("Confirm to remove this config [" + host + "] ?", "Tip", Messages.getWarningIcon());
                if (choice == JOptionPane.YES_OPTION) {
                    ZkUtil.removeHost(host);
                    ZkUtil.refreshZkList(zkHostListComBox);
                }
            }
        });
        actionGroup.addSeparator();
    }

    private void initAddButton() {
        actionGroup.add(new AnAction("Add Config", "Add zookeeper config", AllIcons.General.Add) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
                new InsertHostDialog(zkHostListComBox).show();
            }
        });
        actionGroup.addSeparator();
    }

    private void initZkHostList() {
        zkHostListComBox.setPreferredSize(new Dimension(170, 30));
        ZkUtil.initZkList(zkHostListComBox);
        zkHostListComBox.addActionListener(new AbstractAction() {
            private String lastSelectedHost = "";

            @Override
            public void actionPerformed(ActionEvent e) {
                @SuppressWarnings("unchecked")
                JComboBox<String> source = (JComboBox<String>) e.getSource();
                String root = (String) source.getSelectedItem();
                if (StrUtil.equals(root, lastSelectedHost)) {
                    return;
                }
                lastSelectedHost = root;
                updateTree(root);
            }
        });
        zkHostListComBox.setSelectedItem(zkHostListComBox.getItemAt(0));
    }

    private void initTree() {
        // 选中节点加载子节点
        dataTree.addTreeSelectionListener(new AddChildSelectionListener(dataTree));
        // 选中节点展示节点详情
        dataTree.addTreeSelectionListener(new ShowDataInfoSelectionListener(nodeTableInfo, dataTree));
        // 鼠标右键展示操作
        dataTree.addMouseListener(new RightMouseClickListen(dataTree));
    }

    private void updateTree(String host) {
        if (StrUtil.isBlank(host) || StrUtil.equals(host, GlobalConstants.CLOSE_ITEM)) {
            dataTree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode(GlobalConstants.CLOSE_MSG)));
            dataTree.updateUI();
            return;
        }

        if (dataTree.getModel().getRoot().equals(host)) {
            return;
        }

        DefaultMutableTreeNode root = new DefaultMutableTreeNode(host);
        DefaultTreeModel treeModel = new DefaultTreeModel(root);
        dataTree.setModel(treeModel);

        // 设置选中根节点，以便于触发选中事件
        dataTree.setSelectionRow(0);
    }

}
