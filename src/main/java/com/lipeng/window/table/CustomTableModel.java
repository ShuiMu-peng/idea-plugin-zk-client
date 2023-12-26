package com.lipeng.window.table;

import javax.swing.table.DefaultTableModel;

/**
 * 不可编辑的表格
 * @author lipeng 2023/12/22
 */
public class CustomTableModel extends DefaultTableModel {

    public CustomTableModel(String[] strings) {
        super(null, strings);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}
