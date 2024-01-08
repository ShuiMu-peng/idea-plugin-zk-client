package com.lipeng.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.notification.NotificationType;
import com.lipeng.consts.GlobalConstants;
import com.lipeng.entity.ZkNodeInfo;
import lombok.SneakyThrows;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lipeng 2023/12/20
 */
public class ZkUtil {
    /**
     * 存储配置的zk列表
     */
    private static final Set<String> ZK_HOST = new LinkedHashSet<>();
    private static final Map<String, ZooKeeper> ZK_INSTANCE = new HashMap<>();
    public static final String COM_LIPENG_ZK_HOST = "com:lipeng:zk:host";

    static {
        List<String> list = PropertiesComponent.getInstance().getList(COM_LIPENG_ZK_HOST);
        System.out.println("init list:" + list);
        if (CollectionUtil.isNotEmpty(list)) {
            ZK_HOST.addAll(list);
        }
    }

    public static void addHost(String host) {
        ZK_HOST.add(host);
        PropertiesComponent.getInstance().setList(COM_LIPENG_ZK_HOST, ZK_HOST);
    }

    public static void closeHost(String host) {
        if (ZK_HOST.contains(host)) {
            IoUtil.close(ZK_INSTANCE.remove(host));
        }
    }

    public static void removeHost(String host) {
        ZK_HOST.remove(host);
        if (ZK_HOST.contains(host)) {
            IoUtil.close(ZK_INSTANCE.remove(host));
        }
        PropertiesComponent.getInstance().setList(COM_LIPENG_ZK_HOST, ZK_HOST);
    }

    public static Set<String> getAllHost() {
        return ZK_HOST;
    }

    public static ZooKeeper getZkInstance(String host) {
        ZooKeeper zooKeeper = ZK_INSTANCE.computeIfAbsent(host, k -> {
            ZooKeeper zk = null;
            try {
                zk = new ZooKeeper(host, 5 * 1000, null);
                zk.exists(StrUtil.SLASH, false);
                return zk;
            } catch (Exception e) {
                IoUtil.close(zk);
                MsgUtil.print("connect zookeeper error," + e.getMessage(), NotificationType.ERROR);
                throw new RuntimeException(e);
            }
        });

        // 测试是否可用,可能超时导致不可用
        try {
            zooKeeper.exists(StrUtil.SLASH, false);
        } catch (Exception e) {
            MsgUtil.print("refresh connect zookeeper " + host, NotificationType.WARNING);
            IoUtil.close(ZK_INSTANCE.remove(host));
            return getZkInstance(host);
        }
        return zooKeeper;
    }

    public static void initChild(String host, DefaultMutableTreeNode selectedNode, int i) {
        String pathStr = ZkUtil.getSelectNodePath(selectedNode);
        List<String> child = ZkUtil.getChild(host, pathStr);
        child.forEach(a -> {
            DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(a);
            selectedNode.add(newNode);

            // 加载孙子节点
            if (i == 0) {
                initChild(host, newNode, i + 1);
            }
        });
    }


    public static String getSelectNodePath(DefaultMutableTreeNode selectedNode) {
        TreeNode[] path = selectedNode.getPath();

        String pathStr = Arrays.stream(Arrays.copyOfRange(path, 1, path.length))
                .map(a -> ((DefaultMutableTreeNode) a).getUserObject().toString())
                .collect(Collectors.joining(StrUtil.SLASH));
        return StrUtil.SLASH + pathStr;
    }

    @SneakyThrows
    public static String getValue(String host, DefaultMutableTreeNode path) {
        byte[] data = getZkInstance(host).getData(getSelectNodePath(path), false, new Stat());
        if (ArrayUtil.isEmpty(data)) {
            return StrUtil.EMPTY;
        }
        return new String(data);
    }

    @SneakyThrows
    public static ZkNodeInfo getZkNodeInfo(String host, String path) {
        Stat stat = getZkInstance(host).exists(path, false);
        String data = StrUtil.EMPTY;
        byte[] dataByte = getZkInstance(host).getData(path, false, stat);
        if (ArrayUtil.isNotEmpty(dataByte)) {
            data = new String(dataByte);
        }
        return new ZkNodeInfo(stat, path, data);
    }

    @SneakyThrows
    public static boolean createNode(String host, String path, String data, DefaultMutableTreeNode selectedNode) {
        try {
            String selectNodePath = getSelectNodePath(selectedNode);
            if (!StrUtil.equals(selectNodePath, StrUtil.SLASH)) {
                selectNodePath += StrUtil.SLASH;
            }

            getZkInstance(host).create(selectNodePath + path, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            return true;
        } catch (Exception e) {
            MsgUtil.print(e.getMessage(), NotificationType.ERROR);
        }
        return false;
    }

    /**
     * 修改节点数据
     */
    public static boolean updateNode(String host, String path, String data) {
        try {
            getZkInstance(host).setData(path, data.getBytes(), -1);
            return true;
        } catch (Exception e) {
            MsgUtil.print(e.getMessage(), NotificationType.ERROR);
        }
        return false;
    }

    /**
     * 递归删除节点数据
     */
    public static boolean deleteNode(String host, String path) {
        try {
            List<String> child = getChild(host, path);
            if (CollectionUtil.isNotEmpty(child)) {
                for (String childPath : child) {
                    deleteNode(host, path + StrUtil.SLASH + childPath);
                }
            }
            getZkInstance(host).delete(path, -1);
            return true;
        } catch (Exception e) {
            MsgUtil.print(e.getMessage(), NotificationType.ERROR);
        }
        return false;
    }


    /**
     * 刷新配置的zk列表
     */
    public static void refreshZkList(JComboBox<String> zkHostList) {
        initZkList(zkHostList);
    }

    public static void initZkList(JComboBox<String> zkHostList) {
        zkHostList.removeAllItems();
        zkHostList.addItem(GlobalConstants.CLOSE_ITEM);

        if (CollectionUtil.isEmpty(ZK_HOST)) {
            zkHostList.addItem(StrUtil.EMPTY);
        } else {
            for (String host : ZK_HOST) {
                zkHostList.addItem(host);
            }
        }
    }

    @SneakyThrows
    private static List<String> getChild(String host, String path) {
        return getZkInstance(host).getChildren(path, false);
    }

    public static void destroy() {
        System.out.println("close ... ");
        ZK_INSTANCE.forEach((k, v) -> IoUtil.close(v));
    }
}
