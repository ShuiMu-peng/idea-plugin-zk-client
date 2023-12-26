package com.lipeng.window.listener;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.lipeng.consts.GlobalConstants;
import com.lipeng.entity.ZkNodeInfo;
import com.lipeng.util.ZkUtil;
import com.lipeng.window.table.CustomTableModel;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @author lipeng 2023/12/21
 */
public class ShowDataInfoSelectionListener implements TreeSelectionListener {
    private final JTable nodeTableInfo;
    private final JTree dataTree;

    public ShowDataInfoSelectionListener(JTable nodeTableInfo, JTree dataTree) {
        this.dataTree = dataTree;
        this.nodeTableInfo = nodeTableInfo;
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        String host = String.valueOf(dataTree.getModel().getRoot());
        if (CollectionUtil.isEmpty(ZkUtil.getAllHost()) || host.equals(GlobalConstants.CLOSE_MSG)) {
            return;
        }
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) dataTree.getLastSelectedPathComponent();
        if (ObjectUtil.isEmpty(selectedNode)) {
            return;
        }
        String selectNodePath = ZkUtil.getSelectNodePath(selectedNode);
        ZkNodeInfo zkNodeInfo = ZkUtil.getZkNodeInfo(host, selectNodePath);

        CustomTableModel tableModel = new CustomTableModel(new String[]{"key", "value"});
        tableModel.addRow(new String[]{"path", zkNodeInfo.getPath()});
        tableModel.addRow(new String[]{"data", zkNodeInfo.getData()});
        tableModel.addRow(new String[]{"size", String.valueOf(zkNodeInfo.getSize())});
        tableModel.addRow(new String[]{"version", String.valueOf(zkNodeInfo.getVersion())});
        tableModel.addRow(new String[]{"update time", String.valueOf(zkNodeInfo.getUpdateTime())});
        nodeTableInfo.setModel(tableModel);
    }
}
