package com.lipeng.dialog;

import cn.hutool.core.util.StrUtil;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.JBColor;
import com.lipeng.util.ZkUtil;

import javax.swing.*;
import java.awt.*;

/**
 * @author lipeng 2023/12/22
 */
public class InsertHostDialog extends DialogWrapper {
    JTextField textField;
    private final JComboBox<String> zkHostList;
    private static final String placeholder = "Enter zookeeper host, for example:127.0.0.1:2181";

    public InsertHostDialog(JComboBox<String> zkHostList) {
        super(true);
        this.zkHostList = zkHostList;

        setTitle("输入 Zookeeper 地址");
        init();
        super.setSize(400, 80);
    }

    @Override
    protected JComponent createCenterPanel() {
        JPanel dialogPanel = new JPanel(new BorderLayout());
        dialogPanel.setPreferredSize(new Dimension(200, 50));

        textField = new JTextField();
        // 设置输入提示文本
        textField.setForeground(JBColor.GRAY);
        textField.setText(placeholder);

        // 添加焦点事件监听器
        textField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(JBColor.BLACK);
                }
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
                if (textField.getText().isEmpty()) {
                    textField.setForeground(JBColor.GRAY);
                    textField.setText(placeholder);
                }
            }
        });
        textField.setPreferredSize(new Dimension(100, 30));
        dialogPanel.add(textField, BorderLayout.NORTH);

        return dialogPanel;
    }

    @Override
    protected void doOKAction() {
        String host = textField.getText();
        if (StrUtil.isBlank(host) || StrUtil.equals(host, placeholder)) {
            Messages.showErrorDialog("Can not be empty", "Error");
            return;
        }
        ZkUtil.addHost(host);
        ZkUtil.refreshZkList(zkHostList);
        this.close(0);
    }
}
