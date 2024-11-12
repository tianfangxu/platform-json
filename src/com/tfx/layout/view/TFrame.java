package com.tfx.layout.view;

import com.tfx.layout.interfaces.ResizeHandle;
import com.tfx.layout.manage.ResizeManage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Objects;

/**
 * @author tianfx
 * @date 2022/7/21 1:45 下午
 */
public class TFrame extends JFrame implements ResizeHandle{

    private static int twidth = 1000;
    private static int theight = 700;
    private static String title = "robot memory version 0.01";
    private static TFrame tFrame;
    
    public TFrame(String title) throws HeadlessException {
        super(title);
    }

    public static TFrame getInstance(String title,int width,int height){
        TFrame.title = title;
        TFrame.twidth = width;
        TFrame.theight = height;
        return getInstance();
    }

    public static TFrame getInstance(String title){
        TFrame.title = title;
        return getInstance();
    }
    
    public static TFrame getInstance(){
        if (Objects.nonNull(tFrame)){
            return tFrame;
        }
        tFrame = new TFrame(title);
        tFrame.setSize(twidth, theight);
        // 把窗口位置设置到屏幕中心
        tFrame.setLocationRelativeTo(null);
        // 当点击窗口的关闭按钮时退出程序
        tFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        //样式调整未无样式。需自定义
        tFrame.getContentPane().setLayout(null);
        //窗口大小事件
        tFrame.resizeListener();
        //注册
        ResizeManage.register(tFrame);
        return tFrame;
    }

    public int getTwidth() {
        return twidth;
    }

    public int getTheight() {
        return theight;
    }
    
    public void resizeListener(){
        this.addComponentListener(new ComponentAdapter(){
            @Override
            public void componentResized(ComponentEvent e) {
                TFrame instance = TFrame.getInstance();
                ResizeManage.resize(instance.getWidth(),instance.getHeight());
            }
        });
    }

    @Override
    public void resizeFun(int x, int y) {
        this.twidth = x;
        this.theight = y;
    }
}
